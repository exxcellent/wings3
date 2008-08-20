/***************************************************************************************************
 * WINGS.UPDATE  --  contains: functions to process incremental updates
 **************************************************************************************************/

// Create module namespace
wingS.namespace("update");

/**
 * Adds or removes a script header with the given parameters.
 * @param {Boolean} add - true, if the header should be added
 * @param {String} src - the source of the script header
 * @param {String} type - the type of the script header
 * @param {int} index - the desired position of the header
 */
wingS.update.headerScript = function(add, src, type, index) {
    var loadMethod = wingS.update.headerScript;
    var loadMethodArgs = new Array(add, src, type, index);
    // Enqueue header download in case another one isn't finished yet.
    if (wingS.global.enqueueThisHeader(loadMethod, loadMethodArgs)) return;

    if (src == null || type == null) return;
    var head = document.getElementsByTagName("HEAD")[0];

    if (add) {
        wingS.global.startLoadingHeader();
        var script = document.createElement("script");
        script.src = src;
        script.type = type;
        if (index == null || index < 0 || index > head.childNodes.length) {
            head.appendChild(script);
        } else {
            head.insertBefore(script, wingS.update.headerNext(index));
        }
    } else {
        var scripts = head.getElementsByTagName("SCRIPT");
        for (var i = 0; i < scripts.length; i++) {
            if (scripts[i].getAttribute("src") == src &&
                scripts[i].getAttribute("type") == type) {
                head.removeChild(scripts[i]);
            }
        }
    }
};

/**
 * Adds or removes a link header with the given parameters.
 * @param {Boolean} add - true, if the header should be added
 * @param {String} href - the source of the link header
 * @param {String} type - the type of the link header
 * @param {String} rel - the relationship of the link header
 * @param {String} rev - the reverse relationship of the link
 * @param {String} target - the target of the link header
 * @param {int} index - the desired position of the header
 */
wingS.update.headerLink = function(add, href, type, rel, rev, target, index) {
    if (href == null || type == null) return;
    var head = document.getElementsByTagName("HEAD")[0];

    if (add) {
        var link = document.createElement("link");
        link.href = href;
        link.type = type;
        if (rel != null)
            link.rel = rel;
        if (rev != null)
            link.rev = rev;
        if (target != null)
            link.target = target;
        if (index == null || index < 0 || index > head.childNodes.length) {
            head.appendChild(link);
        } else {
            head.insertBefore(link, wingS.update.headerNext(index));
        }
    } else {
        var links = head.getElementsByTagName("LINK");
        for (var i = 0; i < links.length; i++) {
            if (links[i].getAttribute("href") == href &&
                links[i].getAttribute("type") == type) {
                head.removeChild(links[i]);
            }
        }
    }
};

/**
 * Returns the header next to the one with the given index.
 * @param {int} index - the desired position of the header
 */
wingS.update.headerNext = function(index) {
    var head = document.getElementsByTagName("HEAD")[0];
    var position = 0;
    var header = null;
    for (var i = 0; i < head.childNodes.length; i++) {
        // We have to filter everything (eg. whitespace
        // or title nodes) except for element nodes with
        // a tag name of either script, link or meta.
        header = head.childNodes[i];
        if (header.nodeType == 1 && (
            header.nodeName == "SCRIPT" ||
            header.nodeName == "LINK" ||
            header.nodeName == "META")) {
            if (index == position) break;
            position++;
        }
    }
    // It's ok if "header" is still "null"
    // since head.insertBefore(XXX, null)
    // is equal to head.appendChild(XXX).
    return header;
};

/**
 * Updates the current event epoch of this frame.
 * @param {String} epoch - the current event epoch
 */
wingS.update.epoch = function(epoch) {
    wingS.global.eventEpoch = epoch;
};

/**
 * Gives the focus to the component with the given ID.
 * @param {String} id - the ID of the component to focus
 */
wingS.update.focus = function(id) {
    wingS.util.requestFocus(id);
};

/**
 * Enables or disabled incremental updates for this frame.
 * @param {boolean} enabled - true, if updates are allowed
 */
wingS.update.updateEnabled = function(enabled) {
    wingS.global.updateEnabled = enabled;
};

/**
 * Replaces the HTML code of the component with the given ID.
 * @param {String} componentId - the ID of the component
 * @param {String} html - the new HTML code of the component
 * @param {String} exception - the server exception (optional)
 */
wingS.update.component = function(componentId, html, exception) {
    // Exception handling
    if (exception != null) {
        var update = "ComponentUpdate for '" + componentId + "'";
        wingS.update.alertException(exception, update);
        return;
    }

    // Search DOM for according component
    var component = document.getElementById(componentId);
    if (component == null) {
        throw {message: "Could not find component with id '" + componentId + "'"};
    }

    // Handle layout workaround for IE (if necessary)
    var wrapping = component.getAttribute("wrapping");
    if (wrapping != null && !isNaN(wrapping)) {
        for (var i = 0; i < parseInt(wrapping); i++) {
            component = component.parentNode;
        }
    }

    // Replace the actual JavaScript element
    wingS.update.element(component, html);
    wingS.sdnd.elementUpdated(componentId);
};

/**
 * Replaces the HTML code of the given JavaScript element.
 * @param {String} element - the JavaScript element to replace
 * @param {String} html - the new HTML code of the component
 */
wingS.update.element = function(element, html) {
    if (typeof element.outerHTML != "undefined") {
        // Use outerHTML if available
        element.outerHTML = html;
    } else {
        var parent = element.parentNode;
        if (parent == null) return;

        var nrOfChildElements = 0;
        for (var i = 0; i < parent.childNodes.length; i++) {
            // We have to filter everything except element nodes
            // since browsers treat whitespace nodes differently
            if (parent.childNodes[i].nodeType == 1) {
                nrOfChildElements++;
            }
        }

        if (nrOfChildElements == 1) {
            // If there is only one child it must be our element
            parent.innerHTML = html;
        } else {
            var range;
            // If there is no other way we have to use proprietary methods
            if (document.createRange && (range = document.createRange()) &&
                range.createContextualFragment) {
                range.selectNode(element);
                var newElement = range.createContextualFragment(html);
                parent.replaceChild(newElement, element);
            }
        }
    }
};

/**
 * Adds or replaces the HTML code of the menu with the given ID.
 * @param {String} menuId - the ID of the menu to add or replace
 * @param {String} html - the new HTML code of the menu
 * @param {String} exception - the server exception (optional)
 */
wingS.update.componentMenu = function(menuId, html, exception) {
    // Exception handling
    if (exception != null) {
        var update = "ComponentUpdate for '" + menuId + "'";
        wingS.update.alertException(exception, update);
        return;
    }

    if (document.getElementById(menuId) == null) {
        var menues = document.getElementById("wings_menues");
        wingS.util.appendHTML(menues, html);
    } else {
        wingS.update.component(menuId, html);
    }
};

/**
 * Updates the value of the component with the given ID.
 * @param {String} componentId - the ID of the component
 * @param {String} value - the new value of the component
 * @param {String} exception - the server exception (optional)
 */
wingS.update.value = function(componentId, value, exception) {
    // Exception handling
    if (exception != null) {
        var update = "TextUpdate for '" + componentId + "'";
        wingS.update.alertException(exception, update);
        return;
    }

    var component = document.getElementById(componentId);
    if (component.nodeName == "NOBR")
        component.innerHTML = value;
    else
        component.value = value;
};

/**
 * Updates the text of the component with the given ID.
 * @param {String} componentId - the ID of the component
 * @param {String} text - the new text of the component
 * @param {String} exception - the server exception (optional)
 */
wingS.update.text = function(componentId, text, exception) {
    // Exception handling
    if (exception != null) {
        var update = "TextUpdate for '" + componentId + "'";
        wingS.update.alertException(exception, update);
        return;
    }

    var component = document.getElementById(componentId);
    var textNode = component.getElementsByTagName("SPAN")[0];
    textNode.parentNode.innerHTML = text;
};

/**
 * Updates the icon of the component with the given ID.
 * @param {String} componentId - the ID of the component
 * @param {String} icon - the new icon of the component
 * @param {String} exception - the server exception (optional)
 */
wingS.update.icon = function(componentId, icon, exception) {
    // Exception handling
    if (exception != null) {
        var update = "IconUpdate for '" + componentId + "'";
        wingS.update.alertException(exception, update);
        return;
    }

    var component = document.getElementById(componentId);
    var iconNode = component.getElementsByTagName("IMG")[0];
    iconNode.parentNode.innerHTML = icon;
};

/**
 * Updates the encoding of the form with the given ID.
 * @param {String} formId - the ID of the form to update
 * @param {int} encoding - the encoding to be set
 */
wingS.update.encoding = function(formId, encoding) {
    var form = document.getElementById(formId);
    form.enctype = encoding;
};

/**
 * Updates the method of the form with the given ID.
 * @param {String} formId - the ID of the form to update
 * @param {int} method - the method to be set
 */
wingS.update.method = function(formId, method) {
    var form = document.getElementById(formId);
    form.method = method;
};

/**
 * Updates the class of the component with the given ID.
 * @param {String} componentId - the ID of the component
 * @param {String} value - the new class of the component
 * @param {String} exception - the server exception (optional)
 */
wingS.update.className = function(componentId, className, exception) {
    // Exception handling
    if (exception != null) {
        var update = "ClassNameUpdate for '" + componentId + "'";
        wingS.update.alertException(exception, update);
        return;
    }

    var component = document.getElementById(componentId);
    if (component.nodeName == "NOBR")
        component.innerHTML = className;
    else
        component.className = className;
};

/**
 * Adds a window to the window stack of a SRootContainer.
 * @param {String} componentId - the ID of the window stack
 * @param {String} html - the new HTML code of the window
 */
wingS.update.addWindow = function(componentId, skeleton) {

	// Search DOM for according component	
	var component = document.getElementById(componentId);
	if (component == null) {
        throw {message: "Could not find component with id '" + componentId + "'"};
    }
	
	// Create wrapping tr and td for container table.
	var tr = component.tBodies[0].insertRow(-1);
	var td = document.createElement("td");
	
	tr.appendChild(td);
	wingS.util.appendHTML(td, skeleton);
};

/**
 * Removes a window of the window stack.
 * @param {String} componentId - the ID of the window
 */
wingS.update.removeWindow = function(componentId) {

	// Search DOM for according component	
	var component = document.getElementById(componentId);
	if (component == null) {
        throw {message: "Could not find component with id '" + componentId + "'"};
    }

    var tr = component.parentNode.parentNode.parentNode;
    var table = tr.parentNode;

	// Hide dialog mask if available.
	var dialog = window["dialog_" + componentId];
    if (dialog != null) {
		dialog.hideMask();
        dialog.destroy();
        window["dialog_" + componentId] = null;     // clear global variable
    }
	
	table.removeChild(tr);
};

/**
 * This update scrolls the table with the given ID.
 * @param {String} tableId - the ID of the table to scroll
 * @param {int} oldmin - the old minimum index
 * @param {int} min - the new minumum index
 * @param {int} max - the new maximum index
 * @param {Array} tabledata - the new data to show
 */
wingS.update.tableScroll = function(tableId, oldmin, min, max, tabledata) {
    var table = document.getElementById(tableId);
    for (rownumber in tabledata) {
        var rowindex = rownumber - oldmin;
        var row = table.rows[rowindex];
        if (row == null || row == undefined) {
            row = table.insertRow(rowindex);
        }

        var rowdata = tabledata[rownumber];
        if (rowdata == null) {
            table.deleteRow(rowindex);
        } else {
            // creating a dom node for our new cell
            var celldiv = document.createElement("div");

            for (cellnumber in rowdata) {
                var cell = row.cells[cellnumber];

                // convert data into dom node and replace old child
                celldiv.innerHTML = rowdata[cellnumber];
                var cellnode = celldiv.firstChild();
                row.replaceChild(cell, cellnode);

                var cell = row.cells[cellnumber];
            }
        }
    }
};

/**
 * An update which replaces the cell content of the table with the given ID.
 * @param {String} tableId - the ID of the table which has to be updated
 * @param {int} r - the row index of the cell to be replaced
 * @param {int} c - the column index of the cell to be replaced
 * @param {String} editing - the editing attribute for the cell
 * @param {String} html - the html code for the cell
 */
wingS.update.tableCell = function(tableId, r, c, editing, html) {
    var table = document.getElementById(tableId);
    var row = table.rows[r];
    var col = row.cells[c];
    col.innerHTML = html;
    col.setAttribute("editing", editing);
    if (editing)
        col.className = "cell";
    else
        col.className = "cell clickable";
};

/**
 * Updates the selection of the combobox with the given ID.
 * @param {String} comboBoxId - the ID of the combobox to update
 * @param {int} selectedIndex - the index of the entry to select
 */
wingS.update.selectionComboBox = function(comboBoxId, selectedIndex) {
    var comboBox = document.getElementById(comboBoxId);
    comboBox.selectedIndex = selectedIndex;
};

/**
 * Updates the selection of the list with the given ID.
 * @param {String} listId - the ID of the list to update
 * @param {Array} deselectedIndices - the indices to deselect
 * @param {Array} selectedIndices - the indices to select
 */
wingS.update.selectionList = function(listId, deselectedIndices, selectedIndices) {
    var list = document.getElementById(listId);

    if (list.options) {
        for (var i = 0; i < deselectedIndices.length; i++) {
            list.options[deselectedIndices[i]].selected = false;
        }
        for (var i = 0; i < selectedIndices.length; i++) {
            list.options[selectedIndices[i]].selected = true;
        }
    } else {
        var listItems = list.getElementsByTagName("LI");
        for (var i = 0; i < deselectedIndices.length; i++) {
            YAHOO.util.Dom.removeClass(listItems[deselectedIndices[i]], "selected");
        }
        for (var i = 0; i < selectedIndices.length; i++) {
            YAHOO.util.Dom.addClass(listItems[selectedIndices[i]], "selected");
        }
    }
};

/**
 * Updates the selection of the tree with the given ID.
 * @param {String} treeId - the ID of the tree to update
 * @param {Array} deselectedRows - the rows to deselect
 * @param {Array} selectedRows - the rows to select
 */
wingS.update.selectionTree = function(treeId, deselectedRows, selectedRows) {
    var tree = document.getElementById(treeId);
    var rows = wingS.util.getElementsByAttribute(tree, "td", "row");

    for (var i = 0; i < deselectedRows.length; i++) {
        YAHOO.util.Dom.replaceClass(rows[deselectedRows[i]], "selected", "norm");
    }
    for (var i = 0; i < selectedRows.length; i++) {
        YAHOO.util.Dom.replaceClass(rows[selectedRows[i]], "norm", "selected");
    }
};

/**
 * Updates the selection of the table with the given ID.
 * @param {String} tableId - the ID of the table to update
 * @param {int} indexOffset - the index offset for each row
 * @param {Array} deselectedIndices - the rows to deselect
 * @param {Array} selectedIndices - the rows to select
 * @param {Array} deselectedBodies - the deselected cell bodies
 * @param {Array} selectedBodies - the selected cell bodies
 * @param {String} exception - the server exception (optional)
 */
wingS.update.selectionTable = function(tableId, indexOffset, deselectedIndices,
        selectedIndices, deselectedBodies, selectedBodies, exception) {
    // Exception handling
    if (exception != null) {
        var update = "SelectionUpdate for '" + tableId + "'";
        wingS.update.alertException(exception, update);
        return;
    }

    var table = document.getElementById(tableId);
    if (table.tagName == "FIELDSET") {  // Workaround for tables with titled border: Necessary
        table = table.childNodes[1];    // as long as the fieldset id is equal to the table id.
    }
    var rows = table.rows;

    for (var i = 0; i < deselectedIndices.length; i++) {
        var tr = rows[deselectedIndices[i] + indexOffset];
        YAHOO.util.Dom.removeClass(tr, "selected");
        if (deselectedBodies != null) {
            tr.cells[0].innerHTML = deselectedBodies[i];
        }
    }
    for (var i = 0; i < selectedIndices.length; i++) {
        var tr = rows[selectedIndices[i] + indexOffset];
        YAHOO.util.Dom.addClass(tr, "selected");
        if (selectedBodies != null) {
            tr.cells[0].innerHTML = selectedBodies[i];
        }
    }
};

/**
 * Alerts an error message containing the exception name.
 * @param {String} exception - the exception name to alert
 * @param {String} details - details about the failed update
 */
wingS.update.alertException = function(exception, details) {
	var e = {
		message: "Couldn't apply update due to an exception on server side!",
		detail: "Exception: " + exception + "\n" +
                "Failed Update: " + details + "\n\n" +
                "Please examine your server's log file for further details..."
		}
	
	wingS.dialog.showExceptionDialog(e);
};

/**
 * Updates a component's visibility
 * @param componentId (HTML)-id of the component
 * @param display New Setting of the element.style.display parameter
 * @param visibility New Setting of the element.style.visibility parameter
 */
wingS.update.visibility = function(componentId, display, visibility) {
    var elt = document.getElementById(componentId);
    elt.style.display = display;
    elt.style.visibility = visibility;
};

/**
 * Runs the script in the given parameter.
 * @param scriptToEval Script to run
 */
wingS.update.runScript = function(scriptToEval) {
    eval(scriptToEval);
};
