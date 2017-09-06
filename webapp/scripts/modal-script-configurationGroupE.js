/**
 * @file File contain all the function for the dpi modal
 */



var configurationGroupEcounterE = 0; //initlal text box count
var configurationGroupEcounterBisE=0;
var vectorIdElementE = [];

var idremoveE;

$(document).ready(function() {
    var wrapper         = $(".input_fields_wrap_configurationGroupE"); //Fields wrapper
    var add_buttonE      = $(".add_field_button_configurationGroupE"); //Add button ID

    $(add_buttonE).click(function(e)
    {
        //on add input button click
        e.preventDefault();
        configurationGroupEcounterE++; //text box increment
        configurationGroupEcounterBisE++;
        var tempId ="sE"  + configurationGroupEcounterE.toString();
        vectorIdElementE.push(tempId);
        $(wrapper).append('<div id="sE'+ configurationGroupEcounterE.toString()+ '"><br> List of forbidden words: <input id="' + configurationGroupEcounterE.toString() + 'E"class="form-control" type="text" name="mytext[]"/><a href="#" class="remove_field">Remove</a></div>'); //add input box


    });

    $(wrapper).on("click",".remove_field", function(e)
    {
        //user click on remove text
        e.preventDefault();
        idremoveE =  $(this).parent('div');


        $(this).parent('div').remove();
        for(var iE=0; iE<configurationGroupEcounterE; iE++ )
        {

            if(vectorIdElementE[iE].localeCompare(idremoveE.attr("id"))==0)
            {
                vectorIdElementE[iE]="-1";
            }
        }


    });


    //validateconfigurationGroupE
    $(closeconfigurationGroupE).click(function()
    {
        configurationGroupEmodelcleanE();

    });


    $(validateconfigurationGroupE).click(function()
    {
        //Get all value and save into the gloEal variable-> and finish configuration for configurationGroupE

        var vettoreE = [];
        var countE = configurationGroupEcounterE;

        vettoreE.push($("#0E").val());

       


        for(countE=0; countE<configurationGroupEcounterE; countE ++)
        {

            var tempsE = countE;
            if (vectorIdElementE[countE].localeCompare("-1") == 0);
            else
            {
                tempsE++;

                var nameOfNodeE = $("#" + tempsE.toString() + "E").val();

                vettoreE.push(nameOfNodeE);

            }
        }

        if(changedNodeConfigurationInt!=-1)
        {
            NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration = vettoreE;
            if(updateOrChangeConfigurationFuncType==1)
                NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType = $("#idSelectorModifyNode").val();
            alertSuccess("Modification done!");
        }
        else
        {
            saveAndDraw();
            NFFGcyto.nodes[NFFGcyto.nodes.length - 1].data.configuration = vettoreE;
        }

        change = 1;
        configurationGroupEmodelcleanE();
    });




});

/**
 * Clean input text of the modal and restores the field numbers shown.
 */
function configurationGroupEmodelcleanE()
{

    changedNodeConfigurationInt=-1;
    updateOrChangeConfigurationFuncType=0;

    var countE;
    for(countE=0; countE<configurationGroupEcounterE; countE ++)
    {
        var tempsE = countE;
        if (vectorIdElementE[countE].localeCompare("-1") == 0);
        else {
            tempsE++;

            var elemE = document.getElementById("sE" + tempsE.toString());
            elemE.parentNode.removeChild(elemE);
        }


    }
    $("#0E").val('');
    configurationGroupEcounterE = 0; //initlal text Aox count
    configurationGroupEcounterBisE=0;
    vectorIdElementE=[];


}





