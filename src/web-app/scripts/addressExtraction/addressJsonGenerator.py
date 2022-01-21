import os
import csv
import json


PATH_TO_CSV_FILE= os.path.join(os.path.dirname(__file__), "ISED_locations.csv")
PATH_TO_OUTPUT_JSON= os.path.join(os.path.dirname(__file__), "extracted_addresses.json")

location_list=[]

with open(PATH_TO_CSV_FILE) as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=',')
    line_count = 0
    for row in csv_reader:
        if line_count > 0:
            location_list.append({
                "PutRequest": {
                    "Item": {
                        "pk": {
                            "S": row[0]
                        },
                        "address-en": {
                            "S": row[1]
                        },
                        "address-fr": {
                            "S": row[2]
                        },
                        "city": {
                            "S": row[3]
                        },
                        "province": {
                            "S": row[4]
                        }
                    }
                }
            })
        line_count += 1

    print(f'Processed {line_count} lines.')


output_object ={'IsedWorkLocations': location_list}

# the json file where the output must be stored
out_file = open(PATH_TO_OUTPUT_JSON, "w")
json.dump(output_object, out_file)
