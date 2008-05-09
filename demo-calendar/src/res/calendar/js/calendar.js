if(typeof AppCalendar == 'undefined') 
{
	var AppCalendar = { };
}

/*
AppCalendar.pointerX = function(event) 
{
    var body = (window.document.compatMode && window.document.compatMode == "CSS1Compat") ? window.document.documentElement : window.document.body || null;
	return event.clientX + body.scrollLeft;
	return event.pageX ? event.pageX : event.clientX + body.scrollLeft;
}

AppCalendar.pointerY = function(event) 
{
    var body = (window.document.compatMode && window.document.compatMode == "CSS1Compat") ? window.document.documentElement : window.document.body || null;
	return event.clientY + body.scrollTop;    
	return event.pageY ? event.pageY : event.clientY + body.scrollTop;    
}

*/

AppCalendar.clickAppointment = function(appointment, event, eventName) {
	if(eventName == null)
		return false;

	// cancel event bubbling
	if(typeof(event) == "undefined" && typeof(window.event) != "undefined") { // IE
		event = window.event;
		event.cancelBubble = true;
	} else {
		event.stopPropagation();
	}
	
	//  send ajax request (selection of appointment)
    var eventValue = 'shiftKey=' + event.shiftKey + ';ctrlKey=' + event.ctrlKey + ';altKey=' + event.altKey + ';a:' + appointment.id;
	wingS.request.sendEvent(event, false, true, eventName, eventValue);
	
	return false;
}

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

AppCalendar.loadPopup = function(element, event, eventName)
{
	AppCalendar.timeout = setTimeout("AppCalendar.showPopup('" + eventName + "', '" + element.id + "')", 500);
}

AppCalendar.showPopup = function(eventName, elementID)
{
	// send ajax request (show popup)
	wingS.request.sendEvent(null, false, true,   eventName, "q:"+elementID, null);
	clearTimeout(AppCalendar.timeout);
	AppCalendar.timeout = null;
}