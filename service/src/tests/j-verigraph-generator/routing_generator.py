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

from pprint import pprint
import sys, getopt
import os
from utility import *

# used by test_class_generator
def generate_routing_from_chain(chain):
    routing = {}
    routing["routing_table"] = {}
    
    chain = chain["nodes"]     
    for i in range(0, len(chain)):
        routing["routing_table"][chain[i]["name"]] = {}
        for j in range(i-1, -1, -1):
            routing["routing_table"][chain[i]["name"]][chain[j]["address"]] = chain[i-1]["name"] 
        for k in range (i+1, len(chain)):
            routing["routing_table"][chain[i]["name"]][chain[k]["address"]] = chain[i+1]["name"]
    pprint(routing)
    return routing

def generate_routing_from_chains_file(chains_file, chain_number):
    routing = {}
    routing["routing_table"] = {}
     
    chains = convert_unicode_to_ascii(parse_json_file(chains_file))
    chain = None
    for chn in chains["chains"]:
         if chn["id"] == chain_number:
             chain = chn["nodes"]
             break
    if chain == None:
         return routing
     
    for i in range(0, len(chain)):
        routing["routing_table"][chain[i]["name"]] = {}
        for j in range(i-1, -1, -1):
            routing["routing_table"][chain[i]["name"]][chain[j]["address"]] = chain[i-1]["name"] 
        for k in range (i+1, len(chain)):
            routing["routing_table"][chain[i]["name"]][chain[k]["address"]] = chain[i+1]["name"]
    pprint(routing)
    return routing
    
    
def main(argv):
    if len(argv) < 4:
        print 'routing_generator.py -c <chains_file> -n <chain_number>'
        sys.exit(2)
    chains_file = ""
    chain_number = ""
    try:
        opts, args = getopt.getopt(argv,"hc:n:",["chains=","id="])
    except getopt.GetoptError:
        print 'routing_generator.py -c <chains_file> -n <chain_number>'
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print 'routing_generator.py -c <chains_file> -n <chain_number>'
            sys.exit()
        elif opt in ("-c", "--chains"):
            chains_file = arg
        elif opt in ("-n", "--id"):
            chain_number = arg
    
    print "Chains file is " + chains_file
    print "Chain id is " + chain_number
    
    return generate_routing_from_chains_file(chains_file, chain_number)

if __name__ == '__main__':
    main(sys.argv[1:])