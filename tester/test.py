#!/usr/bin/python
#
#  Copyright 2016 Politecnico di Torino
#  Authors:
#  Project Supervisor and Contact: Riccardo Sisto (riccardo.sisto@polito.it)
#  
#  This file is part of Verigraph.
#  
#  Verigraph is free software: you can redistribute it and/or modify
#  it under the terms of the GNU Affero General Public License as
#  published by the Free Software Foundation, either version 3 of
#  the License, or (at your option) any later version.
#  
#  Verigraph is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU Affero General Public License for more details.
#  
#  You should have received a copy of the GNU Affero General Public
#  License along with Verigraph.  If not, see
#  <http://www.gnu.org/licenses/>.
#
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

# Constants (change them, if appropriate)
VERIGRAPH_PORT = "8080"
TEST_CASES_DIR = "testcases"
BASE_URL = "http://localhost:"+VERIGRAPH_PORT+"/verify/api/graphs/"
SCHEMA_FILE = "testcase_schema.json"

# Variables
success = 0
run = 0

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

# Iterate over .json files contained in the TEST_CASES_DIR
for i in os.listdir(TEST_CASES_DIR):
    if i.endswith(".json"): 
        with open(TEST_CASES_DIR+os.path.sep+i) as data_file:
            try:
                # Load json file (raise exception if malformed)
                data = json.load(data_file)
                
                # Validate input json against schema (raise exception if invalid)
                validate(data, schema)
                
                run += 1
                print("Test case ID: "+str(data["id"]))
                print("\tFILE NAME: "+i)
                print("\tTEST NAME: "+data["name"])
                print("\tTEST DESCRIPTION: "+data["description"])
                
                # POST the graph
                r = requests.post(BASE_URL, json=data["graph"])
                if r.status_code == 201:
                    graph_id = r.json()["id"]
                    print("\tCreated Graph has ID " + str(graph_id) + " on VeriGraph")
                    
                    # GET the policy verification result
                    policy = requests.get(BASE_URL+str(graph_id)+"/policy"+data["policy_url_parameters"])
                    
                    # Check the response
                    if policy.status_code == 200:
                        print("\tVerification result is " + policy.json()["result"])
                        
                        # Check the result with the expected one
                        if policy.json()["result"] == data["result"]:
                            # SUCCESS
                            print("\t+++ Test passed +++")
                            success += 1
                        else:
                            # FAIL
                            eprint("\t[ERROR] Expected result was " + data["result"] + " but VeriGraph returned " + policy.json()["result"])
                            print("\t--- Test failed ---")
                    else:
                        print("\tVeriGraph returned an unexpected response -> " + str(policy.status_code), policy.reason)
                        print("\t--- Test failed ---")
                print()
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
print("Test succeded = "+str(success))
if run != 0:
    if success != run:
        print("\n --- Some tests failed. See the output. ---")
    else:
        print("\n +++ All tests passed +++")
else:
    print("\n\n +++ 0 tests executed +++")
    