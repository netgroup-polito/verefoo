/**
 * @file File contained all the function for the  vpnaccess modal D2.
 */


function  populatedAgainModalD2()
{
    rootCleanningModalModifyNode("D2");

    var wrapper = $(".input_fields_wrap_configurationGroupD2"); //Fields wrapper
    document.getElementById("00D2").remove();

    var conf = NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration;

    $(wrapper).append
    (
        "<div id=\"00D2\">Vpn Exit:  <input class=\"form-control\" id=\"0D2\" type=\"text\"" +
        "name=\"mytext[]\" value=" + conf[0].vpnexit + "></div>"
    );






}






