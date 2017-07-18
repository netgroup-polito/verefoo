/**
 * @file It manages (in real time) the configuration button during the chose of the new functional type.
 * It is able to active or disable this button in relationship of the new functional type.
 */


/**
 * @description It is called during the chose of the new configuration type. It able in real time to
 * active or disable the button configuration and to set or unset it in relationship of the new functional type.
 * It uses another function (verifyAdSettingsModifyNodeConfiguration) in order to understand
 * which functional type has been selected and if the configuration is
 * mandatory, optional  or forbidden.
 */
function updateFormEnabledModifyNodeConfiguration()
{


    var i =  verifyAdSettingsModifyNodeConfiguration();

    if (i.localeCompare("1")==0)
    {
        //Must have the configuration node
        $('#idCheckNodeModify').prop('checked', true).attr('disabled', 'disabled');


    }
    else {
        if (i.localeCompare("2") == 0)
        {
            //No configuration node
            $('#idCheckNodeModify').prop('checked', false).attr('disabled', 'disabled');


        }
        else
        if (i.localeCompare("3") == 0)
        {
            //Optional configuration
            $('#idCheckNodeModify').removeAttr('disabled');

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
function verifyAdSettingsModifyNodeConfiguration()
{

    var selectorInputValue = $('#idSelectorModifyNode').val();

    //Put here the node that you  wish that have the configuration
    //Yes configuration
    if(selectorInputValue.localeCompare("mailclient")==0 ||
        selectorInputValue.localeCompare("vpnaccess")==0 ||
        selectorInputValue.localeCompare("vpnexit")==0 ||
        selectorInputValue.localeCompare("webclient")==0)
    {
        return  "1";
    }



    //Put here the node that you don't wish that have the configuration
    if (selectorInputValue.localeCompare("endpoint") == 0 ||
        selectorInputValue.localeCompare("webserver")==0 ||
        selectorInputValue.localeCompare("fieldmodifier")==0 ||
        selectorInputValue.localeCompare("mailserver")==0)
    {
        return "2";
    }
    return "3";


}