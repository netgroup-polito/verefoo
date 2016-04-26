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

CODE_GENERATOR
Java serializer and formatter


UTILITY
Contains utility methods used by other modules


JSON_GENERATOR
Interactive module to generate the configuration files (default names are "chains.json" and "config.json")
"chains.json" describes all the chains of nodes belonging to a certain scenario


TEST_CLASS_GENERATOR
Generates one or multiple test scenarios given the two configuration files above (default names are "chains.json" and "config.json")
All the test scenarios have to be placed in the examples folder (i. e. under "j-verigraph/service/src/tests/examples").
Here is the script help:

test_class_generator.py -c <chain_file> -f <conf_file> -o <output_name>

Supposing the module gets executed from the project root directory (i.e. "j-verigraph"), a sample command is the following:

service/src/tests/j-verigraph-generator/test_class_generator.py -c "service/src/tests/j-verigraph-generator/examples/budapest/chains.json" -f "service/src/tests/j-verigraph-generator/examples/budapest/config.json" -o "service/src/tests/examples/Scenario"

Keep in mind that in the previous command "Scenario" represents a prefix which will be followed by an underscore and an incremental number starting from 1, which represents the n-th scenario starting from the previously mentioned "chains.json" file (this file can indeed contain multiple chains).


TEST_GENERATOR
Generates a file which performs the verification test through Z3 (theorem prover from Microsoft Research) given a certain scenario generated with the above snippet. All the test modules have to be placed under the "tests" directory (i.e. under "j-verigraph/service/src/tests").
Here is the module help:

test_generator.py -i <inputfile> -o <outputfile> -s <source> -d <destination>

Supposing the module gets executed from the project root directory (i.e. "j-verigraph") a sample command given the previously generated scenario is the following:

service/src/tests/j-verigraph-generator/test_generator.py -i service/src/tests/examples/Scenario_1.java -o service/src/tests/Test.java -s user1 -d webserver

The aforementioned "Test.java" file can be compiled and executed normally. Its output will be either "SAT" or "UNSAT". For possible statistics the test is repeated 10 times and the average execution time in seconds is printed to the console.