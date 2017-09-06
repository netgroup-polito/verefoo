#!/usr/bin/python
##############################################################################
# Copyright (c) 2017 Politecnico di Torino and others.
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Apache License, Version 2.0
# which accompanies this distribution, and is available at
# http://www.apache.org/licenses/LICENSE-2.0
##############################################################################

from __future__ import print_function
from jsonschema import validate
from pprint import pprint
import sys
import requests
from requests.exceptions import *
from jsonschema.exceptions import *
import json
import getopt
import os
import subprocess
import csv
import datetime
import time
import re
import argparse


# Constants (change them, if appropriate)
VERIGRAPH_PORT = "8080"
TEST_CASES_DIR = "testcases"
BASE_URL = "http://localhost:"+VERIGRAPH_PORT+"/verigraph/api/graphs/"
SCHEMA_FILE = "testcase_schema.json"

# Variables
fail = 0
run = 0

tests_number = 1

# Utils
def eprint(toPrint):
    sys.stdout.flush()
    print(toPrint, file=sys.stderr)
    sys.stderr.flush()

# Print PYTHON ver
print("PYTHON " + sys.version)

# Loading schema file
try:
    schema = json.load(open(SCHEMA_FILE))
except ValueError:
    eprint("Invalid json schema (check your "+SCHEMA_FILE+")!\nExiting.")
    exit(-1)
with open('result.csv', 'w') as file:
    writer=csv.writer(file, delimiter=',', quoting=csv.QUOTE_MINIMAL, lineterminator='\n')
    writer.writerow(['SRC','DST','GRAPH_ID','TEST_ID','RESULT','TIMES (ms)'])
    # Iterate over .json files contained in the TEST_CASES_DIR
    for i in os.listdir(TEST_CASES_DIR):
        if i.endswith(".json"):
            with open(TEST_CASES_DIR+os.path.sep+i) as data_file:
                try:
                    # Load json file (raise exception if malformed)
                    data = json.load(data_file)

                    # Validate input json against schema (raise exception if invalid)
                    validate(data, schema)


                    parser = argparse.ArgumentParser(description='iteration number')
                    parser.add_argument('-iteration')
                    args = vars(parser.parse_args())
                    tests_number=args['iteration'][0]

                    print("Test case ID: "+str(data["id"]))
                    print("\tFILE NAME: "+i)
                    print("\tTEST NAME: "+data["name"])
                    print("\tTEST DESCRIPTION: "+data["description"])

                    requested=data["policy_url_parameters"]
                    results=data["results"]

                    if(len(requested)==len(results)):

                        run += 1
                        # POST the graph
                        r = requests.post(BASE_URL, json=data["graph"])
                        if r.status_code == 201:
                            graph_id = r.json()["id"]
                            print("\tCreated Graph has ID " + str(graph_id) + " on VeriGraph")

                            for i in range(len(requested)):
                                print("request #: "+str(i))
                                output=[]
                                total_time=[]
                                result=[]
                                temp=requested[i]
                                temp=re.split(r'[&=]', temp)
                                output.append(temp[3])
                                output.append(temp[5])

                                #range(0, n)) where n is the number of tests to execute for every request (1 is the default)
                                for n in range(0, int(tests_number)):
                                    start_time=datetime.datetime.now()
                                    # GET the policy verification result
                                    policy = requests.get(BASE_URL+str(graph_id)+"/policy"+data["policy_url_parameters"][i])
                                    total_time.append(int((datetime.datetime.now()-start_time).total_seconds() * 1000))

                                    # Check the response
                                    if policy.status_code == 200:
                                        print("\tVerification result is " + policy.json()["result"])

                                        # Check the result with the expected one
                                        if policy.json()["result"] == data["results"][i]:
                                            # SUCCESS
                                            print("\t+++ Test passed +++")
                                            if n==0:
                                                result.append(policy.json()["result"])

                                        else:
                                            # FAIL
                                            #eprint("\t[ERROR] Expected result was " + data["result"][i] + " but VeriGraph returned " + policy.json()["result"])
                                            result.append("FAIL")
                                            fail+=1
                                            print("\t--- Test failed ---")
                                    else:
                                        print("\tVeriGraph returned an unexpected response -> " + str(policy.status_code), policy.reason)
                                        fail+=1
                                        print("\t--- Test failed ---")
                                if(policy.status_code == 200):
                                    output.append(graph_id)
                                    output.append(data["id"])
                                    output.append(result[0])
                                    for j in range(len(total_time)):
                                        output.append(total_time[j])
                                    writer.writerow(output)
                                else:
                                    output.append("Error")
                                    output.append("Policy code="+str(policy.status_code))
                                    writer.writerow(output)
                        print()
                    else:
                        print("\tThe number of requests ("+str(len(requested))+")is not equal to the numer of results ("+str(len(results))+")")
                        print("\tPlease check the testcases file")
                except ValueError:
                    print("Malformed json!\nSkipping "+i+" file")
                    print("\t--- Test failed ---")
                except ValidationError:
                    print("Invalid json (see Schema file)!\nSkipping "+i+" file")
                    print("\t--- Test failed ---")
                except ConnectionError:
                    print("Connection refused!")
                    print("\t--- Test failed ---")
                except HTTPError:
                    print("HTTP error!")
                    print("\t--- Test failed ---")

# Final output
print("\nTest run = "+str(run))
if run != 0:
    if fail != 0:
        print("\n --- Some tests failed. See the output. ---")
    else:
        print("\n +++ All tests passed +++")
else:
    print("\n\n +++ 0 tests executed +++")
    