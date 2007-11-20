/***************************************************************************************************
 * WINGS.DIALOG  --  contains: functions used for dialogs
 **************************************************************************************************/

// Create module namespace
wingS.namespace("dialog");

/**
 * SDialog is an implementation of Panel that behaves like an OS dialog, with a draggable header and an optional close icon at the top right.
 * @namespace wingS.dialog
 * @class SDialog
 * @extends YAHOO.widget.Panel
 * @constructor
 * @param {String}	el	The element ID representing the SDialog <em>OR</em>
 * @param {HTMLElement}	el	The element representing the SDialog
 * @param {Object}	userConfig	The configuration object literal containing the configuration that should be set for this SDialog. See configuration documentation for more details.
 */
wingS.dialog.SDialog = function(el, userConfig) {
    wingS.dialog.SDialog.superclass.constructor.call(this, el, userConfig);
};

/**
 * Constant representing the default CSS class used for a SDialog
 * @property wingS.dialog.SDialog.CSS_DIALOG
 * @static
 * @final
 * @type String
 */
wingS.dialog.SDialog.CSS_DIALOG = "SDialog";

/**
 * Constant representing the SDialog's configuration properties
 * @property wingS.dialog.SDialog._DEFAULT_CONFIG
 * @private
 * @final
 * @type Object
 */
wingS.dialog.SDialog._DEFAULT_CONFIG = {

    "VIEWPORTELEMENT": {
        key: "viewportelement",
        suppressEvent:true,
        supercedes:["iframe"]
    }
};

YAHOO.extend(wingS.dialog.SDialog, YAHOO.widget.Panel, {

    init: function(el, userConfig) {
        wingS.dialog.SDialog.superclass.init.call(this, el);

        this.beforeInitEvent.fire(wingS.dialog.SDialog);

        //YAHOO.util.Dom.addClass(this.element, wingS.dialog.SDialog.CSS_DIALOG);

        if (userConfig) {
            this.cfg.applyConfig(userConfig, true);
        }

        function onBeforeShow() {
            if (typeof console != 'undefined')
                console.log("onBeforeShow");

            this.unsubscribe("beforeShow", onBeforeShow);

            //alert("width: " + this.element.offsetWidth);
        }

        this.subscribe("beforeShow", onBeforeShow);

        this.initEvent.fire(wingS.dialog.SDialog);
    },

    initDefaultConfig : function() {
        wingS.dialog.SDialog.superclass.initDefaultConfig.call(this);

        // Add SDialog config properties //

        var DEFAULT_CONFIG = wingS.dialog.SDialog._DEFAULT_CONFIG;

        /**
         * The element that get the dialog as responsible actor.
         * @config viewportelement
         * @type String
         * @default null
         */
        this.cfg.addProperty(
                DEFAULT_CONFIG.VIEWPORTELEMENT.key,
        {
            handler: this.configContext,
            suppressEvent: DEFAULT_CONFIG.VIEWPORTELEMENT.suppressEvent,
            supercedes: DEFAULT_CONFIG.VIEWPORTELEMENT.supercedes
        }
                );
    },

    center: function() {

        var viewportelementId = this.cfg.getProperty("viewportelement");

        if (typeof viewportelementId == 'undefined') {
            wingS.dialog.SDialog.superclass.center.call(this);
            return;
        }

        var viewportelement = document.getElementById(viewportelementId);

        var xPos = YAHOO.util.Dom.getX(viewportelement);
        var yPos = YAHOO.util.Dom.getY(viewportelement);

        var viewPortWidth = viewportelement.offsetWidth;
        var viewPortHeight = viewportelement.offsetHeight;

        var elementWidth = this.element.offsetWidth;
        var elementHeight = this.element.offsetHeight;

        var x = (viewPortWidth / 2) - (elementWidth / 2) + xPos;
        var y = (viewPortHeight / 2) - (elementHeight / 2) + yPos;

        this.cfg.setProperty("xy", [parseInt(x, 10), parseInt(y, 10)]);

        this.cfg.refireEvent("iframe");
    },

    enforceConstraints : function(type, args, obj) {

        var viewportelementId = this.cfg.getProperty("viewportelement");

        if (typeof viewportelementId == 'undefined') {
            wingS.dialog.SDialog.superclass.enforceConstraints.call(this);
            return;
        }

        var pos = args[0];

        var x = pos[0];
        var y = pos[1];

        var offsetHeight = this.element.offsetHeight;
        var offsetWidth = this.element.offsetWidth;

        var viewportelement = document.getElementById(viewportelementId);

        var xPos = YAHOO.util.Dom.getX(viewportelement);
        var yPos = YAHOO.util.Dom.getY(viewportelement);

        var viewPortWidth = viewportelement.offsetWidth;
        var viewPortHeight = viewportelement.offsetHeight;

        var topConstraint = yPos + 10;
        var leftConstraint = xPos + 10;
        var bottomConstraint = yPos + viewPortHeight - offsetHeight - 10;
        var rightConstraint = xPos + viewPortWidth - offsetWidth - 10;

        if (x < leftConstraint) {
            x = leftConstraint;
        } else if (x > rightConstraint) {
            x = rightConstraint;
        }

        if (y < topConstraint) {
            y = topConstraint;
        } else if (y > bottomConstraint) {
            y = bottomConstraint;
        }

        this.cfg.setProperty("x", x, true);
        this.cfg.setProperty("y", y, true);
        this.cfg.setProperty("xy", [x,y], true);
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

    /**
     * Registers the Panel's header for drag & drop capability.
     * @method registerDragDrop
     */
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

                    var offsetHeight = me.element.offsetHeight;
                    var offsetWidth = me.element.offsetWidth;

                    var viewportelement = document.getElementById(viewportelementId);

                    var xPos = YAHOO.util.Dom.getX(viewportelement);
                    var yPos = YAHOO.util.Dom.getY(viewportelement);

                    var viewPortWidth = viewportelement.offsetWidth;
                    var viewPortHeight = viewportelement.offsetHeight;

                    var topConstraint = yPos + 10;
                    var leftConstraint = xPos + 10;
                    var bottomConstraint = yPos + viewPortHeight - offsetHeight - 10;
                    var rightConstraint = xPos + viewPortWidth - offsetWidth - 10;

                    this.minX = leftConstraint;
                    this.maxX = rightConstraint;
                    this.constrainX = true;

                    this.minY = topConstraint;
                    this.maxY = bottomConstraint;
                    this.constrainY = true;

                } else {

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

    /**
     * Builds the mask that is laid over the document when the Panel is
     * configured to be modal.
     * @method buildMask
     */
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

    /**
     * Hides the modality mask.
     * @method hideMask
     */
    hideMask: function () {
        if (this.cfg.getProperty("modal") && this.mask) {
            this.mask.style.display = "none";
            this.hideMaskEvent.fire();
        }
    },

    /**
     * Shows the modality mask.
     * @method showMask
     */
    showMask: function () {
        if (this.cfg.getProperty("modal") && this.mask) {
            this.sizeMask();
            this.mask.style.display = "block";
            this.showMaskEvent.fire();
        }
    },

    /**
     * Sets the size of the modality mask to cover the entire scrollable
     * area of the document
     * @method sizeMask
     */
    sizeMask: function () {

        var viewportelementId = this.cfg.getProperty("viewportelement");

        if (typeof viewportelementId == 'undefined') {
            wingS.dialog.SDialog.superclass.sizeMask.call(this);
            return;
        }

        var viewportelement = document.getElementById(viewportelementId);

        if (this.mask) {
            this.element.style.zIndex = "2";
            this.mask.style.top = YAHOO.util.Dom.getY(viewportelement) + "px";
            this.mask.style.left = YAHOO.util.Dom.getX(viewportelement) + "px";
            this.mask.style.height = viewportelement.offsetHeight + "px";
            this.mask.style.width = viewportelement.offsetWidth + "px";
        }
    },

    toString: function() {
        return "wingS.dialog.SDialog " + this.id;
    }
});