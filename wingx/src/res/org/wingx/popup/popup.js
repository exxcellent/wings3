// faking a namespace
if (!wingS) {
    var wingS = new Object();
} else if (typeof wingS != "object") {
    throw new Error("wingS already exists and is not an object");
}

/**
 * Creates an ajaxified popup.
 *
 * @param {String} id element id of the popup to be created
 * @param {int} x position (origin top left)
 * @param {int} y position (origin top left)
 * @param {int} width
 * @param {int} height
 * @param {String} anchor element to anchor the popup to
 * @param {String} corner "tr"|"tl"|"br"|"bl"
 *
 * for information about contentsCorner and contextCorner see 
 * http://developer.yahoo.com/yui/container/overlay/index.html
 *
 */
wingS.XPopup = function(id, x, y, width, height, anchor, corner) {
    this.id             = id;
    this.x              = x;
    this.y              = y;
    this.width          = width;
    this.height         = height;
    this.anchor         = anchor;
    this.corner         = corner;
    this.visible        = false;
};

/**
 * Shows this popup.
 *
 * Sends a request to the server. The actual rendering will be done at
 * showPopupCallback().
 */
wingS.XPopup.prototype.show = function show() {
    if (this.anchor) {
        var anchorElement = document.getElementById(this.anchor);
        var xAbs = YAHOO.util.Dom.getX(anchorElement);
        var yAbs = YAHOO.util.Dom.getY(anchorElement);
        if ("bl" == this.corner || "br" == this.corner) {
            yAbs = yAbs + anchorElement.offsetHeight;
        }
        if ("tr" == this.corner || "br" == this.corner) {
            xAbs = xAbs + anchorElement.offsetWidth;
        }
        this.popup = new YAHOO.widget.Overlay(this.id,
        {   xy:      [this.x + xAbs, this.y + yAbs],
            width:   this.width + "px",
            height:  this.height + "px",
            monitorresize: false
        });
    } else {
        this.popup = new YAHOO.widget.Overlay(this.id,
        {   xy:      [this.x, this.y],
            width:   this.width + "px",
            height:  this.height + "px",
            monitorresize: false
        });
    }
    this.popup.setBody(document.getElementById("content_" + this.id));
    this.popup.render(document.body);
    this.popup.show();
    this.popup.visible = true;
}

/**
 * Hides this popup.
 * 
 * Why does it get destroyed and not hidden? Because hide() does not clean 
 * up the DOM which will then lead to a steadily increasing DOM if wingS 
 * is in incremental update mode. 
 */
wingS.XPopup.prototype.hide = function hide() {
    if (this.popup) {
        this.popup.destroy();
    }
    this.visible = false;
}

wingS.XPopup.prototype.toggle = function() {
    if (!this.visible) {
        this.show();
    } else {
        this.hide();
    }
}