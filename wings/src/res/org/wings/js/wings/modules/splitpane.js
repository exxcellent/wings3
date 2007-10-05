/***************************************************************************************************
 * WINGS.SPLITPANE  --  contains: spitpane related functions 
 **************************************************************************************************/

// Create module namespace
wingS.namespace("splitpane");


wingS.splitpane.resized = function(sb, size) {
    wingS.request.sendEvent(null, true, true, sb.el.id, size);
}

