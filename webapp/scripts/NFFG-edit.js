/**
* @file The file contains the function for checking whether the operation that the user wishes to do is right
 * or not. In addition this file contains also some functions (removeNode, addEdge, removeEdge, removeEdge, ...)
 * for manipulating the graph or the variable that represents the graph.
*/


/**
 * @description The function is specific for the arc in particular it checks if the input value is empty or not.
 *  It is able to understand which input variable is empty, in order to send a specific message.
 * @param {string} source - Source node of a edge.
 * @param {string} target - Destination node of a edge.
 * @returns {boolean} - Returns false there is a problem, returns true if there is not a problem.
 */
function checkSourceTarget(source, target) {
    if (source.localeCompare("") == 0 && target.localeCompare("") == 0)
    {
        alertError("Please verify the source and target node");
        return false; // was 0
    }
    if (source.localeCompare("") == 0) {
        alertError("Please verify the source node");
        return false; // was 0
    }
    if (target.localeCompare("") == 0) {
        alertError("Please verify target node");
        return false; // was 0
    }


    return true; // was 1

}

/**
 * @description The function is specific to check the input of the name of the node.
 * If the input is empty, it is able to send a message. It returns true if there have not been problems,
 * in other cases it returns false.
 * @param {string} nameOfNode - Name of node.
 * @returns {boolean} Returns false there is a problem, returns true if there is not a problem.
 */
function checkNameOfNode(nameOfNode) {

   if (nameOfNode == "") {
        alertError("Error: The name node is empty");
        return false; // was 0
    }

    //return true if there have not been problems in other cases it returns false
    return true;
}


/**
 * @description Remove a node by its name
 * @param {int} nodeName - Id of node.
 */
function removeNode(nodeName)
{
    NFFGcyto.nodes = NFFGcyto.nodes.filter(function (jsonObject) {
        return jsonObject.data.id != nodeName;
    });
}

/**
 * @description Add new edge by source and target nodes
 * @param {string} source - Source node of  edge.
 * @param {string} target - Destination node of edge.
 */
function addEdge(source, target)
{

    /*
     Checks are made at an higher level that is in [$ ("# addEdge"). Click (function ()]
     It is possible to move them and put them in here.
      */

    var edgeTemplate = {
        data: {
            source: source,
            target: target
        }
    };


    //Update the variable
    NFFGcyto.edges.push(edgeTemplate);

}

//To remove the edge from the global variable of the draw
/**
 * @description Remove an edge given its source and target nodes.
 * The function navigates on the edges and when it finds the right edge, it deletes that edge from
 * the variable (NFFGcyto)
 * @param {string} source - Source node of  edge.
 * @param {string} target - Destination node of edge.
 */
function removeEdge(source, target) {

    for (var i = 0; i < NFFGcyto.edges.length; i++) {
        var obj = NFFGcyto.edges[i];
        if (obj.data.source == source && obj.data.target == target) {
            delete NFFGcyto.edges[i];
            NFFGcyto.edges.splice(i, 1);
        }
    }

}


/**
 * @description Remove all the edges given their source. In particular it is able to delete all the
 * neighboring edges of a node. The function gets as parameter the node that wishes to delete. After that it looks inside the
 * global variable in order to search all the edges that start or finish with this node. Every time that it finds an arc,
 * it deletes the edge from the structure and it updates the count variable of the cycle (that in this case is "i").
 * More particular every time that it deletes an element, the global variable becomes smaller and the "i" must be decreased
 * in order to get the following elements.
 * @param {string} node - Name of node.
 */
function removeNeighboringEdge(node)
{

    for (var i = 0; i < NFFGcyto.edges.length; i++)
    {
        var obj = NFFGcyto.edges[i];

        if (obj.data.source == node || obj.data.target == node )
        {
            delete NFFGcyto.edges[i];
            NFFGcyto.edges.splice(i, 1);
            i--;
        }
    }

}
