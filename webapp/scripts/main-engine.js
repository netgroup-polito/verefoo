/**
 * @file In this file there are all the functions to modify the graph and the functions to initialize
 * the client
 * @author cristiano
 */


/**
 * @description Function is able:
 * -to initialize the client (initSystem)
 * -to start polling the server in order to understand if it is online or offline
 * -to draw the graph using some functions like addNode.
 */
$(document).ready(function() {

    //console.log("Try number 1"); //To view if tomcat has updated the client

    //to initialize the client.
    initSystem();

    //Check graph saved or no
    checkGraphSavedOrNotSaved();


    //for a better debugging, (maybe) disable the polling
    serverPolling();




    //add node
    /**
     * @description The function is able to add a node.
     * More particular this function gets from the DOM two values: the name of node (or its id) and the functional type of the node.
     * The function checks if the two fields are not empty: if only one of them or both are empty, the function returns false.
     * After that, it checks if the id (or name) of the node already exist in the graph. If it already exists, it (the function) returns false
     * otherwise it adds the node (in the global client structure) and it draws the graph.
     */
    $("#addNode").click(function() {
        //Get the two values: functional type and id of the node
        var nameOfNode = $("#nodeID").val();

        // Get value from the selector of the functional types
        var tmpgetVaueFunctionalType = $('#selectNewFunctionalType').val();

        //Check
        var checkVariable = checkNameOfNode(nameOfNode);
        //console.log(tmpgetVaueFunctionalType);

        if (checkVariable == false) {
            console.log("falso");
            return false;
        }

        // Check if the node exists or not
        //searchNodeElement: 1 if the node does not exist, 0 if the node exists
        if (true == searchNodeElement(nameOfNode)) {
            alertError("It is not possible to add a node because the id node  already exist");
            return false;
        }

        if ($('#idcheckboxconfigurenode').is(":checked"))
        {
            // it is checked
            cleanningModal=true;
            choseconfiguration(document.getElementById("selectNewFunctionalType").value);
            createAndPushNode(nameOfNode, tmpgetVaueFunctionalType, false);
        }
        else
        {
            createAndPushNode(nameOfNode, tmpgetVaueFunctionalType, true);

            //The graph has changed, so the flag about the change graph is updated.
            change = 1;
        }



    });




    //remove the node
    /**
     * @description The function is able to remove a node.
     * More particular the function is able to remove a node from the graph.
     * Initially the function gets the name of node and it checks if the name of the node is empty or not.
     * If the field is empty the function returns false. After that, the function checks if the node exists, so the function
     * can remove the node from the global variable
     * and at the end it draws the new graph.
     */
    $("#removeNode").click(function() {
        //Get the functional value
        var nameOfNode = $("#nodeToRemove").val();


        //Check if the field is empty or not
        if (nameOfNode == "") {
            alertError("Error: The name node is empty");
            return false;
        }

        // Check if the node exists or no
        //searchNodeElement -> 1 node does not exist, 0 -> node exists
        if (false == searchNodeElement(nameOfNode)) {
            alertError("It is not possible to remove the node because this id does not exist");
            return false;
        }

        //To delete the neighboring edge of the node
        removeNeighboringEdge(nameOfNode);

        //Remove node
        removeNode(nameOfNode);


        $('#removeNodeD').collapse('hide');

        //Draw the graph
        drawWithParametre(NFFGcyto);

        //The graph has changed so the flag about the change graph is updated.
        change = 1;


    });

    //Add the edge
    /**
     * @description The function is able to add an edge.
     * More particular the function gets the value and it stores into two variables (source and target).
     * After that it searches if it's possible to create the arc between these two nodes.
     * In first moment it searches if the arc already exists or not. In a second moment it checks if
     * the nodes exist and the arc connect will not connect two different nodes.
     * If all these verifies are passed the edge is saved into the global variable and the graph is updated.
     */
    $("#addEdge").click(function() {

        //Get the value in order to create the arc
        var source = $('#newEdgeSource').val();
        var target = $('#newEdgeTarget').val();


        if (0 == checkSourceTarget(source, target))
            return false;


        //Search if the arc exists or not
        var flagExist = searchEdge(source, target);

        //alert("flagExist " +  flagExist);
        if (flagExist == true) {
            alertError("The edge already exist");
            return false;
        }

        var existSource = searchNodeElement(source);
        var existTarget = searchNodeElement(target);


        if (existSource == false && existTarget == false) {
            alertError("It is not possible to add an edge because the source and target node do not exist ");
            return false;

        }

        if (source.localeCompare(target) == 0) {
            alertError("It is not possible to add an edge because the source and target node are the same ");
            return false;

        }

        //searchNodeElement: 1 if the node does not exist, 0 if the  node exists
        if (false == existSource) {
            alertError("It is not possible to add an edge because the source node does not  exist ");
            return false;
        }


        //searchNodeElement:  1 if the node does not exist, 0 if the node exists

        if (false == existTarget) {
            alertError("It is not possible to add a node because the  target node does not  exist ");
            return false;
        }


        //Add edge
        addEdge(source, target);

        $('#newEdgeDrop').collapse('hide');


        //Draw the graph
        drawWithParametre(NFFGcyto);
        change = 1;

    });


    /**
     * @description The function is able to delete an edge. More particular
     * the function gets the value from the DOM and it checks if there is an arc. If there is an arc,
     * the function deletes it from the global variable.
     * At the end, the function updates the graph.
     */
    $("#removeEdge").click(function() {
        console.log("last " + JSON.stringify(NFFGcyto));
        //Get the value
        var source = $('#sourceToRemove').val();
        var target = $('#targetToRemove').val();


        //Check if source and target are right
        if (false == checkSourceTarget(source, target))
            return false;

        //Check node exists

        var existSource = searchNodeElement(source);
        var existTarget = searchNodeElement(target);


        if (existSource == false && existTarget == false) {
            alertError("It is not possible to remove an edge because the source and target node do not exist ");
            return false;

        }

        if (source.localeCompare(target) == 0) {
            alertError("It is not possible to remove an edge because the source and target node are the same ");
            return false;

        }

        //searchNodeElement:  1 if the node does not exist, 0 if the  node exist
        if (false == existSource) {
            alertError("It is not possible to remove an edge because the source node does not  exist ");
            return false;
        }


        //searchNodeElement: 1 if the node does not exist or 0 if the node exist

        if (false == existTarget) {
            alertError("It is not possible to remove an edge because the  target node does not  exist");
            return false;
        }

        //End check node exist


        //Search id the arc exist o no
        var flagExist = searchEdge(source, target);

        if (flagExist == false) {
            alertError("The edge does not exist");
            return false;
        }


        //Remove the arc
        removeEdge(source, target);

        $('#removeEdgeD').collapse('hide');


        //Draw the graph
        drawWithParametre(NFFGcyto);
        change = 1;
    });


});


/**
 * This function draws graph and it uses the Cytoscape library
 * It consumes the global variable `NFFGcyto` inside it there are the nodes and the arc. This variable is managed by
 * another functions.
 * @param {object} NFFGcyto - graph The graph in Cytoscape form.
 * @see http://js.cytoscape.org/
 */
function drawWithParametre(graph) {



    var cy = window.cy = cytoscape({
        container: document.getElementById('cy'),

        boxSelectionEnabled: false,
        autounselectify: true,

        layout: {
            name: 'dagre'
        },

        style: [{
                selector: 'node',
                style: {
                    'width': '75px',
                    'height': '75px',
                    'content': 'data(id)',
                    'text-opacity': '',
                    'text-valign': 'center',
                    'text-halign': 'center',
                    'color': 'white',
                    'background-color': '#0052bf',
                    'border-color': '#d5d05e',
                    'border-width': '2px'
                }
            },

            {
                selector: 'edge',
                style: {
                    'width': 3,
                    'target-arrow-shape': 'triangle',
                    'line-color': '#d5d05e',
                    'target-arrow-color': '#d5d05e',
                    'curve-style': 'bezier'
                }
            }
        ],

        elements: graph

    });
}

/**
 * @description Creates a new node and pushes it in the actual graph.
 * @param  {string} nameOfNode - Name of the node.
 * @param  {string} tmpgetVaueFunctionalType - Functional type of the node.
 * @param  {boolean} flag
 */
function createAndPushNode(nameOfNode, tmpgetVaueFunctionalType, flag) {


    //Add node
    var nodeTemplate = {
        data: {
            id: null,
            funcType: 3,
            configuration: []
        }
    };




    nodeTemplate.data.id = nameOfNode;
    nodeTemplate.data.funcType = tmpgetVaueFunctionalType;
    tmpNodeSave = nodeTemplate;

    if (flag == true) {
        NFFGcyto.nodes.push(nodeTemplate);
        $('#newNodeDrop').collapse('hide');


        //Draw graph
        drawWithParametre(NFFGcyto);
    }


}
