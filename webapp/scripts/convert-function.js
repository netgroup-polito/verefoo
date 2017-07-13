/**
 *@file In this file there are the two functions used during the creation of the NFFG
 * and three functions used during the editing of NFFG and JSON.
 *
 * The functions used to create the NFFG are:
 * NFFGfromCytoToVerigraphFlagSave and  singleNFFGfromVerigraphToCytoAndCheck.
 *
 * They translate the structure from Verigraph NFFG to Cytoscape NFFG and vice versa.
 * Each structure only translates an element and not the entire vector of Verigraph NFFG or Cytoscape NFFG.
 *
 * The functions used during the editing of the NFFG are:
 *
 * -overWriteCell: used to copy an object variable in another variable.
 *
 * -fromVerigraphToCytoscapeAndSaveIntoVerigraphNFFG: used like the root in order to translate
 * NFFG of Verigraph representation (nffgVerigraph) into NFFG Cytoscape and to save it. In fact it uses from
 * another functions like this NFFGfromCytoToVerigraphFlagSave. It used during the editing of the json in order to
 * save it.
 *
 * -fromSingleNffgVerigraphToNffCyte: used to translate NFFG object from Verigraph representations
 * to NFFG Cytoscape representation and it save into NFFGcyto variable.
 */



/**
 * @description It converts Cytoscape  NFFG element into Verigraph NFFG and it return this element.
 * In other world: Cytoscape -> Verigraph return Verigraph.
 * The function, also, receives a variable in order to know if has to update the Verigraph NFFG vector.
 * @param {string} flagSave - [save] in order to save the NFFG into Verigraph NFFG vector otherwise it doesn't save it.
 * @return {object} tempJson - It is the object that contains  the NFFG translated.
 */
function NFFGfromCytoToVerigraphFlagSave(flagSave)
{
    //Initialize variable.
    var tempJson =
        {
            id: 1,
            nodes: []
        };


    //Get the NFFGcyto element and to cycle it in order to get all elements and to put into tempJson variable.
    for (var x = 0; x < NFFGcyto.nodes.length; x++)
    {

        //Create a template and after it will be filled and it will put inside to tempJson.
        var nodeTemplate = {
            name: 3,
            functional_type: 3,
            neighbours: [
            ],
            configuration :[
            ]

        };

        //Inside it, the functin will store all the neighbours of a node.
        var neighboursVet = [];

        //Save nema and fucntional type.
        nodeTemplate.name = NFFGcyto.nodes[x].data.id;
        nodeTemplate.functional_type = NFFGcyto.nodes[x].data.funcType;


        //Get the configuration if the element has the configuration.
        if (NFFGcyto.nodes[x].data.hasOwnProperty("configuration"))
        {
            nodeTemplate.configuration = NFFGcyto.nodes[x].data.configuration;
        }


        //Get all the neighbours of the node.
        for (var i = 0; i < NFFGcyto.edges.length; i++)
        {

            //Get the source node.
            var source = nodeTemplate.name;
            //It search the destination node from the source node.
            if (source.localeCompare(NFFGcyto.edges[i].data.source)==0)
            {
                //Template for neighbours
                var neighbours =
                    {
                        name: "NAT"
                    };
                //Get the destination node and save into neighboursVet.
                neighbours.name = NFFGcyto.edges[i].data.target;
                neighboursVet.push(neighbours);
            }//End search destination node

        }

        //Save into tempJson.
        nodeTemplate.neighbours = neighboursVet;
        tempJson.nodes.push(nodeTemplate);
    }//End translate.


    if(flagSave.localeCompare("save")==0)
        {
            //Save into NFFGsServer
            if(useLastId==1)
            {

                tempJson.id = NFFGsServer[indexServerLast].id;

            }

            else
            {
                tempJson.id=NFFGsServer[indexServer].id; //Get the id of the sever.
            }


            overWriteCell(tempJson);
            return tempJson;
        }
    else
        return(tempJson); //Return when it has not the necessary to save
}


/**
 * @description It converts  Verigraph NFFG element into Cytoscape NFFG.
 * In other world:  Verigraph  -> Cytoscape.
 * The function, also, receives a variable in order to know which element of NFFGsServer has to translate.
 * It (the function) store the translation into NFFGcyto variable. This variable is used to draw the graph for example.
 * @param {number} newName - It represents the id of the graph.
 */
function singleNFFGfromVerigraphToCytoAndCheck(newName)
{
    //initialized variable (NFFGcyto).
    initVarCyto();
    var foundIdVector;



    for(
        var iteratorVectorIndexServerClient=0;
        iteratorVectorIndexServerClient<vectorIndexServerClient.idDropdown.length;
        iteratorVectorIndexServerClient++
        )
        if(vectorIndexServerClient.idDropdown[iteratorVectorIndexServerClient]==newName)
        {
            foundIdVector= iteratorVectorIndexServerClient;

        }



    //Search the element to translate
    var tempServer = NFFGsServer[foundIdVector];
        //searchGraph(foundIdVector);


    //Create a template in order to store inside all the information.
    var nodeTemplate =
        {
            data:
                {
                    id: 3,
                    funcType: 3,
                    configuration:
                        [
                            {
                            }
                        ]
                }
        };

    //Create a template in order to store inside  the id.
    var idTemplate =
            {
                id: 3
            };
        //Get the id and stores it in idTemplate variable.
        idTemplate.id=tempServer.id;
        //Memorise inside NFFGcyto the id.
        NFFGcyto.id.push(idTemplate);

        //Start to cycling on the nodes.
        for (var i = 0; i < tempServer.nodes.length; i++)
        {
            //Create a template in order to store inside all the information about the node.
            var nodeTemplate = {
                data: {
                    id: 3,
                    funcType: 3,
                    configuration:
                        [
                            {
                            }
                        ]
                }
            };
            //Get the name and functional type of the node and store into nodeTemplate
            nodeTemplate.data.id = tempServer.nodes[i].name;
            nodeTemplate.data.funcType = tempServer.nodes[i].functional_type;

            //Check if there is a configuration.
            if (tempServer.nodes[i].hasOwnProperty("configuration"))
            {
                //Get also the configuration
                nodeTemplate.data.configuration = tempServer.nodes[i].configuration;

            }

            //Memorise the node into NFFGcyto.nodes
            NFFGcyto.nodes.push(nodeTemplate);

            // Add the neighbours as edges for this node
            for (var j = 0; j < tempServer.nodes[i].neighbours.length; j++)
            {
                //Create a template in order to store inside all the information about the the edge.
                var edgeTemplate = {
                    data: {
                        source: null,
                        target: null,
                        id: null
                    }
                };
                //Store the information about the edge.
                edgeTemplate.data.source = tempServer.nodes[i].name;
                edgeTemplate.data.target = tempServer.nodes[i].neighbours[j].name;
                NFFGcyto.edges.push(edgeTemplate);
            }
        }
    //Draw the new graph
    drawWithParametre(NFFGcyto);

}

/**
 * @description The function is used to save correctly the graph in the vector that stays in the browser.
 * It receives the new graph as parameter.
 * @param {object} tempJson
 */
function overWriteCell(tempJson)
{
    var serverVector = [];

    var i =0;
    for(i=0; i<NFFGsServer.length; i++)
    {

        if(useLastId==1)
        {
            if(i == indexServerLast && useLastId==1)
            {
                serverVector.push(tempJson);
                //useLastId=0;
            }
            else
                serverVector.push(NFFGsServer[i]);

        }
        else
            {
                if(i == indexServer )
                {
                    serverVector.push(tempJson);
                }
                else
                    {
                        serverVector.push(NFFGsServer[i]);
                    }
                }

            }

    useLastId=0;

    NFFGsServer= [];


    for(i=0; i<serverVector.length; i++)
    {

        NFFGsServer.push(serverVector[i]);

    }


}

/**
 * @description It translates an NFFG of Verigraph representation (nffgVerigraph) into NFFG Cytoscape representation.
 * After it, it saves it. And it draws the new graph.
 * @param {object} nffgVerigraph - NFFG object of Verigraph representation.
 */
function fromVerigraphToCytoscapeAndSaveIntoVerigraphNFFG (nffgVerigraph)
{
    //Translate from Verigraph to Cytosacepe and put into  NFFGcyto
    fromSingleNffgVerigraphToNffCyte(nffgVerigraph);
    drawWithParametre(NFFGcyto);
    NFFGfromCytoToVerigraphFlagSave("save");
}


/**
 * @description To translate NFFG object from Verigraph representation to NFFG Cytoscape representation and it saves
 * into NFFGcyto variable.
 * @param {object} nffgVerigraph - NFFG object of Verigraph representation.
 */
function fromSingleNffgVerigraphToNffCyte(nffgVerigraph)
{

    initVarCyto();
    
    //Create a template in order to store inside  the id.
    var idTemplate =
        {
            id: 3
        };
    //Get the id and stores it in idTemplate variable.
    idTemplate.id=nffgVerigraph.id;
    //Memorise inside NFFGcyto the id.
    NFFGcyto.id.push(idTemplate);

    //Start to cycling on the nodes.
    for (var i = 0; i < nffgVerigraph.nodes.length; i++)
    {
        //Create a template in order to store inside all the information about the node.
        var nodeTemplate = {
            data: {
                id: 3,
                funcType: 3,
                configuration:
                    [
                        {
                        }
                    ]
            }
        };
        //Get the name and functional type of the node and store into nodeTemplate
        nodeTemplate.data.id = nffgVerigraph.nodes[i].name;
        nodeTemplate.data.funcType = nffgVerigraph.nodes[i].functional_type;

        //Check if there is a configuration.
        if (nffgVerigraph.nodes[i].hasOwnProperty("configuration"))
        {
            //Get also the configuration
            nodeTemplate.data.configuration = nffgVerigraph.nodes[i].configuration;

        }

        //Memorise the node into NFFGcyto.nodes
        NFFGcyto.nodes.push(nodeTemplate);

        // Add the neighbours as edges for this node
        for (var j = 0; j < nffgVerigraph.nodes[i].neighbours.length; j++)
        {
            //Create a template in order to store inside all the information about the the edge.
            var edgeTemplate = {
                data: {
                    source: null,
                    target: null,
                    id: null
                }
            };
            //Store the information about the edge.
            edgeTemplate.data.source = nffgVerigraph.nodes[i].name;
            edgeTemplate.data.target = nffgVerigraph.nodes[i].neighbours[j].name;
            NFFGcyto.edges.push(edgeTemplate);
        }
    }
}