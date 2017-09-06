/**
 * @file Sweetalert management
 * @see https://github.com/t4t5/sweetalert/issues/415
 */

function SweetAlertMultiInput(tooltipsArray,defaultsArray,types)
{
    SweetAlertMultiInputReset();
    var delimited = ",";

    var alertbox = document.getElementsByClassName("sweet-alert")[0];
    var fieldset = alertbox.getElementsByTagName("fieldset")[0];
    var mainTextBox = fieldset.childNodes[1];
    mainTextBox.style.display="none";

    var newmargin = parseFloat(alertbox.style.marginTop.replace("px",""))-(tooltipsArray.length*54);
    alertbox.style.marginTop=newmargin+"px";

    for (var i=0;i<tooltipsArray.length;i++)
    {
        var incls=document.createElement("p");
        incls.className="hackpanel hackp";
        incls.innerHTML=tooltipsArray[i];
        fieldset.appendChild(incls);

        var incls=document.createElement("input");
        incls.className="hackpanel hackinput";
        var type = (types===undefined)?"string":(types[i]===undefined)?"string":types[i];
        incls.dataset.type=type;
        incls.value=defaultsArray[i];
        incls.onkeyup = SweetAlertMultiInputAmalgamate;
        fieldset.appendChild(incls);
    }
    SweetAlertMultiInputAmalgamate();
}

function SweetAlertMultiInputAmalgamate()
{
    var alertbox = document.getElementsByClassName("sweet-alert")[0];
    var fieldset = alertbox.getElementsByTagName("fieldset")[0];
    var mainTextBox = fieldset.childNodes[1];

    var inputBoxes = new Array();
    var deleteInputs = document.getElementsByClassName("hackinput");
    for(var i=0;i<deleteInputs.length;i++)
    {
        var inputElement = deleteInputs[i];
        if (inputElement.dataset.type=="string")
            inputBoxes.push(inputElement.value);
        else if (inputElement.dataset.type=="float")
            inputBoxes.push(parseFloat(inputElement.value));
    }

    mainTextBox.value=JSON.stringify(inputBoxes);
}


function SweetAlertMultiInputReset()
{
    var deleteInputs = document.getElementsByClassName("hackpanel");
    while (deleteInputs.length>0)
    {
        deleteInputs[0].parentNode.removeChild(deleteInputs[0]);
    }
}

function SweetAlertMultiInputFix()
{
    var alertbox = document.getElementsByClassName("sweet-alert")[0];
    if (alertbox===undefined) return 0;
    var fieldset = alertbox.getElementsByTagName("fieldset")[0];
    if (fieldset===undefined) return 0;
    var mainTextBox = fieldset.childNodes[1];
    if (mainTextBox===undefined) return 0;
    mainTextBox.style.display="block";
}
