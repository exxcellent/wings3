/***************************************************************************************************
 * WINGS.XCALENDAR  --  contains: functions related to the XCalendar class
 **************************************************************************************************/

// Create module namespace
wingS.namespace("xcalendar");

wingS.xcalendar.XCalendar = function(componentId, config) {
    this.componentId = componentId;
    this.valueId = componentId + "val";
    this.buttonId = componentId + "btn";
    this.containerId = componentId + "con";
    this.calendarId = componentId + "cal";
    
    this.valueField = document.getElementById(this.valueId);
    
    this.calendarWidget = new wingS.xcalendar.Calendar(this.calendarId, config);
    this.calendarWidget.selectEvent.subscribe(this.handleSelectEvent, this, true);
    this.calendarWidget.clearEvent.subscribe(this.updateContainerTitle, this, true);
    this.calendarWidget.changePageEvent.subscribe(this.updateContainerTitle, this, true);
    
    this.containerWidget = new YAHOO.widget.Dialog(this.containerId, {context:[this.buttonId, "tl", "tl"],
         constraintoviewport:true, draggable:true, close:true, underlay:"none", zIndex:1001});
    
    YAHOO.util.Event.addListener(this.buttonId, "click", this.showPopup, this, true);
}

wingS.xcalendar.XCalendar.prototype.showPopup = function() {
    if (this.valueField.value != "") {
        this.calendarWidget.select(this.valueField.value);
        var selectedDates = this.calendarWidget.getSelectedDates();
        if (selectedDates.length > 0) {
            var selectedDate = selectedDates[0];
            this.calendarWidget.cfg.setProperty("pagedate",
                (selectedDate.getMonth() + 1) + "/" + selectedDate.getFullYear());
        }
    }
    this.updateContainerTitle();
    this.calendarWidget.render();
    this.containerWidget.render();
    this.containerWidget.show();
    this.containerWidget.align("tl", "tl");
}

wingS.xcalendar.XCalendar.prototype.handleSelectEvent = function(type, args, obj) {
    var dates = args[0];
    var date = dates[0];
    var year = date[0];
    var month = date[1];
    var day = date[2];
    if (day < 10) { day = '0' + day; }
    if (month < 10) { month = '0' + month; }
    var value = month + "/" + day + "/" + year;

    if (this.valueField.value != value) {
        wingS.request.sendEvent(null, false, true, this.componentId, value);
    }
    this.containerWidget.hide();
}

wingS.xcalendar.XCalendar.prototype.updateContainerTitle = function(type, args, obj) {
    this.containerWidget.setHeader(this.calendarWidget.buildMonthLabel());
}

wingS.xcalendar.Calendar = function(calendarId, config) {
    wingS.xcalendar.Calendar.superclass.constructor.call(this, calendarId, config);
}

YAHOO.lang.extend(wingS.xcalendar.Calendar, YAHOO.widget.Calendar);

/**
 * Overwrites renderHeader in YAHOO.widget.Calendar
 */
wingS.xcalendar.Calendar.prototype.renderHeader = function(html) {
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
        html[html.length] = "<tr>";
        html[html.length] = '<th colspan="' + colSpan + '" class="' + this.Style.CSS_HEADER_TEXT + '">';
        html[html.length] = '<div class="' + this.Style.CSS_HEADER + '">';

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
            html[html.length] = '<a class="' + this.Style.CSS_NAV_RIGHT + '"' + rightStyle + '>&#160;</a>';
        }

        // <nextYear>
        html[html.length] = '<a class="calnavrightYear">&#160;</a>';
        // </nextYear>

        html[html.length] = '</div>\n</th>\n</tr>';

        if (this.cfg.getProperty(defCfg.SHOW_WEEKDAYS.key)) {
            html = this.buildWeekdays(html);
        }

        html[html.length] = '</thead>';

        return html;
};

/**
 * Overwrites applyListeners in YAHOO.widget.Calendar
 */
wingS.xcalendar.Calendar.prototype.applyListeners = function() {
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