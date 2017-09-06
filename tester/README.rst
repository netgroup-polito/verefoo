.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
In order to run the automatic testing script test.py, you need the
following dependencies installed on your python distribution: -
"requests" python package -> http://docs.python-requests.org/en/master/
- "jsonschema" python package -> https://pypi.python.org/pypi/jsonschema

IMPORTANT - If you have multiple versions of Python installed on your
machine, check carefully that the version you are actually using when
running the script, has the required packages installed. Requested
version is Python 3+

HINT - to install a package you can raise the following command (Bash on
Linux or DOS shell on Windows): python -m pip install jsonschema python
-m pip install requests

Tested on PYTHON 3.4.3

To add a new test, just put a new .json file inside the testcases
folder. The corresponding JSON schema is in the testcase\_schema.json
file and some examples are already available. Each json file should
specify: - id, an integer for the testcase; - name, the name for the
testcase; - description, an optional description; -
policy\_url\_parameters, the parameters to be appended after the
verification URL (including the '?' character), it is an array. -
results, the expected verification results, it is an array;

In case of multiple policy\_url\_parameters and results:
``"policy_url_parameters":[    "?type=reachability&source=sap1&destination=webserver1",    "?type=reachability&source=sap3&destination=webserver1"    ],    "results":[    "SAT",    "SAT"    ],``

The test.py script will test each .json file contained into the
testcases folder and will provide a complete output. The result.csv
contains the verificatin results in the following way (column): 1:
source\_node 2: destination\_node 3: graph\_id 4: testcase\_id 5: result
(FAIL in case of test failed) 6: the execution time for each execution
of the verification

It is possible to do several verification for each request in the
policy\_url\_paramters. The default value is 1, but it can be modified
by edit the the arg value of run-test ant task in build.xml
