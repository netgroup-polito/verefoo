/**
 * @file Save and draw the graph after modal configuration during creation new node.
 *
 */

/**
 * @description Save the actual temporary node in the graph and force it to be drawn on the screen.
 */
function saveAndDraw()
{
    NFFGcyto.nodes.push(tmpNodeSave);
    drawWithParametre(NFFGcyto);
    tmpNodeSave=null;

}
