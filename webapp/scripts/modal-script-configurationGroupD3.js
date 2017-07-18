/**
 * @file File contained all the function for the  vpnexit modal D3.
 */


$(document).ready(function() {


    $("#validateconfigurationGroupD3").click(function()
    {


        var jsonVariable = {};

        jsonVariable["vpnaccess"] = $("#0D3").val();


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
        cleaningModalGroupD3();



    });

    $("#closeconfigurationGroupD3").click(function()
    {
        cleaningModalGroupD3();
    });


});

function cleaningModalGroupD3()
{
    changedNodeConfigurationInt=-1;
    updateOrChangeConfigurationFuncType=0;

    var wrapper = $(".input_fields_wrap_configurationGroupD3"); //Fields wrapper
    document.getElementById("00D3").remove();


    $(wrapper).append
    (
        "<div id=\"00D3\">Vpn Access: <input class=\"form-control\" id=\"0D3\" type=\"text\"" +
        "name=\"mytext[]\"></div>"
    );

}
