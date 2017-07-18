/**
 * Created by Fenix on 15/02/17.
 */

/**
 * @file This file contains the function in order to clean the right modal.
 */

/**
 *  @description It used when a node is going to modify. It chooses the right modal to be cleaned so it
 * calling the right function in relationship with the modal. The function receives a string in order to know
 * which modal has to clean.
 * Before to do this, the function stocks two global variables into two local variables, because during the
 * cleaning of the modal these two variables loses their values. At the end of the function, it puts inside them
 * the previous value.
 * @param {string} cleanx - It represents the symbol used in the web client to indicate each modal.
 * For instance: modal for antispam is modal A and so on (view main-data.js for more documentation). By this string the
 * function is able to show the right modal.
 */
function rootCleanningModalModifyNode(cleanx)
{
    var changeNodeConf = changedNodeConfigurationInt;
    var updateFunc = updateOrChangeConfigurationFuncType;


    switch (cleanx)
    {
        case("A"):
        {
            configurationGroupAmodelcleanA();
            break;
        }
        case("B"):
        {
            configurationGroupAmodelcleanB();
            break;
        }
        case("C"):
        {
            cleaningModalC();
            break;
        }
        case("D1"):
        {
            cleaningModalGroupD1();
            break;
        }
        case("D2"):
        {
            cleaningModalGroupD2();
            break;
        }
        case("D3"):
        {
            cleaningModalGroupD3();
            break;
        }
        case("D4"):
        {
            cleaningModalGroupD4();
            break;
        }
        case("E"):
        {
            configurationGroupEmodelcleanE();
            break;
        }

    }

    changedNodeConfigurationInt= changeNodeConf;
    updateOrChangeConfigurationFuncType = updateFunc;

}