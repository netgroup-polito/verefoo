/**
 * @file File contained all the function for the  webclient modal.
 */


$(document).ready(function() {


    $("#validateconfigurationGroupD4").click(function()
    {


        var jsonVariable = {};

        jsonVariable["webserver"] = $("#0D4").val();


        if(changedNodeConfigurationInt!=-1)
        {
            NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration = [];

            NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration.push(jsonVariable);
            if(updateOrChangeConfigurationFuncType==1)
                NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType=$("#idSelectorModifyNode").val();
            alertSuccess("Modification done!");

        }
        else
        {
            saveAndDraw();
            NFFGcyto.nodes[NFFGcyto.nodes.length - 1].data.configuration.push(jsonVariable);

        }

        change = 1;
        cleaningModalGroupD4();

    });


    $("#closeconfigurationGroupD4").click(function()
    {
        cleaningModalGroupD4();
    });




});

function cleaningModalGroupD4()
{
    changedNodeConfigurationInt=-1;
    updateOrChangeConfigurationFuncType=0;

    var wrapper = $(".input_fields_wrap_configurationGroupD4"); //Fields wrapper
    document.getElementById("00D4").remove();


    $(wrapper).append
    (
        "<div id=\"00D4\"> Web server name: <input class=\"form-control\" id=\"0D4\" type=\"text\"" +
        "name=\"mytext[]\"></div>"
    );


}

