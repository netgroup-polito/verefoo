/**
 * @file In this file there are all the functions for calling the POST, UPDATE, DELETE. More particular it is more or less
 * a middleware between the POST-UPDATE-DELETE and the rest of the site, in particular for the function that the user
 * can use like for instance: delete graph, save graph and so on...
 */


/**
 * @description This function is able to call a post (postGraphToServer) in order to save a new nffg.
 * Before it checks if the server is online or no. If the server is online,  it gets the element from  translate
 * translate without save it and call post-function. If the server is offline it launchs a alert and it return.
 */
function saveAsNewNffg()
{
    //Check if the server is online or no.
    if(serverOnlineFunction("Error connection, server offline!!!")==false)return;


    //Create a variable for the post-function
    var packet=
    {
        nodes: []
    };

    //Get the actual graph and don't save it
    packet = NFFGfromCytoToVerigraphFlagSave("no");

    //Send the information to the server
    postGraphToServer(packet, "1");


}

/**
 * @description This function is able to call a get-function.
 * It checks if the server is online or no. I the server is not online it launches an alert in order
 * to adive the user and it returns.
 * If the server is online it calls the function (loadNFFGfromServerWithSuccesMessage) for getting the information.
 */
function downloadNFFGsFromServer()
{
    //Check if the server is online or no
    if(serverOnlineFunction("Error connection, server offline!!!")==false)return;
    //Call the get-function
    loadNFFGfromServerWithSuccesMessage();

}


/**
 * @description This function is called when the user wishes to verify the graph. The function is able to verify if the
 * server is online or no. If the server is not online the function returns lauching an alert.
 * If the server is online it get the actual graph from NFFGfromCytoToVerigraphFlagSave function and it passes the
 * value to function that has inside a post and delete.
 */
function verifyGraph()
{
    //Check if the server is online or no.
    if(serverOnlineFunction("Error connection, server offline!!!")==false)return;


    var packet= [];

    //Get the NFFG
    packet = NFFGfromCytoToVerigraphFlagSave("no");

    //Launch the function can verify if the graph is right or not.
    verifyGraphToServer(packet);
}


/**
 * @description This function is able to save in local and  on the server  the current graph. Before it, the function
 * checks if the server is online or not. If the server is offline, it informs the user by an alert and it returns.
 * If the server is online (obviously) it calls a function for doing the post.
 */
function updateGraph()
{
    //Check if the server is online o not
    if(serverOnlineFunction("Error connection, server offline!!!")==false)return;


    var packet= [];

    //Save in local the variable and get the varible for the server (Verigraph NFFG)
    packet =  NFFGfromCytoToVerigraphFlagSave("save1");

    //Update server
    updateGraphToServer(packet, NFFGcyto.id[0].id, 1);
    //change=0;


}


/**
 * @description This function is called when the user wishes to delete a graph. Before it checks if the server
 * is online o not. If the server is not online it launches an alert in order to inform the user. If the server is
 * online it calls function that inside it has a delete. It passes to the function-delete the id of the graph.
 */
function removeGraph()
{
    //Check if the server is online or not
    if(serverOnlineFunction("Error connection, server offline!!!")==false)return;

    //Active warning before to delete the graph
    warningDeleteGraph();

}


/**
 * @description This function is called by the system when the client starts in order to download all the
 * NFFGs from the server. Logically, before it, the function checks if the server is online or not. If the
 * server is not online it launches an alert in order to inform the user.
 */
function initSystem()
{
    //Check if the server is online or not
    if(serverOnlineFunction("Error connection, server offline!!!")==false)return;

    loadGraphCreateGraphIfNoGraphAvailable();

}

/**
 * @Description Firstly create a new empty local graph and the POST it to the server.
 */
function addNewGraph()
{

    if(serverOnlineFunction("Error connection, server offline!!!")==false)return;


    var tempJson =
        {
            nodes: []
        };

    indexServerLast = indexServer;


    postGraphToServer(tempJson, "2");

}


/**
 * @Description The function is used when wishes to verify a policy. It receives the url of the server and
 * the source and destination node of the policy. It checks if the server is online or not.
 * If the server is online it calls a function in order to
 * verify the policy.
 * @param {string} url - Second part of the url (it is composed by source and destination node and type of verification and
 * middlle box if the policy is reachability).
 */

function verifiyPolicy(url)
{
    if(serverOnlineFunction("Error connection, server offline!!!")==false)return;
    getVerifyPolicy(url);

}


/**
 * @description It checks if the server is online or not. If the server is not online,
 * it calls a function (alertError) in order to print a phrase. This phrase (str) has been passed like parameter.
 * @param {string} str - It represents the phrase to put the alert if the server is not online.
 */
function serverOnlineFunction(str)
{

    if(serverOnline==false)
    {
        alertError(str);
        return false;
    }
    return true;
}
