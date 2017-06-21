__author__ = 'potercharm'
import csv
import json

csv_files = [
    "raw/pois.csv",
    "raw-de/pois.csv",
    "raw-es/pois.csv",
    "raw-pl/pois.csv",
    "raw-uk/pois.csv",
    ]

def save_json(data, filename):
    with open(filename, 'w') as outfile:
        json.dump(data, outfile)

def create_poi(row):
    poi = {}
    poi["id"] = row[0]
    poi["category"] = row[1]
    poi["pos"] = [row[2], row[3]]
    poi["name"] = row[4]
    poi["description"] = row[5]
    poi["tags"] = row[6].split(" ")
    poi["rate"] = row[7]
    poi["facebook"] = row[8]
    return poi

def generate_json(filename):
    with open(filename, 'rb') as csvfile:
        reader = csv.reader(csvfile, delimiter=',', quotechar='"')
        pois = []
        for row in reader:
            pois.append(create_poi(row))
        save_json(pois, filename.split("/")[0]+".json")

if __name__ == "__main__":
    for filename in csv_files:
        generate_json(filename)