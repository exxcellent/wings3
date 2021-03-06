/***************************************************************************************************
 * WINGS.SCROLLBAR  --  contains: scrollbar related functions
 **************************************************************************************************/

// Create module namespace
wingS.namespace("scrollbar");


wingS.scrollbar.layout_vertical = function(id) {
    var table = document.getElementById(id);
    var outer = table.getElementsByTagName("DIV")[0];
    var inner = outer.getElementsByTagName("DIV")[0];
    var td = outer.parentNode;

    if (document.defaultView) {
        var heightpx = document.defaultView.getComputedStyle(td, null).getPropertyValue("height");
        outer.style.height = heightpx;
        inner.style.height = heightpx;
    }
    else {
        var height = td.offsetHeight;
        outer.style.height = height + "px";
        inner.style.height = height + "px";
    }

    table.callbackObject = {
        _tOutId : 0,
        _adjust : function (target) {
            table.focus();
            wingS.scrollbar.scroll_vertical(target);
            table.callbackObject._tOutId = 0;
        },
        adjust : function (event) {
            if (table.callbackObject._tOutId == 0) {
                var cb = table.callbackObject;
                var target = wingS.event.getTarget(event);
                table.callbackObject._tOutId = setTimeout(function() { cb._adjust(target); }, wingS.global.autoAdjustLayout.delay);
            }
        }
    };

    YAHOO.util.Event.addListener(outer, 'scroll', table.callbackObject.adjust);
};

wingS.scrollbar.handleMouseWheel = function(event, scrollbar) {
     var delta = 0;
     if (event.wheelDelta) {
        delta = event.wheelDelta/120;
        if (window.opera) delta = -delta;
     } else if (event.detail) {
        delta = -event.detail/3;
     }

     var table = document.getElementById(scrollbar);
    if(table == null)
        return;
     var outer = table.getElementsByTagName("DIV")[0];
     var inner = outer.getElementsByTagName("DIV")[0];

     var scrollTop = outer.scrollTop;
     var innerHeight = inner.clientHeight;
     var size = parseInt(outer.getAttribute("size"));

     var position = scrollTop * size / innerHeight;
    
     if (delta > 0) {
        outer.scrollTop = innerHeight * ( position-1 ) / size;
     }
     if (delta < 0) { 
        outer.scrollTop = innerHeight * ( position+1 ) / size;
     }

}

wingS.scrollbar.set_vertical = function(id, position, extent, size) {
    var table = document.getElementById(id);
    var outer = table.getElementsByTagName("DIV")[0];
    var inner = outer.getElementsByTagName("DIV")[0];

    outer.setAttribute("position", position);
    outer.setAttribute("size", size);

    var outerHeight = outer.offsetHeight;
    var innerHeight = outerHeight * size / extent;
    inner.style.height = innerHeight + "px";

    outer.scrollTop = innerHeight * position / size;
};

wingS.scrollbar.scroll_vertical = function(outer) {
    var inner = outer.getElementsByTagName("DIV")[0];
    var table = wingS.util.getParentByTagName(outer, "TABLE");

    var innerHeight = inner.offsetHeight;
    var scrollTop = outer.scrollTop;
    var size = outer.getAttribute("size");

    var position = Math.round(scrollTop / innerHeight * size);

    if (outer.getAttribute("position") != position) {
        outer.setAttribute("position", position);
        wingS.request.sendEvent(null, true, true, table.id, position);
    }
};

wingS.scrollbar.layout_horizontal = function(id) {
    var table = document.getElementById(id);
    var outer = table.getElementsByTagName("DIV")[0];
    var inner = outer.getElementsByTagName("DIV")[0];
    var td = outer.parentNode;

    if (document.defaultView) {
        var widthpx = document.defaultView.getComputedStyle(td, null).getPropertyValue("width");
        outer.style.width = widthpx;
        inner.style.width = widthpx;
    }
    else {
        var width = td.offsetWidth;
        outer.style.width = width + "px";
        inner.style.width = width + "px";
    }

    table.callbackObject = {
        _tOutId : 0,
        _adjust : function (target) {
            table.focus();
            wingS.scrollbar.scroll_horizontal(target);
            table.callbackObject._tOutId = 0;
        },
        adjust : function (event) {
            if (table.callbackObject._tOutId == 0) {
                var cb = table.callbackObject;
                var target = wingS.event.getTarget(event);
                table.callbackObject._tOutId = setTimeout(function() { cb._adjust(target); }, wingS.global.autoAdjustLayout.delay);
            }
        }
    };

    YAHOO.util.Event.addListener(outer, 'scroll', table.callbackObject.adjust);
};

wingS.scrollbar.set_horizontal = function(id, position, extent, size) {
    var table = document.getElementById(id);
    var outer = table.getElementsByTagName("DIV")[0];
    var inner = outer.getElementsByTagName("DIV")[0];

    outer.setAttribute("position", position);
    outer.setAttribute("size", size);

    var outerWidth = outer.offsetWidth;
    var innerWidth = outerWidth * size / extent;
    inner.style.width = innerWidth + "px";

    outer.scrollLeft = innerWidth * position / size;
};

wingS.scrollbar.scroll_horizontal = function(outer) {
    var inner = outer.getElementsByTagName("DIV")[0];
    var table = wingS.util.getParentByTagName(outer, "TABLE");

    var innerWidth = inner.offsetWidth;
    var scrollLeft = outer.scrollLeft;
    var size = outer.getAttribute("size");

    var position = Math.round(scrollLeft / innerWidth * size);

    
    if (outer.getAttribute("position") != position) {
        outer.setAttribute("position", position);
        wingS.request.sendEvent(null, true, true, table.id, position);
    }
};

