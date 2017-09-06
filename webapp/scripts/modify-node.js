/**
 * Created by Fenix on 13/02/17.
 */

/**
 * @file This file manage the edit node. In particular is able to active or disable the field during the
 * chose of the operation, it is able to show the right modal to edit or change the setting configuration of the node.
 */

/**
 * @description It is used in order to understand what the user wish to do:
 * 0 = nothing
 * 1 = change name node
 * 2 = change configuration
 * and to visual the right information.
 * @type {number}
 */
var changeVar = 1;

/**
 * @description It manages the DOM.
 */
$(document).ready(function()
{

    /**
     * @description To add or remove the middle box field.
     */
    $('#selectorConfigurationAction').change(addOrRemoveChangeConfigurationSelector);

    /**
     * @description This function is able to add and to remove the selector of the configuration in
     * the "Modify node" action.
     */
    function addOrRemoveChangeConfigurationSelector()
    {
        var middleBoxHtlm= null;
        var ni = null;
        var newdiv = null;
        var divIdName = null;
        var varJquerySelector = $('#selectorConfigurationAction').val();

        if(varJquerySelector.localeCompare("valueChangeConfiguration")==0)
        {
            if(changeVar==1)
                document.getElementById("idChangeNodeInput").remove();



             middleBoxHtlm = ""+
                "<div id=\"idRootAddRemoveAllSelecotor\">"+
                "<select id=\"idSelectorModifyNode\"" +
                 "onchange=\"updateFormEnabledModifyNodeConfiguration()\">"+
                " <!--The id of option is not used --> " +
                "<option id=\"o1c\" value=\"antispam\">antispam</option>" +
                "<option id=\"o2c\" value=\"cache\">cache</option>" +
                "<option id=\"o3c\" value=\"dpi\">dpi</option>" +
                "<option id=\"o4c\" value=\"fieldmodifier\">fieldmodifier</option>" +
                "<option id=\"o5c\" value=\"firewall\">firewall</option>" +
                "<option id=\"o6c\" value=\"endhost\">endhost</option>" +
                "<option id=\"o7c\" value=\"endpoint\">endpoint</option>" +
                "<option id=\"o8c\" value=\"mailserver\">mailserver</option>" +
                "<option id=\"o11c\" value=\"mailclient\">mailclient</option>" +
                "<option id=\"o12c\" value=\"nat\">nat</option>" +
                "<option id=\"o13c\" value=\"vpnaccess\">vpnaccess</option>" +
                "<option id=\"o14c\" value=\"vpnexit\">vpnexit</option>" +
                " <option id=\"o15c\" value=\"webclient\">webclient</option>" +
                "<option id=\"o16c\" value=\"webserver\">webserver</option>" +
                "</select>"+
                "<div style=\"color: #ffffff;\">" +
                "<input id=\"idCheckNodeModify\" type=\"checkbox\" name=\"configureNode\" value=\"yes\"> Configure node </div>";

             ni = document.getElementById('rootChangeConfigurationSelector');

            newdiv = document.createElement('div');

            divIdName = 'idRootAddRemoveAllSelecotor'+'Div';

            newdiv.setAttribute('id',divIdName);

            newdiv.innerHTML = middleBoxHtlm;
            ni.appendChild(newdiv);
            changeVar=2;




        }
        else
            if(varJquerySelector.localeCompare("valueChangeNaneNode")==0)
            {
                if(changeVar==2)
                    document.getElementById("idRootAddRemoveAllSelecotor").remove();

                middleBoxHtlm = ""+
                    "<input id=\"idChangeNodeInput\" type=\"text\" class=\"form-control input-sm\" placeholder=\"New name\">";

                ni = document.getElementById('rootChangeConfigurationSelector');

                newdiv = document.createElement('div');

                divIdName = 'idChangeNodeInput'+'Div';

                newdiv.setAttribute('id',divIdName);

                newdiv.innerHTML = middleBoxHtlm;
                ni.appendChild(newdiv);

                changeVar=1;

            }
            else
                {
                    if(changeVar==1)
                        document.getElementById("idChangeNodeInput").remove();
                    else
                        if(changeVar == 2)
                            document.getElementById("idRootAddRemoveAllSelecotor").remove();

                    changeVar=0;
                }

    }


});

function modifyNode()
{

    var idModifyNodeNameNodeJquery  = $("#idModifyNodeNameNode").val();
   if(searchNodeElement(idModifyNodeNameNodeJquery)==false)
   {
       alertError("The node doesn't exist!");
       return;
   }

   switch ($('#selectorConfigurationAction').val())
   {
       case("valueChangeNaneNode"):
       {
           changeNameNodeFunctionLaunch();
           break;
       }
       case("valueChangeConfiguration"):
       {
           changeConfigurationOfExistNode();
           break;
       }
       case("valueChangeSettingConfiguraion"):
       {
           changeSettingConfiguration();
           break;
       }
       case("valueDeleteSettingConfiguration"):
       {
           deleteSettingConfiguration();
           break;
       }
   }
    //$('#iDmodifyNodeModal').modal('show');
}




function changeNameNodeFunctionLaunch()
{
    var idModifyNodeNameNodeJquery  = $("#idChangeNodeInput").val();
    var oldNameNode = $("#idModifyNodeNameNode").val();

    //Check if the new name is a empty string.
    if(checkNameOfNode(idModifyNodeNameNodeJquery)==false)
        return;


    //Chech if the new name doesn't exit on the NFFG.
    if(searchNodeElement(idModifyNodeNameNodeJquery)==true)
    {
        alertError("The node already exist!");
        return;
    }

    var i = searchIndexElementReturnElment(oldNameNode);

    if(i==-1)
    {
            alertError("The node doesn't exist!");
            return;
    }

    NFFGcyto.nodes[i].data.id=idModifyNodeNameNodeJquery;
    drawWithParametre(NFFGcyto);
    change=1;
    alertSuccess("Modification done!");
}

/**
 *
 */
function changeConfigurationOfExistNode()
{

    if(checksNameAndSetChangedNodeConfigurationIntVarialble()==false)
        return;


        if($('#idCheckNodeModify').is(":checked"))
        {
            flagChangeConfigurationWithNewSetting=true;
            updateOrChangeConfigurationFuncType=1;
            choseconfiguration($("#idSelectorModifyNode").val());
        }
        else
        {
            NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType=$("#idSelectorModifyNode").val();
            NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration = [];
            alertSuccess("Modification done!");
            change=1;
        }
}


function  changeSettingConfiguration()
{
    //Checks if the node exist or not
    if(checksNameAndSetChangedNodeConfigurationIntVarialble()==false)
        return;

    updateOrChangeConfigurationFuncType=0;

    cleanningModal=false;

     if (
            NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("antispam")==0 ||
            NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("cache")==0 ||
            NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("nat")==0
        )
     {
         //modal A launch populate again
         populatedAgainModalA();
     }
    if (NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("firewall")==0)
    {
        //modal B launch populate again
        populatedAgainModalB()

    }
    if (NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("endhost")==0)
    {
        //modal C
        populatedAgainModalC();

    }
    if (NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("mailclient")==0)
    {
        //modal D1
        populatedAgainModalD1();
    }
    if (NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("vpnaccess")==0)
    {
        //modal D2
        populatedAgainModalD2();

    }
    if (NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("vpnexit")==0)
    {
        //modal D3
        populatedAgainModalD3();

    }
    if (NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("webclient")==0)
    {
        //modal D4
        populatedAgainModalD4();

    }
    if (NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("dpi")==0)
    {
        //modal E
        populatedAgainModalE();

    }
    //NO MODAL -> endpoint, webserver, fieldmodifier, mailserver
    if(
        NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("endpoint")==0 ||
        NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("webserver")==0 ||
        NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("fieldmodifier")==0 ||
        NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType.localeCompare("mailserver")==0
    )
        alertError("For " + NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType + " node no configuration available!");


    choseconfiguration(NFFGcyto.nodes[changedNodeConfigurationInt].data.funcType);

}

/**
 * @description It delete all the configuration of the node.
 * Before it checks, by checksNameAndSetChangedNodeConfigurationIntVarialble function
 * if the node that wishes to modify the configuration exist or not.
 * If returns false, the function return, it returns true, it deletes the setting configuration, it refresh the
 * graphic and advises the user by an alert that all operations  were successful.
 */
function deleteSettingConfiguration ()
{

    if(checksNameAndSetChangedNodeConfigurationIntVarialble()==false)
        return;

    NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration = [];
    drawWithParametre(NFFGcyto);
    change=1;
    alertSuccess("Modification done!");
    
}

/**
 * @description This function is able to get from the DOM the name of the node that wishes to modify
 * and to verify if the node exists or no by searchIndexElementReturnElment function.
 * If the node doesn't exist, it advises the user by a sweet alert and returns false.
 * If the node exists, it returns true.
 * @return {boolean} - If the node exist, it returns true otherwise false.
 */
function checksNameAndSetChangedNodeConfigurationIntVarialble()
{
    var oldNameNode = $("#idModifyNodeNameNode").val();
    changedNodeConfigurationInt = searchIndexElementReturnElment (oldNameNode);
    if(changedNodeConfigurationInt==-1)
    {
        alertError("The node doesn't exist!");
        return false;
    }
    return true;

}

