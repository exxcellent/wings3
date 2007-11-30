YAHOO.widget.XCalendar = function(containerId, config, xcalId, buttonId, hiddenId ) {
    YAHOO.widget.XCalendar.superclass.constructor.call(this, containerId+"ID", containerId, config );

    this.xcalId     = xcalId;
    this.calId      = containerId;
    this.buttonId   = buttonId;
    this.hiddenField = document.getElementById( hiddenId );

    this.selectEvent.subscribe( this.handleSelect, this, true );
    YAHOO.util.Event.addListener(this.buttonId, "click", this.showCalendar, this, true); 
}

YAHOO.lang.extend( YAHOO.widget.XCalendar, YAHOO.widget.Calendar );

YAHOO.widget.XCalendar.prototype.showCalendar = function() {
    if (this.hiddenField.value != "") {
        this.select(this.hiddenField.value); 
        var selectedDates = this.getSelectedDates(); 
        if (selectedDates.length > 0) { 
            var firstDate = selectedDates[0]; 
            this.cfg.setProperty("pagedate", (firstDate.getMonth()+1) + "/" + firstDate.getFullYear());  
        }
    }
    this.render();
    this.show();

    var pos = YAHOO.util.Dom.getXY( "r"+this.calId )
    var cal = document.getElementById(this.calId);
    cal.style.left = pos[0] + "px";
    cal.style.top = pos[1] + "px";

    var regionCalendar = new YAHOO.util.Region(pos[1], pos[0] + cal.offsetWidth, pos[1] + cal.offsetHeight, pos[0]);
    var viewportWidth  = YAHOO.util.Dom.getViewportWidth();
    var viewportHeight = YAHOO.util.Dom.getViewportHeight();

    var overlabRight = regionCalendar.right - ( viewportWidth + YAHOO.util.Dom.getDocumentScrollLeft() );
    if ( overlabRight > 0 ) {
        cal.style.left = (regionCalendar.left - ( overlabRight + 8 ) ) + "px";
    }
    var overlabBottom = regionCalendar.bottom - ( viewportHeight + YAHOO.util.Dom.getDocumentScrollTop() );
    if ( overlabBottom > 0 ) {
        cal.style.top = (regionCalendar.top - ( overlabBottom + 8 ) ) + "px";
    }
}

YAHOO.widget.XCalendar.prototype.handleSelect = function(type,args,obj) {
    var dates = args[0]; 
    var date = dates[0]; 
    var year = date[0];
    var month = date[1];
    var day = date[2];
    if ( day < 10 ) { day = '0'+day; }
    if ( month < 10 ) { month = '0'+month; }
    
    var value = month + "/" + day + "/" + year;
    
    if ( this.hiddenField.value != value ) {
        wingS.request.sendEvent(null, false, true, this.xcalId, value );
    }
    this.hide();
}

/**
 * Overwrites createTitleBar in YAHOO.widget.Calendar
 */
YAHOO.widget.XCalendar.prototype.createTitleBar = function(strTitle) {
	var tDiv = YAHOO.util.Dom.getElementsByClassName(YAHOO.widget.CalendarGroup.CSS_2UPTITLE, "div", this.oDomContainer)[0] || document.createElement("div");
	tDiv.className = YAHOO.widget.CalendarGroup.CSS_2UPTITLE;
	tDiv.innerHTML = this.buildMonthLabel();
	this.oDomContainer.insertBefore(tDiv, this.oDomContainer.firstChild);

	YAHOO.util.Dom.addClass(this.oDomContainer, "withtitle");

	return tDiv;
};

/**
 * Overwrites renderHeader in YAHOO.widget.Calendar
 */
YAHOO.widget.XCalendar.prototype.renderHeader = function(html) {
        var colSpan = 7;

        var DEPR_NAV_LEFT = "us/tr/callt.gif";
        var DEPR_NAV_RIGHT = "us/tr/calrt.gif";
        var defCfg = YAHOO.widget.Calendar._DEFAULT_CONFIG;

        if (this.cfg.getProperty(defCfg.SHOW_WEEK_HEADER.key)) {
                colSpan += 1;
        }

        if (this.cfg.getProperty(defCfg.SHOW_WEEK_FOOTER.key)) {
                colSpan += 1;
        }

        html[html.length] = "<thead>";
        html[html.length] =             "<tr>";
        html[html.length] =                     '<th colspan="' + colSpan + '" class="' + this.Style.CSS_HEADER_TEXT + '">';
        html[html.length] =                             '<div class="' + this.Style.CSS_HEADER + '">';

        var renderLeft, renderRight = false;

        if (this.parent) {
                if (this.index === 0) {
                        renderLeft = true;
                }
                if (this.index == (this.parent.cfg.getProperty("pages") -1)) {
                        renderRight = true;
                }
        } else {
                renderLeft = true;
                renderRight = true;
        }

        var cal = this.parent || this;

        // <previousYear>
        html[html.length] = '<a class="calnavleftYear">&#160;</a>';
        // </previousYear>

        if (renderLeft) {
                var leftArrow = this.cfg.getProperty(defCfg.NAV_ARROW_LEFT.key);
                // Check for deprecated customization - If someone set IMG_ROOT, but didn't set NAV_ARROW_LEFT, then set NAV_ARROW_LEFT to the old deprecated value
                if (leftArrow === null && YAHOO.widget.Calendar.IMG_ROOT !== null) {
                        leftArrow = YAHOO.widget.Calendar.IMG_ROOT + DEPR_NAV_LEFT;
                }
                //var leftStyle = (leftArrow === null) ? "" : ' style="background-image:url(' + leftArrow + ')"';
                var leftStyle = '';
                html[html.length] = '<a class="' + this.Style.CSS_NAV_LEFT + '"' + leftStyle + ' >&#160;</a>';
        }

        // <clearDate>
        html[html.length] = '<a class="calnavclearDate">&#160;</a>';
        // </clearDate>

        if (renderRight) {
                var rightArrow = this.cfg.getProperty(defCfg.NAV_ARROW_RIGHT.key);
                if (rightArrow === null && YAHOO.widget.Calendar.IMG_ROOT !== null) {
                        rightArrow = YAHOO.widget.Calendar.IMG_ROOT + DEPR_NAV_RIGHT;
                }
                var rightStyle = (rightArrow === null) ? "" : ' style="background-image:url(' + rightArrow + ')"';
                var rightStyle = '';
                html[html.length] = '<a class="' + this.Style.CSS_NAV_RIGHT + '"' + rightStyle + ' >&#160;</a>';
        }
        
        // <nextYear>
        html[html.length] = '<a class="calnavrightYear">&#160;</a>';
        // </nextYear>

        html[html.length] =     '</div>\n</th>\n</tr>';

        if (this.cfg.getProperty(defCfg.SHOW_WEEKDAYS.key)) {
                html = this.buildWeekdays(html);
        }

        html[html.length] = '</thead>';

        return html;
};

/**
    Ueberschreibt die Methode applyListeners aus YAHOO.widget.Calendar
*/
YAHOO.widget.XCalendar.prototype.applyListeners = function() {

        var root = this.oDomContainer;
        var cal = this.parent || this;

        var anchor = "a";
        var mousedown = "mousedown";

        var linkLeft = YAHOO.util.Dom.getElementsByClassName(this.Style.CSS_NAV_LEFT, anchor, root);
        var linkRight = YAHOO.util.Dom.getElementsByClassName(this.Style.CSS_NAV_RIGHT, anchor, root);
        var linkLeftYear = YAHOO.util.Dom.getElementsByClassName("calnavleftYear", anchor, root);
        var linkRightYear = YAHOO.util.Dom.getElementsByClassName("calnavrightYear", anchor, root);
        var linkClearDate = YAHOO.util.Dom.getElementsByClassName("calnavclearDate", anchor, root);

        if (linkLeft && linkLeft.length > 0) {
                this.linkLeft = linkLeft[0];
                YAHOO.util.Event.addListener(this.linkLeft, mousedown, cal.previousMonth, cal, true);
        }

        if (linkRight && linkRight.length > 0) {
                this.linkRight = linkRight[0];
                YAHOO.util.Event.addListener(this.linkRight, mousedown, cal.nextMonth, cal, true);
        }
        
        if (linkLeftYear && linkLeftYear.length > 0) {
                this.linkLeftYear = linkLeftYear[0];
                YAHOO.util.Event.addListener(this.linkLeftYear, mousedown, cal.previousYear, cal, true);
        }

        if (linkRightYear && linkRightYear.length > 0) {
                this.linkRightYear = linkRightYear[0];
                YAHOO.util.Event.addListener(this.linkRightYear, mousedown, cal.nextYear, cal, true);
        }
        
        if (linkClearDate && linkClearDate.length > 0) {
                this.linkClearDate = linkClearDate[0];
                YAHOO.util.Event.addListener(this.linkClearDate, mousedown, cal.clear, cal, true);
        }

        if (this.domEventMap) {
                var el,elements;
                for (var cls in this.domEventMap) {
                        if (YAHOO.lang.hasOwnProperty(this.domEventMap, cls)) {
                                var items = this.domEventMap[cls];

                                if (! (items instanceof Array)) {
                                        items = [items];
                                }

                                for (var i=0;i<items.length;i++)        {
                                        var item = items[i];
                                        elements = YAHOO.util.Dom.getElementsByClassName(cls, item.tag, this.oDomContainer);

                                        for (var c=0;c<elements.length;c++) {
                                                el = elements[c];
                                                 YAHOO.util.Event.addListener(el, item.event, item.handler, item.scope, item.correct );
                                        }
                                }
                        }
                }
        }

        YAHOO.util.Event.addListener(this.oDomContainer, "click", this.doSelectCell, this); 
        YAHOO.util.Event.addListener(this.oDomContainer, "mouseover", this.doCellMouseOver, this);
        YAHOO.util.Event.addListener(this.oDomContainer, "mouseout", this.doCellMouseOut, this);
};