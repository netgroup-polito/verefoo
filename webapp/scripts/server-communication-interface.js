/**
 * @file Scripting page dedicated to server communication. This should be the most important file of the entire client!
 *
 * @interface Server RESTful Verigraph
 *
 */



/**
 * @description Obtain all the NFFGs present on the server by an HTTP GET.
 * If the get has been a success, the NFFGsServer variable is updated and all
 * the element of the selector (of NFFG) are created. After this, the function update the
 * DOM with the first graph. In addition it advises the user if the download is successful or not with
 * a message for both cases.
 * If there are not NFFG on the server the function alerts the user about it.
 * If the get returns error the function advises the user about it.
* @method GET
*/
function loadNFFGfromServerWithSuccesMessage()
{

    $("#watingServerId").show();

    $.ajax({
        url:  addressServer, //yes
        type: "GET",
        contentType: "application/json",
        dataType: 'json',
        success: function (respond)
        {
            $("#watingServerId").hide();
            var i=0;

            removeNffgSelect();
             indexServer = 0;


             indexServerLast = 0;

             useLastId=0;

             vectorIndexServerClient =
                {
                    idDropdown:[],
                    idVector:[]
                };

             NFFGsServer = {};



             change =0;


             serverOnline=true;


             NFFGcyto =
                {
                    id:[],
                    nodes: [],
                    edges: []
                };



              tmpNodeSave = null;


             lasVerificationResult = [];


            NFFGsServer = respond;


            for(i=0; i<NFFGsServer.length; i++)
            {
               addNffgSelect(NFFGsServer[i].id);
               addElementTovectorIndexServerClient(NFFGsServer[i].id, i);
            }

            if(NFFGsServer.length!=0)
            {
                indexServer = 0;
                aggiornaGrafoAttuale(NFFGsServer[0].id);
                alertSuccess("Download completed!!!");

            }
            else
                {
                alertWarning("No graph available creating one");
                loadGraphCreateGraphIfNoGraphAvailable();
            }

        },
        error: function (respond) {
            $("#watingServerId").hide();
            //console.log("error" + JSON.stringify(respond));
            alertError("Error: it is not possibile to get the information from server "   +  respond.responseText);
        }
    });


}


/**
 * @description To post the graph on the server. If the operation is successful, it updates the variable NFFGsServer,
 * moreover it adds the id on the selector.
 * If the operation is not successful, it advises the user by a sweet alert.
 * @method POST
 * @param {string} packet - The json to send to the server.
 */
function postGraphToServer(packet, i)
{

    $("#watingServerId").show();



    var pachetStringify = JSON.stringify(packet);

    $.ajax({
        url: addressServer, //yes
        type: "post",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: pachetStringify,
        success :function (respond)
    {
        switch (i)
        {
            case("1"):
            {
                $("#watingServerId").hide();
                alertSuccess("Your graph has been stored!!!");

                addElementTovectorIndexServerClient(respond.id,  NFFGsServer.length-1);
                NFFGfromCytoToVerigraphFlagSave("yes");
                NFFGsServer.push(respond);
                addNffgSelect(respond.id);
                break;

            }
            case("2"):
            {
                $("#watingServerId").hide();
                alertSuccess("New empty graph created!!!");


                addNffgSelect(respond.id);
                NFFGsServer.push(respond);
                addElementTovectorIndexServerClient(respond.id,  NFFGsServer.length-1);

                aggiornaGrafoAttuale(respond.id);
                change=0;
                break;
            }
        }



    },
    error: function (respond)
    {
        $("#watingServerId").hide();
        alertError("ERROR: Your graph has not been stored, maybe is not valid "  +  respond.responseText);
    }
    });


}




/**
 * @deprecated
 * @description This function is able to save on the server the graph. This function is called when
 * the user wishes to load a file.
 * @method POST
 * @param {string} packet - Contains the new nffg in JSON string format.
 */
function postGraphToServerFromFile(packet)
{
    $("#watingServerId").show();
    var pachetStringify = JSON.stringify(packet);

    $.ajax({
        url: addressServer, //yes
        type: "post",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: pachetStringify,
        success :function (respond)
        {

            $("#watingServerId").hide();
            alertSuccess("New graph created from file!!!");


            addNffgSelect(respond.id);
            NFFGsServer.push(respond);
            addElementTovectorIndexServerClient(respond.id,  NFFGsServer.length-1);

            aggiornaGrafoAttuale(respond.id);
            change=0;

        },
        error: function (respond)
        {
            $("#watingServerId").hide();
            alertError("ERROR: Your graph has not been stored maybe it is not valid " +    respond.responseText);
            //console.log("error" + JSON.stringify(respond));
        }
    });


}







/**
 * @description this function is able to delete a specific graph. In order to delete the graph it is necessary to
 * know the id of the graph that stay on the server. The function receives the id like param and uses it to create
 * the url of the server. When the server sends the answer if it is positive the function advise the user and
 * it delete the graph from the client and it updates the selector and it draws the first graph of the global varible.
 * If the server sends a negative answer, the function  adives the user about it.
 * @WARNING if on the server there are these id: 1 2 3 4 5 and you delete the 3°, the server don't shift the others id.
 * So the vector will be 1 2 4 5.
 * Ex:
 * 1) 1 2 3 4 5  id
 * 2) delete 3 id
 * 3) 1 2  4 5  id
 * 4) delete 1
 * 5) 2 4 5 id
 *@method DELETE
 * @param {string} id - Id of the graph that wishes to delete.
 */
function deliteGraphFromServer(id)
{
    $("#watingServerId").show();
    var url = addressServer + id;


    $.ajax({
        url: url, //yes
        type: "delete",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (respond)
        {
            $("#watingServerId").hide();
            //Update the selecotr
            removeNffgSelectSingle(NFFGsServer[indexServer].id);

            //Update the NFFGsServer
            delete NFFGsServer[indexServer];
            NFFGsServer.splice(indexServer, 1);
            deleteElementTovectorIndexServerClient();

            change=0;

            if(NFFGsServer.length!=0)
            {
                alertSuccess("success: the graph has been deleted");
                //Draw the new graph
                aggiornaGrafoAttuale(NFFGsServer[0].id);

            }
            else
                {
                    //alertWarning("No graph available");
                    aggiornaGrafoAttualeEmpty();
                    changeActualNFFGname("");
                    alertSuccess("The graph has been deleted but a new empty graph has been created, on the server there must be at least a graph!");
                    initSystem();
                    //console.log("s2");
                }


        },
        error: function (respond)
        {
            $("#watingServerId").hide();
            alertError("It is not possible to delete the graph!" +    respond.responseText);
            //+ JSON.stringify(respond)
        }
    });


}

/**
 * @description To verify the graph. If the graph is right or not the function advises the user about it.
 * The function does before a post in order to save the graph on the server. The server will only save right
 * graph so if the server replies in a good way, the client has to delete it. So the client will call a function
 * in order to delete it. Moreover it passes to this function the id of the graph that the server has sent during
 * its answer.
 * @method POST
 * @param {string} packet - Contains the nffg in JSON format string format.
 */
function verifyGraphToServer(packet)
{

    $("#watingServerId").show();

    var pachetStringify = JSON.stringify(packet);

    $.ajax({
        url: addressServer, //yes
        type: "post",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: pachetStringify,
        success :function (respond)
        {

            $("#watingServerId").hide();

            deliteGraphFromServerVerify(respond.id);
        },
        error: function (respond)
        {
            $("#watingServerId").hide();
            alertError("ERROR: Your graph is not valid: " +  respond.responseText);
        }
    });


}

/**
 * @description To delete the graph from the server using the id (param) in order to create the url of the server.
 * This function is used during the verification.
 * In both cases (positive/negative answer of the server) the  function advises the user.
 * @method DELETE
 * @param {string} id - Id of the graph.
 */
function deliteGraphFromServerVerify(id)
{

    $("#watingServerId").show();
    var url = addressServer + id;

    $.ajax({
        url: url, //yes
        type: "delete",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (respond)
        {
            $("#watingServerId").hide();
            alertSuccess("Success 100%: your graph is valid!!!");
        },
        error: function (respond)
        {
            $("#watingServerId").hide();
            alertError("Sorry!!! Maybe your graph has not been stored on the server because there has been an error! Please verify :(" +
                  respond.responseText);
            //+ JSON.stringify(respond)
        }
    });
}

/**
 * @description To update the current graph. It does a PUT and advise the user if the PUT is successful or not.
 * @method PUT
 * @param {string} packet - Contains the new nffg in JSON string format.
 * @param {string} id - Id of the graph where wishes to update the graph.
 * @param {string} flag - to do a specific action during the replay of the server.
 * flag = 1  => normal update,
 * flag = 2 => update from json written, or when update after to have modified the json.
 *
*/
function updateGraphToServer (packet, id, flag)
{
    $("#watingServerId").show();
    var pachetStringify = JSON.stringify(packet);


    var url = addressServer +  id;
    $.ajax({
        url: url, //yes
        type: "PUT",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: pachetStringify,
        success: function (respond)
        {

            $("#watingServerId").hide();
            switch(flag)
            {
                case(1):
                {
                    change=0;
                    alertSuccess("Success: your graph is updated!!!");
                    NFFGfromCytoToVerigraphFlagSave("save");
                    break
                }
                case (2):
                {

                    change=0;
                    alertSuccess("Success: your graph is updated!!!");
                    fromVerigraphToCytoscapeAndSaveIntoVerigraphNFFG(packet);
                    $('#modifyJsonModal').modal('hide');
                    break
                }
            }

        },
        error: function (respond)
        {
            $("#watingServerId").hide();
            alertError("Sorry!!! Your graph is not updated!!! " +    respond.responseText);
            //+ JSON.stringify(respond)
        }
    });

}






/**
 * @description The function is used in order to download all the graph and to save them on local vector (NFFGsServer).
 * If there is no graph available the function calls createEmptyGraphAndDownloadIt function in order to create an
 * empty graph. This function is called during the launch of the web-site. So it doesn't advise the user about
 * the creation of the new empty graph.
 * But if there is an error it warns by a sweet alert.
 * @method GET
 */
function loadGraphCreateGraphIfNoGraphAvailable ()
{
    $("#watingServerId").show();
    $.ajax({
        url: addressServer, //yes
        type: "GET",
        contentType: "application/json",
        dataType: 'json',
        success: function (respond)
        {

            $("#watingServerId").hide();
            NFFGsServer=respond;

            for(var i=0; i<NFFGsServer.length; i++)
            {
                addNffgSelect(NFFGsServer[i].id);
                addElementTovectorIndexServerClient(NFFGsServer[i].id,  i);
            }


            if(NFFGsServer.length!=0)
            {
                indexServer = 0;
                aggiornaGrafoAttuale(NFFGsServer[0].id);
            }
            else
            {
                //alertWarning("No graph available");
                //Create graph and download it
                createEmptyGraphAndDownloadIt();
            }


        },
        error: function (respond)
        {
            $("#watingServerId").hide();
            alertError("Error: it is not possibile to get the information from server "   +  respond.responseText);
        }
    });

}

/**
 * @description The function is able to create a new empty graph on the server.
 * It creates a JSON that represents a empty NFFG and to put on the server.
 * If the operation is successful therefore the function advises the user by a sweet alert and it saves the empty
 * graph on local variable and it adds the new id of the graph on selector of the graph and it update the DOM with the
 * empty graph.
 * If the server replay with an error, it informs the user by an sweet alert.
 * @method POST
 */
function createEmptyGraphAndDownloadIt()
{
    $("#watingServerId").show();
    var packet ={"nodes":[]};
    var packetStringify = JSON.stringify(packet);

    $.ajax({
        url: addressServer,
        type: "post",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: packetStringify,
        success :function (respond)
        {

            $("#watingServerId").hide();

            addNffgSelect(respond.id);
            NFFGsServer.push(respond);
            addElementTovectorIndexServerClient(respond.id,   NFFGsServer.length-1);

            aggiornaGrafoAttuale(respond.id);

        },
        error: function (respond)
        {
            $("#watingServerId").hide();
            alertError("ERROR: Your graph has not been stored maybe it is not valid " +   +  respond.responseText);
        }
    });

}

/**
 * @description This function is able to send a get to the server in order
 * to have the result of the verification of the policy.
 * When the server replay without an error, it sees one sweet alert of three:
 * one for SAT result, one for UNSAT result and the last for UNDEFINED result. Each sweet alert has particular symbol:
 * SAT has success symbol, UNSAT has error symbor and UNDEFINED  has warning  symbol in order to distinguish them.
 * To launch the sweet alert the function uses three different functions:
 * - alertSuccess function for SAT
 * - alertError functin for UNSAT
 * - alertWarning function for UNDEFINED
 * Each function receives a phrase from the server in order to explain in a good way the result.
 * There are three sentences depending on the result:
 * - SAT result means: ok or when the model is satisfiable, i.e. when the network scenario is valid;
 * - UNSAT result means: error or when the model is unsatisfiable, i.e. when the network scenario is
 invalid;
 * - UNDEFINED result when the model is unknown, i.e. it could not be determined whether the network scenario is valid or not.
 * where verifySourceNode and verifyDestinatonNode identify respectively source and destination node of the verification.
 * If the server send an error the function advises by a sweet alert with an error symbol and it print an significant message.
 * @method GET
 * @param {string} url - Second part of the url,:
 * it contains id, source and destination node  and the method of the verification (reachability, isolation and traversal)
 * and maybe the midlebox if the verification is not traversal.
 */
function getVerifyPolicy(url)
{

    $("#watingServerId").show();


    $.ajax({
        url: addressServer + url, //yes
        type: "GET",
        contentType: "application/json",
        dataType: 'json',
        success: function (respond)
        {
            $("#watingServerId").hide();
            lasVerificationResult = [];

            lasVerificationResult.push(respond);

            //Another alert
            //alertSuccess("Success verification!!!");


            var mes = lasVerificationResult[0].comment;

            var res ="";

            if(mes.indexOf(".")!=-1)
            {
                res = mes.split(". See");
            }
            else
            {
                res = mes.split(" (");
            }

            if(lasVerificationResult[0].result.localeCompare("SAT")==0)
            {
                alertSuccess(res[0] + ".");
            }
            else
                {
                if (lasVerificationResult[0].result.localeCompare("UNSAT") == 0) {
                    alertError(res[0] + ".");
                }
                else {
                    if (lasVerificationResult[0].result.localeCompare("UNDEFINED") == 0) {
                        alertWarning(res[0] + ".");
                    }
                    else
                    {
                        alertError("Unknown error!");
                    }
                }
            }




                $('#verificationIdModal').modal('show');
            writeJsonCurrentElementVerificationPolicy();



        },
        error: function (respond)
        {
            $("#watingServerId").hide();
            lasVerificationResult = [];

            lasVerificationResult.push(respond);

            //Another alert
            //alertError("Error server:" +    respond.responseText );
            alertError("Error server: " +    lasVerificationResult[0].responseJSON.errorMessage);

            $('#verificationIdModal').modal('show');

            writeJsonCurrentElementVerificationPolicy();
        }
    });

}


/**
 * @description The function is able to update the current graph before to do the verification of the policy calling
 * a function (verifiyPolicy) if the update operation is successful. Otherwise it advises  the user about the error.
 * @method PUT
 * @param {string} packet - Contains the current graph to update.
 * @param {string} id - Id of the graph in oder to create the right url.
 * @param {string} urlVerificationPolicy - The second part of the url of the verification.
 */
function updateGraphToServerAndToVerifyPolicy
                        (
                            packet,
                            id,
                            urlVerificationPolicy
                        )
{
    $("#watingServerId").show();
    var pachetStringify = JSON.stringify(packet);


    var url = addressServer +  id;
    $.ajax({
        url: url, //yes
        type: "PUT",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: pachetStringify,
        success: function (respond)
        {
            change=0;
            alertSuccess("Success: your graph is updated!!!");
            $("#watingServerId").hide();
            verifiyPolicy(urlVerificationPolicy);



        },
        error: function (respond)
        {
            $("#watingServerId").hide();
            alertError("Sorry!!! Your graph is not updated!!! " +    respond.responseText);
            //+ JSON.stringify(respond)
        }
    });

}

/**
 * @description The function is able to update the graph and to change the id of the DOM and the graph.
 * The function is called when has not saved and the user wishes to change the graph with another graph. So this
 * function is able to update the graph on the server and to change the DOM with the next graph.
 * If the server responds correctly (code 2xx) therefore the function launch a alert (sweet alert)
 * in order  to inform the user and
 * it saves the currents graph on the client  NFFGfromCytoToVerigraphFlagSave function, it marks that the graph has been saved
 * by change varible and it gets from the vector graph server (singleNFFGfromVerigraphToCytoAndCheck function) the new graph
 * and it draws it by drawWithParametre function.
 * If the server reaplies with an error, it doesn't change the graph.
 * @method PUT
 * @param {string} packet - Contains the current graph to update.
 * @param {string} id - Id of the graph in oder to create the right url.
 * @param {string} newNameId - The id of the next graph that the user wishes to see.
 */
function updateGraphToServerAndChangeId (packet, id, newNameId)
{

    $("#watingServerId").show();
    var pachetStringify = JSON.stringify(packet);


    var url = addressServer +  id;

    $.ajax({
        url: url, //yes
        type: "PUT",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data: pachetStringify,
        success: function (respond)
        {
            useLastId=1;
            NFFGfromCytoToVerigraphFlagSave("save");
            change=0;
            alertSuccess("Success: your graph is updated!!!");
            $("#watingServerId").hide();

            singleNFFGfromVerigraphToCytoAndCheck(newNameId);
            drawWithParametre(NFFGcyto);


        },
        error: function (respond)
        {
            $("#watingServerId").hide();
            alertError("Sorry!!! Your graph is not updated!!! "  +    respond.responseText);
            
            /**
             * To change the graph.
             * singleNFFGfromVerigraphToCytoAndCheck(newNameId);
             * drawWithParametre(NFFGcyto);
             */
            
        }
    });

}