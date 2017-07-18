/**
 * Created by Renna on 12/01/17.
 */

/**
 * @file This file is used for checking the field of the policy input and to launch the right function
 * in order to communicate to the server the policy that wishes to verify.
 */

/**
 * To know if middlebox is on DOM or not.
 * @type {boolean}
 */
var flagMiddleBox = false;

/**
 * @description Used during verifications policy. It's able to disable middle box field when the selector indicates
 * reachability policy. In other case this field is not disable.
 * In order to do it there is one function: verificationSelect that is able to watch the selector
 * and to add or remove the middle box field.
 * When the verification button is pushed, all the active field are checked by one of these two functions:
 * checkInputValueVerifyNoReachability (if the middle box is not present) or checkInputValueVerify (if the middle
 * box is present). These two functions are able to checked two things:
 * 1)If the field are empty or no
 * 2)If the field are not empty if the name of the node exist or not.
 * If the function sees an error or more errors
 * (ex. a field is empty or one filed is empty and a name of node is not valid) it alerts to the user by a
 * alert.
 * If there are not errors, it build an ulr
 * (using one of two function: creationUrlWithoutReachability (if there is not middle box) or
 * creationUrlWithReachability (if there is middle box field))
 * and to checks if the graph is has been saved or not. If the graph is not saved, it advises the user by
 * updateGraphForVerificationPolicy function otherwise it launches the verification function (verifiyPolicy).
 */
$(document).ready(function()
{

    /**
     * @description To add or remove the middle box field.
     */
    $('#verificationSelect').change(addOrRemoveMidlebox);

    //Get value of the verification
    /**
     * @description When the user click on button in order to verify a policy, the function starts.
     * All the active field are checked by one of these two functions:
     * checkInputValueVerifyNoReachability (if the middle box is not active) or checkInputValueVerify (if the middle
     * box is active). These two functions are able to checked two things:
     * 1)If the field are empty or no
     * 2)If the field are not empty if the name of the node exist or not.
     * If the function sees an error or more errors
     * (ex. a field is empty or one filed is empty and a name of node is not valid) it alerts to the user by a
     * alert.
     * If there are not errors, it build an ulr
     * (using one of two function: creationUrlWithoutReachability (if there is not middle box) or
     * creationUrlWithReachability (if there is middle box field))
     * and it launch a function that is able to communicated with the server.
     *
     */
    $("#verificationStart").click(function ()
    {

        var graphIdOnServer = idGraphVerigraph; //idVerification;



        var verificatonSelect = $('#verificationSelect').val();

          var verifySourceNode = $('#verifyIdSourceNode').val();
          var verifyDestinatonNode = $('#verifyIdDestinationNode').val();

        if(verificatonSelect.localeCompare("reachability")==0)
        {

            if(false == checkInputValueVerifyNoReachability(verifySourceNode, verifyDestinatonNode))
                return false;
            verifyPolicyFunctionLaunch
            (
                creationUrlWithoutReachability
                (
                    verificatonSelect,
                    graphIdOnServer,
                    verifySourceNode,
                    verifyDestinatonNode
                )
            );

        }
        else
        {
            var verifyMiddleBox = $('#verifyMiddleBox').val();
            if(false == checkInputValueVerify(verifySourceNode, verifyDestinatonNode, verifyMiddleBox))
                return false;

            verifyPolicyFunctionLaunch
            (
                creationUrlWithReachability
                (
                    verificatonSelect,
                    graphIdOnServer,
                    verifySourceNode,
                    verifyDestinatonNode,
                    verifyMiddleBox
                )
            );

        }


    });
});

/**
 * @description To build a ulr without the middle box field in order to communicate with the server. The url is the
 * second part of the ulr. The first part of the url, where is the address of ther server is in another function. This
 * function is able to add these two url: address server + parameters ulr (or second part of ulr).
 * @param {string} verificatonSelect - The selector of the verification.
 * @param {string} graphIdOnServer - The graph id.
 * @param {string} verifySourceNode - The source node.
 * @param {string} verifyDestinatonNode - The destination node.
 * @return {string} second part of the url
 */
function creationUrlWithoutReachability(verificatonSelect, graphIdOnServer, verifySourceNode, verifyDestinatonNode)
{

    return graphIdOnServer +
        "/policy?source=" +
        verifySourceNode +
        "&destination=" +
        verifyDestinatonNode +
        "&type=" +
        verificatonSelect;



}

/**
 * @description To build a ulr with the middle box field in order to communicate with the server.
 * The url is the second part of the ulr.
 * The first part of the url, where is the address of ther server is in another function. This
 * function is able to add these two url: address server + parameters ulr (or second part of ulr).
 * @param {string} verificatonSelect - The selector of the verification.
 * @param {string} graphIdOnServer - The graph id.
 * @param {string} verifySourceNode - The source node.
 * @param {string} verifyDestinatonNode - The destination node.
 * @param {string} verifyMiddleBox - Middle box field.
 * @return {string} second part of the url.
 */

function creationUrlWithReachability
(
    verificatonSelect, graphIdOnServer,  verifySourceNode, verifyDestinatonNode, verifyMiddleBox
)
{

    return graphIdOnServer +
        "/policy?source=" +
        verifySourceNode +
        "&destination=" +
        verifyDestinatonNode +
        "&type=" +
        verificatonSelect +
        "&middlebox=" +
        verifyMiddleBox;



}


/**
 * @description To checks if the graph is has been saved or not. If the graph is not saved, it advises the user by
 * updateGraphForVerificationPolicy function otherwise it launches the verification function (verifiyPolicy).
 * updateGraphForVerificationPolicy and  verifiyPolicy function receive:
 * - part of the url that contains id graph, source, destination and midlebox (if the verification is not
 * reachability)
 * - source node of the verification
 * - destination node of ther verification
 * @param {string} secondPartUrl - Url to communicate with the server.
 */
function  verifyPolicyFunctionLaunch(secondPartUrl)
{


    if(change == 1)
    {
        updateGraphForVerificationPolicy(secondPartUrl);
    }
    else
    {
        verifiyPolicy(secondPartUrl);
    }
}


/**
 * @description It is able to add or remove the middle box watching the selector. If the
 * selector indicates "reachability", it removes the middle box field, otherwise it adds this field.
 * When the web site starts, the selector points on reachability policy so the field is obviously not present.
 */
function addOrRemoveMidlebox ()
{


    if($('#verificationSelect').val().localeCompare("reachability")==0)
    {
       // $('#verifyMiddleBox').prop('checked', true).attr('disabled', 'disabled');
        document.getElementById("divMiddleBox").remove();
        flagMiddleBox=false;


    }
    else
    {
        if(flagMiddleBox==false)
        {
            //$('#verifyMiddleBox').removeAttr('disabled');
            var middleBoxHtlm = "<div id=\"divMiddleBox\"><li data-toggle=\"tooltip\" title=\"Midle box (required)\"" +
                "data-placement=\"right\">" +
                "<input id=\"verifyMiddleBox\" type=\"text\" class=\"form-control input-sm\"" +
                "placeholder=\"Middle box\"> </li> </div>";


            var ni = document.getElementById('middleBoxRoot');


            var newdiv = document.createElement('div');

            var divIdName = 'middleBox' + 'Div';

            newdiv.setAttribute('id', divIdName);

            newdiv.innerHTML = middleBoxHtlm;
            ni.appendChild(newdiv);

            flagMiddleBox = true;
        }




    }


}

/**
 * @description It is able to checks if the fields (source and destination node) are empty and if they exist.
 * If one of them or plus of one don't exist or if is/are empty, the function advise the user by alert about
 * error or errors.
 * @param {string} verifySourceNode - Name of source node.
 * @param {string} verifyDestinationNode - Name of destination node.
 * @return {boolean} if the checks is right (true) or failure (false).
 */
function checkInputValueVerifyNoReachability  (verifySourceNode, verifyDestinationNode)
{
    var x=0, y=0;
    var ex=0, ey=0;

    // Chech if the node esxist or not
    //searchNodeElement: 1 if the node does not exist, 0 if the node exists
    if(verifySourceNode.localeCompare("")==0)
        ex=1;
    else
    if (false == searchNodeElement(verifySourceNode))
        x=1;

    if(verifyDestinationNode.localeCompare("")==0)
        ey=1;
    else
    if (false == searchNodeElement(verifyDestinationNode))
        y=1;


    if(ex==1 && ey==1)
    {
        alertError("Error: source node and desitnation node are empty!!!");
        return false;
    }

    if(ex==0 && ey == 1)
    {
        if(x==0)
        {
            alertError("Error: destination node field is empty!");
            return false;
        }
        else
        {
                alertError("Error: destination node field is empty and source node doesn't exist!");
                return false;
        }
    }

    if(ex==1 && ey == 0)
    {
       if(y==0)
        {
            alertError("Error: source node field is empty!");
            return false;
        }
        else
        {
            alertError("Error: source node field is empty and destination node doesn't exist!");
            return false;
        }

    }

    if(x==0 && y==1)
    {
        alertError("Error: destination node don't exist!");
        return false;
    }
    if(x==1 && y==0)
    {
        //Before -> alertError("It is not possible to verify the graph because the source node don't exist!");
        alertError("Error: the source node don't exist!");
        return false;
    }

    if(x==1 && y==1)
    {
        alertError("Error: the source and destination " +
            "node don't exist!!");
        return false;
    }


    return true;

}


/**
 * @description It is able to checks if the fields (source and destination node and middle box) are empty and if they exist.
 * If one of them or plus of one don't exist or if is/are empty, the function advise the user by alert about
 * error or errors.
 * @param {string} verifySourceNode - Source node.
 * @param {string} verifyDestinationNode - Destination node.
 * @param {string} verifyMiddleBox - Midle box verification.
 * @return {boolean} if the checks is right (true) or failure (false).
 */
function checkInputValueVerify(verifySourceNode, verifyDestinationNode, verifyMiddleBox)
{
    var x=0, y=0, z=0;
    var ex=0, ey=0, ez=0;

    // Chech if the node esxist or not
    //searchNodeElement: 1 if the node does not exist, 0 if the node exists
    if(verifySourceNode.localeCompare("")==0)
        ex=1;
    else
        if (false == searchNodeElement(verifySourceNode))
            x=1;

    if(verifyDestinationNode.localeCompare("")==0)
        ey=1;
    else
        if (false == searchNodeElement(verifyDestinationNode))
            y=1;


    if(verifyMiddleBox.localeCompare("")==0)
        ez=1;
    else
    if (false == searchNodeElement(verifyMiddleBox))
        z=1;


    if(ex==1 && ey == 1 && ez ==1)
    {

        alertError("Error: the source, destination node and middle box are empty");
        return false;
    }


    if(ex==1 && ey == 1 && ez ==0)
    {

       if(z==1)
       {
           alertError("Error: the source and destination node are empty and middle box doesn't exist!");
           return false;
       }
       else
       {
           alertError("Error: the source and destination node are empty!");
           return false;

       }
    }



    if(ex==1 && ey == 0 && ez ==1)
    {

        if(y==1)
        {
            alertError("Error: the source node and middle box are empty and destination node doesn't exist!");
            return false;
        }
        else
        {
            alertError("Error: the source node and and middle box are empty!");
            return false;

        }
    }




    if(ex==0 && ey == 1 && ez ==1)
    {

        if(x==1)
        {
            alertError("Error: the destination node and middle box are empty and source node doesn't exist!");
            return false;
        }
        else
        {
            alertError("Error: the destination node and and middle box are empty!");
            return false;

        }
    }





    if(ex==0 && ey == 0 && ez ==1)
    {

        if(x==1 && y == 1)
        {
            alertError("Error: middle box is empty and source node and destination node don't exist!");
            return false;
        }
        if(x==0 && y == 1)
        {
            alertError("Error: middle box is empty and destination node doesn't exist!");
            return false;
        }
        if(x==1 && y == 0)
        {
            alertError("Error: middle box is empty and source node doesn't exist!");
            return false;
        }
        if(x==0 && y == 0)
        {
            alertError("Error: middle box is empty!");
            return false;
        }

    }




    if(ex==0 && ey == 1 && ez ==0)
    {

        if(x==1 && z == 1)
        {
            alertError("Error: destination node is empty and source node and middle box  don't exist!");
            return false;
        }
        if(x==0 && z == 1)
        {
            alertError("Error:destination node is empty and middle box doesn't exist!");
            return false;
        }
        if(x==1 && z == 0)
        {
            alertError("Error: destination node is empty and source node doesn't exist!");
            return false;
        }
        if(x==0 && z == 0)
        {
            alertError("Error: destination node is empty!");
            return false;
        }

    }


    if(ex==1 && ey == 0 && ez ==0)
    {

        if(y==1 && z == 1)
        {
            alertError("Error: source node is empty and destination node and middle box don't exist !");
            return false;
        }
        if(y==0 && z == 1)
        {
            alertError("Error: source  node is empty and middle box doesn't exist!");
            return false;
        }
        if(y==1 && z == 0)
        {
            alertError("Error: source node is empty and destination node doesn't exist !");
            return false;
        }
        if(y==0 && z == 0)
        {
            alertError("Error: source node is empty!");
            return false;
        }
    }

    if(ex==0 && ey == 0 && ez ==0)
    {

        if(x==1 && y == 1 && z == 1)
        {
            alertError("Error: source, destination node  and middle box don't exist!");
            return false;
        }
        if(x==1 && y == 1 && z == 0)
        {
            alertError("Error: source and destination node  don't exist!");
            return false;
        }
        if(x==1 && y == 0 && z == 1)
        {
            alertError("Error: source node and middle box  don't exist!");
            return false;
        }
        if(x==0 && y == 1 && z == 1)
        {
            alertError("Error: destination node and middle box  don't exist!");
            return false;
        }
        if(x==0 && y == 0 && z == 1)
        {
            alertError("Error: middle box  doesn't exist!");
            return false;
        }
        if(x==0 && y == 1 && z == 0)
        {
            alertError("Error: destination node doesn't exist!!");
            return false;
        }
        if(x==1 && y == 0 && z == 0)
        {
            alertError("Error: source node doesn't exist!!");
            return false;
        }


    }

    return true;

}