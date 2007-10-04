
// faking a namespace
if (!wingS) {
	var wingS = new Object();
}
else if (typeof wingS != "object") {
	throw new Error("wingS already exists and is not an object");
}

if (!wingS.XCalendar) {
	wingS.XCalendar = new Object();
}
else if (typeof wingS.XCalendar != "object") {
	throw new Error("wingS.XCalendar already exists and is not an object");
}

wingS.XCalendar = function (calendarId, calendarDivId, buttonId, hiddenId, months_long, weekdays_short, first_day_of_week) {
    this.calendarId = calendarId;
    this.calendarDivId = calendarDivId;
    this.button = document.getElementById(buttonId);
    this.hiddenField = document.getElementById(hiddenId);
    
    this.Calendar = new YAHOO.widget.Calendar("Calendar",calendarDivId, { title:"Choose a date:", close:true } ); 
    this.Calendar.render(); 
    this.Calendar.cfg.setProperty("MONTHS_LONG", months_long );
    this.Calendar.cfg.setProperty("WEEKDAYS_SHORT", weekdays_short );
    this.Calendar.cfg.setProperty("START_WEEKDAY", first_day_of_week );
    this.Calendar.selectEvent.subscribe( this.handleSelect, this, true );
  
    YAHOO.util.Event.addListener(this.button.id, "click", this.showCall, this, true); 
}

wingS.XCalendar.prototype.showCall = function showCall() {
 
    if (this.hiddenField.value != "") { 
        this.Calendar.select(this.hiddenField.value); 
        var selectedDates = this.Calendar.getSelectedDates(); 
        if (selectedDates.length > 0) { 
            var firstDate = selectedDates[0]; 
            this.Calendar.cfg.setProperty("pagedate", (firstDate.getMonth()+1) + "/" + firstDate.getFullYear()); 
            this.Calendar.render(); 
        } else { 
            alert("Cannot select a date"); 
        }
    }
    
    this.Calendar.show();
    
    YAHOO.util.Dom.setX(this.calendarDivId, 0 );
    YAHOO.util.Dom.setY(this.calendarDivId, 0 );
    var x = YAHOO.util.Dom.getX(this.button.id);
    var y = YAHOO.util.Dom.getY(this.button.id);

    var viewportx = YAHOO.util.Dom.getViewportWidth();
    var diff = viewportx - x;
                
    var callWidth = document.getElementById(this.calendarDivId).clientWidth;

    if ( callWidth > diff ) {
        x = x + diff - callWidth - 10;
    }

    YAHOO.util.Dom.setX(this.calendarDivId, x );
    YAHOO.util.Dom.setY(this.calendarDivId, y );
}

wingS.XCalendar.prototype.handleSelect = function handleSelect(type,args,obj) {
    var dates = args[0]; 
    var date = dates[0]; 
    var year = date[0];
    var month = date[1];
    var day = date[2];
    if ( day < 10 ) { day = '0'+day; }
    if ( month < 10 ) { month = '0'+month; }
    
    var value = month + "/" + day + "/" + year;
    
    if ( this.hiddenField.value != value ) {
        wingS.request.sendEvent(null, false, true, this.calendarId, value );
        this.Calendar.hide();
    }
}        