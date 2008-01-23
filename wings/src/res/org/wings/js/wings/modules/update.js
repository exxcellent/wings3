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

    if (typeof component.outerHTML != "undefined") {
        // Use outerHTML if available
        component.outerHTML = html;
    } else {
        var parent = component.parentNode;
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
            // If there is only one child it must be our component
            parent.innerHTML = html;
        } else {
            var range;
            // If there is no other way we have to use proprietary methods
            if (document.createRange && (range = document.createRange()) &&
                range.createContextualFragment) {
                range.selectNode(component);
                var newComponent = range.createContextualFragment(html);
                parent.replaceChild(newComponent, component);
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
 * Hides the mask of the given Dialog.
 * @param {String} componentId - the ID of the component
 * @param {String} dialogName - the name of the dialog
 * @param {String} exception - the server exception (optional)
 */
wingS.update.closeDialog = function(componentId, dialogName, exception ) {
    // Exception handling
    if (exception != null) {
        var update = "CloseDialogUpdate for '" + componentId + "'";
        wingS.update.alertException(exception, update);
        return;
    }

    var dialog = window["dialog_" + dialogName];
    if (dialog != null) {
        dialog.hideMask();
    }
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
            listItems[deselectedIndices[i]].className = "clickable";
        }
        for (var i = 0; i < selectedIndices.length; i++) {
            listItems[selectedIndices[i]].className = "selected clickable";
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
        rows[deselectedRows[i]].className = "norm";
    }
    for (var i = 0; i < selectedRows.length; i++) {
        rows[selectedRows[i]].className = "selected";
    }
};

/**
 * Alerts an error message containing the exception name.
 * @param {String} exception - the exception name to alert
 * @param {String} details - details about the failed update
 */
wingS.update.alertException = function(exception, details) {
    var errorMsg = "Couldn't apply update due to an exception on server side!\n" +
                   "**********************************************\n\n" +
                   "Exception: " + exception + "\n" +
                   "Failed Update: " + details + "\n\n" +
                   "Please examine your server's log file for further details...";
    alert(errorMsg);
};

