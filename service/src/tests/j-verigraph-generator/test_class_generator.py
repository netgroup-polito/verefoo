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
from code_generator import CodeGeneratorBackend
import os, errno
from config import *
from utility import *
from routing_generator import *


#global variables
debug = True
#end of global variables

    
#generates a custom test file
def generate_test_file(chain, number, configuration, output_file="test_class"):

    route = {}
    config = {}
    chn = {}

    #initiatlize the config dictionary for each node
    for node in chain["nodes"]:
        config[node["name"]] = {}

    #initiatlize the route dictionary for each node
    for node in chain["nodes"]:       
        route[node["name"]] = {}

    #initiatlize the chn dictionary for each node
    for node in chain["nodes"]:       
        chn[node["name"]] = {}

    #set chn values ---> chn(name, (field, value))
    for node in chain["nodes"]:
        for key, value in node.items():
            try:
                #name key is redundant in map
                if key != "name":
                    chn[node["name"]][key] = value
            except KeyError, e:
                print "Field " + str(key) + " not found for node " + str(node["name"])
                print "Cotinuing..."
                continue
    #debug print of the chain        
    if debug == True:
        pprint(chn)
        
#OLD ROUTING FROM FILE    
#     #set route values ---> route(name, [list of addresses])
#     for node in routing["routing_table"]:
#         for key, value in node.items():
#             try: 
#                 route[key] = value
#             except KeyError, e:
#                 print "No routing table found for node " + str(key)
#                 print "Continuing..."
#                 continue
#     #debug print of the routing tables
#     if debug == True:
#         pprint(route)

    routing = generate_routing_from_chain(chain)
    
    for node_name, node_rt in routing["routing_table"].items():
        route[node_name] = node_rt  
    pprint(route)
        
    #pprint(configuration["nodes"])
    
    #save all the IPs in a list
    ips = []
    for node_name in chn.keys():
        ips.append(chn[node_name]["address"])
    
    #set config values ---> config(functional_type, (field, value))
    for node in configuration["nodes"]:
        for key, value in node.items():
            #id field is redundant
            if key != "id":
                try:
                    if key == "configuration":
                        #initialize with empty config
                        config[node["id"]][key] = []
                        #make sure configuration refers to a node belonging to the current chain, otherwise skip configuraion field
                        
                        for value_item in value:
                            if isinstance(value_item, dict):
                                for config_item_key, config_item_value in value_item.items():
                                    if config_item_key in ips and config_item_value in ips:
                                        #valid config, add it
                                        config[node["id"]][key].append(value_item)
                            else:
                                if value_item in ips:
                                    config[node["id"]][key].append(value_item)
                    else:
                        config[node["id"]][key] = value
                except KeyError, e:
                    #node not found in current chain 
                    print "Field " + key + " not found for node " + str(node["id"])
                    print key + " probably doesn't belong to the current chain, thus it will be skipped"
                    #sys.exit(1)
                    continue
    pprint(config)
    #debug print of the configuration                
    if debug == True:
        pprint(config)
        
    #prepare a few more helpful data structures
    nodes_names = []
    nodes_types = []
    nodes_addresses = []
    nodes_ip_mappings = []
    nodes_rt = {}
    
    #initialize vectors for node names and routing tables
    for name in chn.keys():
        nodes_names.append(name)
        nodes_rt[name] = []
    
    #add functional types, addresses and ip mapping to vectors    
    for node, field in chn.items():
        nodes_types.append(field["functional_type"])
        nodes_addresses.append(field["address"])
        #nodes_ip_mappings.append(node + ", ctx." + field["address"])
        nodes_ip_mappings.append(field["address"])        
    
                
#     #add routing table entries to rt vector
#     for node, rt in route.items():
#         for entry in rt:
#             for dest, next_hop in entry.items():
#                 #row = "ctx." + dest + ", " + next_hop
#                 row = dest + ", " + next_hop
#                 try:
#                     nodes_rt[node].append(row)
#                 except KeyError, e:
#                     #node not found, notify and exit
#                     print "Node " + node + " not found!"
#                     sys.exit(1)
    for node, rt in route.items():
        for dest, next_hop in rt.items():
            #row = "ctx." + dest + ", " + next_hop
            row = "nctx.am.get(\"" + dest + "\"), " + next_hop
            try:
                nodes_rt[node].append(row)
            except KeyError, e:
                #node not found, notify and exit
                print "Node " + node + " not found!"
                sys.exit(1)
                     
    #begin file generation    
    print "* instantiating chain #" + str(number)
    dirname = os.path.dirname(output_file)
    basename = os.path.basename(output_file)
    basename = os.path.splitext(basename)[0].capitalize()
    with smart_open(dirname + "/" + basename + "_" + str(number) + ".java") as f:
        c = CodeGeneratorBackend()
        c.begin(tab="    ")
        
        c.writeln("package tests.examples;")

        #imports here
        c.writeln("import java.util.ArrayList;")
        c.writeln("import com.microsoft.z3.Context;")
        c.writeln("import com.microsoft.z3.DatatypeExpr;")

        c.writeln("import mcnet.components.Checker;")
        c.writeln("import mcnet.components.NetContext;")
        c.writeln("import mcnet.components.Network;")
        c.writeln("import mcnet.components.NetworkObject;")
        c.writeln("import mcnet.components.Tuple;")

        #import components
        for i in range(0, len(nodes_names)):
            c.writeln("import mcnet.netobjs." + devices_to_classes[str(nodes_types[i])] + ";")
        
        c.writeln("public class " + basename + "_" + str(number) + "{")
        
        c.indent()
        c.writeln("public Checker check;")
        # declare components
        for i in range(0, len(nodes_names)):
            c.writeln("public " + devices_to_classes[str(nodes_types[i])] + " " + str(nodes_names[i]) + ";")

        c.writeln("public " + basename + "_" + str(number) + "(Context ctx){")
        c.indent()
        
        c.write("NetContext nctx = new NetContext (ctx,new String[]")
        #write a list of nodes like the following:
        #
        #{'a', 'b', 'c'}
        c.write_list(nodes_names, wrapper="\"")
        c.append(", new String[]")
        #write a list of ip addresses like the following:
        #
        #['ip_a', 'ip_b', 'ip_c']
        c.write_list(nodes_addresses, wrapper="\"")
        c.append(");\n")

        c.writeln("Network net = new Network (ctx,new Object[]{nctx});")

        for i in range(0, len(nodes_names)):
            #write a line like <node_name> = components.<node_class>(ctx.<node_name>, net, ctx):
            #
            #a = components.EndHost(ctx.a, net, ctx)
            #server = components.PolitoWebServer(ctx.server, net, ctx)
            c.write(str(nodes_names[i]) + " = new " + devices_to_classes[str(nodes_types[i])] + "(ctx, new Object[]{nctx.nm.get(\"" + nodes_names[i] + "\"), net, nctx")
            if devices_initialization[nodes_types[i]] != [] :
                for param in devices_initialization[nodes_types[i]]:
                    c.append(", nctx.am.get(\"" + config[nodes_names[i]][param] + "\")")
            c.append("});\n")
        
        c.writeln("ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();")
        for i in range(0, len(nodes_names)):
            c.writeln("ArrayList<DatatypeExpr> al" + str(i) + " = new ArrayList<DatatypeExpr>();")
            c.writeln("al" + str(i) + ".add(nctx.am.get(\"" + nodes_ip_mappings[i] + "\"));")
            c.writeln("adm.add(new Tuple<>(" + nodes_names[i] + ", al" + str(i) + "));")

        #SET ADDRESS MAPPINGS
        c.writeln("net.setAddressMappings(adm);")

        #CONFIGURE ROUTING TABLE HERE
        for i in range(0, len(nodes_names)):
            #write a line like:
            #
            #net.RoutingTable(<node_name>, [(ctx.<destination_address1>, <next_hop1>]), (ctx.<destination_address2>, <next_hop2>]))
            c.writeln("ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_" + nodes_names[i] + " = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();")
            for row in nodes_rt[nodes_names[i]]:
                c.writeln("rt_" + nodes_names[i] + ".add(new Tuple<DatatypeExpr,NetworkObject>(" + row + "));")
            c.writeln("net.routingTable(" + nodes_names[i] + ", rt_" + nodes_names[i] + ");")

        
        #ATTACH DEVICES
        c.write("net.attach(")
        #write a line like net.Attach(<node1_name>, <node2_name>):
        #
        #net.Attach(a, server, politoCache, fw)
        c.write_list(nodes_names, delimiter = False, wrapper="")
        c.append(");\n")

        #CONFIGURE MIDDLE-BOXES
        for i in range(0, len(nodes_names)):
            #configure middle-box only if its configuration is not empty
            if config[nodes_names[i]]["configuration"] != [] :
                if nodes_types[i] == "cache":
                    c.write(nodes_names[i] + "." + devices_to_configuration_methods[nodes_types[i]] + "(new NetworkObject[]")
                    cache_ips = config[nodes_names[i]]["configuration"]
                    cache_hosts = []
                    for cache_ip in cache_ips:
                        i = -1
                        for host in nodes_addresses:
                            i += 1
                            if host == cache_ip:
                                cache_hosts.append(nodes_names[i])
                    c.write_list(formatted_list_from_list_of_maps(cache_hosts), wrapper="")
                    c.append(");\n")
                elif nodes_types[i] == "nat":
                    c.writeln("ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();")
                    config_elements = []
                    config_elements = formatted_list_from_list_of_maps(config[nodes_names[i]]["configuration"])
                    for address in config_elements:
                        c.writeln("ia.add(nctx.am.get(\"" + address + "\"));")
                    c.writeln(nodes_names[i] + "." + devices_to_configuration_methods[nodes_types[i]] + "(ia);")
                elif nodes_types[i] == "firewall":
                    c.writeln("ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();")
                    for config_element in config[nodes_names[i]]["configuration"]:
                        for key, value in config_element.items():
                            c.writeln("acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get(\"" + key + "\"),nctx.am.get(\"" + value + "\")));")
                    c.writeln(nodes_names[i] + "." + devices_to_configuration_methods[nodes_types[i]] + "(acl);")
                elif nodes_types[i] == "antispam":
                    c.write(nodes_names[i] + "." + devices_to_configuration_methods[nodes_types[i]] + "(new int[]")
                    c.write_list(formatted_list_from_list_of_maps(config[nodes_names[i]]["configuration"]))
                    c.append(");\n")

        c.writeln("check = new Checker(ctx,nctx,net);")
        c.dedent()
        c.writeln("}")
        c.dedent()
        c.writeln("}")

        #write c object to file    
        print >>f, c.end()

        #rename class to upper case
        #os.rename(dirname + "/" + basename + "_" + str(number) + ".java", dirname + "/" + basename + "_" + str(number) + ".java")

        print "wrote test file " + os.path.abspath(dirname + "/" + basename + "_" + str(number)) + ".java" + " successfully!"


def main(argv):
    #exit if any command line argument is missing
    if len(argv) < 6:
        print 'test_class_generator.py -c <chain_file> -f <conf_file> -o <output_name>'
        sys.exit(2)
     #initialize json file names   
    chains_file = ''
    configuration_file = ''
    output_file = ''
    #parse command line arguments and exit if there is an error
    try:
        opts, args = getopt.getopt(argv,"hc:f:r:o:",["help","chain=","config=","route=","ofile="])
    except getopt.GetoptError as err:
        print str(err)
        print 'test_class_generator.py -c <chain_file> -f <conf_file> -o <output_name>'
        sys.exit(2)
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print 'test_class_generator.py -c <chain_file> -f <conf_file> -o <output_name'
            sys.exit()
        elif opt in ("-c", "--chain"):
            chains_file = arg
        elif opt in ("-f", "--config"):
            configuration_file = arg
        elif opt in ("-o", "--ofile"):
            output_file = arg

    
    #parse chains file
    chains = convert_unicode_to_ascii(parse_json_file(chains_file))
    
    #parse configuration file
    configuration = convert_unicode_to_ascii(parse_json_file(configuration_file))
    
    #OLD ROUTING FROM FILE
    #parse routing file
    #routing = convert_unicode_to_ascii(parse_json_file(routing_file))
    
    
    #debug prints with pprint
    if debug == True:
        pprint(chains)
        pprint(configuration)
    
    #custom formatted prints
    print_chains(chains)
    print_configuration(configuration)
    #print_routing_table(routing)

    #counter for the number of chains
    number_of_chains = 0
    
    #generate test classes
    for chain in chains["chains"]:
        #increment the number of chains
        number_of_chains += 1;
        #generate test files
        generate_test_file(chain, number_of_chains, configuration, output_file)


if __name__ == "__main__":
    main(sys.argv[1:])

    
