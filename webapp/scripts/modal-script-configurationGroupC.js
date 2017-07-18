/**
 * @file File contains all the functions for the endhost modal. In this case there are not functions.
 */


$(document).ready(function() {

    //Get the value from the modal
    $("#validateconfigurationGroupC").click(function()
    {

        //Create a local variable and to save the value inside
        var jsonVariable = {};


        //HTTP body
        if ($('#0CC1CB').is(":checked"))
        {
            // it is checked
            jsonVariable["body"] = $("#0CC1").val();
        }

        //Sequence number
        if ($('#0CC2CB').is(":checked"))
        {
            // it is checked
            var s = parseInt($("#0CC2").val());

            if(isNaN(parseFloat(($("#0CC2").val()))))
            {
                alertError("Your sequence number is not of sequence of number!");
                return false;
            }
            jsonVariable["sequence"] = parseInt($("#0CC2").val());
        }

        //Protocol
        if ($('#0CC3CB').is(":checked"))
        {
            // it is checked
            jsonVariable["protocol"] = $("#0CC3").val();
        }

        //E-mail sender
        if ($('#0CC4CB').is(":checked"))
        {
            // it is checked
            jsonVariable["email_from"] = $("#0CC4").val();
        }

        //URL
        if ($('#0CC5CB').is(":checked"))
        {
            // it is checked
            jsonVariable["url"] = $("#0CC5").val();
        }

        //Options
        if ($('#0CC6CB').is(":checked"))
        {
            // it is checked
            jsonVariable["options"] = $("#0CC6").val();
        }

        //Destination node
        if ($('#0CC7CB').is(":checked"))
        {
            // it is checked
            jsonVariable["destination"] = $("#0CC7").val();
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
            //Save also the configuragion.
            NFFGcyto.nodes[NFFGcyto.nodes.length - 1].data.configuration.push(jsonVariable);
        }
        cleaningModalC();
        change = 1;

    });

    $("#closeconfigurationGroupC").click(function()
    {
        cleaningModalC();

    });

});

function cleaningModalC()
{
    changedNodeConfigurationInt=-1;
    updateOrChangeConfigurationFuncType=0;

    //HTTP body
    if ($('#0CC1CB').is(":checked"))
    {
        $('#0CC1CB').removeAttr('disabled').prop('checked', false);

        $("#0CC1").attr("disabled", "disabled");
        var ta = document.getElementById('0CC1');
        ta.value = "";

    }

    //Sequence number
    if ($('#0CC2CB').is(":checked"))
    {
        $('#0CC2CB').removeAttr('disabled').prop('checked', false);

        $('#0CC2').attr("disabled", "disabled");
        var ta = document.getElementById('0CC2');
        ta.value = "";
    }

    //Protocol
    if ($('#0CC3CB').is(":checked"))
    {
        $('#0CC3CB').removeAttr('disabled').prop('checked', false);

        $('#0CC3').attr("disabled", "disabled");
        var ta = document.getElementById('0CC3');
        ta.value = "";
    }

    //E-mail sender
    if ($('#0CC4CB').is(":checked"))
    {
        $('#0CC4CB').removeAttr('disabled').prop('checked', false);

        $('#0CC4').attr("disabled", "disabled");
        var ta = document.getElementById('0CC4');
        ta.value = "";
    }

    //URL
    if ($('#0CC5CB').is(":checked"))
    {
        $('#0CC5CB').removeAttr('disabled').prop('checked', false);

        $('#0CC5').attr("disabled", "disabled");
        var ta = document.getElementById('0CC5');
        ta.value = "";
    }

    //Options
    if ($('#0CC6CB').is(":checked"))
    {
        $('#0CC6CB').removeAttr('disabled').prop('checked', false);

        $('#0CC6').attr("disabled", "disabled");
        var ta = document.getElementById('0CC6');
        ta.value = "";
    }

    //Destination node
    if ($('#0CC7CB').is(":checked"))
    {
        $('#0CC7CB').removeAttr('disabled').prop('checked', false);

        $('#0CC7').attr("disabled", "disabled");
        var ta = document.getElementById('0CC7');
        ta.value = "";
    }

}



