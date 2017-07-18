/**
 * @file Global and shared variables pool. No algorhytms and functions here.
 */

/**
 * @description This flag determines the visibility of the JSON representation of the graph.
 * @type {boolean}
 */
var flagJsonOpenClose=false;


/**
 * @description JSON of the server. All nffg. This variable is a support variable. It is not necessary
 * to update it during the execution of the client.
 * @version Verigraph
 * @type {json}
 */
var NFFGsServer = {};


/**
 * @description: Is the index of the NFFGsServer, that in this moment the user is using.
 * @type {number}
 */
var indexServer = 0;


/**
 * @deprecated
 * @description Used during the creation of the url of the verification
 * @type {number}
 */
var idVerification =0;


/**
 * @description The id on server of Verfigraph it represents the id of NFFG.
 * @type {number}
 */
var idGraphVerigraph = 0;

/**
 * @description: Identify the last graph in relation of NFFGsServer.
 * @type {number}
 */
var indexServerLast = 0;

/**
 * @description: Identify the last graph
 * @type {number}
 */
var useLastId=0;

/**
 * @description: Identify the id of the vector on the client
 * @type {{idDropdown: Array, idVector: Array}}
 */
var vectorIndexServerClient =
    {
        idDropdown:[],
        idVector:[]
    };

/**
 * @description If the graph is changed or not.
 * @type {number}
 */
var change =0;


/**
 * @description If the server is online or offline.
 * @type {boolean}
 */
var serverOnline=true;

/**
 * @type {json}
 * @description JSON for the client in order to draw the graph.
 * @version Cytoscape
 */
var NFFGcyto =
    {
        id:[],
        nodes: [],
        edges: []
    };


/**
 * @description Contains a temporary object NFFG.
 * @type {null}
 */
var  tmpNodeSave = null;

/**
 * @description Contains the last verification result.
 * @type {Array}
 */
var lasVerificationResult = [];

/**
 * @description This variable is used to memorise the json during the modification of the JSON itself by the relative
 * modal.
 * @type {null}
 */
var editor = null;

/**
 * @deprecated
 * @description It used to manage the drop down.
 * @type {number}
 */
var xOption = 0;


/**
 * @description This variable is used when the user wishes to change the configuration with another one.
 * This variable memorise the index of the object that stay the NFFG into NFFGcyto variable.
 * @type {number}
 */
var changedNodeConfigurationInt = -1;


/**
 * @description It used in order to understand if it's possible to update the functype or no during the edit opertion.
 * In other word if the user wishes to change the configuration of node, this variable is sets to one. So when the
 * new configuration is saved, also the new configuration type is saved. After that, it is set again with zero.
 * @type {number}
 */
var updateOrChangeConfigurationFuncType = 0;

/**
 * @description It used to know if it's necessary to cleaning the modal.
 * @type {boolean}
 */
var cleanningModal = true;


/**
 * @description It use like flag to know if it is possible to save the global variable during the cleaning of the modal.
 * @type {boolean}
 */
var flagChangeConfigurationWithNewSetting = false;

/**
 * @description It is the address of the server without any thing.
 * @type {string}
 */
var rootAddressServer =  location.host;


/**
 * @description It is the address of the server. It is initialized when the web site start.
 * @type {string}
 */
//var  addressServer  = "http://" + location.host + "/verigraph/api/graphs/";
var  addressServer  = "http://" + rootAddressServer + "/verigraph/api/graphs/";

/**
 * @description Summary table of the configuration node modal scheme.


 All name of modal           Group      min    max     Name of field                      CHECKS SIDE CLIENT
 --------------------------------------------------------------------------------------------------------------
 antispam                    A           0      inf    Name of existing node                     NO
 --------------------------------------------------------------------------------------------------------------
 cache                       A           0      inf    Name of existing node                     NO
 --------------------------------------------------------------------------------------------------------------
 nat                         A           0      inf    Name of existing node                     NO
 --------------------------------------------------------------------------------------------------------------
 firewall                    B           0      inf    Source existing node /                    NO
 --------------------------------------------------------------------------------------------------------------
 endhost                     C           0      1        [varies] [STRING-ENUM-INT]             YES [*1]
 --------------------------------------------------------------------------------------------------------------
 mailclient                 D1           1      1        Mail server name                         NO
 --------------------------------------------------------------------------------------------------------------
 vpnaccess                  D2           1      1        Vpn Exit                                 NO
 --------------------------------------------------------------------------------------------------------------
 vpnexit                    D3           1      1        Vpn Access                               NO
 --------------------------------------------------------------------------------------------------------------
 webclient                  D4           1      1        Web server name                          NO
 --------------------------------------------------------------------------------------------------------------
 dpi                        E           0       inf     Name of existing node                     NO
 --------------------------------------------------------------------------------------------------------------
 endpoint                   NO-MODAl
 --------------------------------------------------------------------------------------------------------------
 webserver                  NO-MODAl
 --------------------------------------------------------------------------------------------------------------
 fieldmodifier              NO-MODAl
 --------------------------------------------------------------------------------------------------------------
 mailserver                 NO-MODAl
 --------------------------------------------------------------------------------------------------------------

 [*1] Has tested only one field: "sequence". It checks if it is a number or no.

 Legend:

 -- All name of modal: are the names of the modal in a human representation.

 -- Group: is the group of modal in a web-client representation. It means for instance:
 Group A means: The modal A is used for antispam, cache and nat functional type.

 -- min: means how many field must be have this configuration type? For instance mailclient must have one field.

 -- max: means how many field can  have this configuration type? For instance mailclient can have one field.

 -- Name of field: The name of field.

 -- CHECKS SIDE CLIENT: If the client has special controls. NO means no control,  YES means that the client does a control.

 -- NO-MODAl: means that this particular type of node has not a configuration.


 Classification: JSON, MODAL X and NUMBER:

 GROUP A 3 min 0 max inf
 ---------------------------------------------------------------------

     antispam :
         {
             "$schema": "http://json-schema.org/draft-04/schema#",
             "title": "Antispam",
             "description": "Polito Antispam",
             "type": "array",
             "items": {
                 "type": "string"
             },
             "minItems": 0,
             "uniqueItems": true
         },
     cache:
         {
             "$schema": "http://json-schema.org/draft-04/schema#",
             "title": "Cache",
             "description": "Polito Cache",
             "type": "array",
             "items": {
                 "type": "string"
             },
             "minItems": 0,
             "uniqueItems": true
         },
    nat:
        {
            "$schema": "http://json-schema.org/draft-04/schema#",
            "title": "Nat",
            "description": "Polito Nat",
            "type": "array",
            "items": {
            "type": "string"
            },
            "minItems": 0,
            "uniqueItems": true
        },



 GROUP B 1 min 0 max inf
 ---------------------------------------------------------------------


    firewall:
    {
        "$schema": "http://json-schema.org/draft-04/schema#",
        "title": "Firewall",
        "description": "Polito Firewall",
        "type": "array",
        "items": {
            "type": "object"
        },
        "minItems": 0,
        "uniqueItems": true
    },

 GROUP C 1 min 0 max 1
 ---------------------------------------------------------------------
     endhost:
         {
             "$schema": "http://json-schema.org/draft-04/schema#",
             "title": "Endhost",
             "description": "Polito Endhost",
             "type": "array",
             "items": {
                 "type": "object",
                 "properties": {
                     "body": {
                         "description": "HTTP body",
                         "type": "string"
                     },
                     "sequence": {
                         "description": "Sequence number",
                         "type": "integer"
                     },
                     "protocol": {
                         "description": "Protocol",
                         "type": "string",
                         "enum": ["HTTP_REQUEST", "HTTP_RESPONSE", "POP3_REQUEST", "POP3_RESPONSE"]
                     },
                     "email_from": {
                         "description": "E-mail sender",
                         "type": "string"
                     },
                     "url": {
                         "description": "URL",
                         "type": "string"
                     },
                     "options": {
                         "description": "Options",
                         "type": "string"
                     },
                     "destination": {
                         "description": "Destination node",
                         "type": "string"
                     }
                 },
                 "additionalProperties": false
             },
             "maxItems": 1
         },


 Gruppo D  4 (Sempre min=1 max=1)
 ---------------------------------------------------------------------
 mailclient:
 {
     "$schema": "http://json-schema.org/draft-04/schema#",
     "title": "Mail Client",
     "description": "Polito Mail Client",
     "type": "array",
     "items": {
         "type": "object",
         "properties": {
             "mailserver": {
                 "description": "Mail server name",
                 "type": "string"
             }
         },
         "additionalProperties": false,
         "required": [
             "mailserver"
         ]
     },
     "minItems": 1,
     "maxItems": 1,
     "uniqueItems": true
 },


 vpnaccess:
 {
     "$schema": "http://json-schema.org/draft-04/schema#",
     "title": "Vpn Access",
     "description": "Polito Vpn Access",
     "type": "array",
     "items": {
         "type": "object",
         "properties": {
             "vpnexit": {
                 "description": "Vpn Exit",
                 "type": "string"
             }
         },
         "additionalProperties": false,
         "required": [
             "vpnexit"
         ]
     },
     "minItems": 1,
     "maxItems": 1,
     "uniqueItems": true
 },
 vpnexit:
 {
     "$schema": "http://json-schema.org/draft-04/schema#",
     "title": "Vpn Exit",
     "description": "Polito Vpn Exit",
     "type": "array",
     "items": {
         "type": "object",
         "properties": {
             "vpnaccess": {
                 "description": "Vpn Access",
                 "type": "string"
             }
         },
         "additionalProperties": false,
         "required": [
             "vpnaccess"
         ]
     },
     "minItems": 1,
     "maxItems": 1,
     "uniqueItems": true
 },
 webclient:
 {
     "$schema": "http://json-schema.org/draft-04/schema#",
     "title": "Web client",
     "description": "Polito Web Client",
     "type": "array",
     "items": {
         "type": "object",
         "properties": {
             "webserver": {
                 "description": "Web server name",
                 "type": "string"
             }
         },
         "additionalProperties": false,
         "required": [
             "webserver"
         ]
     },
     "minItems": 1,
     "maxItems": 1,
     "uniqueItems": true
 },

 GROUP E 1 min 0 max inf
 ---------------------------------------------------------------------

 dpi:
 {
     "$schema": "http://json-schema.org/draft-04/schema#",
     "title": "Dpi",
     "description": "Polito IDS",
     "type": "array",
     "items": {
         "type": "string"
     },
     "minItems": 0,
     "uniqueItems": true
 },



 No configurazione  4
 ---------------------------------------------------------------------

 endpoint:
         {
             "$schema": "http://json-schema.org/draft-04/schema#",
             "title": "Endpoint",
             "description": "Polito Endpoint",
             "type": "array",
             "minItems": 0,
             "maxItems":0,
             "uniqueItems": true
         },
 webserver:
 {
     "$schema": "http://json-schema.org/draft-04/schema#",
     "title": "Web Server",
     "description": "Polito Web Server",
     "type": "array",
     "items": {
      "type": "object"
     },
     "minItems": 0,
     "maxItems": 0,
     "uniqueItems": true
 },

 fieldmodifier:
 {
      "$schema": "http://json-schema.org/draft-04/schema#",
     "title": "Field Modifier",
     "description": "Polito Field Modifier",
     "type": "array",
     "items": {
          "type": "object"
     },
     "minItems": 0,
     "maxItems":0,
     "uniqueItems": true
 },
 mailserver:
 {
      "$schema": "http://json-schema.org/draft-04/schema#",
     "title": "Mail Server",
     "description": "Polito Mail Server",
     "type": "array",
     "items": {
         "type": "object"
     },
     "minItems": 0,
     "maxItems": 0,
     "uniqueItems": true
 }



 */


/**
 * @description Comments for modal (json)

    var configurationSchema =
    {
        antispam :
            {
                configuration: [] // Vettore di stringhe
            },
        cache:
            {
                configuration: [] // Vettore di stringhe
            },
        dpi:
            {
                configuration: [] // Vettore di stringhe
            },
        endhost:
            {
                "items": {
                    "type": "object",
                    "properties": {
                        "body": {
                            "description": "HTTP body",
                            "type": "string"
                        },
                        "sequence": {
                            "description": "Sequence number",
                            "type": "integer"
                        },
                        "protocol": {
                            "description": "Protocol",
                            "type": "string",
                            "enum": ["HTTP_REQUEST", "HTTP_RESPONSE", "POP3_REQUEST", "POP3_RESPONSE"]
                        },
                        "email_from": {
                            "description": "E-mail sender",
                            "type": "string"
                        },
                        "url": {
                            "description": "URL",
                            "type": "string"
                        },
                        "options": {
                            "description": "Options",
                            "type": "string"
                        },
                        "destination": {
                            "description": "Destination node",
                            "type": "string"
                        }
                    },
                    "additionalProperties": false
                },
                "maxItems": 1
            },
        endpoint:
            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "Endpoint",
                "description": "Polito Endpoint",
                "type": "array",
                "minItems": 0,
                "maxItems":0,
                "uniqueItems": true
            },
        fieldmodifier:
            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "Field Modifier",
                "description": "Polito Field Modifier",
                "type": "array",
                "items": {
                    "type": "object"
                },
                "minItems": 0,
                "maxItems":0,
                "uniqueItems": true
            },
        firewall:
            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "Firewall",
                "description": "Polito Firewall",
                "type": "array",
                "items": {
                    "type": "object"
                },
                "minItems": 0,
                "uniqueItems": true
            },
        mailclient:
            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "Mail Client",
                "description": "Polito Mail Client",
                "type": "array",
                "items": {
                    "type": "object",
                    "properties": {
                        "mailserver": {
                            "description": "Mail server name",
                            "type": "string"
                        }
                    },
                    "additionalProperties": false,
                    "required": [
                        "mailserver"
                    ]
                },
                "minItems": 1,
                "maxItems": 1,
                "uniqueItems": true
            },
        mailserver:
            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "Mail Server",
                "description": "Polito Mail Server",
                "type": "array",
                "items": {
                    "type": "object"
                },
                "minItems": 0,
                "maxItems": 0,
                "uniqueItems": true
            },
        nat:
            {
                configuration : [] // Vettore di stringhe
            },
        vpnaccess:
            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "Vpn Access",
                "description": "Polito Vpn Access",
                "type": "array",
                "items": {
                    "type": "object",
                    "properties": {
                        "vpnexit": {
                            "description": "Vpn Exit",
                            "type": "string"
                        }
                    },
                    "additionalProperties": false,
                    "required": [
                        "vpnexit"
                    ]
                },
                "minItems": 1,
                "maxItems": 1,
                "uniqueItems": true
            },
        vpnexit:
            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "Vpn Exit",
                "description": "Polito Vpn Exit",
                "type": "array",
                "items": {
                    "type": "object",
                    "properties": {
                        "vpnaccess": {
                            "description": "Vpn Access",
                            "type": "string"
                        }
                    },
                    "additionalProperties": false,
                    "required": [
                        "vpnaccess"
                    ]
                },
                "minItems": 1,
                "maxItems": 1,
                "uniqueItems": true
            },
        webclient:
            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "Web client",
                "description": "Polito Web Client",
                "type": "array",
                "items": {
                    "type": "object",
                    "properties": {
                        "webserver": {
                            "description": "Web server name",
                            "type": "string"
                        }
                    },
                    "additionalProperties": false,
                    "required": [
                        "webserver"
                    ]
                },
                "minItems": 1,
                "maxItems": 1,
                "uniqueItems": true
            },
        webserver:
            {
                "$schema": "http://json-schema.org/draft-04/schema#",
                "title": "Web Server",
                "description": "Polito Web Server",
                "type": "array",
                "items": {
                    "type": "object"
                },
                "minItems": 0,
                "maxItems": 0,
                "uniqueItems": true
            }
    };
 */




