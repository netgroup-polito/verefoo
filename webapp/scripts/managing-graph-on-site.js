/**
 * Created by Fenix on 13/02/17.
 */

/**
 * @file Contains all the functions to use to manipulation the right visual of the graph during the addition
 * of new graph or the remove and so on (like the change of the graph).
 */

/**
 * @description This function does two things:
 *  - changes the name of the actual NFFG by argument
 *  - changes the name of the actual NFFG at the top navbar
 * @param {int} newName - id of graph
 */
function changeActualNFFGname(newName)
{

    actualNFFGname = newName;
    if (newName == "empty") {
        $('#actualNFFG').text(actualNFFGname).css('color', '#ff605a');
    } else {
        $('#actualNFFG').text(actualNFFGname).css('color', '#269aff');
    }


}


/**
 * @description: To add a new name on the selector. The function gets the name of the graph and it add a new item
 * on the selecotr. There are two new id that has been created. In such a way as to differentiate them, an id has been
 * added a "s".
 * @param {int} NFFGname - id of the graph
 */
function addNffgSelect(NFFGname)
{
    xOption++;

    var ul = document.getElementById("optList");

    var li = document.createElement("li");

    li.id = NFFGname + "s";

    var a = document.createElement("a");
    a.setAttribute("id", NFFGname);
    a.text = NFFGname;
    a.onclick = function(){aggiornaGrafoAttuale(this.id)};

    li.appendChild(a);
    ul.appendChild(li);


}

/**
 * @description: To update the current page with the actual graph. More in particular, the function receives the name
 * of the graph. It checks if the server if online or it is not online. If the server is offline it prints an alert
 * in order to inform the user.
 * If the server is online, it changes name of the graph and it draws the graph.
 * Moreover it checks if there is at least a graph. If there is not graph, it prints an error message.
 * @param {int} newName - id of graph.
 */

function aggiornaGrafoAttuale(newName)
{

    //Check server online/offline
    if(serverOnline==false)
    {
        alertError("Error server not online!!!");
        return;
    }


    //Deprecated
    //idVerification=newName;

    idGraphVerigraph = newName;

    indexServerLast = indexServer;
    for(
        var iteratorVectorIndexServerClient=0;
        iteratorVectorIndexServerClient<vectorIndexServerClient.idDropdown.length;
        iteratorVectorIndexServerClient++
    )
        if(vectorIndexServerClient.idDropdown[iteratorVectorIndexServerClient]==newName)
        {
            indexServer=iteratorVectorIndexServerClient;//vectorIndexServerClient.idVector[iteratorVectorIndexServerClient];

        }


    // Cambia il nome del monitor
    var dropNFFGname = document.getElementById('dropNFFGname');
    dropNFFGname.textContent = newName;

    changeActualNFFGname(newName);


    //changeActualNFFGname
    if(NFFGcyto.id.length!=0)
    {

        if(change==1)
        {

            warningSave(newName);
            change=0;

        }
        else
        {
            singleNFFGfromVerigraphToCytoAndCheck(newName);
            drawWithParametre(NFFGcyto);

        }
    }
    else
    {


        singleNFFGfromVerigraphToCytoAndCheck(newName);
        drawWithParametre(NFFGcyto);
        change=0;
    }


}

/**
 * @description: To update the client when there is not element in the selector. It prints a empty graph.
 */

function aggiornaGrafoAttualeEmpty()
{
    var l=[];
    drawWithParametre(l);
    var dropNFFGname = document.getElementById('dropNFFGname');
    dropNFFGname.textContent = "";
}

/**
 * @description: To remove all element of the selector of the index page. When the selector has been created, has been
 * concatenated a "s" in order to produce a different id for two things. Because in realty during the creation of the
 * selector, the client produce two different things and in order to differentiate, one of them has in addition a "s".
 */
function removeNffgSelect()
{


    for(var i=0; i<NFFGsServer.length; i++)
    {
        var name= NFFGsServer[i].id + "s";

        var elem = document.getElementById(name);
        elem.parentNode.removeChild(elem);
    }


}

/**
 * @description: To remove a single element of the selector of the index page. It receives the name of the
 * selector(or of the graph) and by it, it will delete the selector. When the selector has been created, has been
 * concatenated a "s" in order to produce a different id for two things. Because in realty during the creation of the
 * selector, the client produce two different things and in order to differentiate, one of them has in addition a "s".
 * @param {string} nameNffg - id of graph.
 */

function removeNffgSelectSingle(nameNffg)
{
    //concatenation with s
    var name= nameNffg + "s";

    //Delete the element of the selector.
    var elem = document.getElementById(name);
    elem.parentNode.removeChild(elem);
}

/**
 * @description To initialize the client variable that is used on the client to modify the graph and to draw it.
 */
function initVarCyto ()
{

    //Don't work
    //NFFGscyto = [];

    //Work
    NFFGcyto =
        {
            id:[],
            nodes: [],
            edges: []
        };

}

/**
 * @description It adds an element to the vectorIndexServerClient vecotor.
 * @param {int} idDropdown - id of graph on web site.
 * @param {int} idVector - id of graph on the server.
 */
function addElementTovectorIndexServerClient(idDropdown, idVector)
{
    vectorIndexServerClient.idDropdown.push(idDropdown);
    vectorIndexServerClient.idVector.push(idVector);


}

/**
 * @description It used for deleting an element from the vectorIndexServerClient vector.
 */
function deleteElementTovectorIndexServerClient()
{
    delete vectorIndexServerClient.idDropdown[indexServer];
    vectorIndexServerClient.idDropdown.splice(indexServer, 1);

    delete vectorIndexServerClient.idVector[indexServer];
    vectorIndexServerClient.idVector.splice(indexServer, 1);


}