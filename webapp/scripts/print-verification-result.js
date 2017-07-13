/**
 *@file In this file there is all the code to print the json on the sidebar.
 */




/**
 * @description Jquery function for index.htlm
 */
$(document).ready(function () {



    $("#openCloseVerificationPolicy").click(function ()
    {
            $('#verificationIdModal').modal('show');

    });


    /**
     * @description It gets a  JSON.stringify(object) and it returns a json-htlm well formed with also che colour.
     * @param json
     * @return {string|XML}
     */
    function syntaxHighlightVerificationPolicy(json)
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

    //--> http://stackoverflow.com/questions/4335069/calling-a-javascript-function-from-another-js-file
    /**
     The problem is, that menuHoverStart is not accessible outside of its scope (which is defined by the .ready() callback function in file #1). You need to make this function available in the global scope (or through any object that is available in the global scope):

     function menuHoverStart(element, topshift, thumbchange) {
        // ...
    }

     $(document).ready(function() {
        // ...
    });
     If you want menuHoverStart to stay in the .ready() callback, you need to add the function to the global object manually (using a function expression):

     $(document).ready(function() {
        window.menuHoverStart = function (element, topshift, thumbchange) {
            // ...
        };
        // ...
    });
     **/


    //writeJsonCurrentElement();
    window.writeJsonCurrentElementVerificationPolicy = function ()
    {


        if($("#" + "sDVerificationPolicy").length == 0)
        {

        }
        else
        {
            var elemA = document.getElementById("sDVerificationPolicy");
            elemA.parentNode.removeChild(elemA);
            //console.log("2");

        }
        delLat=true;



        var str = JSON.stringify(lasVerificationResult[0], undefined, 4);



        var wrapper         = $(".jsonVerify"); //Fields wrapper
        stringJsonGraphVerigraphSidebar = syntaxHighlightVerificationPolicy(str);
        $(wrapper).append('<div id="sDVerificationPolicy"><br>' + stringJsonGraphVerigraphSidebar  + '</a></div>'); //add input box


    }
});
