if(typeof AppCalendar == 'undefined') 
{
	var AppCalendar = { };
}

AppCalendar.pointerX = function(event) {
	return event.pageX || (event.clientX +
		(document.documentElement.scrollLeft || document.body.scrollLeft));
}

AppCalendar.pointerY = function(event) {
	return event.pageY || (event.clientY +
		(document.documentElement.scrollTop || document.body.scrollTop));
}

/*
if(wingS.update.selectCalendarElement == undefined && wingS.update.deselectCalendarElement == undefined)
{
	wingS.update.selectCalendarElement = function(elementId) {
		document.getElementById(elementId).className = "selected";
	}
	
	wingS.update.deselectCalendarElement = function(elementId) {
		document.getElementById(elementId).className = null;
	}
}
*/
AppCalendar.clickAppointment = function(appointment, event, eventName) {
	if(eventName == null)
		return false;

	var eventValue = 'shiftKey=' + event.shiftKey + ';ctrlKey=' + event.ctrlKey + ';altKey=' + event.altKey + ';a:' + appointment.id;
	wingS.request.sendEvent(event, false, true, eventName, eventValue);
	
	return false;
}

AppCalendar.clickDate = function(date, event, eventName) {
	if(eventName == null)
		return false;
		
	if(document.getElementById('AppointmentCalendar-Popup').style.visibility == 'visible' || AppCalendar.timeout != null) {
		return false;
	}

	var eventValue = 'shiftKey=' + event.shiftKey + ';ctrlKey=' + event.ctrlKey + ';altKey=' + event.altKey + ';d:' + date.id;
	wingS.request.sendEvent(event, false, true, eventName, eventValue);
	
	return false;
}

AppCalendar.timeout = null;

AppCalendar.loadPopup = function(element, event, eventName)
{
	if(!event) {
		event = window.event;
	}

	AppCalendar.updatePopupPosition(event);
	AppCalendar.timeout = setTimeout("AppCalendar.showPopup('" + eventName + "', '" + element.id + "')", 500);
}

AppCalendar.showPopup = function(eventName, elementID)
{
	document.getElementById(elementID).onmousemove = AppCalendar.updatePopupPosition;
	document.getElementById(eventName + '-Popup').style.visibility = 'visible';
	wingS.request.sendEvent(null, false, true,   eventName, "q:"+elementID, null);
	AppCalendar.timeout = null;
}

AppCalendar.updatePopupPosition = function(event)
{
	if(!event) {
		event = window.event;
	}	
	var elt = document.getElementById('AppointmentCalendar-Popup');
	var windowHeight = 0;
	var windowWidth = 0;
	if(typeof(window.innerWidth) == 'number') {
		windowHeight = window.innerHeight;
		windowWidth = window.innerWidth;
	} else if( document.documentElement && (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
		windowHeight = document.documentElement.clientHeight;
		windowWidth = document.documentElement.clientWidth;
	} else if( document.body && (document.body.clientWidth || document.body.clientHeight)) {
		windowHeight = document.body.clientHeight;
		windowWidth = document.body.clientWidth;
	}
	
	if(AppCalendar.pointerX(event) + elt.clientWidth + 12 > windowWidth) {
		elt.style.left = (AppCalendar.pointerX(event) - 1 - elt.clientWidth) + "px";
	} else {
		elt.style.left = (AppCalendar.pointerX(event) + 12) + "px";
	}
	
	if(AppCalendar.pointerY(event) + elt.clientHeight + 12 > windowHeight) {
		elt.style.top = (AppCalendar.pointerY(event) - 6 - elt.clientHeight) + "px";
	} else {
		elt.style.top = (AppCalendar.pointerY(event)+12) + "px";
	}
}

AppCalendar.hidePopup = function(element)
{
	//document.getElementById('AppointmentCalendar-Popup').style.display = 'none';
	clearTimeout(AppCalendar.timeout);
	document.getElementById('AppointmentCalendar-Popup').style.visibility = 'hidden';
	document.getElementById('AppointmentCalendar-Popup').innerHTML = "<span>...</span>";
	element.onmousemove = null;
	AppCalendar.timeout = null;
	//AppCalendar.swapColors(element);
}
