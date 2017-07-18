/**
 * @file File contained all the function for the  vpnexit modal.
 */



function  populatedAgainModalD3()
{
    rootCleanningModalModifyNode("D3");

    var wrapper = $(".input_fields_wrap_configurationGroupD3"); //Fields wrapper
    document.getElementById("00D3").remove();

    var conf = NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration;

    $(wrapper).append
    (
        "<div id=\"00D3\">Vpn Access: <input class=\"form-control\" id=\"0D3\" type=\"text\"" +
        "name=\"mytext[]\" value=" + conf[0].vpnaccess + "></div>"
    );


}



