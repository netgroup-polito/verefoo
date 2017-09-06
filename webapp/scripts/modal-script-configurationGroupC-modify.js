/**
 * @file File contains all the functions for the endhost modal. In this case there are not functions.
 */

function  populatedAgainModalC()
{


    //NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration;
    //Get the value from the modal

    var conf = NFFGcyto.nodes[changedNodeConfigurationInt].data.configuration;

    var i = 0;
    var key;

    rootCleanningModalModifyNode("C");

    for (key in conf[0])
    {
        switch (key)
        {
            //HTTP body
            case("body"):
            {
                $('#0CC1CB').removeAttr('disabled').prop('checked', true);
                $('#0CC1').removeAttr('disabled');
                var ta = document.getElementById('0CC1');
                ta.value = conf[0][[key]];
                break;
            }
            //Sequence number
            case("sequence"):
            {
                $('#0CC2CB').removeAttr('disabled').prop('checked', true);

                $('#0CC2').removeAttr('disabled');
                var ta = document.getElementById('0CC2');
                ta.value = conf[0][[key]];


                break;
            }
            //Protocol
            case("protocol"):
            {
                $('#0CC3CB').removeAttr('disabled').prop('checked', true);

                $('#0CC3').removeAttr('disabled');
                var ta = document.getElementById('0CC3');
                ta.value = conf[0][[key]];


                break;
            }
            //E-mail sender
            case("email_from"):
            {
                $('#0CC4CB').removeAttr('disabled').prop('checked', true);

                $('#0CC4').removeAttr('disabled');
                var ta = document.getElementById('0CC4');
                ta.value = conf[0][[key]];


                break;
            }
            //URL
            case("url"):
            {
                $('#0CC5CB').removeAttr('disabled').prop('checked', true);

                $('#0CC5').removeAttr('disabled');
                var ta = document.getElementById('0CC5');
                ta.value = conf[0][[key]];


                break;
            }
            //Options
            case("options"):
            {
                $('#0CC6CB').removeAttr('disabled').prop('checked', true);

                $('#0CC6').removeAttr('disabled');
                var ta = document.getElementById('0CC6');
                ta.value = conf[0][[key]];


                break;
            }
            //Destination node
            case("destination"):
            {
                $('#0CC7CB').removeAttr('disabled').prop('checked', true);

                $('#0CC7').removeAttr('disabled');
                var ta = document.getElementById('0CC7');
                ta.value = conf[0][[key]];


                break;
            }

        }

    }

}




