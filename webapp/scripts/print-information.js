/**
 *@file In this file there is all the code to print the json on the sidebar.
 */



/**
 * @description Thi variable describes if the sidebar is visible or not.
 */
var delLat;
delLat = false;
var stringJsonGraphVerigraphSidebar; //before was 's'

/**
 * @description Jquery function for index.htlm
 */
$(document).ready(function () {

    /**
     * @description To open the sidebar. The function gets the global variable (delLat) and it checks if it's
     * true or false. If true the siderbar will be closed and the global variable sets to false and vice-versa when
     * the global is false and the sidebar will be open.
     */
    $("#openClose").click(function ()
    {
        if (flagJsonOpenClose == false)
        {
            writeJsonCurrentElement();
            flagJsonOpenClose = true;
            openNav();

        }
        else {
            flagJsonOpenClose = false;
            closeNav();

        }

    });

    /**
     * @description To open the sidebar. The function gets the global variable (delLat) and it checks if it's
     * true or false. If true the siderbar will be closed and the global variable sets to false and vice-versa when
     * the global is false and the sidebar will be open.
     *
     * There are two functions for the same actions because using jquery it's not possible to have
     * the same id index.htlm.
     */
    $("#openCloseX").click(function ()
    {
        if (flagJsonOpenClose == false)
        {
            writeJsonCurrentElement();
            flagJsonOpenClose = true;
            openNav();

        }
        else {
            flagJsonOpenClose = false;
            closeNav();

        }

    });

    /**
     * @description dimension of sidebar (sidebarId)
     */
    function openNav() {
        document.getElementById("sidebarId").style.width = "50%";
    }

    function closeNav() {
        document.getElementById("sidebarId").style.width = "0";
    }



    /**
     * @description It gets a  JSON.stringify(object) and it returns a json-htlm well formed with also che colour.
     * @param {JSON} json - Json to convert.
     * @return {string|XML} - Json that has translated.
     */
    function syntaxHighlight(json)
    {
        json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
            var cls = 'number';
            if (/^"/.test(match)) {
                if (/:$/.test(match)) {
                    cls = 'key';
                } else {
                    cls = 'string';
                }
            } else if (/true|false/.test(match)) {
                cls = 'boolean';
            } else if (/null/.test(match)) {
                cls = 'null';
            }
            return '<span class="' + cls + '">' + match + '</span>';
        });
    }

    /**
     * @description It writes json into the sidebar of the current graph.
     */
    window.writeJsonCurrentElement = function ()
    {

        if($("#" + "sD").length == 0)
        {
            //console.log("3");
        }
        else
            {
            var elemA = document.getElementById("sD");
            elemA.parentNode.removeChild(elemA);
            //console.log("2");

        }
        delLat=true;


        var objcet = NFFGfromCytoToVerigraphFlagSave("no");
        objcet.id = NFFGsServer[indexServer].id;
        console.log(objcet);
        var str1  = JSON.stringify(objcet, undefined, 4);



        var wrapper         = $(".json"); //Fields wrapper

        stringJsonGraphVerigraphSidebar = syntaxHighlight(str1);
        $(wrapper).append('<div id="sD"><br>' + stringJsonGraphVerigraphSidebar  + '</a></div>'); //add input box

        //Polling draw information. It starts during the first visualization
        setTimeout(writeJsonCurrentElement, 3000);
    }
});
