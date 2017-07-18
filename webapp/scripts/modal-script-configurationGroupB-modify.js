/**
 * @file File contains the function for populate again the firewall  modal during the
 * edit configuration.
 */


/**
 * @description This function is able to populate all the fields of modal B with the old name of the configuration
 * of the node X.
 * The function before gets the "point id" of "input_fields_wrap_configurationGroupA" id into wrapper variable and
 * it copies the configuration into a variable (conf variable). After that, it checks if the length is more thant
 * zero in order to delete the first field of the modal and to put another (with the same id) but in addition with
 * the name of the configuration into the text field.
 * After this operation, it continues in this way (more or less) but before it checks if the length is more than one
 * and if it is more than than one, it creates new field similar to modal A js file. In this way the two files
 * (this, and modal-script-configurationGroupB.js) are able to work together because in the DOM there are the some
 * id for the field. Consequently when the user remove a field, the action is done from the other file. This file is
 * only able to populate the modal with the old field.
 */


    function  populatedAgainModalB() {
    var wrapper = $(".input_fields_wrap_configurationGroupB"); //Fields wrapper
    var conf = NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration;

    var sourceNodeFirewall = "";
    var destinationNodeFirewall = "";
    var demp = null;
    var i = 0;
    var key;
    rootCleanningModalModifyNode("B");

    for (key in conf[0])
    {
        if (i == 0)
        {
            document.getElementById("00B").remove();
            demp = Object.keys(conf[0]);
            sourceNodeFirewall = demp[0];
            destinationNodeFirewall = conf[0][sourceNodeFirewall];
            $(wrapper).append
            (
                "<div id=\"00B\">" +
                "Source existing node" +
                "<input class=\"form-control\" id=\"0B\" type=\"text\" name=\"mytext[]\" value='" + sourceNodeFirewall + "'> <br>" +
                "Destination existing source" +
                "<input class=\"form-control\" id=\"0BB\" type=\"text\" name=\"mytext[]\" value='" + destinationNodeFirewall + "'></div>" +
                "</div>"
            );
        }

        else {
            //demp = key;
            sourceNodeFirewall = key;
            destinationNodeFirewall = conf[0][sourceNodeFirewall];

            configurationGroupAcounterB++; //text box increment
            configurationGroupAcounterBisB++;
            var tempId = "sB" + configurationGroupAcounterB.toString();
            vectorIdElementB.push(tempId);

            $(wrapper).append(
                '<div id="sB' + configurationGroupAcounterB.toString() +
                '"><br>Source existing node<input id="' + configurationGroupAcounterB.toString()
                + 'B"class="form-control" type="text" name="mytext[]" value="' + sourceNodeFirewall + '"/>' +

                '<br>Destination existing source<input id="'
                + configurationGroupAcounterB.toString()
                + 'BB"class="form-control" type="text" name="mytext[]"' +
                ' value="' + destinationNodeFirewall + '" />' +
                '<a href="#" class="remove_field">Remove</a></div>');

        }
        i++;
    }


}




