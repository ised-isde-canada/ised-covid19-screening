import boto3
import os
import simplejson as json


class Table:
    def __init__(self, table_name, db_client):
        self._table = boto3.resource('dynamodb', endpoint_url=os.getenv("DDB_ENDPOINT", None)).Table(table_name)
        self._db_client = db_client

    def batch_put(self, items):
        with self._table.batch_writer() as batch:
            for item in items:
                batch.put_item(Item=item)

    def scan_and_load_into_file(self, destination_file_name):
        scan_kwargs = {}

        done = False
        start_key = None
        while not done:
            if start_key:
                scan_kwargs['ExclusiveStartKey'] = start_key

            response = self._table.scan(**scan_kwargs)
            items = response.get('Items', [])

            with open(destination_file_name, "a") as results_file:
                for item in items:
                    json_string = json.dumps(item)
                    results_file.write(f'{json_string}\n')

            start_key = response.get('LastEvaluatedKey', None)
            done = start_key is None

    def load_file_contents_into_table(self, source_file_name):
        with open(source_file_name) as file:
            lines = file.readlines()
            lines = [line.rstrip() for line in lines]

        items_dict_list = [json.loads(line) for line in lines]

        with self._table.batch_writer() as batch:
            for item in items_dict_list:
                batch.put_item(Item=item)


SOURCE_COVID_SCREENING_TABLE_NAME = 'covid-screening-dev'
SOURCE_WORK_LOCATIONS_TABLE_NAME = 'ised-work-locations-dev'
DESTINATION_COVID_SCREENING_TABLE_NAME = 'covid-app-dev-covid-screening'
DESTINATION_WORK_LOCATIONS_TABLE_NAME = 'covid-app-dev-ised-work-locations'


_ddb_client = boto3.client('dynamodb', endpoint_url=os.getenv("DDB_ENDPOINT", None))

_source_covid_screening_table = Table(SOURCE_COVID_SCREENING_TABLE_NAME, _ddb_client)
_source_work_location_table = Table(SOURCE_WORK_LOCATIONS_TABLE_NAME, _ddb_client)

_destination_covid_screening_table = Table(DESTINATION_COVID_SCREENING_TABLE_NAME, _ddb_client)
_destination_work_location_table = Table(DESTINATION_WORK_LOCATIONS_TABLE_NAME, _ddb_client)

# Extract Data
# _source_covid_screening_table.scan_and_load_into_file('covid_screening_results.txt')
# _source_work_location_table.scan_and_load_into_file('work_location_results.txt')

# Import Data
# _destination_covid_screening_table.load_file_contents_into_table('covid_screening_results.txt')
# _destination_work_location_table.load_file_contents_into_table('work_location_results.txt')


