/**
 * @file You can find all the information of this library:
 * http://t4t5.github.io/sweetalert/. In this file there are all the possible alerts.
 */


/**
 * @description The function gets a phrase and it prints a sweet alert with this phrase.
 * This function is used when there has been at least one problem during an operation.
 * @param {string} phrase - The alert print this phrase.
 */
function alertError(phrase)
{
    swal({   title: "Error!",   text: phrase,   type: "error",   confirmButtonText: "Cool" });

}
/**
 * @description The function gets a phrase and it prints a sweet alert with this phrase.
 * This function is used to advise the user about a problem.
 * @param  {string} phrase -  The alert print this phrase.
 */
function alertWarning(phrase)
{
    swal({   title: "Warning!",   text: phrase,   type: "warning",   confirmButtonText: "Cool" });

}


/**
 * @description The function get a phrase and it prints a sweet alert with this phrase.
 * This function is used when there have not been problems during a operation
 * @param {string} phrase -  The alert print this phrase.
 */
function alertSuccess(phrase)
{
    swal({   title: "Success!",   text: phrase,   type: "success",   confirmButtonText: "Cool" });

}

/**
 * @description The function is active when the user wishes to change the graph and current graph is not saved.
 * If the graph is saved or updated the function is not called by the system.
 * The function asks if the graph needs to be saved or not. The user can decide whether to save the current graph before to pass
 * to the new graph or discard the modification of the graph.
 * When the user saves it, the function calls NFFGfromCytoToVerigraphFlagSave function in order to translate the current
 * graph in Verigraph json form and it updates using updateGraphToServerAndChangeId function.
 * If the user decides to discard, the function updates the page with the new graph.
 * @param {int} newNameId -Id of the graph in the browser.
 */
function warningSave(newNameId)
{



    swal({
            title: "Warning",
            text: "Do you wish to save the graph?",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: "#DD6B53",
            confirmButtonText: "Yes, save it!",
            cancelButtonText: "No, don't save  it!",
            closeOnConfirm: false,
            closeOnCancel: true
        },
        function (isConfirm)
        {
            if (isConfirm)
            {


                var pr = NFFGfromCytoToVerigraphFlagSave("save1");

                //The exception that proves the rule, because this function should stay in another file
                updateGraphToServerAndChangeId(pr, vectorIndexServerClient.idDropdown[indexServerLast], newNameId);

            } else
                {
                    singleNFFGfromVerigraphToCytoAndCheck(newNameId);
                    drawWithParametre(NFFGcyto);

                }

        });

}

/**
 * @description The function asks to the user if is sure to delete the graph. If the user is sure
 * to delete it, the function checks if the server is online or not and after that, deletes the graph by delete method http
 * and it deletes on the browser.
 */

function warningDeleteGraph()
{
    swal({
            title: "Warning",
            text: "Are you sure, to delete this graph?",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: "#DD6B53",
            confirmButtonText: "Yes, delete it!",
            cancelButtonText: "No, don't delete it!",
            closeOnConfirm: false,
            closeOnCancel: true
        },
        function (isConfirm)
        {
            if (isConfirm)
            {
                //Check if the server is online or not
                if(serverOnlineFunction("Error connection, server offline!!!")==false)return;
                deliteGraphFromServer(NFFGcyto.id[0].id);

            } else
            {

            }

        });

}

/**
 * @description The function advises the user that it is not possible to verify the policy because the graph is not saved.
 * The user can save it or no. If the user doesn't wish to save it, the verification doesn't start. If the user decides to
 * save the graph the verification starts only if the graph has been correctly saved.
 * @param {string} urlVerificationPolicy - The second part of url.
 */
function updateGraphForVerificationPolicy(urlVerificationPolicy)
{



    swal({
            title: "Warning",
            text: "It is not possible to verificated the graph because it has not been saved!!!",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: "#DD6B53",
            confirmButtonText: "Yes, save it!",
            cancelButtonText: "No, don't save  it!",
            closeOnConfirm: false,
            closeOnCancel: true
        },
        function (isConfirm)
        {
            if (isConfirm)
            {
                    //Check if the server is online o not
                    if(serverOnlineFunction("Error connection, server offline!!!")==false)return;

                    var packet= [];

                    //Save in local the variable and get the varible for the server (Verigraph NFFG)
                    packet =  NFFGfromCytoToVerigraphFlagSave("save");

                    //Update server
                    updateGraphToServerAndToVerifyPolicy
                    (
                        packet,
                        NFFGcyto.id[0].id,
                        urlVerificationPolicy
                    );


            } else
            {
            }

        });
        /*t not nessary to have a catch error for this type o alert. This catch is already inside of the
        *library. -> .catch(swal.noop);
        */

}