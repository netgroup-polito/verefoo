/**
 * @file File contain all the function for the firewall,  modal
 */




/**
 * @deprecated
 */
var confJsonB;


var obj = {};
var configurationGroupAcounterB = 0; //initlal text box count
var configurationGroupAcounterBisB=0;
var vectorIdElementB = [];

var idremoveB;

$(document).ready(function() {
    var wrapper         = $(".input_fields_wrap_configurationGroupB"); //Fields wrapper
    var add_buttonB      = $(".add_field_button_configurationGroupB"); //Add button ID

    $(add_buttonB).click(function(e)
    {
        //on add input button click
        e.preventDefault();
        configurationGroupAcounterB++; //text box increment
        configurationGroupAcounterBisB++;
        var tempId ="sB"  + configurationGroupAcounterB.toString();
        vectorIdElementB.push(tempId);
        $(wrapper).append('<div id="sB'+ configurationGroupAcounterB.toString()+ '"><br>Source existing node<input id="' + configurationGroupAcounterB.toString() + 'B"class="form-control" type="text" name="mytext[]"/>' +

            '<br>Destination existing source<input id="' + configurationGroupAcounterB.toString() + 'BB"class="form-control" type="text" name="mytext[]"/>' +
            '<a href="#" class="remove_field">Remove</a></div>'); //add input box


    });

    $(wrapper).on("click",".remove_field", function(e)
    {
        //user click on remove text
        e.preventDefault();
        idremoveB =  $(this).parent('div');

        //console.log("id-----> " +  $(this).parent('div'));
        //console.log( $(this).parent('div'));
        $(this).parent('div').remove();
        for(var iB=0; iB<configurationGroupAcounterB; iB++ )
        {
            //console.log(vectorIdElementB[iB] + "   "  +  idremoveB.attr("id"));
            if(vectorIdElementB[iB].localeCompare(idremoveB.attr("id"))==0)
            {
                vectorIdElementB[iB]="-1";

            }
        }


    });


    //validateconfigurationGroupB
    $(closeconfigurationGroupB).click(function()
    {
        changedNodeConfigurationInt=-1;
        updateOrChangeConfigurationFuncType=0;
        configurationGroupAmodelcleanB();

    });


    $(validateconfigurationGroupB).click(function()
    {
        //Get all value and save into the global variable-> and finish configuration for configurationGroupA
        var countB;


        var jsonVariable = {};

        jsonVariable[$("#0B").val()] = $("#0BB").val();


        for(countB=0; countB<configurationGroupAcounterB; countB ++)
        {

            var tempsB = countB;
            if (vectorIdElementB[countB].localeCompare("-1") == 0);
            else
            {
                tempsB++;


                var confNode1B = $("#" + tempsB.toString() + "B").val();
                var confNode2B = $("#" + tempsB.toString() + "BB").val();

                jsonVariable[confNode1B] = confNode2B;



            }
        }

        if(changedNodeConfigurationInt!=-1)
        {
            NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration = [];

            NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration.push(jsonVariable);
            if(updateOrChangeConfigurationFuncType==1)
                NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType=$("#idSelectorModifyNode").val();
            changedNodeConfigurationInt=-1;
            updateOrChangeConfigurationFuncType=0;
            alertSuccess("Modification done!");
        }
        else
        {
            saveAndDraw();
            NFFGcyto.nodes[NFFGcyto.nodes.length - 1].data.configuration.push(jsonVariable);
        }
        configurationGroupAmodelcleanB();
        change = 1;



    });

});


/**
     * Clean input text of the modal and restores the field numbers shown.
     */
    function configurationGroupAmodelcleanB()
    {

        changedNodeConfigurationInt=-1;
        updateOrChangeConfigurationFuncType=0;

        var countB = 0;// configurationGroupAcounterB;
        for(countB=0; countB<configurationGroupAcounterB; countB ++)
        {
            var tempsB = countB;
            if (vectorIdElementB[countB].localeCompare("-1") == 0);
            else {
                tempsB++;



                var elemB = document.getElementById("sB" + tempsB.toString());
                elemB.parentNode.removeChild(elemB);
            }


        }
        $("#0B").val('');
        $("#0BB").val('');
        configurationGroupAcounterB = 0; //initlal text Aox count
        configurationGroupAcounterBisB=0;
        vectorIdElementB = [];

    }






