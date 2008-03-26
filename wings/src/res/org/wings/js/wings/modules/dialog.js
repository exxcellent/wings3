/***************************************************************************************************
 * WINGS.DIALOG  --  contains: functions used for dialogs
 **************************************************************************************************/

// Create module namespace
wingS.namespace("dialog");


/**
 * SDialog behaves like an OS dialog, with a draggable
 * header and an optional close icon at the top right.
 * @param {String} el - the element (ID) representing the SDialog
 * @param {Object} userConfig - the configuration object literal
 */
wingS.dialog.SDialog = function(el, userConfig) {
    wingS.dialog.SDialog.superclass.constructor.call(this, el, userConfig);    
    wingS.global.overlayManager.register(this);
    this.focus();
};

/**
 * Constant representing the default CSS class used for a SDialog
 */
wingS.dialog.SDialog.CSS_DIALOG = "SDialog";

/**
 * Constant representing the SDialog's configuration properties
 */
wingS.dialog.SDialog._DEFAULT_CONFIG = {
    "VIEWPORTELEMENT": {
        key: "viewportelement",
		validator: YAHOO.lang.isString
    },
	
	"PROPAGATE_MOVE_EVENT": {
        key: "propagateMoveEvent",
		value:true,
		validator: YAHOO.lang.isBoolean
    }
};

YAHOO.extend(wingS.dialog.SDialog, YAHOO.widget.Dialog, {

    shadowWidth: 0,

    init: function(el, userConfig) {
        wingS.dialog.SDialog.superclass.init.call(this, el, userConfig);
        
		YAHOO.util.Dom.addClass(this.element, wingS.dialog.SDialog.CSS_DIALOG);

		// Handle close events.
		this.cancelEvent.subscribe(this.destroy, this, true);
		
		if (this.cfg.getProperty("underlay") == "shadow") {
            // Actually the default shadow width is 3px,
            // however, this doesn't seem to be enough.
            this.shadowWidth = 5;
        };
    },

    initDefaultConfig : function() {
        wingS.dialog.SDialog.superclass.initDefaultConfig.call(this);

        // Add SDialog config properties //
        var DEFAULT_CONFIG = wingS.dialog.SDialog._DEFAULT_CONFIG;
		
        // The element that get the dialog as responsible actor
        this.cfg.addProperty(DEFAULT_CONFIG.VIEWPORTELEMENT.key,
        {
            handler: this.configContext,
            validator: DEFAULT_CONFIG.VIEWPORTELEMENT.validator
        });
		
		this.cfg.addProperty(DEFAULT_CONFIG.PROPAGATE_MOVE_EVENT.key,
        {
            handler: this.configMoveHandler,
            value: DEFAULT_CONFIG.PROPAGATE_MOVE_EVENT.value,
			validator: DEFAULT_CONFIG.PROPAGATE_MOVE_EVENT.validator
        });
    },
	
	configMoveHandler: function(type, args, obj) {
		// Register the dialog for move events if args[0] == true.
		if (args[0]) {
			this.moveEvent.subscribe(this.moveHandler, this, true);
		}
	},
	
	/**
	 * Handle move events. Send a request back to server containing the
	 * x-position and y-position of this dialog. That permits a consitent
	 * component state between server and client.
	 * 
	 * @param {Object} type
	 * @param {Object} args
	 * @param {Object} obj
	 */
	moveHandler: function(type, args, obj) {
		wingS.request.sendEvent(null, false, true, this.id + "_xy", args);
	},
	
	doSubmit: function() {
		// do nothing
	},
	
	submit: function() {
		// do nothing
	},
	
	/**
	 * Sends a destroy event to the server and destroys this dialog afterwards.
	 * 
	 * @param {Object} type
	 * @param {Object} args
	 * @param {Object} obj
	 */
	destroy: function(type, args, obj) {
		wingS.request.sendEvent(null, false, true, this.id + "_dispose", 1);
	},

	/**
	 * Centers this dialog within the window or within a viewport element if specified
	 * at instanciation time.
	 */
    center: function() {        
        // Workarounds for IE:
        //  - avoid toggeling dialog width (e.g. while dragging) in IE 7
        //  - resize to correct width regarding border-box-issues in IE 6/7
        //  - TODO: it works but it's still a hack - reevaluate with YUI 2.3.2
        if (YAHOO.env.ua.ie > 6) { // IE 7
            this.element.style.width = (this.element.offsetWidth - 6) + "px";
            // Why subtract 6 ? --> 6 = 3 (padding-left) + 3 (padding-right) of surrounding div
        } else if (YAHOO.env.ua.ie > 0) { // IE 6 and below
            this.element.firstChild.style.width = (this.element.firstChild.offsetWidth - 8) + "px";
            this.element.lastChild.style.width = (this.element.lastChild.offsetWidth - 8) + "px";
            // Why subtract 8 ? --> 8 = 3 (padding-left) + 5 (padding-right) of surrounding div
        }
        
        var viewportelementId = this.cfg.getProperty("viewportelement");
        if (typeof viewportelementId == 'undefined') {
            wingS.dialog.SDialog.superclass.center.call(this);
            return;
        }
        var viewportelement = document.getElementById(viewportelementId);
        var viewportX = YAHOO.util.Dom.getX(viewportelement);
        var viewportY = YAHOO.util.Dom.getY(viewportelement);
        var viewportW = viewportelement.offsetWidth;
        var viewportH = viewportelement.offsetHeight;
        var viewportO = YAHOO.widget.Overlay.VIEWPORT_OFFSET;
        var dialogW = this.element.offsetWidth + (2 * this.shadowWidth);
        var dialogH = this.element.offsetHeight + (1 * this.shadowWidth);
        var centeredX;
        var centeredY;
        
        if (dialogW < viewportW) {
            centeredX = viewportX + (viewportW / 2) - (dialogW / 2);
        } else {
            centeredX = viewportX + viewportOffset;
        }
        
        if (dialogH < viewportH) {
            centeredY = viewportY + (viewportH / 2) - (dialogH / 2);
        } else {
            centeredY = viewportY + viewportOffset;
        }

        this.cfg.setProperty("xy", [parseInt(centeredX, 10), parseInt(centeredY, 10)]);
        this.cfg.refireEvent("iframe");
    },
    
    getConstrainedXY: function(x, y) {
        var viewportelementId = this.cfg.getProperty("viewportelement");
        if (typeof viewportelementId == 'undefined') {
            return wingS.dialog.SDialog.superclass.getConstrainedXY.call(this, x, y);
        }
        var viewportelement = document.getElementById(viewportelementId);
        var viewportX = YAHOO.util.Dom.getX(viewportelement);
        var viewportY = YAHOO.util.Dom.getY(viewportelement);
        var viewportW = viewportelement.offsetWidth;
        var viewportH = viewportelement.offsetHeight;
        var viewportO = YAHOO.widget.Overlay.VIEWPORT_OFFSET;
        var dialogW = this.element.offsetWidth + (2 * this.shadowWidth);
        var dialogH = this.element.offsetHeight + (1 * this.shadowWidth);
        var constrainedX = x;
        var constrainedY = y;

        if (dialogW + viewportO < viewportW) {
            var leftConstraint = viewportX + viewportO;
            var rightConstraint = viewportX + viewportW - dialogW - viewportO;
            if (x < leftConstraint) {
                constrainedX = leftConstraint;
            } else if (x > rightConstraint) {
                constrainedX = rightConstraint;
            }
        } else {
            constrainedX = viewportO + viewportX;
        }

        if (dialogH + viewportO < viewportH) {
            var topConstraint = viewportY + viewportO;
            var bottomConstraint = viewportY + viewportH - dialogH - viewportO;
            if (y < topConstraint) {
                constrainedY  = topConstraint;
            } else if (y  > bottomConstraint) {
                constrainedY  = bottomConstraint;
            }
        } else {
            constrainedY = viewportO + viewportY;
        }

        return [constrainedX, constrainedY];
    },

    configModal: function(type, args, obj) {
        var modal = args[0];
        if (modal) {
            if (!this._hasModalityEventListeners) {
                this.buildMask();
                this.bringToTop();
                this.showMask();
                this.sizeMask();
            }
        } else {
            if (this._hasModalityEventListeners) {

                if (this.cfg.getProperty("visible")) {
                    this.hideMask();
                    this.removeMask();
                }

                this.unsubscribe("beforeShow", this.buildMask);
                this.unsubscribe("beforeShow", this.bringToTop);
                this.unsubscribe("beforeShow", this.showMask);
                this.unsubscribe("hide", this.hideMask);

                Overlay.windowResizeEvent.unsubscribe(this.sizeMask, this);

                this._hasModalityEventListeners = false;
            }
        }
    },

    registerDragDrop: function () {
        var viewportelementId = this.cfg.getProperty("viewportelement");
        if (typeof viewportelementId == 'undefined') {
            wingS.dialog.SDialog.superclass.registerDragDrop.call(this);
            return;
        }

        var me = this;

        if (this.header) {
            if (!YAHOO.util.DD) {
                YAHOO.log("DD dependency not met.", "error");
                return;

            }
            this.dd = new YAHOO.util.DD(this.element.id, this.id);

            if (!this.header.id) {
                this.header.id = this.id + "_h";
            }
            
            this.dd.startDrag = function () {
                var offsetHeight,
                    offsetWidth,
                    viewPortWidth,
                    viewPortHeight,
                    scrollX,
                    scrollY,
                    topConstraint,
                    leftConstraint,
                    bottomConstraint,
                    rightConstraint;

                if (YAHOO.env.ua.ie == 6) {
                    YAHOO.util.Dom.addClass(me.element, "drag");
                }

                if (me.cfg.getProperty("constraintoviewport")) {
                    var viewportelement = document.getElementById(viewportelementId);        
                    var viewportX = YAHOO.util.Dom.getX(viewportelement);
                    var viewportY = YAHOO.util.Dom.getY(viewportelement);
                    var viewportW = viewportelement.offsetWidth;
                    var viewportH = viewportelement.offsetHeight;
                    var viewportO = YAHOO.widget.Overlay.VIEWPORT_OFFSET;
                    var dialogW = me.element.offsetWidth + (2 * me.shadowWidth);
                    var dialogH = me.element.offsetHeight + (1 * me.shadowWidth);
                    
                    var topConstraint = viewportY + viewportO;
                    var leftConstraint = viewportX + viewportO;
                    var bottomConstraint = viewportY + viewportH - dialogH - viewportO;
                    var rightConstraint = viewportX + viewportW - dialogW - viewportO;

                    this.minX = leftConstraint;
                    this.maxX = rightConstraint;
                    this.constrainX = true;
                    this.minY = topConstraint;
                    this.maxY = bottomConstraint;
                    this.constrainY = true;
                }
                else {
                    this.constrainX = false;
                    this.constrainY = false;
                }
                me.dragEvent.fire("startDrag", arguments);
            };

            this.dd.onDrag = function () {
                me.syncPosition();
                me.cfg.refireEvent("iframe");
                if (this.platform == "mac" && YAHOO.env.ua.gecko) {
                    this.showMacGeckoScrollbars();
                }
                me.dragEvent.fire("onDrag", arguments);
            };

            this.dd.endDrag = function () {
                if (YAHOO.env.ua.ie == 6) {
                    YAHOO.util.Dom.removeClass(me.element, "drag");
                }
                me.dragEvent.fire("endDrag", arguments);
                me.moveEvent.fire(me.cfg.getProperty("xy"));
            };

            this.dd.setHandleElId(this.header.id);
            this.dd.addInvalidHandleType("INPUT");
            this.dd.addInvalidHandleType("SELECT");
            this.dd.addInvalidHandleType("TEXTAREA");
        }
    },

    buildMask: function () {
        var oMask = this.mask;
        if (!oMask) {
            oMask = document.createElement("div");
            oMask.className = "mask";
            oMask.innerHTML = "&#160;";
            oMask.id = this.id + "_mask";

            YAHOO.util.Dom.insertAfter(oMask, this.element);

            this.mask = oMask;

             // Stack mask based on the element zindex
            this.stackMask();
        }
    },

    hideMask: function () {
        if (this.cfg.getProperty("modal") && this.mask) {
            this.mask.style.display = "none";
            this.hideMaskEvent.fire();
        }
    },

    showMask: function () {
        if (this.cfg.getProperty("modal") && this.mask) {
            this.sizeMask();
            this.mask.style.display = "block";
            this.showMaskEvent.fire();
        }
    },

    sizeMask: function () {
        var viewportelementId = this.cfg.getProperty("viewportelement");
        if (typeof viewportelementId == 'undefined') {
            wingS.dialog.SDialog.superclass.sizeMask.call(this);
            return;
        }
        var viewportelement = document.getElementById(viewportelementId);
        
        if (this.mask) {
            var zIndex = "2";
            var top = YAHOO.util.Dom.getY(viewportelement) + "px";
            var left = YAHOO.util.Dom.getX(viewportelement) + "px";
            var height = viewportelement.offsetHeight + "px";
            var width = viewportelement.offsetWidth + "px";

            if (YAHOO.env.ua.ie == 7) {
                top = (YAHOO.util.Dom.getY(viewportelement) - 2) + "px";
                left = (YAHOO.util.Dom.getX(viewportelement) - 2) + "px";
            }

            this.element.style.zIndex = zIndex;
            this.mask.style.top = top;
            this.mask.style.left = left;
            this.mask.style.height = height;
            this.mask.style.width = width;
        }
    },

    toString: function() {
        return "wingS.dialog.SDialog " + this.id;
    }
});

/**
 * Creates the markup for a dialog with the given container id and returns
 * the container element.
 * @param {Object} containerId The id that will be set for the container.
 */
wingS.dialog.createDialogMarkup = function(containerId, cfg){
    var container = document.createElement("div");
    container.id = containerId;
	
	var hd = wingS.dialog.createConfiguredElement("hd", cfg);
	var bd = wingS.dialog.createConfiguredElement("bd", cfg);
	var ft = wingS.dialog.createConfiguredElement("ft", cfg);
	
    container.appendChild(hd);
	container.appendChild(bd);
	container.appendChild(ft);
	
    return container;
}

wingS.dialog.createConfiguredElement = function(className, cfg) {
	var el = document.createElement("div");
	el.className = className;
	
	if (cfg == null || cfg == 'undefined') {
		return el;
	}
	
	el.className = el.className + (cfg.className != 'undefined' ? " " + cfg.className : "");
	
	return el;
}

wingS.dialog.showExceptionDialog = function(exception){
	
	// Initialize exception dialog count if not already done.
    if (!wingS.dialog.exceptionDialogCount) {
        wingS.dialog.exceptionDialogCount = 0;
    }
    
	// Count of currently available exception dialogs.
    var count = wingS.dialog.exceptionDialogCount;

	// Ids for exception message and exception detail container.    
    var exceptionMessageId = "exceptionMessage" + count;
    var exceptionDetailId = "exceptionDetail" + count;
    
	// Create exception dialog markup containing exception message
	// container and exception detail container.
    var exceptionMarkup = wingS.dialog.createExceptionDialogMarkup(exceptionMessageId, exceptionDetailId)
	document.body.appendChild(exceptionMarkup);	
    
    // Define various event handlers for Dialog
    var handleClose = function(){
		this.removeMask();
        this.destroy();
        wingS.request.reloadFrame();
    };
    var toggleDetails = function(){
		var visible = detail.cfg.getProperty("visible");
		detail.cfg.setProperty("visible", !visible);
    };
    
	// Instantiate the Dialog
    var dialog = new YAHOO.widget.SimpleDialog(exceptionMessageId, {
        width: "400px",
        fixedcenter: true,
        visible: false,
        draggable: true,
        modal: true,
        close: true,
        icon: YAHOO.widget.SimpleDialog.ICON_BLOCK,
        constraintoviewport: true,
        buttons: [{
            text: "Close",
            handler: handleClose,
            isDefault: true
        }, {
            text: "Details",
            handler: toggleDetails
        }]
    });
	
	// Instantiate the Module
    var detail = new YAHOO.widget.Module(exceptionDetailId, {
		width: "400px",
		visible: false
	});
    
    dialog.setHeader("Error message");
    dialog.setBody(exception.message);
    dialog.render();
    
	// Prepares the message.
    var detailedMessage = wingS.dialog.prepareDetailedMessage(exception.message + "\n" + exception.detail);
    
	detail.setHeader("Detailed Message:");
    detail.setBody(detailedMessage);
    detail.render();
    
    dialog.show();
};

/**
 * Creates the markup for the exception dialog.
 * 
 * @param {Object} exceptionMessageId The id for the exception message container.
 * @param {Object} exceptionDetailId The id for the exception detail container.
 */
wingS.dialog.createExceptionDialogMarkup = function(exceptionMessageId, exceptionDetailId){

    var exceptionMessage = wingS.dialog.createDialogMarkup(exceptionMessageId);
	
    var exceptionDetail = document.createElement("div");
	exceptionDetail.id = exceptionDetailId;
	
	exceptionMessage.appendChild(exceptionDetail);
	
	return exceptionMessage;
}

/**
 * Uses a message and converts each '\n' into a '<br/>' element and wraps the rest
 * of the message into text nodes.
 * @param {Object} msg The message to be prepared.
 * @return {HTMLElement} The prepared message wrapped into a '<pre>' element.
 */
wingS.dialog.prepareDetailedMessage = function(msg) {
	var parts = msg.split("\\n");

	var detailedMessage = document.createElement("textarea");
/*
	for (var i = 0; i < parts.length; i++) {
		var textNode = document.createTextNode(parts[i]);
		detailedMessage.appendChild(textNode);
		
		if ((i + 1) < parts.length) {
			var br = document.createElement("br");
			detailedMessage.appendChild(br);
		}
	}
*/
    detailedMessage.value = msg;
	
//	detailedMessage.style.overflow = "auto";
    detailedMessage.style.width = "100%";
	detailedMessage.style.height = "120px";
	detailedMessage.style.border = "1px solid black";
	detailedMessage.readOnly = true;
//	detailedMessage.style.backgroundColor = "white";
	
	return detailedMessage;
}