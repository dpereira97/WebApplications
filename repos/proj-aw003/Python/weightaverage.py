import os
import sys
import csv
import linecache
from collections import OrderedDict

def read_ids(ids_path):
    # ids array
    ids = []

    with open(ids_path) as f:
        for line in f:
            ids.append(int(line))

    return ids

def average(weights, factor_files_path, ids_path):

    ids = read_ids(ids_path)

    output = {key: [] for (key) in ids}

    for id in ids:
        all_factors = []
        for factor_filename in factor_files_path:
            factor = get_factor(id, factor_filename)
            if (factor is None):
                factor=0
            all_factors.append(factor)
        output[id] = round( sum(map(lambda x: x[0]*x[1],zip(all_factors,weights)))/sum(weights),2)

    # print(output)
    return output


def get_factor(id, filename):
    with open(filename) as f:
        for line in f:
            line_array = line.split("\t")
            id_read = int(line_array[0])
            factor = float(line_array[1])
            if (id ==  id_read):
                return factor
    return None


def save_weigths(filename):
   weights = []
   line = linecache.getline(filename, 1)
   line = line.split("\t")
   try:
       weights.append(float(line[0]))
       weights.append(float(line[1]))
       weights.append(float(line[2]))
       weights.append(float(line[3]))
   except IndexError as error:
       sys.exit("Input file must have 4 arguments (weights)")
   return weights

def save_params():
    params = []
    params.append(sys.argv[1])
    params.append(sys.argv[2])
    return params

def start():
    params = save_params()
    print(params)

    field = params[0]
    disease = params[1]

    # name of the output file
    output_file_csv = ('/home/aw003/public_html/'+field+'/'+disease+'/'+disease+'Output.tsv')

    # input file with all ids
    ids_path = ('/home/aw003/public_html/'+field+'/'+disease+'/'+disease+'.tsv')
    # array with 4 factor filenames (order matters and must match weights)
    factor_files_path = [('/home/aw003/public_html/'+field+'/'+disease+'/'+disease+'tfidf.tsv'),('/home/aw003/public_html/'+field+'/'+disease+'/'+disease+'Dishln.tsv'),
                        ('/home/aw003/public_html/'+field+'/'+disease+'/'+disease+'AvgFeedback.tsv'),('/home/aw003/public_html/'+field+'/'+disease+'/'+disease+'RelevDate.tsv')]
    print(factor_files_path)
    # array with 4 weights (order matters)
    weights = save_weigths('/home/aw003/public_html/weights.txt')

    if (len(weights) != 4):
        sys.exit("Must have 4 weights")

    if (sum(weights) != 1):
        sys.exit("Sum of weights must be 1")

    if (len(factor_files_path) != 4):
        sys.exit("Must have 4 factor files")

    print("Weights: {0}".format(weights))
    print("Factor files: {0}".format(factor_files_path))

    output = average(weights, factor_files_path, ids_path)
    print("Weight: {0}".format(output))

    output_sorted = OrderedDict(sorted(output.items(), key=lambda x: x[1],reverse= 1))

    print("Weight average by id: {0}".format(output_sorted))

    export_as_csv(output_file_csv, output_sorted)

def export_as_csv(output_filename, output):
    with open(output_filename, mode='w') as file:
        writer = csv.writer(file, delimiter="\t", quotechar='"', quoting=csv.QUOTE_MINIMAL)

        for key in output.keys():
            writer.writerow([key, output[key]])

if __name__ == '__main__':
    start()