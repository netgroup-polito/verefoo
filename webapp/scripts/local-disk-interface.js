/**
 * @file This file contains the functions to load and save from the files.
 */

/**
 * @description The function is called when the user wishes to load the a file.
 */
$(document).ready(function () {

    $('#loadFileX').click(function () {
        $('#inputFileX').click();
    });

    document.getElementById('inputFileX')
        .addEventListener('change', readSingleFile, false);

    saveOnFile();
    saveOnFileVerificationPoliy();
});


/**
 * @description Function is able to manage the reading of JSON file. Before it checks if there is a file. If there is
 * not a file, it launches an alert in order to inform the user. After that, it checks if the file is a JSON file.
 * If the file is not a JSON file, it launches a message and it returns. If the test is passed the function does a POST
 * with postGraphToServerFromFile.
 * The server checks if the graph is right or not.
 * @param e
 */
function readSingleFile(e)
{


    var file = e.target.files[0];
    //To delete all the information about the last operation in order to have the possibiliy
    // to reload the same file.
    this.value =null;
    //checks if the file exists
    if (!file)
    {
        alertError("not a file");
        return;
    }
    var reader = new FileReader();
    reader.onload = function (e)
    {
        var NFFGfile = e.target.result;

        var  loadNffgServerFormat =  null;


        //Check if the file is a json file or not. If the json file is not a right file the function return false.
        try {
            loadNffgServerFormat = JSON.parse(NFFGfile);
        } catch(errorFormatationJsonFile) {
            alertError("Error your file is not a json file");
            return false;
        }

        //Post function
        postGraphToServer(loadNffgServerFormat, "2");


    };
    reader.readAsText(file);
}


/**
 * @description The function is able to save the graph into a file with the follow name:
 * nffg[id graph].json.
 */
function saveOnFile() {
    $( "#save" ).click(function(even)
    {
        var object = NFFGfromCytoToVerigraphFlagSave("no");

        object.id = NFFGsServer[indexServer].id;

        var text = JSON.stringify(object, null, 2);//$("#textarea").val();

        var blob = new Blob([text], {type: "text/plain;charset=utf-8"});
        saveAs(blob, "nffg_"+ idGraphVerigraph +".json");

    });



}

/**
 * @description The function is able to save on disk of the pc a json file of the result of the policy.
 * The name of the file contains a timestamp.
 */
function saveOnFileVerificationPoliy() {



    $( "#saveVerificationPolicy" ).click(function(even)
    {

        if(lasVerificationResult.length == 0)
        {
            alertError("It' is not possible to save, beacuese there is not a result");
            return false;
        }

        var text = JSON.stringify(lasVerificationResult[0], null, 2);


        if (/^[\],:{}\s]*$/.test(text.replace(/\\["\\\/bfnrtu]/g, '@').
            replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
            replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

            //the json is ok

            var text1 = JSON.stringify(lasVerificationResult[0], null, 2);//$("#textarea").val();
            //var filename = $("#input-fileName").val();
            var blob = new Blob([text1], {type: "text/plain;charset=utf-8"});
            saveAs(blob, "verify_"+ timeStampCreation() + ".json");

        }else{

            //the json is not ok
            console.log("Is not json");
            alertError("It' is not possible to save in jason format!!!");
            //https://github.com/eligrey/FileSaver.js
            var blob = new Blob([lasVerificationResult[0]], {type: "text/plain;charset=utf-8"});
            saveAs(blob, "verify_"+ timeStampCreation() + ".json");

        }


    });

}

