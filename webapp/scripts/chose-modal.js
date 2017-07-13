/**
 * @file Manage modal for the node configuration.
 */

/**
 * @description Manages the right modal to be shown basing onto the "configuration" parameter.
 * Before that, it checks, if it is necessary to clean the modal by cleanningModal variable,
 * if it is true, it cleans launch a specific function in relationship of the modal.
 * At the end it sets two variables (changedNodeConfigurationInt and updateOrChangeConfigurationFuncType)
 * if they are to be set. They have been memorises in two
 * temporary variables (changeNodeConf and updateFunc) at the beginning of the function.
 * @param {string} configuration - The selected node type (selected from the select element).
 */
function choseconfiguration(configuration)
{
    var changeNodeConf = changedNodeConfigurationInt;
    var updateFunc = updateOrChangeConfigurationFuncType;


    switch (configuration) {
        //Group A antispam , cache, and nat  modal
        case "antispam": {
            if(cleanningModal==true)
            {
                configurationGroupAmodelcleanA();
                cleanningModal = false;
            }
            $('#configurationGroupAmodalA').modal('show');
            break;
        }
        case "cache": {
            if(cleanningModal==true)
            {
                configurationGroupAmodelcleanA();
                cleanningModal = false;
            }
            $('#configurationGroupAmodalA').modal('show');
            break;
        }
        case "nat": {
            if(cleanningModal==true)
            {
                configurationGroupAmodelcleanA();
                cleanningModal = false;
            }
            $('#configurationGroupAmodalA').modal('show');
            break;
        }

        //Group B firewall modal
        case "firewall": {
            if(cleanningModal==true)
            {
                configurationGroupAmodelcleanB();
                cleanningModal = false;
            }
            $('#configurationGroupBmodalB').modal('show');
            break;
        }
        //Group C endhost  modal
        case "endhost": {
            if(cleanningModal==true)
            {
                cleaningModalC();
                cleanningModal = false;
            }
            $('#configurationGroupBmodalC').modal('show');
            break;
        }

        //Group Dx  mailclient, vpnaccess, vpnexit, webclient modal configuration
        case "mailclient": {
            if(cleanningModal==true)
            {
                cleaningModalGroupD1();
                cleanningModal = false;
            }
            $('#configurationGroupDmodalD1').modal('show');
            break;
        }

        case "vpnaccess": {
            if(cleanningModal==true)
            {
                cleaningModalGroupD2();
                cleanningModal = false;
            }
            $('#configurationGroupDmodalD2').modal('show');
            break;
        }
        case "vpnexit": {
            if(cleanningModal==true)
            {
                cleaningModalGroupD3();
                cleanningModal = false;
            }
            $('#configurationGroupDmodalD3').modal('show');
            break;
        }
        case "webclient": {
            if(cleanningModal==true)
            {
                cleaningModalGroupD4();
                cleanningModal = false;
            }
            $('#configurationGroupDmodalD4').modal('show');
            break;
        }

        //Group E  dpi modal configuration
        case "dpi": {
            if(cleanningModal==true)
            {
                configurationGroupEmodelcleanE();
                cleanningModal = false;
            }
            $('#configurationGroupEmodalE').modal('show');
            break;
        }

        //No configuration endpoint
        case "endpoint": {
            break;
        }

        case "webserver": {
            //$('#configurationGroupBmodalB').modal('show');
            break;
        }

        case "mailserver": {
            //$('#configurationGroupBmodalB').modal('show');
            break;
        }

        case "fieldmodifier": {
            //$('#configurationGroupBmodalB').modal('show');
            break;
        }


    }

    if(flagChangeConfigurationWithNewSetting ==true)
    {
        flagChangeConfigurationWithNewSetting=false;

        changedNodeConfigurationInt= changeNodeConf;
        updateOrChangeConfigurationFuncType = updateFunc;

    }

}

