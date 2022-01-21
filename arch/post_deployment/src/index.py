import json
import logging
import boto3
from boto3.session import Session
import os
import zipfile
import tempfile
import shutil
import uuid
import mimetypes
import datetime

def extract_artifact(data):
  if data.get('creds', None):
    session = Session(
      aws_access_key_id=data['creds']['aws_access_key_id'],
      aws_secret_access_key=data['creds']['aws_secret_access_key'],
      aws_session_token=data['creds']['aws_session_token']
    )
    client = session.client('s3')
  else:
      client = boto3.client('s3')

  tmp_dir = tempfile.TemporaryDirectory()
  with tempfile.NamedTemporaryFile() as tmp_file:
    client.download_file(
      data['bucket'], 
      data['object'],
      tmp_file.name
    )
    with zipfile.ZipFile(tmp_file.name, 'r') as zip:
      zip.extractall(path=tmp_dir.name)
  return tmp_dir


def deployed_manifest_changed(source_dir, source_path, dest_bucket, dest_prefix, manifest):
  s3 = boto3.resource("s3")
  dest = s3.Bucket(dest_bucket)

  remote = dest.Object('/'.join([dest_prefix, manifest])) if dest_prefix else dest.Object(manifest)
  try:
    remote_manifest = remote.get()['Body'].read().decode('utf-8')
  except s3.meta.client.exceptions.NoSuchKey as e:
    logging.getLogger().info(e)
    return True ## Remote doesnt exist hence manifest has by default changed
  
  try:
    with open('/'.join([source_dir.name, source_path, manifest])) as reader:
      local_manifest =  reader.read()
  except Exception as e:
    logging.getLogger().info(e)
    return True ## Local doesnt exist hence cannot determine change - default to changed.
  
  if remote_manifest != local_manifest:
    return True
  return False

def archive_app(bucket, app_prefix, archive_name, archive_path):
  with tempfile.TemporaryDirectory() as local_dir:
    s3 = boto3.resource("s3")
    bucket = s3.Bucket(bucket)
    # Download all objects to local path
    for obj in bucket.objects.filter(Prefix=app_prefix):
      if archive_path in obj.key:
        continue
      local_file = '/'.join([local_dir, obj.key.lstrip(app_prefix)])
      path, _ = os.path.split(local_file)
      os.makedirs(path, exist_ok=True)
      bucket.download_file(obj.key, local_file)
    # archive and upload
    with tempfile.TemporaryDirectory() as local_zip_dir:
      shutil.make_archive('/'.join([local_zip_dir, archive_name]), 'zip', local_dir)
      bucket.upload_file('/'.join([local_zip_dir, archive_name + ".zip"]), ''.join([archive_path, archive_name, ".zip"]))

def deploy_app(source_dir, source_path, dest_bucket, dest_prefix, delete_blacklist=[]):
  s3 = boto3.resource("s3")
  dest = s3.Bucket(dest_bucket)

  seen_keys = []
  for path, _, files in os.walk(source_dir.name):
    if source_path not in path:
      continue
    for f in files:
      dest_path = path.removeprefix(source_dir.name).removeprefix(source_path).lstrip('/')
      if dest_prefix:
        dest_path = '/'.join([dest_path, dest_prefix])
      mime = mimetypes.guess_type(f.removesuffix('.map'))
      dest_key = '/'.join([dest_path, f]) if dest_path else f
      dest.upload_file(
        '/'.join([path, f]),
        dest_key,
        ExtraArgs={'ContentType': mime[0] if mime[0] else 'text/html'}
      )
      seen_keys.append(dest_key)

  delete = []
  for obj in dest.objects.filter(Prefix=dest_prefix):
    if obj.key not in seen_keys and not [ x for x in delete_blacklist if obj.key.startswith(x) ]:
      delete.append({'Key': obj.key})
  if delete:
    for i in range(0, len(delete), 1000): ## deal with delete operation accepting <= 1000 objects
      objects = delete[i:i+1000]
      files_deleted = dest.delete_objects(Delete={'Objects': objects})
      logging.getLogger().info(f'Files Deleted: {files_deleted}')

def static_app_deploy(event):
  manifest = os.getenv("APP_MANIFEST", None)
  dest_bucket = os.getenv('ASSET_BUCKET') ## Will fail if not specified - no fallback
  dest_prefix = os.getenv('ASSET_DEPLOY_DIR', '')
  artifact_source_path = os.getenv("ARTIFACT_SOURCE_PATH", "/build/web-app")
  archive_path = os.getenv("ARCHIVE_PATH", 'archive/')
  delete_blacklist = os.getenv("DELETE_BLACKLIST", 'static/icons').replace(" ", "").split(",")
  delete_blacklist.append(archive_path)

  ## Download Pipeline artifact
  artifact_path = extract_artifact(event['pipeline_artifact'])

  ## If a app manifest exists use it to determine the need for app deployment
  if manifest and not deployed_manifest_changed(artifact_path, artifact_source_path, dest_bucket, dest_prefix, manifest):
    logging.getLogger().info("App Manifest Not Changed - Deployment Skipped")
    return

  ## Archive existing app contents
  if event.get("SourceCommit", None):
    archive = '_'.join([event.get("SourceBranch", "unknown_branch"), event.get("SourceCommit")])
  else:
    archive = str(uuid.uuid4())
  archive_app(dest_bucket, dest_prefix, archive, archive_path)

  # Deploy new App
  deploy_app(
    artifact_path, 
    artifact_source_path, 
    dest_bucket, 
    dest_prefix, 
    delete_blacklist
  )


def create_apigateway_deployment(event):
  rest_api_id = os.getenv('REST_API_GW_ID')
  stage_name = os.getenv('REST_API_GW_STAGE_NAME')
  logging.getLogger().info(f'Creating API Gateway Deployment for Rest API "{rest_api_id}" and stage "{stage_name}"')

  if event.get("SourceCommit", None):
    source_branch = event.get("SourceBranch", "N/A")
    source_commit = event.get("SourceCommit")
  else:
    source_branch = source_commit = 'N/A'

  gw_client = boto3.client('apigateway')
  response = gw_client.create_deployment(
    restApiId=rest_api_id,
    stageName=stage_name,
    description=f'Deployment created from Branch: "{source_branch}", Commit "{source_commit}", on {datetime.datetime.now()}'
  )
  logging.getLogger().info(response)


def setup_default_logging(request_id, level=logging.INFO):
    logger = logging.getLogger()
    console_handler = logging.StreamHandler()
    formatter = logging.Formatter(
        '[%(levelname)s] %(asctime)s {0} [%(module)s:%(lineno)d]: %(message)s'.format(request_id))
    console_handler.setFormatter(formatter)

    logger.handlers = []
    logger.addHandler(console_handler)
    logger.setLevel(level)
    return logger


def handler(event, context):
  logger =setup_default_logging(context.aws_request_id, os.getenv("LOG_LEVEL", "INFO"))
  logger.info(event)
  if 'create' in event.get('post_actions', ''):
    static_app_deploy(event)
    create_apigateway_deployment(event)
  if 'update' in event.get('post_actions', ''):
    static_app_deploy(event)
    create_apigateway_deployment(event)

  return {
    "success": True
  }