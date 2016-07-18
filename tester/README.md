In order to run the automatic testing script test.py, you need the following dependencies installed on your python distribution:
- "requests" python package -> http://docs.python-requests.org/en/master/
- "jsonschema" python package -> https://pypi.python.org/pypi/jsonschema

IMPORTANT - If you have multiple versions of Python installed on your machine, check carefully that the version you are actually using when running the script, has the required packages installed. Requested version is Python 3+

HINT - to install a package you can raise the following command (Bash on Linux or DOS shell on Windows):
	python -m pip install jsonschema
	python -m pip install requests
	
Tested on PYTHON 3.4.3

To add a new test, just put a new .json file inside the testcases folder. The corresponding JSON schema is in the testcase_schema.json file and some examples are already available. Each json file should specify:
- id, an integer for the testcase;
- name, the name for the testcase;
- description, an optional description;
- policy_url_parameters, the parameters to be appended after the verification URL (including the '?' character);
- result, the expected verification result;
- graph, the graph to be tested (the same object that you usually POST to VeriGraph to create a new graph).
The test.py script will test each .json file contained into the testcases folder and will provide a complete output.