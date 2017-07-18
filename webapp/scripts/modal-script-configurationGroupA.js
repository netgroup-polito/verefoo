/**
 * @file File contains all the functions to manipulate modal for the antispam , cache and nat node
 * during the creation node.
 */



var configurationGroupAcounterA = 0; //initlal text box count
var configurationGroupAcounterBisA=0;
var vectorIdElementA = [];

var idremoveA;

$(document).ready(function() {
    var wrapper         = $(".input_fields_wrap_configurationGroupA"); //Fields wrapper
    var add_buttonA      = $(".add_field_button_configurationGroupA"); //Add button ID

    $(add_buttonA).click(function(e)
    {
        //on add input button click
        e.preventDefault();
        configurationGroupAcounterA++; //text box increment
        configurationGroupAcounterBisA++;
        var tempId ="sA"  + configurationGroupAcounterA.toString();
        vectorIdElementA.push(tempId);
        $(wrapper).append('<div id="sA'+ configurationGroupAcounterA.toString()+ '"><br> Name of existing node: <input id="' + configurationGroupAcounterA.toString() + 'A"class="form-control" type="text" name="mytext[]"/><a href="#" class="remove_field">Remove</a></div>'); //add input box


    });

    $(wrapper).on("click",".remove_field", function(e)
    {
        //user click on remove text
        e.preventDefault();
        idremoveA =  $(this).parent('div');

        $(this).parent('div').remove();
        for(var iA=0; iA<configurationGroupAcounterA; iA++ )
        {
            if(vectorIdElementA[iA].localeCompare(idremoveA.attr("id"))==0)
            {
                vectorIdElementA[iA]="-1";
            }
        }
    });


    //validateconfigurationGroupA
    $(validateconfigurationGroupA).click(function()
    {
        //Get all value and save into the gloAal variable-> and finish configuration for configurationGroupA

        var vettoreA = [];
        var countA = configurationGroupAcounterA;

        vettoreA.push($("#0A").val());


        for(countA=0; countA<configurationGroupAcounterA; countA ++)
        {

            var tempsA = countA;
            if (vectorIdElementA[countA].localeCompare("-1") == 0);
            else
            {
                tempsA++;

                var nameOfNodeA = $("#" + tempsA.toString() + "A").val();

                vettoreA.push(nameOfNodeA);


            }
        }
        if(changedNodeConfigurationInt!=-1)
        {
            NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration = vettoreA;
            if(updateOrChangeConfigurationFuncType==1)
                NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType = $("#idSelectorModifyNode").val();
            updateOrChangeConfigurationFuncType=0;
            configurationGroupAmodelcleanA();
            changedNodeConfigurationInt=-1;
            alertSuccess("Modification done!");
        }
        else
            {
                saveAndDraw();
                NFFGcyto.nodes[NFFGcyto.nodes.length - 1].data.configuration = vettoreA;
                configurationGroupAmodelcleanA();
            }
        change = 1;

    });

    $(closeconfigurationGroupA).click(function()
    {
        configurationGroupAmodelcleanA();


    });




});

    /**
     * @description Clean input text of the modal and restores the field numbers shown.
     */
    function configurationGroupAmodelcleanA()
    {

        changedNodeConfigurationInt=-1;
        updateOrChangeConfigurationFuncType=0;

        var countA = configurationGroupAcounterA;
        for(countA=0; countA<configurationGroupAcounterA; countA ++)
        {
            var tempsA = countA;
            if (vectorIdElementA[countA].localeCompare("-1") == 0);
            else {
                tempsA++;


                //console.log(countA);

                var elemA = document.getElementById("sA" + tempsA.toString());
                elemA.parentNode.removeChild(elemA);
            }


        }
        $("#0A").val('');
        configurationGroupAcounterA = 0;
        configurationGroupAcounterBisA=0;
        vectorIdElementA=[];

    }








