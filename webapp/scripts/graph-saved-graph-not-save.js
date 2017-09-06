/**
 * Created by Fenix on 15/02/17.
 */

/**
 * @file This file manage the icons  on the DOM that indicate if the graph is saved or no.
 */


/**
 * @description It used to indicate to the user if the graph is saved or no. If the graph is saved on the DOM
 * there is a green icon with the follow phrase:
 * "Graph saved",
 * If the NFFG is not saved because has been touch therefore there is a red icon with this phrase:
 * "Graph not saved"
 * This function uses a polling system in order to call itself and to check it (if the NFFG is saved or no).
 * It works in this way: It checks if the change variable is changed or not. This variable indicates if the NFFG
 * is changed or not. If it is 0 the NFFG is not changed so it shows the green icon, otherwise it shows the red
 * symbol.
 */
function checkGraphSavedOrNotSaved()
{

    if(change==0)
    {
        $("#graphSaved").show();
        $("#graphNotSaved").hide();
    }
    else
    {
        $("#graphSaved").hide();
        $("#graphNotSaved").show();
    }
    setTimeout(checkGraphSavedOrNotSaved, 3000);
}