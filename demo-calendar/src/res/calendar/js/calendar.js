// don't overwrite other AppCalendar "namespaces"
if(typeof AppCalendar == 'undefined')
{
	var AppCalendar = { };
}

/**
 * Function that is executed, when a click on a Appointment occured
 * @param appointment (HTML/DOM) Appointment-Element on which the click was registered
 * @param event (Javascript-)Event handler
 * @param eventName Event Name to be sent to the wingS framework
 */
AppCalendar.clickAppointment = function(appointment, event, eventName) {
	if(eventName == null)
		return false;

    if(!event)
        event = window.event;

    event.cancelBubble = true;

    if(event.stopPropagation)
		event.stopPropagation();

	//  send ajax request (selection of appointment)
    var eventValue = 'shiftKey=' + event.shiftKey + ';ctrlKey=' + event.ctrlKey + ';altKey=' + event.altKey + ';a:' + appointment.id;
	wingS.request.sendEvent(event, false, true, eventName, eventValue);
	
	return false;
};

/**
 * Function that is executed, when a click on a Appointment occured
 * @param appointment (HTML/DOM) Appointment-Element on which the click was registered
 * @param event (Javascript-)Event handler
 * @param eventName Event Name to be sent to the wingS framework
 */
AppCalendar.doubleClickAppointment = function(appointment, event, eventName) {
	if(eventName == null)
		return false;

    if(!event)
        event = window.event;

    event.cancelBubble = true;

    if(event.stopPropagation)
		event.stopPropagation();

	//  send ajax request (selection of appointment)
    var eventValue = 'shiftKey=' + event.shiftKey + ';ctrlKey=' + event.ctrlKey + ';altKey=' + event.altKey + ';da:' + appointment.id;
	wingS.request.sendEvent(event, false, true, eventName, eventValue);

	return false;
};

/**
 * Function that is executed, when a click on a Date-Cell occured
 * @param date (HTML/DOM) Date element on which the click was registered
 * @param event Javascript-event-HAndler
 * @param eventName Name of the Event to send to the wingS-Framework
 */
AppCalendar.clickDate = function(date, event, eventName) {
	if(eventName == null)
		return false;

	// send ajax request (selection of date)
	var eventValue = 'shiftKey=' + event.shiftKey + ';ctrlKey=' + event.ctrlKey + ';altKey=' + event.altKey + ';d:' + date.id;
	wingS.request.sendEvent(event, false, true, eventName, eventValue);
	
	return false;
};

/**
 * Function that is executed, when a click on a Date-Cell occured
 * @param date (HTML/DOM) Date element on which the click was registered
 * @param event Javascript-event-HAndler
 * @param eventName Name of the Event to send to the wingS-Framework
 */
AppCalendar.doubleClickDate = function(date, event, eventName) {
	if(eventName == null)
		return false;

	// send ajax request (selection of date)
	var eventValue = 'shiftKey=' + event.shiftKey + ';ctrlKey=' + event.ctrlKey + ';altKey=' + event.altKey + ';dd:' + date.id;
	wingS.request.sendEvent(event, false, true, eventName, eventValue);

	return false;
};

AppCalendar.timeout = null;
AppCalendar.popupShown = false;

/**
 * Function that is executed on every appointment-mouseover - it calls showPopup after a specified time interval
 * @param element DOM-Element of the mppointment
 * @param event event of this event
 * @param eventName eventname, to be given to showPopup
 */
AppCalendar.loadPopup = function(element, event, eventName)
{
    if(AppCalendar.timeout != null)
        return false;

    AppCalendar.timeout = setTimeout("AppCalendar.showPopup('" + eventName + "', '" + element.id + "')", 500);
};

/**
 * Function that is called when a popup should be shown, it sends the ajax event for a popup to the wingS
 * framework
 * @param eventName Name of the Event to be sent to the wingS-Framework
 * @param elementID (DOM-)Element-ID of the appointment - it must be unique
 */
AppCalendar.showPopup = function(eventName, elementID)
{
	// send ajax request (show popup)
	wingS.request.sendEvent(null, false, true,   eventName, "q:"+elementID, null);
    clearTimeout(AppCalendar.timeout);
    AppCalendar.timeout = null;
};

AppCalendar.hidePopup = function(element) {
    if(!window.UnTip)
        tt_Hide(); // remove this when wz_tooltip is replaced with a version >=5 (only call UnTip
    else
        UnTip();
    clearTimeout(AppCalendar.timeout);
    AppCalendar.timeout = null;
};

/**
 * Calendar Drag and Drop code
 */
(function(){
    var dndLib = window.wingS.sdnd;

    function getTarget(event) {
        return wingS.event.getTarget(event);
    }

    function stopEvent(event) {
        return YAHOO.util.Event.stopEvent(event);
    }

    function getFirstParentWithHandler(element, handler) {
        return dndLib.getFirstParentWithHandler(element, handler);
    }

    function getFirstParentWithHandlerBeforeParent(element, handler, parent) {
        return dndLib.getFirstParentWithHandlerBeforeParent(element, handler, parent);
    }

    function getDateParent(element) {
        if(element == null)
            return null;

        if(element.id && typeof(element.id) == 'string')
            if(/^.+_[0-9]+:[0-9]+$/.exec(element.id))
                return element;

        return getDateParent(element.parentNode);
    }
    
    var calendarDragCode = {
        dragStart : function(event, realEvent) { // is called when the drag operation should start (first mousemove while clicking)
                                      // - decides, if the whiledragging events should be registered (mouseenter/leave)
            if(event == null)
                return false;

            var target = getTarget(event);
            var parent = getFirstParentWithHandler(target, dndLib.getDragSources());
            var element = getFirstParentWithHandlerBeforeParent(target, 'onmouseover', parent);
            if(element == null)
                return false;
            
            dndLib.sendDragEvent(event, "ds", 'ctrlKey=' + realEvent.ctrlKey + ':shiftKey=' + realEvent.shiftKey + ':' + element.id);

            stopEvent(realEvent);
            return true;
        }
    }

    var calendarDropCode = {
        drop : function(event) {    // called when the mouse button is released on a droptarget
                                    // - decides if the events should be deregistered
                                    // return false only if you're using your own events
            var target = getTarget(event);
            var dateparent = getDateParent(target);
            var params = null;
            if(dateparent != null && dateparent.id != undefined) {
                params = dateparent.id.substring(dateparent.id.indexOf("_")+1);
            }

            dndLib.sendDragEvent(event, "dr", params);
            stopEvent(event);
            return true;
        }
    }

    dndLib.registerCode('drag', 'calendar', calendarDragCode);
    dndLib.registerCode('drop', 'calendar', calendarDropCode);
})();
