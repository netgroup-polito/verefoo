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
from pprint import pprint
import sys, getopt
import commands
import os
from config import *
from utility import *
import subprocess

#global variables
chains = {}
chains["chains"] = []
routing = {}
routing["routing_table"] = []
configuration = {}
configuration["nodes"] = []
#end of global variables


#generates json file describing the chains (default chains.json)
def generate_chains(curr_dir, multiplier, flowspace):
    filename = "chains.json"
    
    multiplier = int(multiplier)
    number_of_chains = multiplier*multiplier
    for i in range(0, int(number_of_chains)):
        chains["chains"].insert(i, {})
        chains["chains"][i]["id"] = i+1
        chains["chains"][i]["flowspace"] = flowspace
        chain_nodes = multiplier
        chains["chains"][i]["nodes"] = []
        #set attributes for nth client
        chains["chains"][i]["nodes"].insert(0, {})
        node_name = "client_" + str((i%multiplier)+1)
        chains["chains"][i]["nodes"][0]["name"] = node_name
        node_type = "web_client"
        chains["chains"][i]["nodes"][0]["functional_type"] = node_type
        node_address = "ip_web_client_" + str((i%multiplier)+1)
        chains["chains"][i]["nodes"][0]["address"] = node_address
        #set attributes for chain of firewalls
        for j in range(1, chain_nodes+1):
            chains["chains"][i]["nodes"].insert(j, {})
            node_name = "firewall_" + str(j)
            chains["chains"][i]["nodes"][j]["name"] = node_name
            node_type = "firewall"
            chains["chains"][i]["nodes"][j]["functional_type"] = node_type
            node_address = "ip_firewall_" + str(j)
            chains["chains"][i]["nodes"][j]["address"] = node_address
        #set attributes for nth web server
        chains["chains"][i]["nodes"].insert(chain_nodes+1, {})
        node_name = "server_" + str((i%multiplier)+1)
        chains["chains"][i]["nodes"][chain_nodes+1]["name"] = node_name
        node_type = "web_server"
        chains["chains"][i]["nodes"][chain_nodes+1]["functional_type"] = node_type
        node_address = "ip_web_server_" + str((i%multiplier)+1)
        chains["chains"][i]["nodes"][chain_nodes+1]["address"] = node_address
    #pprint(chains)
    with smart_open(curr_dir + "/" + filename) as f:
        print >>f, json.dumps(chains)
    return filename
    
#generates json file describing the node configurations (default config.json)
def generate_config(curr_dir):
    chains_file = "chains.json"
    
    chains = parse_chains(curr_dir + "/" + chains_file)
    
    print "Chains read from file:"
    pprint(chains)
    chains_id = []
    
    for chain_id, chain in chains.items():
        chains_id.append(chain_id)
        print "Chain #" +  str(chain_id) + " has " + str(len(chain)) + " elements"
        for node_name in chain.keys():
            print node_name + " ",
        print ""
    
            
    filename = "config.json"
    
    config_names = []
    
    i = -1
    
    for number_of_chain in chains_id:            
        number_of_nodes = len(chains[number_of_chain].keys())
        
    #     for i in range(0, number_of_nodes):
        
        for node_name, node_map in chains[number_of_chain].items():
            if node_name in config_names:
                continue
            config_names.append(node_name)
            i += 1
            configuration["nodes"].insert(i, {})
    #         node_id = raw_input("Node #" + str(i+1) + " id? -->")
    #         configuration["nodes"][i]["id"] = node_id
            configuration["nodes"][i]["id"] = node_name
            
            name_split = node_name.split("_")
            
            #init = raw_input("Any parameter for inizialization of node " + node_name + "? (N/Y)-->")
            init_list = devices_initialization[node_map["functional_type"]]
            if init_list != []:
                for init_item in init_list:
                    init_param = "ip_" + init_item + "_" + name_split[1]
                    configuration["nodes"][i][init_item] = init_param
                    
            node_description = name_split[0] + " denies any traffic from web_client #" + name_split[1] + " to web_server #" + name_split[1]
            configuration["nodes"][i]["description"] = node_description
            while(True):
                #node_configuration_type = raw_input("Node " + node_id +"'s configuration type (list, maps)? (L/M) -->")
                #n = search_node_in_chains(node_id)
                
                node_configuration_type = devices_configuration_methods[node_map["functional_type"]]
                if node_configuration_type == "list":
                    #list
                    configuration["nodes"][i]["configuration"] = []
                    
                    break
                if node_configuration_type == "maps":
                    #maps
                    configuration["nodes"][i]["configuration"] = [] 
                    n_entries = 1
                                  
                    for m in range(0, n_entries):
                        configuration["nodes"][i]["configuration"].insert(m, {})
                        
                        map_elements = 1
                        
                        for n in range(0, map_elements):
                            key = "ip_web_server_" + name_split[1]
                            value = "ip_web_client_" + name_split[1]
                            configuration["nodes"][i]["configuration"][m][key] = value
                    break
                else:
                    print "Invalid config, please edit the config file"
        #pprint(configuration)
        with smart_open(curr_dir + "/" + filename) as f:
            print >>f, json.dumps(configuration)
    return filename

def main(argv):
    #exit if any command line argument is missing
    if len(argv) < 4:
        print 'batch_generator.py -m <multiplier> -o <output_directory>'
        sys.exit(2)
     #initialize json file names   
    chains_file = ''
    configuration_file = ''
    output_dir = ''
    multiplier = ''
    #parse command line arguments and exit if there is an error
    try:
        opts, args = getopt.getopt(argv,"hm:o:",["mutliplier=","help","odir="])
    except getopt.GetoptError as err:
        print str(err)
        print 'batch_generator.py -m <multiplier> -o <output_directory>'
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print 'batch_generator.py -m <multiplier> -o <output_directory>'
            sys.exit()
        elif opt in ("-o", "--output"):
            output_dir = arg
        elif opt in ("-m", "--multiplier"):
            multiplier = arg
            
    generate_chains(output_dir, multiplier, "tcp=80")
    generate_config(output_dir)


if __name__ == "__main__":
    main(sys.argv[1:])
