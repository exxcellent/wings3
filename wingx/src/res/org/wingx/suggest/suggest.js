/***************************************************************************************************
 * WINGS.SUGGEST  --  contains: autocompletion functionality
 **************************************************************************************************/

// Create module namespace
wingS.namespace("suggest");


YAHOO.widget.AutoComplete.prototype.formatResult = function(aResultItem, sQuery) {
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
};

wingS.suggest.SuggestDS = function(id, popupid) {
    if (!id) {
        return;
    }
    this.wingSInit(id, popupid);
};

YAHOO.extend(wingS.suggest.SuggestDS, YAHOO.widget.DataSource);

wingS.suggest.SuggestDS.prototype.wingSInit = function(id, popupid) {
    this.id = id;
    this.popupid = popupid;
    this._init();
}

/**
 * Abstract method implemented by subclasses to make a query to the live data
 * source. Must call the callback function with the response returned from the
 * query. Populates cache (if enabled).
 *
 * @method doQuery
 * @param oCallbackFn {HTMLFunction} - callback function implemented by oParent to which to return results
 * @param sQuery {String} - the query string
 * @param oParent {Object} - the object instance that has requested data
 */
wingS.suggest.SuggestDS.prototype.doQuery = function(oCallbackFn, sQuery, oParent) {
    wingS.request.sendEvent(null, false, true, oParent._elTextbox.id, "q:" + sQuery);
    wingS.suggest.SuggestDS.ac = oParent;
};

/**
 * Updates the method of the form with the given ID.
 * @param {String} formId - the ID of the form to update
 * @param {int} method - the method to be set
 */
wingS.update.suggest = function(suggestId, query) {
    var suggest = document.getElementById(suggestId);
    var suggestions = new Array();
    for (var i = 2, j = arguments.length; i < j; i+=2) {
        suggestions.push(new Array(arguments[i], arguments[i+1]));
    }
    wingS.suggest.SuggestDS.ac._populateList(query, suggestions, wingS.suggest.SuggestDS.ac);
};

