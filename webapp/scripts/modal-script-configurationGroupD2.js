/**
 * @file File contained all the function for the  vpnaccess modal D2.
 */


$(document).ready(function() {


    $("#validateconfigurationGroupD2").click(function()
    {


        var jsonVariable = {};

        jsonVariable["vpnexit"] = $("#0D2").val();

        if(changedNodeConfigurationInt!=-1)
        {
            NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration = [];

            NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration.push(jsonVariable);
            if(updateOrChangeConfigurationFuncType==1)
                NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType=$("#idSelectorModifyNode").val();
            changedNodeConfigurationInt=-1;
            updateOrChangeConfigurationFuncType=0;
            alertSuccess("Modification done!");
        }
        else
        {
            saveAndDraw();
            NFFGcyto.nodes[NFFGcyto.nodes.length - 1].data.configuration.push(jsonVariable);
        }

        change = 1;

    });

    $("#closeconfigurationGroupD2").click(function()
    {

        cleaningModalGroupD2();

    });





});

function cleaningModalGroupD2()
{
    changedNodeConfigurationInt=-1;
    updateOrChangeConfigurationFuncType=0;

    var wrapper = $(".input_fields_wrap_configurationGroupD2"); //Fields wrapper
    document.getElementById("00D2").remove();


    $(wrapper).append
    (
        "<div id=\"00D2\">Vpn Exit:  <input class=\"form-control\" id=\"0D2\" type=\"text\"" +
        "name=\"mytext[]\" ></div>"
    );

}
