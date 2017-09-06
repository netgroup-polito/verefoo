/**
 * @file Various utilities.
 */


$(document).ready(function () {
    $('[data-toggle="tooltip"]').tooltip({container: 'body', trigger: 'hover'});

});



/**
 * JavaScript HTML binding for pusating elements
 * @deprecated This function has been used for some experiments.
 * @param element
 */
function pulsate(element) {
    $(element || this).animate({opacity: 0}, 5000, function () {
        $(this).animate({opacity: 1}, 5000, pulsate);
    });
}

/**
 * @description Server polling with HEAD HTTP method every 5 seconds
 */
function serverPolling() {

    //http://127.0.0.1:8080/verigraph/api/graphs
    $.ajax({
        url: "http://" + rootAddressServer + "/verigraph", //yes
        type: "HEAD",
        success: function ()
        {
            console.log("server online");
            $("#serverOffline").hide();
            $("#serverOnline").show();
            serverOnline=true;

        },
        error: function (risposta)
        {
        	console.log("server offline");
            $("#serverOnline").hide();
            $("#serverOffline").show();
            serverOnline=false;

        }
    });


    setTimeout(serverPolling, 3000);
}




/**
 * @description It creates a string-timestamp.
 * @return {string} - Returns a string that it is formed by the timestamp.
 */
function timeStampCreation ()
{
    var time = new Date();
    var year = time.getFullYear();
    var month = time.getMonth()+1;
    var date1 = time.getDate();
    var hour = time.getHours();
    var minutes = time.getMinutes();
    var seconds = time.getSeconds();
    return ("time "+  year + "-" + month+"-"+date1+" "+hour+":"+minutes+":"+seconds);
}


/**
 * @description Checks is the JSON is syntactically right for JSON.
 * @param {string} json - The JSON to be verified like this:
 * {
 *"id": 1,
 *"nodes":
 *      [
 *     ]
 *}
 * @return {boolean} - False if it is not right JSON, true if it is right JSON.
 */
 function isValidJsonFunction(json)
 {
    try
    {
        JSON.parse(json);
        return true;
    }
    catch (e)
    {
        return false;
    }
 }


/**
 * It opens a new page for doing on swagger.
 * @warning Swagger has a problem about the address: It only works with localhost address.
 */
function linkToSwagger()
 {
     window.open("http://" + rootAddressServer + "/verigraph/api-docs/index.html");
 }