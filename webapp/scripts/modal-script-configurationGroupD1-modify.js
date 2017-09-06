/**
 * @file File contained all the function for the  mailserver modal D1.
 */



function  populatedAgainModalD1()
{

    rootCleanningModalModifyNode("D1");

    var wrapper = $(".input_fields_wrap_configurationGroupD1"); //Fields wrapper
    document.getElementById("00D1").remove();

    var conf = NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration;

    $(wrapper).append
    (
        "<div id=\"00D1\">Mail server name: <input class=\"form-control\" id=\"0D1\" type=\"text\"" +
        "name=\"mytext[]\" value=" + conf[0].mailserver + "></div>"
    );


}




