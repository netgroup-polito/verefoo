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

import json
import contextlib
import sys
import os
import subprocess
from pprint import pprint

#manages output easily (can either write to file or to stdout)            
@contextlib.contextmanager
def smart_open(filename=None):
    if filename and filename != '-':
        fh = open(filename, 'w')
    else:
        fh = sys.stdout
    try:
        yield fh
    finally:
        if fh is not sys.stdout:
            fh.close()
            
def check_input_is_int(text):
    while True:
        data = raw_input(text)
        try:
            int_value = int(data)
        except ValueError:
            print "Please enter a valid number!"
            continue
        return int_value
    
#parses a json file into a unicode dictionary
def parse_json_file(filename):
    with open(filename) as json_file:    
        return json.load(json_file)

#returns an ascii dictionary from a unicode one
def convert_unicode_to_ascii(input):
    if isinstance(input, dict):
        return {convert_unicode_to_ascii(key): convert_unicode_to_ascii(value) for key, value in input.iteritems()}
    elif isinstance(input, list):
        return [convert_unicode_to_ascii(element) for element in input]
    elif isinstance(input, unicode):
        return input.encode('utf-8')
    else:
        return input
    
#parses a chains file
def parse_chains(chains_file):
    chains_json = convert_unicode_to_ascii(parse_json_file(chains_file))
    
    chains = {}
    
    for chn in chains_json["chains"]:
        try:
            chains[chn["id"]] = {}        
            #initiatlize the config dictionary for each node
            for node in chn["nodes"]:
                chains[chn["id"]][node["name"]] = {}
        except:
            raise KeyError("Chains file is not valid!")
    
    for chn in chains_json["chains"]:
        try:
            #set chn values ---> chn(name, (field, value))
            for node in chn["nodes"]:
                for key, value in node.items():
                    #name key is redundant in map
                    if key != "name":
                        chains[chn["id"]][node["name"]][key] = value
        except:
            raise KeyError("Chains file is not valid!")
    return chains

def check_chains_integrity(filename):
    print "Checking input file..."
    try:
        chains = convert_unicode_to_ascii(parse_json_file(filename))
        print "File correctly parsed"
        if isinstance(chains["chains"], list) == False:
            print "Child of chains is not a list!"
            return False
        for chain in chains["chains"]:
            print "Chain found, checking its fields..."
            print "Checking chain id field... "
            chain["id"]
            print "OK!"
            print "Checking chain flowspace field... "
            chain["flowspace"]
            print "OK!"
            if isinstance(chain["nodes"], list) == False:
                print "Chain #" + chain["id"] + " does not have a list of nodes!" 
                return False
            for node in chain["nodes"]:
                print "Node found, checking its fields..."
                print "Checking node name... "
                node["name"]
                print "OK!"
                print "Checking node functional_type field... "
                node["functional_type"]
                print "OK!"
                print "Checking node address field... "
                node["address"]
                print "OK!"
    except (KeyboardInterrupt, SystemExit):
        raise
    except:
        print "One or more required fields are missing!"
        return False
    print filename + " validated successfully!"
    return True

def check_config_integrity(filename):
    print "Checking input file..."
    try:
        config = convert_unicode_to_ascii(parse_json_file(filename))
        pprint(config)
        print "File correctly parsed"
        if isinstance(config["nodes"], list) == False:
            print "Child of nodes is not a list!"
            return False
        for node in config["nodes"]:
            print "Node found, checking its fields..."
            print "Checking id field... "
            node["id"]
            print "OK!"
            print "Checking description field... "
            node["description"]
            print "OK!"
            print "Checking configuration field... "
            node["configuration"]
            print "OK!"
            if isinstance(node["configuration"], list) == False:
                print "Checking if node configuration is a list..."
                print "Node with id " + node["id"] + " does not have a configuration list!" 
                return False
            for c in node["configuration"]:
                print "Checking if node configuration element is a string or a dictionary..."
                if (isinstance(c, str) == False and isinstance(c, dict) == False):
                    print "At least one element of node with id " + node["id"] + " has an invalid configuration (it is neither a string or a map)"
                    return False
    except (KeyboardInterrupt, SystemExit):
        raise
    except:
        print "One or more required fields are missing!"
        return False
    print filename + " validated successfully!"
    return True

def check_routing_integrity(filename):
    print "Checking input file..."
    try:
        routing = convert_unicode_to_ascii(parse_json_file(filename))
        print "File correctly parsed"
        if isinstance(routing["routing_table"], list) == False:
            print "Child of routing_table is not a list!"
            return False
        for node in routing["routing_table"]:
            if isinstance(node, dict) == False:
                print "Child of routing_table is not a map!" 
                return False
            for n, rt in node.items():
                if isinstance(rt, list) == False:
                    print "Routing table of element " + n + " is not a list!"
                    return False
                for entry in rt:
                    if isinstance(entry, dict) == False:
                        print "Invalid entry for node " + n + " (not a map)!"
                        return False
    except (KeyboardInterrupt, SystemExit):
        raise
    except:
        print "One or more required fields are missing!"
        return False
    return True

#prints every node for each input chain    
def print_chains(chains):
    for chain in chains["chains"]:
        print "CHAIN #" + str(chain["id"])
        for node in chain["nodes"]:
            print "Name: " + str(node["name"])
            print "Functional type: " + str(node["functional_type"])
            print "Address: " + str(node["address"])
            print "-----------------------------------"
        print ""

#prints every node's configuration 
def print_configuration(configuration):
    print "NODES CONFIGURATION"
    for node in configuration["nodes"]:
        print "Name: " + str(node["id"])
        print "Description: " + str(node["description"])
        print "Configuration: "
        pprint(node["configuration"])
        print "-----------------------------------"
    print ""

#print every node's routing table
def print_routing_table(routing):
    print "ROUTING"
    for table in routing["routing_table"]:
        for node,rt in table.items():
            print "Name: " + str(node)
            pprint(rt)
            print "-----------------------------------"
        print ""

#returns a list of tuple [(k1, v1), (k2, v2)] from a list of maps like [{k1 : v1},{k2 : v2}]
def formatted_list_from_list_of_maps(maps):
    l = []
    for map in maps:
        if isinstance(map, dict):
            for k, v in map.items():
                #l.append("(ctx." + str(k) + ", ctx." + str(v) + ")")
                l.append(str(k))
                l.append(str(v))
        else:
            #l.append("ctx." + map)
            l.append(map)
    return l
    
def list_directories(dir):
    #output = subprocess.call(["ls", "-d", "*/"])
    output = subprocess.call(["find", dir, "-type", "d"])
    
    #TREE VERSION
    #find = subprocess.Popen(["find", ".", "-type", "d"], stdout=subprocess.PIPE)
    #output = subprocess.check_output(["sed", "-e", "s/[^-][^\/]*\//  |/g", "-e", "s/|\([^ ]\)/|-\1/"], stdin=find.stdout)
    #find.wait()
    
#     ps = subprocess.Popen(('ps', '-A'), stdout=subprocess.PIPE)
#     output = subprocess.check_output(('grep', 'process_name'), stdin=ps.stdout)
#     ps.wait()
    return output

def list_files(dir):
    output = subprocess.call(["find", dir, "-type", "f"])
    return output

def search_node_in_chains(n):
    found = []
    for chain in chains["chains"]:
        for node in chain["nodes"]:
            if node["name"] == n:
                found.append(node)
    return found