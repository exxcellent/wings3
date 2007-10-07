/***************************************************************************************************
 * WINGS.TABLE  --  contains: functions for tables
 **************************************************************************************************/

// Create module namespace
wingS.namespace("table");
wingS.table.cellClick = function(event, cell, submit, async, eventName, eventValue) {
    event = wingS.event.getEvent(event);
    var editing = cell.getAttribute("editing");
    if (!editing || editing == "false") {
        wingS.request.sendEvent(event, submit, async, eventName, eventValue);
        return false;
    }
    else
        return true;
}
