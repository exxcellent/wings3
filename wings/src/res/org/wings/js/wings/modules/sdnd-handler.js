(function() {
    var lib = wingS.sdnd;

    function getFirstParentWithHandlerBeforeParent(element, handler, parent) {
        return lib.getFirstParentWithHandlerBeforeParent(element, handler, parent);
    }

    function isMSIE() {
        if(document.all)
            return true;
        return false;
    }

    function error(text) {
        return lib.error(text);
    }

    function addEvent(element, event, func) {
        return lib.addEvent(element, event, func);
    }

    function removeEvent(element, event, func) {
        return lib.removeEvent(element, event, func);
    }

    function getTarget(event) {
        return wingS.event.getTarget(event);
	}

    function stopEvent(event) {
        return YAHOO.util.Event.stopEvent(event);
    }

    function log(text) {
        if("console" in window && console.log) {
            console.log(text);
        }
    }

    function getFirstParentWithHandler(element, handler) {
        return lib.getFirstParentWithHandler(element, handler);
    }

    function getFirstParentWithTagName(element, tagName, tagName2) {
        if(element != null && element.tagName == tagName || (tagName2 != undefined && tagName2 != null && element.tagName == tagName2))
            return element;
        else if(element.parentNode)
            return getFirstParentWithTagName(element.parentNode, tagName, tagName2);
        else
            return null;
    }

    /**
     * Drag/Drop-Codes
     *
     *
     *
     *
     */

    /**
     * Text Element Drag and Drop Code
     */
    (function() {
        function getSelectionStart(event, target) {
            if(target.selectionStart != undefined)
                return target.selectionStart;

            // get the selection, move the start of the selection to the beginning, return the absolute number_of_chars_moved
            if(target.tagName == "INPUT")
                return Math.abs(document.selection.createRange().moveStart("character", -1000000));

            if(target.tagName == "TEXTAREA") { // the method for INPUT returns wrong values on TEXTAREAs,
                                                // this method doesn't work with INPUTs
                var range = document.selection.createRange();
                var stored_range = range.duplicate();
                stored_range.moveToElementText(target);
                stored_range.setEndPoint( 'EndToEnd', range );
                return stored_range.text.length - range.text.length;
            }

            return -1;
        }

        function getSelectionEnd(event, target) {
            if(target.selectionEnd != undefined)
                return target.selectionEnd;

            // get the selection, move the end of the selection to the beginning, return the absolute number_of_chars_moved
            if(target.tagName == "INPUT")
                return Math.abs(document.selection.createRange().moveEnd("character", -1000000));

            if(target.tagName == "TEXTAREA") { // the method for INPUT returns wrong values on TEXTAREAs,
                                                // this method doesn't work with INPUTs
                var range = document.selection.createRange();
                var stored_range = range.duplicate();
                stored_range.moveToElementText(target);
                stored_range.setEndPoint( 'EndToEnd', range );
                return stored_range.text.length;
            }
            return -1;
        }

        function getClickedPosition(event, target) {
            if(event.rangeOffset) { // gecko code
                var range = document.createRange(); // unfortunately .rangeOffset only provides us with the position within the current line
                var rangeStart = event.rangeOffset; // therefore we create a range and extend it using the "internal" anonymous div that
                var rangeEnd = event.rangeOffset;   // ff uses to render the element, other methods (.previousSibling etc. do not work, as
                range.setStart(event.originalTarget, 0); // they are forbidden to be accessed within TextNodes)
                range.setEnd(event.rangeParent, event.rangeOffset); // this unfortunately doesn't  work if event.originalTarget
                                                        // is the textarea instead of the anonymous div - luckily
                var contents = range.cloneContents();	// this only happens when the cursor it dropped very close to the edges - in this case
                var lengthOfRange = 0;					// the lengthOfRange stays 0 and the text is inserted at the beginning - when dragging it
                var childNodes = contents.childNodes;	 // doesn't matter
                for(var i=0; i<childNodes.length;++i) {
                    if(childNodes[i].nodeType == 3) {
                        lengthOfRange += childNodes[i].length;
                    } else { //assume it's a <br> tag
                        lengthOfRange += 1;
                    }
                }

                return lengthOfRange;
            }

            if(target.tagName == "INPUT") {
                if(!target.createTextRange) { // if there is no .rangeOffset available, but the browser isn't ie (most likely a click into the scrollfield, decide on server what position to use)
                    return -1;
                }
                var range = target.createTextRange();
                range.moveToPoint(event.clientX, event.clientY);
                var pos = Math.abs(range.moveStart('character', -1));
                // buggy in ie - god knows why
                return pos;
            } else if(target.tagName == "TEXTAREA") {
                if(!target.createTextRange) { // if there is no .rangeOffset available, but the browser isn't ie (most likely a click into the scrollfield, decide on server what position to use)
                    return -1;
                }
                var range = target.createTextRange();
                range.moveToPoint(event.x, event.y);
                var pos = Math.abs(range.moveEnd('character', -1000000));
                return pos;
            }

            return -1;
        }

        function isSelectionEvent(event) {
            var target = getTarget(event);
            // returns true if you want to drag 1 selected char (not fixable!)
            if(target.tagName == "INPUT" || target.tagName == "TEXTAREA") {
                if(document.selection) // don't decide for internet explorer, it'll decide itself
                    return false;
                
                var rangeOffset = getClickedPosition(event, target);
                var selectionStart = getSelectionStart(event, target);
                var selectionEnd = getSelectionEnd(event, target);
                if(selectionStart > selectionEnd) {
                    var temp = selectionEnd;
                    selectionEnd = selectionStart;
                    selectionStart = temp;
                }

                if(selectionStart == selectionEnd)
                    return true;
                if((rangeOffset <= selectionStart || rangeOffset >= selectionEnd))
                    return true;
            }

            return false;
        };

        var textDragCode = {
            install : function(element) {
                if(isMSIE()) { // internet explorer
                    // use the ondragstart event on textelements in the internet explorer
                    // as it is impossible to determine if a user wanted to start
                    // a new selection or drag the text with onmousedown etc.
                    // because it is impossible to determine the position of the cursor
                    // in the text, when a selection is active in the onmousedown event
                    addEvent(element.id, 'dragstart', lib.dragStartTextComponentsMSIE);
                } else {
                    addEvent(element.id, 'mousedown', lib.drag);
                }
            },
            uninstall : function(element) {
                if(isMSIE()) {
                    removeEvent(element.id, 'dragstart', lib.dragStartTextComponentsMSIE);
                } else {
                    removeEvent(element.id, 'mousedown', lib.drag);
                }
            },
            abort : function(element, event) {
                if(event.rangeOffset) { // put the caret at the position where clicked - removes selection, works in ff
                    element.selectionStart = event.rangeOffset;
                    element.selectionEnd = event.rangeOffset;
                }  /*else { /* ie handles the textfield dragging differently, therefore no need for this, maybe in opera?
                    if(document.selection) {
                        var range = document.selection.createRange();
                        range.moveToPoint(event.clientX, event.clientY);
                    }
                }    */

                return true;
            },
            dragPrepare : function(event) { // decides if a drag operation is possible after the initial click
                if(isSelectionEvent(event))
                    return false;

                stopEvent(event);
                return true;
            },
            dragStart : function(event, realEvent) { // is called when the drag operation should start (first mousemove while clicking)
                                          // - decides, if the whiledragging events should be registered (mouseenter/leave)
                var target = getTarget(event);

                var additionalParams = null;
                if(target.tagName == "INPUT" || target.tagName == "TEXTAREA") {
                    additionalParams = getSelectionStart(event, target) + "-" + getSelectionEnd(event, target);
                }
                lib.sendDragEvent(event, "ds", additionalParams);

                stopEvent(realEvent);
                return true;
            }
        }

        var textDropCode = {
            drop : function(event) {    // called when the mouse button is released on a droptarget
                                        // - decides if the events should be deregistered
                                        // return false only if you're using your own events
                var target = getTarget(event);
                var additionalParams = null;
                if(target.tagName == "INPUT" || target.tagName == "TEXTAREA") {
                    additionalParams = getClickedPosition(event, target);
                }

                lib.sendDragEvent(event, "dr", additionalParams);

                stopEvent(event);
                return true;
            }
        }

        function sendCaretPositionAndSelection(event, destinationElementId) {
            event = wingS.event.getEvent(event);
            var pos = getClickedPosition(event, wingS.event.getTarget(event));
            var selStart = getSelectionStart(event, wingS.event.getTarget(event));
            var selEnd = getSelectionEnd(event, wingS.event.getTarget(event));

            var posString = "";
            if(pos == -1) {
                posString = selStart + "-" + selEnd;
            } else {
                posString = ""+pos;
            }
            
            wingS.request.sendEvent(event, false, true, destinationElementId, posString, null);
        }

        lib.installTextPositionHandler = function(elementId, destinationElementId) {
            lib.addEvent(elementId, "select", function(event) { sendCaretPositionAndSelection(event, destinationElementId); });
            lib.addEvent(elementId, "mousedown", function(event) { sendCaretPositionAndSelection(event, destinationElementId); });
            lib.addEvent(elementId, "keyup", function(event) { sendCaretPositionAndSelection(event, destinationElementId); });
        }

        lib.registerCode('drag', 'text', textDragCode);
        lib.registerCode('drop', 'text', textDropCode);
    })();

    /**
     * List Drag and Drop Code
     */
    (function(){
        function isSelectionEvent(event) {
            var target = getTarget(event);
            if(target.tagName == "OPTION") { // lists in firefox
                if(target.selected == false) {// if it isn't selected yet, it is a selection event
                    return true;
                }

                return false;
            } else if(target.tagName == "SELECT") { // TODO: implement this for ie (if possible)
                log("determinating if this was a selection event isn't possible in internet explorer");
    /*                if(event.button == 2) {
                    return false;
                } else
                    return true; */
            }

            return false;
        }

        function getRowIndex(event) {
            var target = getTarget(event);
            var parentElement = null;

            if(target.tagName == "OPTION") { // in firefox we can attach mouselisteners to the option elements, therefore just count at which element-position we are
                parentElement = getFirstParentWithTagName(target, "SELECT")
            } else if(target.tagName == "SELECT") { // ie unfortunately doesn't give us the OPTION-element on mouseover-events, therefore no way to get the index
                parentElement = null;
            } else {
                target = getFirstParentWithTagName(target, "LI")
                parentElement = getFirstParentWithTagName(target, "UL", "OL")
            }

            var childrenNumber = -1;
            if(parentElement != null) {
                if(parentElement.childNodes) {
                    childrenNumber = 0;
                    for(var i=0; i<parentElement.childNodes.length; ++i) {
                        if(parentElement.childNodes[i].tagName == "LI" || parentElement.childNodes[i].tagName == "OPTION") {
                            if(parentElement.childNodes[i] == target) {
                                break;
                            }
                            childrenNumber++; // only count LI-nodes, not text or comments
                        }
                    }

                } else {
                    error('element.childNodes not supported on this browser');
                }
            } // else would be the internet-explorer case
            
            return childrenNumber;
        }

        var listDragCode = {
            install : function(element) { // called upon addDragSource
                // in ff:
                // if you register mousdown with a select element, you'd get the event in ff AFTER the selection is done
                // therefore it is impossible to detect if the user wanted to start a drag-operation or selection
                // so we must register every option on the mousedown and get the parentNode of it for the real dragCode

                // in ie (at least in 7)
                // mousedown isn't available in the OPTION tag, therefore we register onmousedown in the select-element
                // and have to look
                if(element.tagName == "UL" || element.tagName == "OL") { // setShowAsFormComponent == false
                    addEvent(element.id, 'mousedown', lib.drag);
                } else { // setShowAsFormComponent = true
                    if(!isMSIE()) {
                        var childNodes = element.options;
                        for(var i=0;i<childNodes.length; ++i) {
                            addEvent(childNodes[i], 'mousedown', lib.drag);
                        }
                    } else {
                        addEvent(element.id, 'mousedown', lib.drag);
                    }
                }
            },
            uninstall : function(element) { // called upon removeDragSource
                if(!isMSIE()) {
                    var childNodes = element.options;
                    for(var i=0;i<childNodes.length; ++i) {
                        removeEvent(childNodes[i], 'mousedown', lib.drag);
                    }
                } else {
                    removeEvent(element.id, 'mousedown', lib.drag);
                }
            },
            abort : function(element, event) { // gets called in case the drag operation wasn't started (i.e. click into the selection)
                                        // needs to reset the selection
                if(element.tagName == "UL" || element.tagName == "OL") {
                    return true;
                }

                var node = null;
                if(element.tagName == "SELECT") { // get the SELECT element
                    var childNodes = element.options ? element.options : element.all;
                    for(var i=0;i<childNodes.length; ++i) { // deselect all entries
                        childNodes[i].selected = false;
                    }
                }

                if(event == null)
                    return true;

                var target = getTarget(event);
                if(target && target.tagName == "OPTION") { // reselect the clicked element (in ff, ie doesn't give as the selectedindex)
                    target.selected = true;
                }

                return true;
            },
            dragPrepare : function(event) { // decides if a drag operation is possible after the initial click
                var target = getTarget(event);

                if(target.tagName == "UL" || target.tagName == "OL")
                    return false;

                if(target.tagName == "TEXTAREA" || target.tagName == "INPUT") {
                    var dragcode = lib.getCode('drag', 'text'); // this could also be archived by just using textDragCode
                    return dragcode.dragPrepare(event);
                }

                if(isSelectionEvent(event))
                    return false;

                stopEvent(event);
                return true;
            },
            dragStart : function(event, realEvent) { // is called when the drag operation should start (first mousemove while clicking)
                                          // - decides, if the whiledragging events should be registered (mouseenter/leave)
                lib.sendDragEvent(event, "ds", getRowIndex(event) + ":ctrlKey=" + event.ctrlKey + ":shiftKey=" + event.shiftKey);

                stopEvent(realEvent);
                return true;
            }
        };

        var listDropCode = {
            drop : function(event) {    // called when the mouse button is released on a droptarget
                                        // - decides if the events should be deregistered
                                        // return false only if you're using your own events
                lib.sendDragEvent(event, "dr", getRowIndex(event));

                stopEvent(event);
                return true;
            }
        };

        lib.registerCode('drag', 'list', listDragCode);
        lib.registerCode('drop', 'list', listDropCode);
    })();

    /**
     * Table Drag and Drop code
     */
    (function(){
        function getRowAndCol(event) {
            var target = getTarget(event);
            if(target == null)
                return "-1:-1";
            
            var colElt = wingS.util.getParentByAttribute(target, "col");
            if(colElt == null)
                return "-1:-1";
            var col = -1;
            col = colElt && colElt.getAttribute("col");
            var rowElt = getFirstParentWithTagName(colElt, "TR");
            var row = -1;
            row = rowElt && rowElt.sectionRowIndex;

            return row + ":" + col;
        }
        
        var tableDragCode = {
            dragStart : function(event, realEvent) { // is called when the drag operation should start (first mousemove while clicking)
                                          // - decides, if the whiledragging events should be registered (mouseenter/leave)
                if(event == null)
                    return false;
                
                lib.sendDragEvent(event, "ds", getRowAndCol(event) + ":ctrlKey=" + event.ctrlKey + ":shiftKey=" + event.shiftKey);

                stopEvent(realEvent);
                return true;
            }
        }

        var tableDropCode = {
            drop : function(event) {    // called when the mouse button is released on a droptarget
                                        // - decides if the events should be deregistered
                                        // return false only if you're using your own events
                lib.sendDragEvent(event, "dr", getRowAndCol(event));

                stopEvent(event);
                return true;
            }
        }

        lib.registerCode('drag', 'table', tableDragCode);
        lib.registerCode('drop', 'table', tableDropCode);
    })();

    /**
     * Tree Drag and Drop code
     */
    (function(){
        function getRowIndex(event, dragOrDropSources) {
            var target = getTarget(event);
            var parent = getFirstParentWithHandler(target, dragOrDropSources);
            var element = getFirstParentWithHandlerBeforeParent(target, 'onclick', parent);

            var rowIndex = -1;
            if(element != null) {
                rowIndex = element.getAttribute && element.getAttribute("row");
            }
            
            return rowIndex;
        }

        var treeDragCode = {
            dragStart : function(event, realEvent) { // is called when the drag operation should start (first mousemove while clicking)
                                          // - decides, if the whiledragging events should be registered (mouseenter/leave)
                lib.sendDragEvent(event, "ds", getRowIndex(event, lib.getDragSources()) + ":ctrlKey=" + event.ctrlKey + ":shiftKey=" + event.shiftKey);


                stopEvent(realEvent);
                return true;
            }
        }

        var treeDropCode = {
            stayedOnElement : function(event) {
                var rowIndex = getRowIndex(event, lib.getDropTargets());

                if(rowIndex != -1)
                    lib.sendDragEvent(event, "st", rowIndex);
            },
            drop : function(event) {    // called when the mouse button is released on a droptarget
                                        // - decides if the events should be deregistered
                                        // return false only if you're using your own events
                lib.sendDragEvent(event, "dr", getRowIndex(event, lib.getDropTargets()));

                stopEvent(event);
                return true;
            }
        }

        lib.registerCode('drag', 'tree', treeDragCode);
        lib.registerCode('drop', 'tree', treeDropCode);
    })();



    /**
     * Default Drag and Drop code
     */
    (function(){
        var defaultDragCode = {
            install : function(element) {
                addEvent(element.id, 'mousedown', lib.drag); // use the mousedown indicator for every other element/browser
            },
            uninstall : function(element) {
                removeEvent(element.id, 'mousedown', lib.drag);
            },
            abort : function(element, event) {
                return true;
            },
            dragPrepare : function(event) { // decides if a drag operation is possible after the initial click
                var target = getTarget(event);

                if(target.tagName == "TEXTAREA" || target.tagName == "INPUT") {
                    var dragcode = lib.getCode('drag', 'text'); // this could also be archived by just using textDragCode
                    return dragcode.dragPrepare(event);
                }
                
                stopEvent(event);

                return true;
            },
            dragStart : function(event, realEvent) { // is called when the drag operation should start (first mousemove while clicking)
                                          // - decides, if the whiledragging events should be registered (mouseenter/leave)
                var target = getTarget(event);

                lib.sendDragEvent(event, "ds", null);

                stopEvent(realEvent);
                return true;
            },
            whileDragging : function(event) { // called while the mouse is dragged (every mousemove)
                stopEvent(event);
            }
        }

        var defaultDropCode = {
            install : function(element) {
                addEvent(element.id, 'mouseover', lib.dragOverEnter);
                addEvent(element.id, 'mouseout', lib.dragOverLeave);
            },
            uninstall : function(element) {
                removeEvent(element.id, 'mouseout', lib.dragOverLeave);
                removeEvent(element.id, 'mouseover', lib.dragOverEnter);
            },
            drop : function(event) {    // called when the mouse button is released on a droptarget
                                        // - decides if the events should be deregistered
                                        // return false only if you're using your own events
                var target = getTarget(event);

                lib.sendDragEvent(event, "dr", null);

                stopEvent(event);
                return true;
            }
        }

        lib.registerCode('drag', 'default', defaultDragCode);
        lib.registerCode('drop', 'default', defaultDropCode);
    })();
})();
