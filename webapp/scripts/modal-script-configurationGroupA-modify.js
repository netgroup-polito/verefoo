/**
 * @file File contains the function for populate again the antispam , cache, dpi and nat modal during the
 * edit configuration.
 */


/**
 * @description This function is able to populate all the fields of modal A with the old name of the configuration
 * of the node X.
 * The function before gets the "point id" of "input_fields_wrap_configurationGroupA" id into wrapper variable and
 * it copies the configuration into a variable (conf variable). After that, it checks if the length is more thant
 * zero in order to delete the first field of the modal and to put another (with the same id) but in addition with
 * the name of the configuration into the text field.
 * After this operation, it continues in this way (more or less) but before it checks if the length is more than one
 * and if it is more than than one, it creates new field similar to modal A js file. In this way the two files
 * (this, and modal-script-configurationGroupA.js) are able to work together because in the DOM there are the some
 * id for the field. Consequently when the user remove a field, the action is done from the other file. This file is
 * only able to populate the modal with the old field.
 */
function  populatedAgainModalA()
{

    var wrapper = $(".input_fields_wrap_configurationGroupA"); //Fields wrapper
    var conf = NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration;
    rootCleanningModalModifyNode("A");
    if(conf.length>0)
    {
        document.getElementById("00A").remove();
        $(wrapper).append
        (

            '<div id="00A">Name of existing node:' +
            ' <input class="form-control" id="0A" ' +
            'type="text" name="mytext[]" value="' + conf[0] + '"></div>'


        );
    }

    if(conf.length>1)
    {
        for(var i=1; i<conf.length; i++)
        {
            configurationGroupAcounterA++;
            configurationGroupAcounterBisA++;
            var tempId ="sA"  + configurationGroupAcounterA.toString();
            vectorIdElementA.push(tempId);
            $(wrapper).append
            (
                '<div id="sA'+ configurationGroupAcounterA.toString()
                + '"><br> Name of existing node: <input id="'
                + configurationGroupAcounterA.toString()
                + 'A"class="form-control" type="text" name="mytext[]" value="' + conf[i] + '"/><a href="#" class="remove_field">Remove</a></div>'
            ); //add input box

        }
    }


};




