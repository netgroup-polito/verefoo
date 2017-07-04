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
import sys
import commands
import os
from config import *
from utility import *
import batch_generator
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
def generate_chains(curr_dir):
    filename = "chains.json"
    fn = raw_input("Please enter a file name for the json file describing the nodes chains (default \"chains.json\") -->")
    if fn != "":
        filename = fn
    
    number_of_chains = check_input_is_int("Please enter the number of chains you wish to simulate: -->")
    for i in range(0, int(number_of_chains)):
        chains["chains"].insert(i, {})
        #decomment the following 2 lines to make chain id an arbitrary integer
        #chain_id = check_input_is_int("Chain #" + str(i+1) + " id? -->")
        #chains["chains"][i]["id"] = chain_id
        chains["chains"][i]["id"] = i+1
        flowspace = raw_input("Chain #" + str(i+1) + " flowspace? -->")
        chains["chains"][i]["flowspace"] = flowspace
        chain_nodes = check_input_is_int("How many nodes does the chain #" + str(i+1) + " have? -->")
        chains["chains"][i]["nodes"] = []
        for j in range(0, chain_nodes):
            chains["chains"][i]["nodes"].insert(j, {})
            node_name = raw_input("Node #" + str(j+1) + " name? -->")
            chains["chains"][i]["nodes"][j]["name"] = node_name
            print "Available functional types are:"
            for device in devices_to_classes.keys():
                print device + " ",
            while True:
                node_type = raw_input("Node #" + str(j+1) + " functional_type (see valid options above)? -->")
                if node_type in devices_to_classes.keys():
                    break
            chains["chains"][i]["nodes"][j]["functional_type"] = node_type
            node_address = raw_input("Node #" + str(j+1) + " address? -->")
            chains["chains"][i]["nodes"][j]["address"] = node_address
    #pprint(chains)
    with smart_open(curr_dir + "/" + filename) as f:
        print >>f, json.dumps(chains)
    return filename
    
#generates json file describing the node configurations (default config.json)
def generate_config(curr_dir):
    chains_file = "chains.json"
    while True:
        list_files(curr_dir)
        fn = raw_input("Please enter the file name of the json file containing the chains (default \"chains.json\") -->")
        if fn != "":
            chains_file = fn        
        try:  
            chains = parse_chains(curr_dir + "/" + chains_file)
        except:
            print "Chains file is not valid"
            continue
        break
    print "Chains read from file:"
    pprint(chains)
    chains_id = []
    
    for chain_id, chain in chains.items():
        chains_id.append(chain_id)
        print "Chain #" +  str(chain_id) + " has " + str(len(chain)) + " elements"
        for node_name in chain.keys():
            print node_name + " ",
        print ""
    
    
    while True:
        number_of_chain = check_input_is_int("Please enter the number of the chain you wish to configure: -->")
        if number_of_chain in chains_id:
            break
        else:
            print "Please enter a valid chain id (see options above)"
            
    filename = "config.json"
    fn = raw_input("Please enter a file name for the json file describing the nodes configuration (default \"config.json\") -->")
    if fn != "":
        filename = fn
            
    number_of_nodes = len(chains[number_of_chain].keys())
    
#     for i in range(0, number_of_nodes):
    i = -1
    for node_name, node_map in chains[number_of_chain].items():
        i += 1
        configuration["nodes"].insert(i, {})
#         node_id = raw_input("Node #" + str(i+1) + " id? -->")
#         configuration["nodes"][i]["id"] = node_id
        configuration["nodes"][i]["id"] = node_name
        #init = raw_input("Any parameter for inizialization of node " + node_name + "? (N/Y)-->")
        init_list = devices_initialization[node_map["functional_type"]]
        if init_list != []:
            for init_item in init_list:
                init_param = raw_input("Please enter the IP address of parameter \"" + init_item + "\" for node " + node_name + ": -->")
                configuration["nodes"][i][init_item] = init_param
                
        node_description = raw_input("Node " + node_name +"'s configuration description? -->")
        configuration["nodes"][i]["description"] = node_description
        while(True):
            #node_configuration_type = raw_input("Node " + node_id +"'s configuration type (list, maps)? (L/M) -->")
            #n = search_node_in_chains(node_id)
            
            node_configuration_type = devices_configuration_methods[node_map["functional_type"]]
            if node_configuration_type == "list":
                #list
                configuration["nodes"][i]["configuration"] = []
                config_elements = check_input_is_int("How many configuration elements for node " + node_name + "? (type 0 to skip configuration) -->")
                for e in range(0, config_elements):
                    element = raw_input("\tPlease enter " + devices_configuration_fields[node_map["functional_type"]] + "#" + str(e+1) + " -->")
                    configuration["nodes"][i]["configuration"].append(element)
                break
            elif node_configuration_type == "maps":
                #maps
                configuration["nodes"][i]["configuration"] = [] 
                n_entries = check_input_is_int("How many maps for the configuration of node " + node_name + "? (type 0 to skip configuration) -->")
                              
                for m in range(0, n_entries):
                    configuration["nodes"][i]["configuration"].insert(m, {})
                    
                    map_elements = check_input_is_int("How many elements for map #" + str(m+1) + "? -->")
                    
                    for n in range(0, map_elements):
                        key = raw_input("\tKey for " + devices_configuration_fields[node_map["functional_type"]] + "#" + str(n+1) + ": -->")
                        value = raw_input("\tValue for " + devices_configuration_fields[node_map["functional_type"]] + "#" + str(n+1) + ": -->")
                        configuration["nodes"][i]["configuration"][m][key] = value
                break
            else:
                print "Invalid config, please edit the config file"
    #pprint(configuration)
    with smart_open(curr_dir + "/" + filename) as f:
        print >>f, json.dumps(configuration)
    return filename

#ROUTING IS AUTOMATICALLY DEDUCED

# #generates json file describing the routing tables (default routing.json)
# def generate_routing(curr_dir):    
#     chains_file = "chains.json"
#     while True:
#         list_files(curr_dir)
#         fn = raw_input("Please enter the file name of the json file containing the chains (default \"chains.json\") -->")
#         if fn != "":
#             chains_file = fn        
#         try:  
#             chains = parse_chains(curr_dir + "/" + chains_file)
#         except:
#             print "Chains file is not valid"
#             continue
#         break
#     print "Chains read from file:"
#     pprint(chains)
#     chains_id = []
#     
#     for chain_id, chain in chains.items():
#         chains_id.append(chain_id)
#         print "Chain #" +  str(chain_id) + " has " + str(len(chain)) + " elements"
#         for node_name in chain.keys():
#             print node_name + " ",
#         print ""
#     
#     
#     while True:
#         number_of_chain = check_input_is_int("Please enter the number of the chain you wish to configure: -->")
#         if number_of_chain in chains_id:
#             break
#         else:
#             print "Please enter a valid chain id (see options above)"
#     
#     filename = "routing.json"
#     fn = raw_input("Please enter a file name for the json file describing the nodes routing tables (default \"routing.json\") -->")
#     if fn != "":
#         filename = fn
#     
#         
#      
#     i = -1
#     for node_name, node_map in chains[number_of_chain].items():
#         i += 1 
#         
# #     number_of_nodes = len(chains[number_of_chain]["nodes"])  
# #     for i in range(0, number_of_nodes):
#         routing["routing_table"].insert(i, {})
#         
# #         print "Chain #" + str(number_of_chain) + " available nodes are: ",
# #         for node in chain["nodes"]:
# #             available_nodes.append(node["name"])
# #             print node["name"] + " ",
# #                 
# #         while True:
# #             node_name = raw_input("Node name? (see options above) -->")
# #             if node_name in available_nodes:
# #                 break
# #             else:
# #                 print "Please enter a valid node name (see options above)"
#                 
#         routing["routing_table"][i][node_name] = []
#         node_entries = check_input_is_int("How many entries for node " + str(node_name) + "? -->")
#         
#         for j in range(0, node_entries):
#             routing["routing_table"][i][node_name].insert(j, {})
#             destination = raw_input("Destination #" + str(j+1) + "? -->")
#             next_hop = raw_input("Next hop #" + str(j+1) + "? -->")
#             routing["routing_table"][i][node_name][j][destination] = next_hop
#     #pprint(routing)
#     with smart_open(curr_dir + "/" + filename) as f:
#         print >>f, json.dumps(routing)
#     return filename

def main():
    
    chains_file = ""
    configuration_file = ""
    routing_file = ""
    
    curr_dir = os.getcwd()
    current_path = curr_dir
    
    set_dir = raw_input("Change working directory? (" + curr_dir + ") (N/Y) -->")
    if set_dir == "Y" or set_dir == "y":
        print "List of subdirectories:"
        print list_directories(curr_dir)
        while True:
            curr_dir = os.path.abspath(raw_input("Enter working path (relative or absolute path are supported) -->"))
            if os.path.exists(curr_dir):
                current_path = curr_dir
                break
            else:
                print "Please enter a valid path!"
          
    directory = raw_input("Do you want to create a new test directory? (N/Y) -->")
    if directory == "Y" or directory =="y":
        directory_name = raw_input("Directory name? -->")
        print commands.getoutput("mkdir -v " + curr_dir + "/" + directory_name)
        current_path = curr_dir + "/" + directory_name
    
    print "Files will be created at " + current_path
    
    firewall_chain = False
                
    while True:
        choice = raw_input("""CHAINS?\n
        Choose one of the following options:\n
        1) Automatic generation of chains.json and config.json for an N-firewall chain
        2) Generate step-by-step
        3) Verify the integrity of an existing json file
        4) Skip step\n-->""")
        try:
            if int(choice) == 1:
                multiplier = check_input_is_int("Please enter N -->")
                arguments = ["-m", str(multiplier), "-o", current_path]
                batch_generator.main(arguments)
                firewall_chain = True
                break
            elif int(choice) == 2:
                chains_file = generate_chains(current_path)
                break
            elif int(choice) == 3:
                chains_file = raw_input("Input file for CHAINS? -->")
                if(check_chains_integrity(current_path + "/" + chains_file)) == True:
                    break
                else:
                    print "Input json file for CHAINS not well formed, please try again!"
            elif int(choice) == 4:
                break
            else:
                print "Invalid choice, please try again!"
        except ValueError, e:
            print "Invalid choice, please try again!"
            continue
    
    
    while True:
        
        if firewall_chain == True:
            chains_file = "chains.json"
            configuration_file = "config.json"
            routing_file = ""
            break
            
        choice = raw_input("""CONFIGURATION?\n
        Choose one of the following options:\n
        1) Generate step-by-step
        2) Verify the integrity of an existing json file
        3) Skip step\n-->""")
        try:
            if int(choice) == 1:
                configuration_file = generate_config(current_path)
                break
            elif int(choice) == 2:
                configuration_file = raw_input("Input file for CONFIGURATION? -->")
                if(check_config_integrity(current_path + "/" + configuration_file)) == True:
                    break
                else:
                    print "Input json file for CONFIGURATION not well formed, please try again!"
            elif int(choice) == 3:
                break
            else:
                print "Invalid choice, please try again!"
        except ValueError, e:
            print "Invalid choice, please try again!"
            continue
    

#     while True:
#         choice = raw_input("""ROUTING?\n
#         Choose one of the following options:\n
#         1) Generate step-by-step
#         2) Verify the integrity of an existing json file
#         3) Quit\n-->""")
#         try:
#             if int(choice) == 1:
#                 routing_file = generate_routing(current_path)
#                 break
#             elif int(choice) == 2:
#                 routing_file = raw_input("Input file for ROUTING? -->")
#                 if(check_routing_integrity(current_path + "/" + routing_file)) == True:
#                     break
#                 else:
#                     print "Input json file for ROUTING not well formed, please try again!"
#             elif int(choice) == 3:
#                 return chains_file, configuration_file, routing_file
#                 sys.exit(0)
#             else:
#                 print "Invalid choice, please try again!"
#         except ValueError, e:
#             print "Invalid choice, please try again!"
#             continue

    print "All done, you are ready to launch the test generator like so:"
    print "test_class_generator.py -c " + chains_file + " -f " + configuration_file + " -o <output_file>"
    
    return chains_file, configuration_file, routing_file, current_path


if __name__ == "__main__":
    main()
