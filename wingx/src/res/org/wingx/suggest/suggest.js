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
    if (YAHOO.env.ua.ie && YAHOO.env.ua.ie < 7) {
        this.useIFrame = true;
    }
};

YAHOO.extend(wingS.suggest.XSuggest, YAHOO.widget.AutoComplete, {
    
    formatResult: function(oResultData, sQuery, sResultMatch) {
        return (oResultData[1]) ? oResultData[1] : "";
    }
    
});

/**
 * XSuggest uses this DataSource which either gets suggestions from its local
 * cache or requests live data from the server by means of wingS' ajax engine. 
 * @param {String} oSuggestId - the element (ID) representing the XSuggest
 * @param {Object} oConfigs - the configuration object literal
 */
wingS.suggest.DataSource = function(oSuggestId, oConfigs) {
    if (!oSuggestId) return;
    var oLiveData = function(oRequest) {
        wingS.request.sendEvent(null, false, true, oSuggestId, "q:" + oRequest);
    };
    this.responseSchema = {fields:["key", "value"]};
    this.responseType = YAHOO.util.DataSourceBase.TYPE_JSARRAY;
    this.constructor.superclass.constructor.call(this, oLiveData, oConfigs);
};

YAHOO.extend(wingS.suggest.DataSource, YAHOO.util.DataSourceBase, {
    
    connectionParams: null,

    makeConnection: function(oRequest, oCallback, oCaller) {
        var tId = YAHOO.util.DataSourceBase._nTransactionId++;
        this.connectionParams = {tId:tId, request:oRequest, callback:oCallback, caller:oCaller};
        this.fireEvent("requestEvent", this.connectionParams);
        this.liveData(oRequest);
        return tId;
    },
    
    updateCallback: function(oRawResponse) {
        var tId = this.connectionParams.tId;
        var oRequest = this.connectionParams.request;
        var oCallback = this.connectionParams.callback;
        var oCaller = this.connectionParams.caller;
        this.handleResponse(oRequest, oRawResponse, oCallback, oCaller, tId);
    }
    
});

wingS.update.suggest = function(oSuggestId, oRawResponse) {
    var oSuggestDs = window["xs_datasource_" + oSuggestId];
    oSuggestDs.updateCallback(oRawResponse);
};

