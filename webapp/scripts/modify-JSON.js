/**
 * Created by Fenix on 12/02/17.
 */

/**
 * @file This file is able to manage the modal for editing the JSON.
 */


/**
 * @description This function is able to manage the modal that contains the manipulation of the JSON.
 * Before it prepares the modal: firstly it cleans it deleting the DIV that contains the JSON and
 * it creates a new empty div. After that, the function sets the right option of the library that is able to
 * manage the library in to "options" variable.
 * After that, the function gets json of the graph and it translates this json in a Verigraph JSON
 * and it updates the right id.
 * Finally it prints into the new DIV, created earlier the JSON well formatted by the library and
 * it shows the modal.
 */
 function modifyJsonViewModal()
{
    $( "div" ).remove( ".jsoneditor" );

    // create the editor
    var container = document.getElementById('jsoneditor');
    var options = {
        modes: ['text', 'code', 'tree', 'form', 'view'],
        mode: 'code',
        ace: ace
    };

    var json =  NFFGfromCytoToVerigraphFlagSave("no");
    json.id = NFFGsServer[indexServer].id;

    /* Example to manage the library
     var json = {
     'array': [1, 2, 3],
     'boolean': true,
     'null': null,
     'number': 123,
     'object': {'a': 'b', 'c': 'd'},
     'string': 'Hello World'
     };
     */
    editor = new JSONEditor(container, options, json);
    $('#modifyJsonModal').modal('show');


}

/**
 * @description This function is called when the user clicks on update. This function gets the new json
 * and it updates the graph launch "updateGraphToServer" function.
 * Before that, it checks if the JSON is syntactically right for JSON but maybe for Verigraph.
 * If the JSON is right it checks if the server is online o or. If the server is online it does a PUT to
 * the server.
 */
function saveModifyJson()
{
    var jsonString= editor.getText();
    if(isValidJsonFunction(jsonString)==true)
    {
        if(serverOnline==false)
        {
            alertError("Error server not online!!!");
            return;
        }
        updateGraphToServer(JSON.parse(jsonString),idGraphVerigraph, 2);
        
    }
    else
    {
       alertError("Error: your json is not valid, please re-write!");
    }
}
