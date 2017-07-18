/**
 * Created by Fenix on 13/02/17.
 */

/**
 * @file In this file there are all the useful functions for the search of the elements of NFFG, like
 * for example the node of the NFFG.
 */

/**
 * @description This function is able to search the right graph in the vector of the client
 * @param {int} iDGraph - Id of NFFG
 * @return {object} NFFGscyto[x] - Object of NFFG.
 */

function searchGraph(iDGraph)
{

    for(var x=0; x<NFFGsServer.length; x++)
    {
        if(iDGraph == NFFGsServer[x].id)
        {
            //console.log("here");
            indexServer=x;
            return NFFGsServer[x];
        }

    }


}


/**
 * @description Search duplicate node. Returns true if the node exist otherwise false
 * @param {string} nameNode - Name of node.
 * @returns {boolean} - Returns true if there has found the name of node, otherwise false.
 */
function searchNodeElement(nameNode) {

    for (var i = 0; i < NFFGcyto.nodes.length; i++) {
        var obj = NFFGcyto.nodes[i];
        if (obj.data.id == nameNode) {
            return true; // was 0
        }
    }
    return false; // was 1
}


/**
 * @description Search duplicate arcs. Return true if there is a duplicate otherwise false
 * @param {string} source - Source node of the arc.
 * @param {string} target - Destination node of the arc.
 * @returns {boolean} - Returns false if edge doesn't exist, otherwise true.
 */
function searchEdge(source, target) {

    for (var i = 0; i < NFFGcyto.edges.length; i++) {
        var obj = NFFGcyto.edges[i];
        if (obj.data.source == source && obj.data.target == target) {
            return true; // was 0
        }
    }
    return false; // was 1
}

/**
 * @description This function is able to search a node into the NFFG. If finds the node it return the index,
 * otherwise it returns -1.
 * @param  {string} nameOfnode - Name of the node
 * @return {number} - Return -1 if has not found the node or the index of the node.
 */
function searchIndexElementReturnElment(nameOfnode)
{
    for (var i=0; i<NFFGcyto.nodes.length; i++)
    {
        if(NFFGcyto.nodes[i].data.id.localeCompare(nameOfnode)==0)
        {
            return i;
        }
    }
    return -1;
}