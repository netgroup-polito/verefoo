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
from pprint import pformat
import sys, getopt
from code_generator import CodeGeneratorBackend
import os, errno
from config import *
from utility import *
from routing_generator import *
import logging
from pip._vendor.pkg_resources import null_ns_handler

    
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

    #set chn values: chn[name][key] = value
    for node in chain["nodes"]:
        for key, value in node.items():
            try:
                #name key is redundant in map
                if key != "name":
                    chn[node["name"]][key] = value
            except KeyError, e:
                logging.debug("Field " + str(key) + " not found for node " + str(node["name"]))
                logging.debug("Cotinuing...")
                continue
            
    #debug print of chn        
    logging.debug(pformat((chn)))

    routing = generate_routing_from_chain(chain)
    
    for node_name, node_rt in routing["routing_table"].items():
        route[node_name] = node_rt
        
    #debug print of route  
    logging.debug(pformat((route)))
    
    #set config: config[node_name][key] = value
    for node in configuration["nodes"]:
        for key, value in node.items():
            #id field is redundant
            if key != "id":
                try:
                    if key == "configuration":
                        #init config[node_name][key] with an empty array
                        config[node["id"]][key] = []
                        
                        for value_item in value:
                            change_key = "key" in convert_configuration_property_to_ip[chn[node["id"]]["functional_type"]]
                            change_value = "value" in convert_configuration_property_to_ip[chn[node["id"]]["functional_type"]]
                            if (change_key==False and change_value==False):
                                config[node["id"]][key].append(value_item)
                                continue
                            # config[node_name][configuration] is a dictionary
                            if isinstance(value_item, dict):
                                for config_item_key, config_item_value in value_item.items():
                                    new_key = config_item_key
                                    changed_key = False
                                    changed_value = False
                                    if change_key and config_item_key in chn.keys():
                                        changed_key = True
                                        new_key = "ip_" + str(config_item_key)
                                        value_item[new_key] = str(config_item_value)
                                        del value_item[config_item_key]
                                    if change_value and config_item_value in chn.keys():
                                        changed_value = True
                                        new_value = "ip_" + str(config_item_value)
                                        value_item[new_key] = new_value
                                    if(change_key==changed_key) and (change_value==changed_value):
                                        config[node["id"]][key].append(value_item)
                            else:
                                if change_value:
                                    if value_item in chn.keys():
                                        new_value = "ip_" + str(value_item)
                                        config[node["id"]][key].append(new_value)
                                else:
                                    config[node["id"]][key].append(str(value_item))
                    else:
                        config[node["id"]][key] = value
                except KeyError, e:
                    #node not found in current chain 
                    logging.debug("Field '" + key + "' not found for node '" + str(node["id"]) + "'")
                    logging.debug(key + " probably doesn't belong to the current chain, thus it will be skipped")
                    #sys.exit(1)
                    continue
                
    # debug print of config            
    logging.debug(pformat((config)))
        
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
        nodes_ip_mappings.append(field["address"])        

    for node, rt in route.items():
        for dest, next_hop in rt.items():
            row = "nctx.am.get(\"" + dest + "\"), " + next_hop
            try:
                nodes_rt[node].append(row)
            except KeyError, e:
                #node not found, notify and exit
                logging.debug("Node " + node + " not found!")
                sys.exit(1)
                     
    #begin file generation    
    logging.debug("* instantiating chain #" + str(number))
    dirname = os.path.dirname(output_file)
    basename = os.path.basename(output_file)
    basename = os.path.splitext(basename)[0]
    basename = basename[0].upper() + basename[1:]
    with smart_open(dirname + "/" + basename + "_" + str(number) + ".java") as f:
        c = CodeGeneratorBackend()
        c.begin(tab="    ")
        
        c.writeln("package tests.scenarios;")

        #imports here
        c.writeln("import java.util.ArrayList;")
        c.writeln("import com.microsoft.z3.Context;")
        c.writeln("import com.microsoft.z3.DatatypeExpr;")

        c.writeln("import mcnet.components.Checker;")
        c.writeln("import mcnet.components.NetContext;")
        c.writeln("import mcnet.components.Network;")
        c.writeln("import mcnet.components.NetworkObject;")
        c.writeln("import mcnet.components.Tuple;")
        c.writeln("import mcnet.netobjs.PacketModel;")
        
        #import components
        #for i in range(0, len(nodes_names)):
        #    c.writeln("import mcnet.netobjs." + devices_to_classes[str(nodes_types[i])] + ";")
        
        for key, value in devices_to_classes.items():
            c.writeln("import mcnet.netobjs." + value + ";")
        
        c.writeln("public class " + basename + "_" + str(number) + "{")
        
        c.indent()
        c.writeln("public Checker check;")
        # declare components
        for i in range(0, len(nodes_names)):
            c.writeln("public " + devices_to_classes[str(nodes_types[i])] + " " + str(nodes_names[i]) + ";")
            
        # method setDevices
        c.writeln("private void setDevices(Context ctx, NetContext nctx, Network net){")
        c.indent()
        for i in range(0, len(nodes_names)):
            c.write(str(nodes_names[i]) + " = new " + devices_to_classes[str(nodes_types[i])] + "(ctx, new Object[]{nctx.nm.get(\"" + nodes_names[i] + "\"), net, nctx")
            if devices_initialization[nodes_types[i]] != [] :
                for param in devices_initialization[nodes_types[i]]:
                    print "configuring node " + nodes_names[i]
                    for config_param in config[nodes_names[i]]["configuration"]:
                        if param in config_param:
                            c.append(", nctx.am.get(\"" + config_param[param] + "\")")
            c.append("});\n")
        c.dedent()
        c.writeln("}")
        # end method setDevices
        
        # method doMappings
        c.writeln("private void doMappings(NetContext nctx, ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm){")
        c.indent()
        for i in range(0, len(nodes_names)):
            c.writeln("ArrayList<DatatypeExpr> al" + str(i) + " = new ArrayList<DatatypeExpr>();")
            c.writeln("al" + str(i) + ".add(nctx.am.get(\"" + nodes_ip_mappings[i] + "\"));")
            c.writeln("adm.add(new Tuple<>((NetworkObject)" + nodes_names[i] + ", al" + str(i) + "));")
        c.dedent()
        c.writeln("}")
        # end method doMappings
        
        # for each node methods setRouting and configureDevice
        for i in range(0, len(nodes_names)):
            # method setRouting
            c.writeln("private void setRouting" + nodes_names[i] + "(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_" + nodes_names[i] + "){")
            c.indent()
            for row in nodes_rt[nodes_names[i]]:
                c.writeln("rt_" + nodes_names[i] + ".add(new Tuple<DatatypeExpr,NetworkObject>(" + row + "));")
            c.writeln("net.routingTable(" + nodes_names[i] + ", rt_" + nodes_names[i] + ");")
            c.dedent()
            c.writeln("}")
            # end method setRouting
            # method configureDevice
            c.writeln("private void configureDevice" + nodes_names[i] + "(NetContext nctx) {")
            c.indent()
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
                    c.writeln("ArrayList<DatatypeExpr> ia" + str(i) +" = new ArrayList<DatatypeExpr>();")
                    config_elements = []
                    config_elements = formatted_list_from_list_of_maps(config[nodes_names[i]]["configuration"])
                    for address in config_elements:
                        c.writeln("ia" + str(i) + ".add(nctx.am.get(\"" + address + "\"));")
                    c.writeln(nodes_names[i] + ".natModel(nctx.am.get(\"ip_" + nodes_names[i] + "\"));")
                    c.writeln(nodes_names[i] + "." + devices_to_configuration_methods[nodes_types[i]] + "(ia" + str(i) +");")
                elif nodes_types[i] == "firewall":
                    c.writeln("ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl" + str(i) + " = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();")
                    for config_element in config[nodes_names[i]]["configuration"]:
                        if isinstance(config_element,dict):
                            for key, value in config_element.items():
                                if key in nodes_addresses and value in nodes_addresses:
                                    c.writeln("acl" + str(i) + ".add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get(\"" + key + "\"),nctx.am.get(\"" + value + "\")));")
                    c.writeln(nodes_names[i] + "." + devices_to_configuration_methods[nodes_types[i]] + "(acl" + str(i) + ");")
                elif nodes_types[i] == "antispam":
                    c.write(nodes_names[i] + "." + devices_to_configuration_methods[nodes_types[i]] + "(new int[]")
                    c.write_list(formatted_list_from_list_of_maps(config[nodes_names[i]]["configuration"]))
                    c.append(");\n")
                elif nodes_types[i] == "dpi":
                    for index in range(0, len(config[nodes_names[i]]["configuration"])):
                        config[nodes_names[i]]["configuration"][index] = "String.valueOf(\"" + str(config[nodes_names[i]]["configuration"][index]) + "\").hashCode()"
                    c.write(nodes_names[i] + "." + devices_to_configuration_methods[nodes_types[i]] + "(new int[]")
                    c.write_list(formatted_list_from_list_of_maps(config[nodes_names[i]]["configuration"]), wrapper="")
                    c.append(");\n")
                elif nodes_types[i] == "endhost":
                    c.writeln("PacketModel pModel" + str(i) + " = new PacketModel();")
                    if "body" in config[nodes_names[i]]["configuration"][0]:
                        c.writeln("pModel" + str(i) + ".setBody(String.valueOf(\"" + config[nodes_names[i]]["configuration"][0]["body"] + "\").hashCode());")
                    if "sequence" in config[nodes_names[i]]["configuration"][0]:
                        c.writeln("pModel" + str(i) + ".setSeq(" + config[nodes_names[i]]["configuration"][0]["sequence"] + ");")
                    if "protocol" in config[nodes_names[i]]["configuration"][0]:
                        c.writeln("pModel" + str(i) + ".setProto(nctx." + config[nodes_names[i]]["configuration"][0]["protocol"] + ");")
                    if "email_from" in config[nodes_names[i]]["configuration"][0]:
                        c.writeln("pModel" + str(i) + ".setEmailFrom(String.valueOf(\"" + config[nodes_names[i]]["configuration"][0]["email_from"] + "\").hashCode());")
                    if "url" in config[nodes_names[i]]["configuration"][0]:
                        c.writeln("pModel" + str(i) + ".setUrl(String.valueOf(\"" + config[nodes_names[i]]["configuration"][0]["url"] + "\").hashCode());")
                    if "options" in config[nodes_names[i]]["configuration"][0]:
                        c.writeln("pModel" + str(i) + ".setOptions(String.valueOf(\"" + config[nodes_names[i]]["configuration"][0]["options"] + "\").hashCode());")
                    if "destination" in config[nodes_names[i]]["configuration"][0]:
                        c.writeln("pModel" + str(i) + ".setIp_dest(nctx.am.get(\"" + config[nodes_names[i]]["configuration"][0]["destination"] + "\"));")
                        
                    c.writeln(nodes_names[i] + "." + devices_to_configuration_methods[nodes_types[i]] + "(pModel" + str(i) + ");")
                elif nodes_types[i] == "vpnaccess":
                    c.writeln(nodes_names[i] + "." + devices_to_configuration_methods[nodes_types[i]] + "(nctx.am.get(\"" + nodes_addresses[i] + "\"), nctx.am.get(\"" + config[nodes_names[i]]["configuration"][0]["vpnexit"] + "\"));")
                elif nodes_types[i] == "vpnexit":
                    c.writeln(nodes_names[i] + "." + devices_to_configuration_methods[nodes_types[i]] + "(nctx.am.get(\"" + config[nodes_names[i]]["configuration"][0]["vpnaccess"] + "\"), nctx.am.get(\"" + nodes_addresses[i] + "\"));")
            
            # config is empty but configure device anyway        
            elif nodes_types[i] == "fieldmodifier":
                c.writeln(nodes_names[i] + "." + devices_to_configuration_methods[nodes_types[i]] + "();")
            c.dedent()
            c.writeln("}")
            # end method configureDevice
            
        

        c.writeln("public " + basename + "_" + str(number) + "(Context ctx){")
        c.indent()
        
        c.write("NetContext nctx = new NetContext (ctx,new String[]")
        c.write_list(nodes_names, wrapper="\"")
        c.append(", new String[]")
        c.write_list(nodes_addresses, wrapper="\"")
        c.append(");\n")

        c.writeln("Network net = new Network (ctx,new Object[]{nctx});")
        
        # call method setDevices
        c.writeln("setDevices(ctx, nctx, net);")

        #SET ADDRESS MAPPINGS
        c.writeln("ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();")
        # call doMappings
        c.writeln("doMappings(nctx, adm);")
        c.writeln("net.setAddressMappings(adm);")

        #CONFIGURE ROUTING TABLE
        for i in range(0, len(nodes_names)):
            c.writeln("ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_" + nodes_names[i] + " = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); ")
            c.writeln("setRouting" + nodes_names[i] + "(nctx, net, rt_" + nodes_names[i] + ");")
                  
        #ATTACH DEVICES
        c.write("net.attach(")
        c.write_list(nodes_names, delimiter = False, wrapper="")
        c.append(");\n")

        #CONFIGURE MIDDLE-BOXES
        for i in range(0, len(nodes_names)):
            c.writeln("configureDevice" + nodes_names[i] + "(nctx);")

        c.writeln("check = new Checker(ctx,nctx,net);")
        
        c.dedent()
        c.writeln("}")
        
        c.dedent()
        c.writeln("}")

        #write c object to file    
        print >>f, c.end()

        logging.debug("wrote test file " + os.path.abspath(dirname + "/" + basename + "_" + str(number)) + ".java" + " successfully!")


def main(argv):
    #exit if any command line argument is missing
    if len(argv) < 6:
        print 'test_class_generator.py -c <chain_file> -f <conf_file> -o <output_name>'
        sys.exit(1)
    
    #initialize json file names   
    chains_file = ''
    configuration_file = ''
    output_file = ''
    
    #parse command line arguments and exit in case of any error
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

    #set logging
    logging.basicConfig(stream=sys.stderr, level=logging.DEBUG)

    #parse chains file
    chains = convert_unicode_to_ascii(parse_json_file(chains_file))
    
    #parse configuration file
    configuration = convert_unicode_to_ascii(parse_json_file(configuration_file))
    
    logging.debug(pformat((chains)))
    logging.debug(pformat((configuration)))
    
    #custom formatted prints
    print_chains(chains)
    print_configuration(configuration)

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

    
