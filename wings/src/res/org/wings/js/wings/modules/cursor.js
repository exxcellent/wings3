/***************************************************************************************************
 * WINGS.CURSOR  --  contains: functions for cursor changing support
 **************************************************************************************************/

(function() {
	var wingS = window.wingS;
	// Create module namespace
	wingS.namespace("cursor");

    var lib = wingS.cursor;

    var cursor = null;
    var xOffset = 0;
    var yOffset = 20;

    function getEvent(event) {
        return wingS.event.getEvent(event);
    }

    function getTarget(event) {
        return wingS.event.getTarget(event); 
    }

    var posX = 0;
    var posY = 0;

    function followCursor(event) {
        event = getEvent(event);

        if (event.pageX || event.pageY) {
            posX = event.pageX;
            posY = event.pageY;
        } else if (event.clientX || event.clientY) {
            posX = event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
            posY = event.clientY + document.body.scrollTop + document.documentElement.scrollTop;
        }
        /* if (target.nodeName == "OPTION" && !YAHOO.env.ua.ie) {
            posX += wingS.util.absLeft(target);
            posY += wingS.util.absTop(target.parentNode) + 18;
        }*/

        lib.updatePosition();
    }

    function startFollow() {
        document.body.onmousemove = followCursor;
    }

    lib.updatePosition = function() {
        var newX = posX + xOffset;
        if (newX > 0 && newX < (YAHOO.util.Dom.getDocumentWidth() - cursor.offsetWidth - 2)) {
            cursor.style.left = newX + "px";
        }

        newX -= xOffset; newX += ajaxActivityIndicatorElement.dx;
        if(newX > 0 && newX < (YAHOO.util.Dom.getDocumentWidth() - wingS.global.updateCursor.width - 2)) {
            ajaxActivityIndicatorElement.style.left = newX + "px";
        }

        var newY = posY + yOffset;
        if (newY > 0 && newY < (YAHOO.util.Dom.getDocumentHeight() - cursor.offsetHeight - 2)) {
            cursor.style.top = newY + "px";
        }

        newY -= yOffset; newY += ajaxActivityIndicatorElement.dy;
        if(newY > 0 && newY < (YAHOO.util.Dom.getDocumentHeight() - wingS.global.updateCursor.height - 2)) {
            ajaxActivityIndicatorElement.style.top = newY + "px";
        }
    }

    lib.setCursorElement = function(cursorElementId) {
        cursor = document.getElementById(cursorElementId);

        startFollow();
    }

    var ajaxActivityIndicatorElement = null;

    function createAjaxActivityIndicatorElement() {
        ajaxActivityIndicatorElement = document.createElement("div");
        ajaxActivityIndicatorElement.style.position = "absolute";
        ajaxActivityIndicatorElement.style.top = "0px";
        ajaxActivityIndicatorElement.style.left = "0px";
        ajaxActivityIndicatorElement.style.zIndex = "9999";
        ajaxActivityIndicatorElement.style.display = "none";
        ajaxActivityIndicatorElement.style.visibility = "hidden";
        ajaxActivityIndicatorElement.dx = wingS.global.updateCursor.dx;
        ajaxActivityIndicatorElement.dy = wingS.global.updateCursor.dy;
        ajaxActivityIndicatorElement.innerHTML = "<img src=\"" + wingS.global.updateCursor.image + "\"/>";

        document.body.insertBefore(ajaxActivityIndicatorElement, document.body.firstChild);
        return true;
    }

    lib.setAjaxActivityIndicatorVisible = function(enable) {
        if(wingS.global == undefined || wingS.global == null || wingS.global.updateCursor == undefined)
            return;
        if(ajaxActivityIndicatorElement == null)
            createAjaxActivityIndicatorElement();

        if(enable) {
            ajaxActivityIndicatorElement.style.display = "block";
            ajaxActivityIndicatorElement.style.visibility = "visible";
        } else {
            ajaxActivityIndicatorElement.style.display = "none";
            ajaxActivityIndicatorElement.style.visibility = "hidden";
        }
    }
})();