/**
 * @file File contained all the function for the  webclient modal.
 */



function  populatedAgainModalD4()
{

    rootCleanningModalModifyNode("D4");
    var wrapper = $(".input_fields_wrap_configurationGroupD4"); //Fields wrapper
    document.getElementById("00D1").remove();

    var conf = NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration;

    $(wrapper).append
    (
        "<div id=\"00D4\"> Web server name: <input class=\"form-control\" id=\"0D4\" type=\"text\"" +
        "name=\"mytext[]\" value=" + conf[0].webserver + "></div>"
    );


}