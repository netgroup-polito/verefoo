/**
 * @file File contained all the function for the  mailclient modal.
 */


$(document).ready(function() {


    $("#validateconfigurationGroupD1").click(function()
    {


        var jsonVariable = {};

        jsonVariable["mailserver"] = $("#0D1").val();

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

        cleaningModalGroupD1();
        change = 1;

    });

    $("#closeconfigurationGroupD1").click(function()
    {
        cleaningModalGroupD1();

    });




});

function cleaningModalGroupD1()
{
    changedNodeConfigurationInt=-1;
    updateOrChangeConfigurationFuncType=0;

    var wrapper = $(".input_fields_wrap_configurationGroupD1"); //Fields wrapper
    document.getElementById("00D1").remove();


    $(wrapper).append
    (
        "<div id=\"00D1\">Mail server name: <input class=\"form-control\" id=\"0D1\" type=\"text\"" +
        "name=\"mytext[]\" ></div>"
    );

}


