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
}

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
}

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
}

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
}

AppCalendar.hidePopup = function(element) {
    if(!window.UnTip)
        tt_Hide(); // remove this when wz_tooltip is replaced with a version >=5 (only call UnTip
    else
        UnTip();
    clearTimeout(AppCalendar.timeout);
    AppCalendar.timeout = null;
}