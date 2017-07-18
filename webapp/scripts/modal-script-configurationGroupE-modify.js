/**
 * @file File contain all the function for the dpi modal
 */




function  populatedAgainModalE()
{
    rootCleanningModalModifyNode("E");

    var wrapper = $(".input_fields_wrap_configurationGroupE"); //Fields wrapper
    var conf = NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration;

    if(conf.length>0)
    {
        document.getElementById("00E").remove();
        $(wrapper).append
        (
            "<div id=\"00E\">  List of forbidden words:  <input class=\"form-control\" id=\"0E\" type=\"text\""+
            "name=\"mytext[]\" value=" + conf[0] + "></div>"
        );
    }

    if(conf.length>1)
    {
        for(var i=1; i<conf.length; i++)
        {
            configurationGroupEcounterE++;
            configurationGroupEcounterBisE++;
            var tempId ="sE"  + configurationGroupEcounterE.toString();
            vectorIdElementE.push(tempId);
            $(wrapper).append
            (
                '<div id="sE'+ configurationGroupEcounterE.toString()
                + '"><br> List of forbidden words: <input id="'
                + configurationGroupEcounterE.toString() +
                'E"class="form-control" type="text" name="mytext[]" value="' +  conf[i]  + '"/>' +
                '<a href="#" class="remove_field">Remove</a></div>'
            ); //add input box

        }
    }


};




