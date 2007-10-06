// faking a namespace
if (!wingS) {
    var wingS = new Object();
}
else if (typeof wingS != "object") {
    throw new Error("wingS already exists and is not an object");
}

/**
 * Creates a new color picker widget. 
 * 
 * @param {String} dwrObject         DWR interface object to communicate with the server
 * @param {int}    timeout           Ajax request timeout
 * @param {String} panelDiv          id of div to extend with color picker functionality
 * @param {String} panelDNDHandleDiv id of div to start drag n drop
 * @param {String} pickerDiv         id of two dim. slider to select the saturation and greyvalue
 * @param {String} pickerSwatchDiv   id of color selection preview
 * @param {String} pickerHValInput   id of hue input field
 * @param {String} pickerSValInput   id of saturation input field
 * @param {String} pickerVValInput   id of greyvalue input field
 * @param {String} pickerRValInput   id of red input field
 * @param {String} pickerGValInput   id of green input field
 * @param {String} pickerBValInput   id of blue input field
 * @param {String} pickerHexValInput id of RGB hex value input field
 * @param {String} selectorDiv       id of the picker selector div
 * @param {String} hueBackgroundDiv  id of the hue background div
 * @param {String} hueThumbDiv       id of the hue selector div
 * @param {int}    red               inital RGB value
 * @param {int}    green             inital RGB value
 * @param {int}    blue              inital RGB value
 *
 * @author Christian Schyma
 */
wingS.HSVColorPicker = function(dwrObject, timeout, panelDiv, panelDNDHandleDiv, pickerDiv, pickerSwatchDiv, 
								pickerHValInput, pickerSValInput, pickerVValInput, 
								pickerRValInput, pickerGValInput, pickerBValInput,
								pickerHexValInput,
								selectorDiv, hueBackgroundDiv, hueThumbDiv,
								red, green, blue) {
        
        // method has to be exposed by DWR                                                                    
        this.dwrSetter         = dwrObject["setSelectedColor"];
        
        this.timeout           = timeout;
	
        this.panelDiv          = panelDiv;              
	this.panelDNDHandleDiv = panelDNDHandleDiv;     
	this.pickerDiv         = pickerDiv;                          
	this.pickerSwatchDiv   = pickerSwatchDiv;
	
	this.pickerHValInput   = pickerHValInput;
	this.pickerSValInput   = pickerSValInput;
	this.pickerVValInput   = pickerVValInput;
	this.pickerRValInput   = pickerRValInput;
	this.pickerGValInput   = pickerGValInput;
	this.pickerBValInput   = pickerBValInput;
	this.pickerHexValInput = pickerHexValInput;
		
	this.selectorDiv       = selectorDiv;
	this.hueBackgroundDiv  = hueBackgroundDiv;
	this.hueThumbDiv       = hueThumbDiv; 
		
	this.hue;    // hue slider
	this.picker; // saturation and gray value picker
	this.panel;  // dragable panel	
	
        this.red   = red;
        this.green = green;
        this.blue  = blue;        
                       
        // init sliders
	this.hue = YAHOO.widget.Slider.getVertSlider(this.hueBackgroundDiv, this.hueThumbDiv, 0, 180);	                
	this.picker = YAHOO.widget.Slider.getSliderRegion(this.pickerDiv, this.selectorDiv , 0, 180, 0, 180);	
        		
        // init drag n drop
	this.panel = new YAHOO.util.DD(this.panelDiv);
	this.panel.setHandleElId(this.panelDNDHandleDiv);	
                       
        this.setColors(red, green, blue);
        
        // attach event listeners
        this.hue.subscribe("change", this.hueUpdate, this, true);        
        this.hue.subscribe("slideEnd", this.sendColor, this, true);                        
        this.picker.subscribe("change", this.pickerUpdate, this, true);
        this.picker.subscribe("slideEnd", this.sendColor, this, true);                
};

/**
 * Sends currently selected color to the server object using XmlHtmlRequest.
 */
wingS.HSVColorPicker.prototype.sendColor = function() {       
    this.dwrSetter(this.red, this.green, this.blue,
        {callback: this.successHandler.bind(this), 
         //timeout: this.timeout, 
         errorHandler: this.timeoutHandler.bind(this)}
    );
};

wingS.HSVColorPicker.prototype.timeoutHandler = function(msg) {        
    alert(msg);    
};

/**
 * XmlHtmlRequest response handler.
 * @param {boolean} requestUpdate if true a incremental update will be requested
 */
wingS.HSVColorPicker.prototype.successHandler = function(requestUpdate) {        
    if (requestUpdate) {
        wingS.ajax.requestUpdates();
    }       
}; 

/**
 * handles a change event of the hue slider
 */
wingS.HSVColorPicker.prototype.hueUpdate = function() {	
	var h = (180 - this.hue.getValue()) / 180;
	if (h == 1) { h = 0; }

	var a = YAHOO.util.Color.hsv2rgb(h, 1, 1);
	document.getElementById(this.pickerDiv).style.backgroundColor =
		"rgb(" + a[0] + ", " + a[1] + ", " + a[2] + ")";
               
	this.pickerUpdate();
};  

/**
 * handles a change event of the sat./greyvalue slider (picker)
 */
wingS.HSVColorPicker.prototype.pickerUpdate = function() {
	var h = (180 - this.hue.getValue());
	if (h == 180) { h = 0; }
	document.getElementById(this.pickerHValInput).value = (h*2);

	h = h / 180;

	var s = this.picker.getXValue() / 180;
	document.getElementById(this.pickerSValInput).value = Math.round(s * 100);

	var v = (180 - this.picker.getYValue()) / 180;
	document.getElementById(this.pickerVValInput).value = Math.round(v * 100);

	var a = YAHOO.util.Color.hsv2rgb( h, s, v );

        this.red   = a[0];
        this.green = a[1];
        this.blue  = a[2];
        
	document.getElementById(this.pickerSwatchDiv).style.backgroundColor =
		"rgb(" + a[0] + ", " + a[1] + ", " + a[2] + ")";

	document.getElementById(this.pickerRValInput).value = a[0];
	document.getElementById(this.pickerGValInput).value = a[1];
	document.getElementById(this.pickerBValInput).value = a[2];
	var hexvalue = document.getElementById(this.pickerHexValInput).value =
		YAHOO.util.Color.rgb2hex(a[0], a[1], a[2]);                 
};

/**
 * Set the selected color by using RGB.
 * @param {int} red
 * @param {int} green
 * @param {int} blue
 */
wingS.HSVColorPicker.prototype.setColors = function(red, green, blue) {                
	var hsv = this.rgb2hsv(red, green, blue);
	var h = hsv[0];
	var s = hsv[1];
	var v = hsv[2];			 			
		     		
	var hueValue = 180 - (180 * h);
	this.hue.setValue(hueValue);
	
	var xValue = s * 180;
	var yValue = 180 - (180 * v);
	this.picker.setRegionValue(xValue, yValue, false);        
}

/**
 * Converts RGB to HSV color values. 
 * Adapted from http://www.easyrgb.com/math.html
 * @param {Object} r 0 - 255
 * @param {Object} g 0 - 255
 * @param {Object} b 0 - 255
 * @return array of hsv values (0 - 255)
 */	
wingS.HSVColorPicker.prototype.rgb2hsv = function(r, g, b) {
	var h, s, v;
	
	var var_R = ( r / 255 );            //RGB values = 0 ÷ 255
	var var_G = ( g / 255 );
	var var_B = ( b / 255 );
	    	
	var tempMin = Math.min(var_R, var_G);
	var var_Min = Math.min(tempMin, var_B); //Min. value of RGB
	
	var tempMax = Math.max(var_R, var_G);
	var var_Max = Math.max(tempMax, var_B); //Max. value of RGB
			   
	var del_Max = var_Max - var_Min;        //Delta RGB value

	v = var_Max;

	if ( del_Max == 0 )         //This is a gray, no chroma...
	{
   		h = 0;               	//HSV results = 0 ÷ 1
   		s = 0;
	}
	else        				//Chromatic data...
	{
   		s = del_Max / var_Max;

		var del_R = ( ( ( var_Max - var_R ) / 6 ) + ( del_Max / 2 ) ) / del_Max;
	   	var del_G = ( ( ( var_Max - var_G ) / 6 ) + ( del_Max / 2 ) ) / del_Max;
	   	var del_B = ( ( ( var_Max - var_B ) / 6 ) + ( del_Max / 2 ) ) / del_Max;

   		if      ( var_R == var_Max ) {h = del_B - del_G}
   		else if ( var_G == var_Max ) {h = ( 1 / 3 ) + del_R - del_B}
   		else if ( var_B == var_Max ) {h = ( 2 / 3 ) + del_G - del_R}

   		if ( h < 0 ) {h += 1}
   		if ( h > 1 ) {h -= 1}
	}
			
	return [h, s, v];
};
