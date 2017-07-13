/**
 * @file this file contains the function to active/disable the checkbox of the configuration of the node during
 * the creation of the node.
 * In this way only the nodes tha have the possibility to have a configuration will have this option active.
 */

/**
 * @description "Read" the DOM and to understand if the selector of creation node has been changed.
 * If has been changed, it actives a function (updateFormEnabled) in order to understand if the new node
 * must have a configuration or the configuration is optional or forbidden.
 */
$(document).ready(function ()
{

    $('#selectNewFunctionalType').change(updateFormEnabled);

});

/**
 * @description Enable and manages the "configuration node" checkbox. Using verifyAdSettings function in order
 * to understand if the node has the necessity to have a configuration or no.
 */
function updateFormEnabled()
{

    var i =  verifyAdSettings();

    if (i.localeCompare("1")==0)
    {
        //Must have the configuration node
        if($('#myCheckbox').attr('checked', false))
            $('#idcheckboxconfigurenode').prop('checked', true).attr('disabled', 'disabled');


    }
    else {
        if (i.localeCompare("2") == 0) {
            //Don't mush have the configuration node
            if ($('#myCheckbox').attr('checked', true))
                $('#idcheckboxconfigurenode').prop('checked', false).attr('disabled', 'disabled');


        }
        else
        if (i.localeCompare("3") == 0)
        {
            //Don't mush have the configuration node
            $('#idcheckboxconfigurenode').removeAttr('disabled');

        }

    }
}

/**
 * @description Capture the value of the select and return if the configuration is mandatory (1),
 * forbidden(2) or optional (3)
 * @returns {int} - Return status (mandatory, forbidden and optional) of the configuration in relation of the
 * selector. In particular
 * - 1 returns when the functional type is mandatory.
 * - 2 returns when the functional type is forbidden.
 * - 3 return when the functional type is optional
 */
function verifyAdSettings()
{
    //Put here the node that you  wish that have the configuration
    //Yes configuration
    var dempVarNewFunctionalType = $('#selectNewFunctionalType').val();
    if(dempVarNewFunctionalType.localeCompare("mailclient")==0 ||
        dempVarNewFunctionalType.localeCompare("vpnaccess")==0 ||
        dempVarNewFunctionalType.localeCompare("vpnexit")==0 ||
        dempVarNewFunctionalType.localeCompare("webclient")==0)
    {
        return  "1";
    }



    //Put here the node that you don't wish that have the configuration
    //No configuration
    if (dempVarNewFunctionalType.localeCompare("endpoint") == 0 ||
        dempVarNewFunctionalType.localeCompare("webserver")==0 ||
        dempVarNewFunctionalType.localeCompare("fieldmodifier")==0 ||
        dempVarNewFunctionalType.localeCompare("mailserver")==0)
    {
        return "2";
    }
    return "3";


}