/***************************************************************************************************
 * WINGS.SUGGEST  --  contains: autocompletion functionality
 **************************************************************************************************/

// Create module namespace
wingS.namespace("suggest");


/**
 * XSuggest resembles an input box with autocomplete capabilities
 * @param {String} elInput - the element (ID) representing the XSuggest
 * @param {Object} elContainer - the element (ID) representing the container
 * @param {Object} oDataSource - the datasource instance to use
 * @param {Object} oConfigs - the configuration object literal
 */
wingS.suggest.XSuggest = function(elInput, elContainer, oDataSource , oConfigs) {
    wingS.suggest.XSuggest.superclass.constructor.call(this, elInput, elContainer, oDataSource , oConfigs);    
    this.useIFrame = true;
};

YAHOO.extend(wingS.suggest.XSuggest, YAHOO.widget.AutoComplete, {

    formatResult: function(aResultItem, sQuery) {
        var sKey = aResultItem[1]; // the entire result key
        var sKeyQuery = sKey.substr(0, sQuery.length); // the query itself
        var sKeyRemainder = sKey.substr(sQuery.length); // the rest of the result
    
        // some other piece of data defined by schema
        var attribute1 = aResultItem[1];
        // and another piece of data defined by schema
        var attribute2 = aResultItem[2];
    
        var aMarkup = [
            "<div>",
            // "<span style='font-weight:bold'>",
            sKeyQuery,
            // "</span>",
            sKeyRemainder,
            "</div>"];
        return (aMarkup.join(""));
    }
});


wingS.suggest.DataSource = function(id, popupid) {
    if (!id) return;
    this.doInit(id, popupid);
};

YAHOO.extend(wingS.suggest.DataSource, YAHOO.widget.DataSource, {

    doInit: function(id, popupid) {
        this.id = id;
        this.popupid = popupid;
        this._init();
    },
    
    doQuery: function(oCallbackFn, sQuery, oParent) {
        wingS.request.sendEvent(null, false, true, oParent._elTextbox.id, "q:" + sQuery);
        wingS.suggest.DataSource.xs = oParent;
    }
});


/**
 * Updates the method of the form with the given ID.
 * @param {String} formId - the ID of the form to update
 * @param {int} method - the method to be set
 */
wingS.update.suggest = function(suggestId, query) {
    var suggestions = new Array();
    for (var i = 2, j = arguments.length; i < j; i+=2) {
        suggestions.push(new Array(arguments[i], arguments[i+1]));
    }
    wingS.suggest.DataSource.xs._populateList(query, suggestions, wingS.suggest.DataSource.xs);
};

