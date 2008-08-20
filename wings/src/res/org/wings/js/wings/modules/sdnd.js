/********************************************************************************
	New wingS Drag-and-Drop Javascript-Support-Library
*********************************************************************************/

(function() {
	var wingS = window.wingS;
	// create namespace in global wingS variable
	wingS.namespace("sdnd");
	var lib = wingS.sdnd;

    /**
     * Helper variables
     */
    var dragSources = { };
	var dropTargets = { };
	var manager = null;

    var dragCodeRegistry = { };
    var dropCodeRegistry = { };
    
    // From DnDConstants, need to be changed if the java constants change
    var ACTION_NONE = 0;
    var ACTION_COPY = 1;
    var ACTION_MOVE = 2;
    var ACTION_LINK = 0x40000000;

    // draggedElement == null when no (possible) dragging operation is in progress, if != null,
    // it contains the currently dragged element and gets filled right after the mouosedown on a draggable element
    var draggedElementId = null
    // isDragging is true after the dragging gesture was detected and while the dragging is in progress, false after drop
    var isDragging = false;
    var lastTarget = null;
    var pixelDelta = 0;

    /**
     * Helper functions - called within this context but aren't exported outside of it
     */
    lib.error = function(errorText) {
        alert(errorText);
	}

    function error(text) {
        lib.error(text);
    }

    /**
     * Wrapper for YAHOO.util.Event.addListener
     * @param element
     * @param event
     * @param func
     */
    lib.addEvent = function(element, event, func) {
		if(!YAHOO.util.Event.addListener(element, event, func))
			error("addListener for " + element + " event: " + event + " failed.");
	}
    function addEvent(element, event, func) {
        lib.addEvent(element, event, func);
    }

    /**
     * Wrapper for YAHOO.util.Event.removeListener
     * @param element
     * @param event
     * @param func
     */
    lib.removeEvent = function(element, event, func) {
        YAHOO.util.Event.removeListener(element, event);
// sometimes removing events bugs
//		if(!YAHOO.util.Event.removeListener(element, event, func))
//			error("removeListener for " + element + " event: " + event + " failed.");
	}
    function removeEvent(element, event, func) {
        lib.removeEvent(element, event, func);
    }


    /**
     * Gets the first parent of element that has a handler called handler and is in between element and parent
     */
    lib.getFirstParentWithHandlerBeforeParent = function(element, handler, parent) {
        if(element == undefined || element == null || element == parent)
            return null;

        if(typeof(element[handler]) == "function" || typeof(element[handler]) == "string")
            return element;

        if(element.parentNode && element.parentNode != null)
            return lib.getFirstParentWithHandlerBeforeParent(element.parentNode, handler, parent);

        return null;
    }

    function getTarget(event) {
        return wingS.event.getTarget(event);
	}

    function log(text) {
        if("console" in window && console.log) {
            console.log(text);
        }
    }
    
    function getEvent(event) {
        // make sure we use event if possible and use window.event only if event isn't available
        if(event != undefined && event != null)
            return event;
        return wingS.event.getEvent(event);
    }

    function addDragPrepareEvents() {
        addEvent(document.body, 'mousemove', lib.drag);
        addEvent(document.body, 'mouseup', lib.drop);
    }

    function removeDragPrepareEvents() {
        removeEvent(document.body, 'mouseup', lib.drop);
        removeEvent(document.body, 'mousemove', lib.drag);
    }

    function addDragStartEvents() {
        for(var elementId in dropTargets) {
            var elt = document.getElementById(elementId);
            if(elt == null) // if the element isn't on the page at the moment
                continue;
            if(typeof(dropTargets[elementId].dropCode.install) == "function") {
                dropTargets[elementId].dropCode.install(elt);
            } else {
                dropCodeRegistry['default'].install(elt);
            }
        }
    }

    var checkIfStayedOnElementTimer = null;
    var StayOnElement = null;
    function checkIfStayedOnElement() {
        if(StayOnElement == lastTarget) {
            var element = getFirstParentWithHandler(StayOnElement, dropTargets)
            if(typeof(dropTargets[element.id].dropCode.stayedOnElement) == "function") {
                var fakeEvent = { };

                fakeEvent.ctrlKey = oldCtrlKeyStatus; // set the new keystatus in the 'old' status
                fakeEvent.shiftKey = oldShiftKeyStatus;
                fakeEvent.target = StayOnElement;
                dropTargets[element.id].dropCode.stayedOnElement(fakeEvent);

                var options = dropTargets[element.id].options;
                if(options != undefined && options != null) {
                    if(typeof(options.stayOnElementTimeoutInterval) == "number")
                        setTimeout(checkIfStayedOnElement, options.stayOnElementTimeoutInterval);
                }
            }
        }
    }

    function removeDragStartEvents() {
        for(var elementId in dropTargets) {
            var elt = document.getElementById(elementId);
            if(elt == null)
                return;
            if(typeof(dropTargets[elementId].dropCode.uninstall) == "function") {
                dropTargets[elementId].dropCode.uninstall(elt);
            } else {
                dropCodeRegistry['default'].uninstall(elt);
            }
        }
    }

    var isHoveringDropTarget = false;
    var hoverParent = null;
    var checkIfLeftParent = false;

    /**
     * Called in drag-operations, when a element is entered - will automatically check if it was a subelement
     * @param event
     */
    lib.dragOverEnter = function(event) {
        if(!isDragging)
            return;

        event = getEvent(event);
        lastTarget = getTarget(event);

        var element = getFirstParentWithHandler(lastTarget, dropTargets);
        if(element == null || element.id == null || typeof(dropTargets[element.id]) != "object")
            return;

        isHoveringDropTarget = true;

        if(checkIfStayedOnElementTimer != null)
            clearTimeout(checkIfStayedOnElementTimer)

        var options = dropTargets[element.id].options;
        if(options != undefined && options != null) {
            if(typeof(options.stayOnElementTimeout) == "number")
                checkIfStayedOnElementTimer = setTimeout(checkIfStayedOnElement, options.stayOnElementTimeout);
        }
        StayOnElement = lastTarget;

        if(typeof(dropTargets[element.id].dropCode.enter) == "function") { // if a custom dragenter function exists
            if(dropTargets[element.id].dropCode.enter(event)) { // call it, and send event if it returns true
                lib.sendDragEvent(event, "de");
            }
            return;
        }

        if(element != hoverParent) {
            hoverParent = element;
            lib.sendDragEvent(event, "de");
        }
    }

    /**
     * Called in drag-operations, when a element is left - will automatically check if it was a subelement
     * @param event
     */
    lib.dragOverLeave = function(event) {
        if(!isDragging)
            return;

        event = getEvent(event);
        var target = getTarget(event);
        
        clearTimeout(checkIfStayedOnElementTimer);

        var element = getFirstParentWithHandler(target, dropTargets);
        if(element == null || element.id == null || typeof(dropTargets[element.id]) != "object")
            return;

        isHoveringDropTarget = false;

        if(typeof(dropTargets[element.id].dropCode.leave) == "function") { // if a custom dragenter function exists
            if(dropTargets[element.id].dropCode.leave(event)) { // call it, and send event if it returns true
                lib.sendDragEvent(event, "dl");
            }
            return;
        }

        checkIfLeftParent = true;
    }

    function checkIfSendLeaveEvent(currentEvent) {
        var element = getFirstParentWithHandler(getTarget(currentEvent), dropTargets);
        if(checkIfLeftParent && element != hoverParent) {
            var fakeEvent = { };

            fakeEvent.ctrlKey = oldCtrlKeyStatus; // set the new keystatus in the 'old' status
            fakeEvent.shiftKey = oldShiftKeyStatus;
            fakeEvent.target = lastTarget;

            lib.sendDragEvent(fakeEvent, "dl");

            hoverParent = null;
        }
        
        checkIfLeftParent = false;
    }


    /**
     * Returns the first parent-element that has a entry in handler
     * @param element
     * @param handler
     */
    function getFirstParentWithHandlerRecursively(element, handler) {
        if(element.id != undefined && typeof(handler[element.id]) == "object")
            return element;
        else if(element.parentNode != null) {
            return getFirstParentWithHandlerRecursively(element.parentNode, handler);
        } else {
            return null;
        }
    }

    lib.getFirstParentWithHandler = function(element, handler) {
        return getFirstParentWithHandlerRecursively(element, handler);
    }

    function getFirstParentWithHandler(element, handler) {
        return lib.getFirstParentWithHandler(element, handler);
    }

    var backupHandler = null;

    /**
     * Disables all selection in Internet Explorer
     */
    function disableSelection() {
        if(navigator.userAgent.indexOf("MSIE") == -1)
            return;
        backupHandler = document.onselectstart;
        document.onselectstart = function() { return false; }
    }

    /**
     * Reenables selections in Internet Explorer
     */
    function enableSelection() {
        if(navigator.userAgent.indexOf("MSIE") == -1)
            return;
        
        document.onselectstart = backupHandler;
    }


    var backupStartEvent = null;
    
    /**
     * Called upon any drag-operation (mousedown, mousemove)
     * @param event
     */
    lib.drag = function(event) {
        event = getEvent(event);
        var target = getTarget(event);

        if(navigator.userAgent.indexOf("MSIE") != -1) { // only permit the left mouse button
            if(event.button != 1) // ie
                return;
        } else {
            if(event.button != 0) { // firefox/w3c
                return;
            }
        }
        
        if(draggedElementId == null) {
            var element = getFirstParentWithHandler(target, dragSources);

            draggedElementId = element.id;

            backupStartEvent = { }; // backup the event at the time of the first click (because one pixel further away
                                    // the element under the mouse could be a different one)
            for(var prop in event) {
                if(typeof(event[prop]) != "function") {
                    backupStartEvent[prop] = event[prop];
                }
            }

            lastTarget = target;
            hoverParent = element;

            if(typeof(dragSources[element.id].dragCode.dragPrepare) == "function") {
                if(dragSources[element.id].dragCode.dragPrepare(event)) {
                    addDragPrepareEvents();
                } else { // don't start drag and drop
                    draggedElementId = null;
                }
            } else {
                if(dragCodeRegistry['default'].dragPrepare(event)) {
                    addDragPrepareEvents();
                } else { // don't start drag and drop
                    draggedElementId = null;
                }
            }

            isDragging = false;
        } else {
            if(!isDragging) {
                if(pixelDelta < 5) {
                    if(pixelDelta == 0) { // disable selection (in internet explorer) when a drag may start
                        disableSelection();
                    }

                    pixelDelta++;

                    YAHOO.util.Event.stopEvent(event);
                    return false;
                }

                pixelDelta = 0;
                isDragging = true;

                // don't start the drag operation, if the requested action isn't allowed by the source element
                if(isActionAllowedBySource(event) && dragSources[draggedElementId].dragCode.dragStart(backupStartEvent, event)) {
                    addDragStartEvents();
                } else { // abort drag operation
                    removeDragPrepareEvents();
                    isDragging = false;
                    draggedElementId = null;
                }
            } else {
                if(typeof(dragSources[draggedElementId].dragCode.whileDragging) == "function") {
                    dragSources[draggedElementId].dragCode.whileDragging(event);
                }
                checkIfSendLeaveEvent(event);
            }
        }
	}

    /**
     * Called upon a drop - determines if a drop is possible, on which element it was, calls further drop handlers inside the dropcode or aborts the transfer
     * @param event
     */
    lib.drop = function(event) {
        hoverParent = null;

        clearTimeout(checkIfStayedOnElementTimer);

        event = getEvent(event);
        var element = getTarget(event);
        element = getFirstParentWithHandler(element, dropTargets);

        enableSelection();

        if(!isDragging) { // drop called before dragging started
            if(typeof(dragSources[draggedElementId].dragCode.abort) == "function") {
                if(dragSources[draggedElementId].dragCode.abort(document.getElementById(draggedElementId), event)) {
                    removeDragPrepareEvents();
                    draggedElementId = null;
                    isDragging = false;
                }
            } else {
                if(dragCodeRegistry['default'].abort(document.getElementById(draggedElementId), event)) {
                    removeDragPrepareEvents();
                    draggedElementId = null;
                    isDragging = false;
                }
            }
            return;
        }

        isDragging = false;

        if(element == null || dropTargets[element.id] == undefined) {
            lib.sendDragEvent(event, "ab", null);
            removeDragPrepareEvents();
            removeDragStartEvents();
            draggedElementId = null;
            return;
        }

        if(dropTargets[element.id].dropCode.drop(event)) {
            removeDragPrepareEvents();
            removeDragStartEvents();
        }

        draggedElementId = null;
    }

    /**
     * Gets the requested action from the given keys in event
     * @param event
     */
    function getActionFromEvent(event) {
        if(event.shiftKey == false && event.ctrlKey == false) {
            if((dragSources[draggedElementId].sourceActions & ACTION_MOVE) == ACTION_MOVE)
                return ACTION_MOVE;
            if((dragSources[draggedElementId].sourceActions & ACTION_COPY) == ACTION_COPY)
                return ACTION_COPY;
            if((dragSources[draggedElementId].sourceActions & ACTION_LINK) == ACTION_LINK)
                return ACTION_COPY;

            return ACTION_NONE;
        }

        if(event.shiftKey == true && event.ctrlKey == false)
            return ACTION_MOVE;

        if(event.shiftKey == false && event.ctrlKey == true)
            return ACTION_COPY;

        if(event.shiftKey == true && event.ctrlKey == true)
            return ACTION_LINK;

        return ACTION_NONE;
    }

    /**
     * Determines if the requested action by the keys in event is allowed by the drag source
     * @param event
     */
    function isActionAllowedBySource(event) {
        var action = getActionFromEvent(event);

        return (dragSources[draggedElementId].sourceActions & action) == action;
    }

    /**
     * called in the case, that keypresses occured - needs to check if the new (requested) action is allowed
     * @param ctrlKey
     * @param shiftKey
     */
    function ReviewDraggingOperation(ctrlKey, shiftKey) {
        if(!isHoveringDropTarget) // we don't need to do anything if we aren't on a droptarget
            return;

        if(ctrlKey == undefined || shiftKey == undefined)
            error("ReviewDraggingOperation - undefined keys");
        
        var fakeEvent = { };

        // build a fake-event (we can't change the last event object 'cause the attributes are sometimes get-only)
        fakeEvent.ctrlKey = ctrlKey; // set the new keystatus in the 'old' status
        fakeEvent.shiftKey = shiftKey;
        fakeEvent.target = lastTarget;

        // simulate dragenter event (will send target + requested action, will answer if action is allowed or not)
        hoverParent = null;
        lib.dragOverEnter(fakeEvent)
    }

    /**
	* Public (exported) functions, to be used with wingS.sdnd.functionname
	*/

    /**
     * Adds element as a drag source, dragCode is either null, or a customized drag-and-drop Handler
     * @param element Element to be registered as a drag source
     * @param sourceActions Source (Drag) Actions supported by the drag source
     * @param dragCode Either a string, pointing to the dragcode-handler in the dragcoderegistry or a dragcode-handler
     */
    lib.addDragSource = function(elementId, sourceActions, dragCode) {
        if(typeof(elementId) == 'string') {
            var element = document.getElementById(elementId);
        }

        dragSources[elementId] = { };

        if(typeof(dragCode) == 'string') {
            if(typeof(dragCodeRegistry[dragCode]) != "object") {
                error("no dragcode registered for '" + dragCode +"'");
                return;
            }
            dragSources[elementId].dragCode = dragCodeRegistry[dragCode];
        } else if(typeof(dragCode) == 'object') {
	        dragSources[elementId].dragCode = dragCode;
	    } else {
	        error("invalid dragCode");
            return;
        }

        if(typeof(sourceActions) == 'number') {
            dragSources[elementId].sourceActions = sourceActions;
        } else {
            error("no actions for addDragSource given");
            return;
        }

        if(element == null) // element isn't on page (at the moment)
            return;

        if(typeof(dragSources[element.id].dragCode.install) == "function") {
            dragSources[elementId].dragCode.install(element);
        } else {
            dragCodeRegistry['default'].install(element);
        }
    };


    /**
     * Removes element as a drag source
     * @param element
     */
    lib.removeDragSource = function(element) {
        if(typeof(element) == "string")
            element = document.getElementById(element);

        if(typeof(dragSources[element.id].dragCode.uninstall) == "function") {
            dragSources[element.id].dragCode.uninstall(element);
            dragSources[element.id] = null;
        } else {
            dragCodeRegistry['default'].uninstall(element);
        }
	};

    /**
     * Adds element as a drop target
     * @param element Element to be registered
     * @param dropCode Dropcode to use
     * @param options Array of options for the element
     */
    lib.addDropTarget = function(elementId, dropCode, options) {
        if(typeof(elementId) == 'string') {
            var element = document.getElementById(element);
        }

        dropTargets[elementId]  = { };
        
        if(typeof(dropCode) == 'string') {
            dropTargets[elementId].dropCode = dropCodeRegistry[dropCode];
            dropTargets[elementId].options = options;
        } else if(typeof(dragCode) == 'object') {
	        dropTargets[elementId].dropCode = dropCode;
            dropTargets[elementId].options = options;
        } else {
	        error("invalid dropCode");
        }
    };

    /**
     * Removes a drop target
     * @param element
     */
    lib.removeDropTarget = function(element) {
		if(typeof(element) == "string") {
            dropTargets[element] = null;
        } else { // assume it's a object
            dropTargets[element.id] = null;
        }
	};

    /**
     * Sets the SDnD Manager isntance
     * @param newDnDManager
     */
    lib.setManager = function(newDnDManager) {
		manager = newDnDManager;
	};

    /**
     * Returns the Classname of the Drag-and-Drop Manager, to be used for ajax requests
     */
    lib.getManager = function() {
        return manager;
    };

    /**
     * Returns if a drag operation is in progress
     */
    lib.isDragging = function() {
        return isDragging;
    };

    var oldShiftKeyStatus = false;
    var oldCtrlKeyStatus = false;

    /**
     * Method to receive keypresses while dragging
     * @param ctrlKey
     * @param shiftKey
     */
    lib.keyChange = function(ctrlKey, shiftKey) {
        if(oldCtrlKeyStatus != ctrlKey || oldShiftKeyStatus != shiftKey ) {
            // TODO: maybe wait a little to check if another key was pressed/released to prevent server hammering
            ReviewDraggingOperation(ctrlKey, shiftKey);
        }

        oldCtrlKeyStatus = ctrlKey;
        oldShiftKeyStatus = shiftKey;
    };

    /**
     * Sends a drag event to the SDragAndDropManager
     * @param event
     * @param dragEvent
     * @param additionalInformation
     */
    lib.sendDragEvent = function(event, dragEvent, additionalInformation) {
        // format: <eventdescriptor>:sourceid:destinationid:eventdata:requestedaction:additional (depends on type of element/component)
        // eventdescriptor: ds = dragstart, de = dragenter, dl = dragleave, dr = drop
        // on ds source and destination will be equal
        var target = getTarget(event);

        if(dragEvent == "ds") {
            target = getFirstParentWithHandler(target, dragSources);
        } else if(dragEvent == "de") {
            target = getFirstParentWithHandler(target, dropTargets);
        } else if(dragEvent == "dl") {
            target = getFirstParentWithHandler(target, dropTargets);
        } else if(dragEvent == "dr") {
            target = getFirstParentWithHandler(target, dropTargets);
        } else if(dragEvent  == "st") {
            target = getFirstParentWithHandler(target, dropTargets);
        }

        if(additionalInformation == undefined || additionalInformation == null) {
            additionalInformation = "";
        } else {
            additionalInformation = ";" + additionalInformation;
        }
        
        var targetstr = ""
        if(target != null) {
            targetstr = target.id;
        }
        var str = dragEvent + ";" + draggedElementId + ";" + targetstr + ";" + getActionFromEvent(event) + additionalInformation;
        wingS.request.sendEvent(event, true, true, lib.getManager(), str);
    };

    /**
     * Registers Code with type <i>codetype</i> with key and code
     * @param codetype Either 'drag' or 'drop'
     * @param key Key 
     * @param code
     */
    lib.registerCode = function(codetype, key, code) {
        if(codetype == "drag") {
            dragCodeRegistry[key] = code;
        } else if(codetype == "drop") {
            dropCodeRegistry[key] = code;
        }
    };

    /**
     * Returns a array of droptargets - the eleemntid is the key
     */
    lib.getDropTargets = function() {
        return dropTargets;
    };

    /**
     * Returns a array of dragsources, the elementid is the key
     */
    lib.getDragSources = function() {
        return dragSources;
    };

    /**
     * Reapplys all Drag and Drop handlers recursively to element and all of it's childs
     * @param element
     */
    function reapplyDragAndDropOperationsRecursively(element) {
        if(element != null && typeof(element) == "object" && element.id != null && typeof(element.id) == "string" && dragSources[element.id] != null && typeof(dragSources[element.id]) == "object") {
            if(typeof(dragSources[element.id].dragCode.install) == "function") {
                dragSources[element.id].dragCode.install(element);
            } else {
                dragCodeRegistry['default'].install(element);
            }
        }

        if(element != null && typeof(element) == "object" && element.id != null && typeof(element.id) == "string" && dropTargets[element.id] != null && typeof(dropTargets[element.id]) == "object") {
            if(typeof(dropTargets[element.id].dropCode.install) == "function") {
                dropTargets[element.id].dropCode.install(element);
            } else {
                dropCodeRegistry['default'].install(element);
            }
        }

        if(element == null || element.childNodes == null || typeof(element.childNodes) == 'undefined')
            return;
        
        for(var i=0; i<element.childNodes.length; ++i) {
            if(element.childNodes[i] != null)
                reapplyDragAndDropOperationsRecursively(element.childNodes[i]);
        }
    };

    /**
     * Needs to be called when a element has lost it's handlers due to a replacement
     * @param element
     */
    lib.elementUpdated = function(elementId) {
        var element = document.getElementById(elementId);
        reapplyDragAndDropOperationsRecursively(element);
    };

    /**
     * Helper function for dragging text components
     */
    lib.dragStartTextComponentsMSIE = function() { // workaround
        var event = window.event;
        draggedElementId = this.id;
        addDragPrepareEvents();
        lib.drag(event);
        return false;
    }

    /**
     * Gets the Lsat Target - may be useful in custom handlers
     */
    lib.getLastTarget = function() {
        return lastTarget;
    }
})();
