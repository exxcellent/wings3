// faking a namespace
if (!wingS) {
	var wingS = new Object();
}
else if (typeof wingS != "object") {
	throw new Error("wingS already exists and is not an object");
}

/**
 * In place editor widget.
 *
 * @param {String} element element id of element to build editor upon
 * @param {String} dwrObject DWR interface object to communicate with the server
 * @param {int} cols number of columns of edit textarea
 * @param {int} rows number of rows of edit textarea
 * @param {Object} options see setOptions()
 *
 * @author Christian Schyma
 */
wingS.InplaceEditor = function(element, dwrObject, options) {
    this.elementId = element;
    
    // methods have to be exposed by DWR
    this.dwrSetter = dwrObject["setAjaxText"];
    this.dwrGetter = dwrObject["getAjaxText"];
        
    this.EDITOR_DIV      = this.elementId + "_editor";
    this.EDITOR_TEXTAREA = this.elementId + "_editarea";
    this.EDITOR_STATUS   = this.elementId + "_status";
    this.EDITOR_SAVE     = this.elementId + "_save";
    this.EDITOR_CANCEL   = this.elementId + "_cancel";
    
    this.setOptions(options);
    
    this.registerTooltip();
    this.registerListeners();    
};

/**
 * Set options. If no options given, set default values.
 * @param {Object} options object literal with the following optional attributes:
 * cols: <int>, rows: <int>, timeout: <int> in ms, 
 * saveStatusText: <string>, timeoutErrorText: <string>, clickNotificationText: <string>,
 * okButtonUrl: <string>, cancelButtonUrl: <string>
 */
wingS.InplaceEditor.prototype.setOptions = function(options) {
	
    // default values
    this.cols      = 60;
    this.rows      = 4; 
    this.timeout   = 3000; // ms
    this.saveStatusText        = "Saving ...";
    this.timeoutErrorText      = "The server is not responding, so I can't save your changes. Sorry."    
    this.clickNotificationText = "click here to edit";
    this.okButtonUrl           = "-org/wingx/icons/nuvola/button_ok.png";
    this.cancelButtonUrl       = "-org/wingx/icons/nuvola/button_cancel.png";                
    
    // set given values
    if (typeof options == "object") {
        if (options.cols)
            this.cols = options.cols;

        if (options.rows)
            this.rows = options.rows;

        if (options.timeout)
            this.timeout = options.timeout;
        
        if (options.saveStatusText)
            this.saveStatusText = options.saveStatusText;

        if (options.timeoutErrorText)
            this.timeoutErrorText = options.timeoutErrorText;
        
        if (options.clickNotificationText)
            this.clickNotificationText = options.clickNotificationText;
        
        if (options.okButtonUrl)
            this.okButtonUrl = options.okButtonUrl;
        
        if (options.cancelButtonUrl)
            this.cancelButtonUrl = options.cancelButtonUrl;
    }
			
};

wingS.InplaceEditor.prototype.registerListeners = function() {
    YAHOO.util.Event.addListener(this.elementId, "mouseover", this.highlightOn.bind(this));
    YAHOO.util.Event.addListener(this.elementId, "mouseout", this.highlightOff.bind(this));
    YAHOO.util.Event.addListener(this.elementId, "click", this.edit.bind(this));        
};    

wingS.InplaceEditor.prototype.registerTooltip = function() {
    this.toolTip = new YAHOO.widget.Tooltip("myTooltip", {
        context:this.elementId, text: this.clickNotificationText, showDelay:250 
    }); 	
};
	
/**
 * highlight editable region
 */
wingS.InplaceEditor.prototype.highlightOn = function() {
    YAHOO.util.Dom.addClass(this.elementId, "editable");			
};

/**
 * disable highlight
 */
wingS.InplaceEditor.prototype.highlightOff = function() {
    YAHOO.util.Dom.removeClass(this.elementId, "editable");		
};

/**
 * removes editor functionality and restores original view 
 */
wingS.InplaceEditor.prototype.cleanUp = function() {		
    MochiKit.DOM.removeElement(this.EDITOR_DIV);
    MochiKit.Style.showElement(this.elementId);	
};

/**
 * save changes by sending text to server
 */
wingS.InplaceEditor.prototype.saveChanges = function() {		    
    MochiKit.Style.hideElement(this.EDITOR_DIV);
            
    var statusDiv = DIV({'id': this.EDITOR_STATUS});    
    var statusText = document.createTextNode(this.saveStatusText);
    statusDiv.appendChild(statusText);    
    
    wingS.util.insertAfter(statusDiv, $(this.elementId));
                       
    this.dwrSetter($(this.EDITOR_TEXTAREA).value, 
        {callback: this.successHandler.bind(this), 
         timeout: this.timeout, 
         errorHandler: this.timeoutHandler.bind(this)}
    );
};

/**
 * Timeout-Handler for saveChanges(). Restores the editor.
 */
wingS.InplaceEditor.prototype.timeoutHandler = function(msg) {    
    alert(this.timeoutErrorText + '('+msg+')');
    MochiKit.DOM.removeElement(this.EDITOR_STATUS);
    MochiKit.Style.showElement(this.EDITOR_DIV);
};

/**
 * Success-Hanlder for saveChanges(). Displays the new static text.
 */
wingS.InplaceEditor.prototype.successHandler = function(data) {    
    MochiKit.DOM.removeElement(this.EDITOR_STATUS);
    MochiKit.DOM.removeElement(this.EDITOR_DIV);
    this.getTextSpan().innerHTML = data;    
    MochiKit.Style.showElement(this.elementId);    
};    

/**
 * Returns the HTML element which contains the static text.
 */
wingS.InplaceEditor.prototype.getTextSpan = function() {
    return wingS.util.findElement(this.elementId, "span");
};

/**
 * Initializes the edit mode by requesting the current text and
 * forwarding the successful answer to buildEditor().
 */
wingS.InplaceEditor.prototype.edit = function() {
    this.dwrGetter(
        {callback: this.buildEditor.bind(this),
        timeout: this.timeout,
        errorHandler: this.timeoutHandler.bind(this)}
    );
};

/**
 * Builds the editor by hiding the static text and displaying a textarea.
 * @param {String} current text
 */
wingS.InplaceEditor.prototype.buildEditor = function(text) {			
    MochiKit.Style.hideElement(this.elementId); // hide the static text element
        
    var htmlElement = MochiKit.DOM.getElement(this.elementId);
        
    // generate editor div
    var editor = DIV({'id': this.EDITOR_DIV});

    // generate text area    
    if (this.rows == 1) {
        var textArea = INPUT({'name': this.EDITOR_TEXTAREA, 'id': this.EDITOR_TEXTAREA, 'size': this.cols, 'class': "InplaceEditorTextArea", 'value': text} );            
    }
    else {        
        var textArea = TEXTAREA({'name': this.EDITOR_TEXTAREA, 'id': this.EDITOR_TEXTAREA, 'rows': this.rows, 'cols': this.cols, 'class': "InplaceEditorTextArea"} );            
        var textAreaText = document.createTextNode(text);
        textArea.appendChild(textAreaText);
    }    
    editor.appendChild(textArea);	
		
    // generate belonging buttons         
    var br = BR();
    editor.appendChild(br);	    
    var buttons = document.createElement("div");
    buttons.innerHTML =  "<table border='0'><tr>" +
                "<td><img id='"+this.EDITOR_SAVE+"' src="+this.okButtonUrl+" /></td>" +
                "<td><img id='"+this.EDITOR_CANCEL+"' src="+this.cancelButtonUrl+" /></td>" +                
                "</tr></table>";        
    editor.appendChild(buttons);            	
    wingS.util.insertAfter(editor, htmlElement);

    // register event handler    
    YAHOO.util.Event.addListener(this.EDITOR_CANCEL, "click", this.cleanUp.bind(this));
    YAHOO.util.Event.addListener(this.EDITOR_SAVE, "click", this.saveChanges.bind(this));
	
    // mark editable text
    YAHOO.util.Dom.get(this.EDITOR_TEXTAREA).select();					
    YAHOO.util.Dom.get(this.EDITOR_TEXTAREA).focus(); // Opera needs it
};