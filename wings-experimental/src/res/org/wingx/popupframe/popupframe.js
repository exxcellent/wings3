// faking a namespace
if (!wingS) {
    var wingS = new Object();
}
else if (typeof wingS != "object") {
    throw new Error("wingS already exists and is not an object");
}

wingS.PopupFrame = function(id, width, height) {
	this.id     = id;	
	this.width  = width;
	this.height = height;
	
	this.panel = new YAHOO.widget.Panel('"+ id +"_popupWindow',
        			{
						width:'"+ this.width +"px', 
						height:'"+ this.heigth +"px', 
						visible:false, 
						draggable:true,
						close:true,
						fixedcenter:true,
						modal:true
					});
	this.panel.render(document.body);
		 
	//this.panel.subscribe("hideEvent", this.hideHandler, this, true);
	this.panel.hideEvent.subscribe(this.hideHandler, this, true);
	//myPanel.hideEvent.subscribe(panelHide_cb, myPanel, true);
	                        
};

wingS.PopupFrame.prototype.hideHandler = function hideHandler() {
	wingS.ajax.requestUpdates();
}

wingS.PopupFrame.prototype.show = function show() {	
	test.getFrameUrl({
		callback: this.showCallback.bind(this),
		timeout: 2000, 
		errorHandler: this.errorHandler.bind(this)
	});		
}

wingS.PopupFrame.prototype.showCallback = function showCallback(response) {	
	this.panel.setBody("<iframe frameborder='0' width='"+this.width+"' height='"+this.height+"' src='"+ response +"'></iframe>"); 	
	this.panel.render();
	this.panel.show();	
}

wingS.PopupFrame.prototype.showBlueshift = function showCallback(response) {		
	this.panel.setBody("<iframe src='http://www.blueshift.de/'></iframe>");
	this.panel.render();
	this.panel.show();	
}

wingS.PopupFrame.prototype.errorHandler = function errorHandler(message) {    
    alert(message);            
}