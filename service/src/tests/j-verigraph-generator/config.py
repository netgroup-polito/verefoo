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

devices_to_classes = {  "webclient" : "PolitoWebClient",
                        "webserver" : "PolitoWebServer",
                        "cache" : "PolitoCache",
                        "nat" : "PolitoNat",
                        "firewall" : "AclFirewall",
                        "mailclient" : "PolitoMailClient",
                        "mailserver" : "PolitoMailServer",
                        "antispam" : "PolitoAntispam",
                        "endpoint": "EndHost",
                        "dpi": "PolitoIDS",
                        "endhost": "PolitoEndHost",
                        "vpnaccess":"PolitoVpnAccess",
                        "vpnexit":"PolitoVpnExit",
                        "fieldmodifier":"PolitoFieldModifier"
                     }
devices_to_configuration_methods = {"webclient" : "",
                                    "webserver" : "",
                                    "cache" : "installCache",
                                    "nat" : "setInternalAddress",
                                    "firewall" : "addAcls",
                                    "mailclient" : "",
                                    "mailserver" : "",
                                    "antispam" : "",
                                    "endpoint": "",
                                    "dpi": "installIDS",
                                    "endhost": "installEndHost",
                                    "vpnaccess":"vpnAccessModel",
                                    "vpnexit":"vpnAccessModel",
                                    "fieldmodifier":"installFieldModifier"
                                    }
devices_initialization = {  "webclient" : ["webserver"],
                            "webserver" : [],
                            "cache" : [],
                            "nat" : [],
                            "firewall" : [],
                            "mailclient" : ["mailserver"],
                            "mailserver" : [],
                            "antispam" : [],
                            "endpoint": [],
                            "dpi":[] ,
                            "endhost":[],
                            "vpnaccess":[],
                            "vpnexit":[],
                            "fieldmodifier":[]                        
                          }

convert_configuration_property_to_ip = {    "webclient" : ["value"],
                                            "webserver" : [],
                                            "cache" : ["value"],
                                            "nat" : ["value"],
                                            "firewall" : ["key", "value"],
                                            "mailclient" : ["value"],
                                            "mailserver" : [],
                                            "antispam" : [],
                                            "endpoint": [],
                                            "dpi": [],
                                            "endhost": [],
                                            "vpnaccess": ["value"],
                                            "vpnexit": ["value"],
                                            "fieldmodifier": []
                                        }

devices_configuration_fields = {    "webclient" : "",
                                    "webserver" : "",
                                    "cache" : "cached address",
                                    "nat" : "natted address",
                                    "firewall" : "acl entry",
                                    "mailclient" : "",
                                    "mailserver" : "",
                                    "antispam" : "",
                                    "endpoint": "",
                                    "dpi":"words blacklist",
                                    "endhost":"",
                                    "vpnaccess":"vpn access",
                                    "vpnexit":"vpn exit",
                                    "fieldmodifier":"field modifier"
                                 }
