#!/bin/bash

while getopts ":r:p:n:g:h" opt; do
  case ${opt} in
    r )
      REGION=$OPTARG
      ;;
    p )
      SEARCH_PATH=$OPTARG
      ;;
    n )
      ACCT_NUM=$OPTARG
      ;;
    g )
      TAG=$OPTARG
      ;;
    \? )
      echo "Invalid option: $OPTARG" 1>&2
      ;;
    : )
      echo "Invalid option: $OPTARG requires an argument" 1>&2
      ;;
  esac
done
shift $((OPTIND -1))


# RELATIVE_SCRIPT_DIR=$(dirname $0)
# RELATIVE_SCRIPT_DIR=${RELATIVE_SCRIPT_DIR#./}
# FULL_SCRIPT_DIR="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )"


if [[ $REGION == "" ]]; then
  REGION="ca-central-1"
fi

if [[ $SEARCH_PATH == "" ]]; then
  echo "Must supply search path"
  exit 1
fi

if [[ $ACCT_NUM == "" ]]; then
  echo "Must supply account number for registry"
  exit 1
fi

if [[ $TAG == "" ]]; then
  echo "Must supply tag for build"
  exit 1
fi


echo $REGION
echo $SEARCH_PATH
echo $ACCT_NUM
echo $TAG


dockerfile_count=$(find $SEARCH_PATH -name Dockerfile | wc -l)
if [[ $dockerfile_count -le 0 ]]; then
  echo "No Dockerfiles found. Nothing to do."
  exit 0
fi

aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin "${ACCT_NUM}.dkr.ecr.${REGION}.amazonaws.com"

for file in $(find $SEARCH_PATH -name Dockerfile | xargs realpath); do
  path=$(dirname $file)
  echo $path
  application=$(basename $path)  # application name is the directory containing the Dockerfile.
  application=$(echo "$application" | tr '[:upper:]' '[:lower:]')  # ECR repo names must be lowercase.

  if aws ecr describe-repositories --repository-names ${application} 2>&1 | grep -q RepositoryNotFoundException
  then
    aws ecr create-repository --repository-name ${application} --image-tag-mutability IMMUTABLE
  fi
  echo $path
  cd $path
  docker build . -t "${ACCT_NUM}.dkr.ecr.${REGION}.amazonaws.com/${application}:${TAG}"
  docker push "${ACCT_NUM}.dkr.ecr.${REGION}.amazonaws.com/${application}:${TAG}"
done