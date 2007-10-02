// faking a namespace 
if (!wingS) {
    var wingS = new Object();
}
else if (typeof wingS != "object") {
    throw new Error("wingS already exists and is not an object");
}

/**
 * Creates a new suggest widget.
 * 
 * (based upon the example of Dave Crane, Eric Pascarello and Darren James 
 *  see 'Ajax in Action', Manning Publications Co. October 2005, ISBN: 1932394613)
 * 
 * @param {String} inputId input field to extend with suggest functionality
 * @param {String} dwrObject DWR interface object to communicate with the server
 * @param {Object} options see setOptions()
 *
 * @author Christian Schyma
 */
wingS.Suggest = function(inputId, dwrObject, options) {	
    this.id           = inputId;    
    this.textInput    = $(this.id);            
    this.suggestions  = [];
    this.suggestionsValue = [];    	
    this.inputDelay   = true;
    this.inputDelayTimeout;
        
    this.browser      = navigator.userAgent.toLowerCase();
    this.isIE         = this.browser.indexOf("msie") != -1;
    this.isOpera      = this.browser.indexOf("opera")!= -1;
    	
    // methods have to be exposed by DWR
    this.populator    = dwrObject["generateSuggestions"];
    this.textGetter   = dwrObject["getAjaxText"];
    this.textSetter   = dwrObject["setAjaxText"];
            
    this.setOptions(options);		
    this.injectBehavior();
};

/**
 * Set options. If no options given, set default values.
 * @param {Object} options object literal with the following optional attributes:
 * 
 * suggestDivClassName: {String}
 * suggestionClassName: {String}
 * inputDelayTime: {int} in ms, delay to avoid uneccessary requests for fast typists
 * timeout: {int} in ms
 * activityIndicatorClass: {String}
 * suggestBoxWidth: "auto" | "inherit" | {int}
 *
 * example: options = {suggestBoxWidth = "inherit", inputDelayTime = 1000}
 */
wingS.Suggest.prototype.setOptions = function setOptions(options) {
	        
    // default values	                   
    this.suggestDivClassName    = "suggestDiv";
    this.suggestionClassName    = "suggestion";	
    this.inputDelayTime         = 500;
    this.timeout                = 6000;
    this.activityIndicatorClass = "activityIndicator";
    this.suggestBoxWidth        = "auto";
	
    // set given values
    if (typeof options == "object") {                       
        if (options.preventSubmit)
            this.preventSubmit = options.preventSubmit;

        if (options.suggestDivClassName)
            this.suggestDivClassName = options.suggestDivClassName;

        if (options.suggestionClassName)
            this.suggestionClassName = options.suggestionClassName;
        
        if (options.inputDelayTime)
            this.inputDelay = options.inputDelayTime;
			
	if (options.timeout)
            this.timeout = options.timeout;
			
	if (options.activityIndicatorClass)
            this.activityIndicatorClass = options.activityIndicatorClass;
        
        if (options.suggestBoxWidth)
            this.suggestBoxWidth = options.suggestBoxWidth;
    }
			
};

/**
 * Prepares and extends the input field to work as an suggest widget.
 */
wingS.Suggest.prototype.injectBehavior = function injectBehavior() {
    var keyEventHandler = new wingS.SuggestKeyHandler(this);

    // disable browser native autocomplete
    MochiKit.DOM.setNodeAttribute(this.textInput, "autocomplete", "off");
	
    // housekeeping: due to incremental updates an old div can already be present 
    var oldSuggestionsDiv = $(this.suggestDivClassName +"_"+ this.id); 		
    if (oldSuggestionsDiv) {		
	this.textInput.parentNode.removeChild(oldSuggestionsDiv);	
    }
	
    // create suggestion div			
    this.suggestionsDiv = document.createElement("div");
    this.suggestionsDiv.className = this.suggestDivClassName;
    this.suggestionsDiv.id = this.suggestDivClassName +"_"+ this.id;	
    var divStyle = this.suggestionsDiv.style;
    divStyle.position = 'absolute';
    divStyle.zIndex   = 101;
    divStyle.display  = "none";
    this.textInput.parentNode.appendChild(this.suggestionsDiv);				
};

/**
 * show activity indicator animation
 */
wingS.Suggest.prototype.showActivity = function showActivity() {    		
    YAHOO.util.Dom.addClass(this.textInput, this.activityIndicatorClass);
};
	
/**
 * hide activity indicator animation
 */
wingS.Suggest.prototype.hideActivity = function hideActivity() {    	
    YAHOO.util.Dom.removeClass(this.textInput, this.activityIndicatorClass);		
};

/**
 * generates a new suggestions box
 */
wingS.Suggest.prototype.updateSuggestionsDiv = function updateSuggestionsDiv() {
    this.suggestionsDiv.innerHTML = "";
    var suggestLines = this.createSuggestionSpans();
    for (var i = 0 ; i < suggestLines.length ; i++)
    	this.suggestionsDiv.appendChild(suggestLines[i]);			
};

/**
 * Handler used by handleTextInput.
 */
wingS.Suggest.prototype.inputTimeoutHandler = function inputTimeoutHandler() {
    this.inputDelay = false;     
    this.handleTextInput();
};
    
/**
 * Handles new text input of the input field and avoids to send too
 * much requests to the server by using a timeout.
 */
wingS.Suggest.prototype.handleTextInput = function handleTextInput() {    

    if (this.inputDelay) {     		
	window.clearTimeout(this.inputDelayTimeout); // remove old timeout
	this.inputDelayTimeout = setTimeout(this.inputTimeoutHandler.bind(this), this.inputDelayTime);        
        return;
    }
    else {		
        this.inputDelay = true;        
    }
    
    var previousRequest    = this.lastRequestString;
    this.lastRequestString = this.textInput.value;
    
    if (this.lastRequestString == "")
    	this.hideSuggestions();
    else if (this.lastRequestString != previousRequest) {
    	this.sendRequestForSuggestions();
    }    
};

/**
 * Move suggestion selection index up.
 */
wingS.Suggest.prototype.moveSelectionUp = function moveSelectionUp() {	
    if (this.selectedIndex > 0) {
        this.updateSelection(this.selectedIndex - 1);
    }
};

/**
 * Move suggestion selection index down.
 */
wingS.Suggest.prototype.moveSelectionDown = function moveSelectionDown() {    
    if (this.selectedIndex < (this.suggestions.length - 1)) {
    	this.updateSelection(this.selectedIndex + 1);
    }
};

/**
 * Update the suggestion selection by setting a CSS class.
 * @param {Object} n span index to show as selected
 */
wingS.Suggest.prototype.updateSelection = function updateSelection(n) {
    var span = $(this.id + "_" + this.selectedIndex);
    
    if (span){    	
        MochiKit.DOM.removeElementClass(span, "selected");
    }
    
    this.selectedIndex = n;

    var span = $( this.id + "_" + this.selectedIndex );
    if (span){    	
	MochiKit.DOM.addElementClass(span, "selected");		 
    }
};

/**
 * Requests server for suggestions. The answer will be handled by updateSuggestions()
 */
wingS.Suggest.prototype.sendRequestForSuggestions = function sendRequestForSuggestions() {    
    if (this.handlingRequest) {
    	this.pendingRequest = true;
        return;
    }
    this.handlingRequest = true;	        
        
    this.showActivity();
       
    this.populator(this.lastRequestString, 		
            {   callback: this.updateSuggestions.bind(this), 
                //timeout: this.timeout, 
                errorHandler: this.errorHandler.bind(this)                
             });		 	
};

wingS.Suggest.prototype.errorHandler = function errorHandler(message) {
    this.hideActivity();
    alert(message);        
}

/**
 * Fill suggestions field with suggestions from the server response.
 * @param {Object} suggestions
 */
wingS.Suggest.prototype.createSuggestions = function createSuggestions(response) {    
    this.suggestions = [];
    this.suggestionsValue = [];
    if (response == null) return;
    
    for (var i = 0; i < response.length; i++) {
        this.suggestions.push(response[i]);
        this.suggestionsValue.push(response[++i]);
    }         
};

/**
 * Handles the server response.
 * @param {Object} ajaxResponse
 */
wingS.Suggest.prototype.updateSuggestions = function updateSuggestions(ajaxResponse) {
    this.createSuggestions(ajaxResponse);	
    this.hideActivity();

    // if no suggestion was returned check for the intermediate (ajax) text at
    // the server
    if (this.suggestions.length == 0) {
    	this.hideSuggestions();  
        this.sendRequestForSelectionValue();
    }
    // suggestions have beeen returned so show them
    else {
    	this.updateSuggestionsDiv();
        this.showSuggestions();
        this.updateSelection(0);        
    }

    this.handlingRequest = false;

    if (this.pendingRequest) {
    	this.pendingRequest    = false;
        this.lastRequestString = this.textInput.value;
        this.sendRequestForSuggestions();
    }
};

/**
 * Puts selected suggestion to the input field and closes the suggestion div.
 */
wingS.Suggest.prototype.setInputFromSelection = function setInputFromSelection() {    
    var value = this.suggestionsValue[this.selectedIndex];	

    // if nothing was selected before (e.g. when tabbing through suggest fields): ignore undefined
    if (typeof value == 'undefined')
	value = '';
				
    this.setInputValue(value);    
    this.hideSuggestions();
    
    // send current selection so that the server intermediate (Ajax) text is
    // up to date
    this.sendSelectionValue(value);
};

/**
 * Keeps the server value up to date.
 */
wingS.Suggest.prototype.sendSelectionValue = function sendSelectionValue(value) {            
    this.textSetter(value, 		
        {   callback: function() {}, 
            errorHandler: this.errorHandler.bind(this) 
        });		 	
};

/**
 * Sends a request to receive the current selection value. The response
 * will then be set to the text input field.
 */
wingS.Suggest.prototype.sendRequestForSelectionValue = function sendRequestForSelectionValue() {            
    this.textGetter( 		
        {   callback: this.setInputValue.bind(this), 
            errorHandler: this.errorHandler.bind(this) 
        });		 	
};

/**
 * Set the value of the input field.
 * @param value value to set
 */
wingS.Suggest.prototype.setInputValue = function setInputValue(value) {
    this.textInput.value = value;    
};

/**
 * Makes the suggestion div visible.
 */
wingS.Suggest.prototype.showSuggestions = function showSuggestions() {    
    this.positionSuggestionsDiv();
    var divStyle = this.suggestionsDiv.style;            
    MochiKit.Visual.appear(this.suggestionsDiv);	    
};

/**
 * Hides the suggestion div by setting display:none.
 */
wingS.Suggest.prototype.hideSuggestions = function hideSuggestions() {		
    MochiKit.Style.hideElement(this.suggestionsDiv);	    
};

/**
 * Sets the position of the suggestion box right below then input field and 
 * sizes its width to match the width of the input field.
 */
wingS.Suggest.prototype.positionSuggestionsDiv = function positionSuggestionsDiv() {      
    var divStyle = this.suggestionsDiv.style;      	
    var textInputPos = MochiKit.Style.getElementPosition(this.textInput);	

    // if IE and its backward compatibility mode (quirks mode) is used,
    // the CSS-Box-Model-Bug has to be handled 
    if ((this.isIE) && (document.compatMode == "BackCompat")) {				

        var tBorder = MochiKit.Style.computedStyle(this.suggestionsDiv, "borderTopWidth",  "border-top-width" );
        var bBorder = MochiKit.Style.computedStyle(this.suggestionsDiv, "borderBottomWidth", "border-bottom-width" );
        var vertBorder = parseInt(tBorder) + parseInt(bBorder);			
        divStyle.top   = (textInputPos.y + this.textInput.offsetHeight) - vertBorder + "px";		

        var lBorder = MochiKit.Style.computedStyle(this.suggestionsDiv, "borderLeftWidth",  "border-left-width" );
        var rBorder = MochiKit.Style.computedStyle(this.suggestionsDiv, "borderRightWidth", "border-right-width" );
        var horizBorder = parseInt(lBorder) + parseInt(rBorder);			
        divStyle.left   = textInputPos.x - horizBorder + "px"; 

        if (this.suggestBoxWidth == "inherit")
            divStyle.width = this.textInput.offsetWidth + "px";	        
    }
    else if (this.isIE) {
        divStyle.top   = (textInputPos.y + this.textInput.offsetHeight) + "px";
        divStyle.left  = textInputPos.x + "px";
        
        if (this.suggestBoxWidth == "inherit")
            divStyle.width = (this.textInput.offsetWidth - this.padding()) + "px";	        
    }
    else {
        divStyle.top   = (textInputPos.y + this.textInput.offsetHeight) + "px";
        divStyle.left  = textInputPos.x + "px";        
        
        if (this.suggestBoxWidth == "inherit")
            divStyle.width = (this.textInput.offsetWidth) + "px";	        
    }
    	
    if (this.suggestBoxWidth != "inherit" && this.suggestBoxWidth != "auto") 
        divStyle.width = this.suggestBoxWidth;
    
};

wingS.Suggest.prototype.createSuggestionSpans = function createSuggestionSpans() {
    var suggestionSpans = [];
    for (var i = 0; i < this.suggestions.length; i++) {
        suggestionSpans.push(this.createSuggestionSpan(i));
    }
			
    return suggestionSpans;
};

wingS.Suggest.prototype.createSuggestionSpan = function createSuggestionSpan(n) {
    var suggestion = this.suggestions[n];

    var suggestionSpan = document.createElement("span");
    suggestionSpan.className     = this.suggestionClassName;
    suggestionSpan.style.width   = '100%';
    suggestionSpan.style.display = 'block';    
    suggestionSpan.id            = this.id + "_" + n;    	    
            
    suggestionSpan.innerHTML = suggestion;
    
    // suggestions can be formatted with HTML, so all child nodes
    // also have to be attached to the mouseover handler and get an id 
    /*var childNodes = suggestionSpan.childNodes;
    for (var i = 0; i < childNodes.length; i++) {
        YAHOO.util.Event.addListener(childNodes[i], "mouseover", this.mouseoverHandler, this, true); 
        childNodes[i].id = this.id + i + "_" + n;
    } */   
        
    YAHOO.util.Event.addListener(suggestionSpan, "mouseover", this.mouseoverHandler, this, true);	    	
	
    return suggestionSpan;	
};

wingS.Suggest.prototype.mouseoverHandler = function mouseoverHandler(e) {    
    var src = e.srcElement ? e.srcElement : e.target;
    var index = parseInt(src.id.substring(src.id.lastIndexOf('_')+1));
    this.updateSelection(index);
};

/**
 * Calculate padding of the suggestions box. 
 * 
 * This is done due to the possibility to style the input box using CSS. Otherwise
 * a user could use CSS borders or padding and thus making the visual alignment to
 * the width of the input box wrong.
 */
wingS.Suggest.prototype.padding = function padding() {	
    var lPad    = MochiKit.Style.computedStyle(this.suggestionsDiv, "paddingLeft",      "padding-left");
    var rPad    = MochiKit.Style.computedStyle(this.suggestionsDiv, "paddingRight",     "padding-right" );
    var lBorder = MochiKit.Style.computedStyle(this.suggestionsDiv, "borderLeftWidth",  "border-left-width" );
    var rBorder = MochiKit.Style.computedStyle(this.suggestionsDiv, "borderRightWidth", "border-right-width" );							
    return parseInt(lPad) + parseInt(rPad) + parseInt(lBorder) + parseInt(rBorder);
};

wingS.SuggestKeyHandler = function(textSuggest) {
    // key codes
    this.upArrow     = 38;
    this.downArrow   = 40;
    this.enterKey    = 13;    
    this.tabKey      = 9;
    this.ShiftTabKey = 16;
    this.escKey      = 27;
        
    this.textSuggest = textSuggest;
    this.input       = this.textSuggest.textInput;
    this.addKeyHandling();
};

/**
 * register key handlers 
 */
wingS.SuggestKeyHandler.prototype.addKeyHandling = function addKeyHandling() {    
    YAHOO.util.Event.addListener(this.input, "keyup", this.keyupHandler, this, true);
    YAHOO.util.Event.addListener(this.input, "keydown", this.keydownHandler, this, true);
    YAHOO.util.Event.addListener(this.input, "blur", this.onblurHandler, this, true);	
};
	
wingS.SuggestKeyHandler.prototype.keydownHandler = function keydownHandler(e) {    
    if (e.keyCode == this.upArrow) {
	this.textSuggest.moveSelectionUp();
        setTimeout( this.moveCaretToEnd.bind(this), 1 );										
    }
    else if ( e.keyCode == this.downArrow ){
    	this.textSuggest.moveSelectionDown();
    }
    else if ( e.keyCode == this.enterKey ) {
        // prevent the default browser behaviour to submit a form
        YAHOO.util.Event.preventDefault(e);   
    }
};

wingS.SuggestKeyHandler.prototype.keyupHandler = function keyupHandler(e) {    
    if ( this.input.length == 0 && !this.isOpera )
        this.textSuggest.hideSuggestions();

    if ( !this.handledSpecialKeys(e) )
        this.textSuggest.handleTextInput();
};

/**
 * react on special keys like enter
 * @return true, when special key was handles
 */
wingS.SuggestKeyHandler.prototype.handledSpecialKeys = function handledSpecialKeys(e) {    
    if ( e.keyCode == this.upArrow || e.keyCode == this.downArrow  
        || e.keyCode == this.tabKey || e.keyCode == this.ShiftTabKey) {
    	return true;
    }
    else if ( e.keyCode == this.enterKey ) {
    	this.textSuggest.setInputFromSelection();
        
        // prevent the default browser behaviour to submit a form
        YAHOO.util.Event.preventDefault(e);
        
        return true;
    }    
    else if ( e.keyCode == this.escKey ) {
        this.textSuggest.hideSuggestions();	
        return true;
    }

    return false;
};

wingS.SuggestKeyHandler.prototype.moveCaretToEnd = function moveCaretToEnd() {
    var pos = this.input.value.length;

    if (this.input.setSelectionRange) {
        this.input.setSelectionRange(pos, pos);
    }
    else if(this.input.createTextRange){
    	var m = this.input.createTextRange();
        m.moveStart('character',pos);
        m.collapse();
        m.select();
    }
};

wingS.SuggestKeyHandler.prototype.onblurHandler = function onblurHandler(e) {	
    if (this.textSuggest.suggestionsDiv.style.display == 'block') {	
    this.textSuggest.setInputFromSelection();    
            this.textSuggest.hideSuggestions();	
    }
};


