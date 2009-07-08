/***************************************************************************************************
 * WINGS.KEYBOARD  --  contains: keyboard handling functionality
 **************************************************************************************************/

// Create module namespace
wingS.namespace("keyboard");


wingS.keyboard.keyStrokes = null;

wingS.keyboard.handler = function(event) {
    var element;

    if (window.event) {
        event = window.event;
        element = event.srcElement;
    }
    else
        element = event.target;

    if (wingS.keyboard.handler.match(event, element)) {
        if (window.event && !event.shiftKey && !event.ctrlKey)
            event.keyCode = 16;

        return false;
    }
    return true;
};

wingS.keyboard.uphandler = function(event) {
    if (window.event) {
        event = window.event;
    }
    var shiftKey = event.shiftKey;
    var ctrlKey = event.ctrlKey;

    if(wingS.sdnd.isDragging()) { // don't process keyboard operations while dragging - forward them to sdnd.js
        wingS.sdnd.keyChange(ctrlKey, shiftKey);
        return false;
    }

    return false;
}

wingS.keyboard.handler.match = function(event, element) {
    var keyCode = event.keyCode;
    var shiftKey = event.shiftKey;
    var ctrlKey = event.ctrlKey;
    var altKey = event.altKey;
    var keyStrokes = wingS.keyboard.keyStrokes;

    if(wingS.sdnd.isDragging()) { // don't process keyboard operations while dragging - forward them to sdnd.js
        wingS.sdnd.keyChange(ctrlKey, shiftKey);
        return false;
    }

    if (!keyStrokes)
        return false;

    var wu = wingS.util;
    for (var index = 0, len = keyStrokes.length; index < len; ++index) {
        var keyStroke = keyStrokes[index];
        if (keyStroke.keyCode == keyCode && keyStroke.shiftKey == shiftKey && keyStroke.ctrlKey == ctrlKey && keyStroke.altKey == altKey) {
            if (!keyStroke.focussed || wu.getParentByAttributeAndValue(element, "id", keyStroke.component) != null) {
                wingS.request.sendEvent(event, true, true, keyStroke.component + '_keystroke', keyStroke.command);
                if ("console" in window && window.console)
                    window.console.log("match true");
                return true;
            }
        }
    }
    return false;
}

/**
 * @param {String} component - will receive the event
 * @param {Boolean} focussed - check wether event has been fired in focus of component
 * @param {String} command - the value of the event
 * @param {Integer} keyCode - the keyCode of the key stroke
 * @param {Boolean} shiftKey - the shift modifier of the key stroke
 * @param {Boolean} ctrlKey - the ctrl modifier of the key stroke
 * @param {Boolean} altKey - the alt modifier of the key stroke
 */
wingS.keyboard.KeyStroke = function(component, focussed, command, keyCode, shiftKey, ctrlKey, altKey) {
    this.component = component;
    this.focussed = focussed;
    this.command = command;
    this.keyCode = keyCode;
    this.shiftKey = shiftKey;
    this.ctrlKey = ctrlKey;
    this.altKey = altKey;
}

document.onkeydown = wingS.keyboard.handler;
document.onkeyup = wingS.keyboard.uphandler;
