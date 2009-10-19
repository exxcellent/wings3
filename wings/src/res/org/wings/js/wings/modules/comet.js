/***************************************************************************************************
 * WINGS.COMET  --  contains: functions used to process ajax comet requests
 **************************************************************************************************/

// Create module namespace
wingS.namespace("comet");

// Set the Comet Path
//wingS.comet.cometPath = "http://localhost:8080/comet_chat/Chat/blub";
wingS.comet.cometPath = "hanging";

wingS.comet.newHangingGetAllowed = false;

wingS.comet.requestUpdates = function() {
    wingS.request.sendEvent(null, false, true, "comet", "1");
};

wingS.comet.requestUpdates_polling = function() {
    wingS.request.sendEvent(null, false, true, "polling", "1");
};

wingS.comet.connect = function() {
    //wingS.comet.newHangingGetAllowed = true;
    //if (wingS.global.asyncHeaderQueue.length == 0) {
        //wingS.ajax.dequeueNextRequest();
    //}
    wingS.comet.sendHangingGetRequest();
};

wingS.comet.disconnect = function() {
    clearInterval(wingS.comet.intervalID);
    wingS.comet.newHangingGetAllowed = false;
};

wingS.comet.periodicPolling = function(interval) {
    wingS.comet.intervalID = setInterval("wingS.comet.requestUpdates_polling()", interval);
};

wingS.comet.switchToHanging = function() {
    clearInterval(wingS.comet.intervalID);
    wingS.comet.connect();
};

wingS.comet.init = function() {

    // Send first Hanging GET request
    wingS.comet.connect();

    //wingS.comet.periodicPolling(2000);
    //wingS.comet.streamTriggers();
};

wingS.comet.handleSuccess = function(request) {
    if (wingS.global.debugMode)
        wingS.ajax.updateDebugView(request);

    // Get the received XML response
    var xmlDoc = request.responseXML;
    if (xmlDoc == null) {
        // In case we don't get any XML there is nothing more
        // what we can do here; the only thing --> do reload!
        wingS.request.reloadFrame();
        // Better?: wingS.ajax.processRequestFailure(request);
        return;
    }

    // Get the document's root element
    var xmlRoot = xmlDoc.getElementsByTagName("updates")[0];
    if (xmlRoot == null) {
        // Workaround to prevent IE from showing JS errors
        // if session has meanwhile timed out --> do reload!
        wingS.request.reloadFrame();
        // Better?: wingS.ajax.processRequestFailure(request);
        return;
    }

    // Process each incremental update
    var updates = xmlRoot.getElementsByTagName("update");
    if (updates.length > 0) {
        for (var i = 0; i < updates.length; i++) {
            try {
                // Dispatch update to the corresponding
                // handler function simply by evaluation
                window.eval(updates[i].firstChild.data);
            } catch(e) {
                var errorMsg = "Failure while processing the reponse of an AJAX Comet request!\n" +
                               "**********************************************\n\n" +
                               "Error Message: " + e.message + "!\n\n" +
                               "The error occurred while evaluating the following JS code:\n" +
                               updates[i].firstChild.data;
                alert(errorMsg);
            }
        }
    }
};

wingS.comet.handleFailure = function(request) {

    /*
    if (request.status == -1) {
        alert("transaction aborted");
    } else if (request.status == 0) {
        alert("communication failure");
    }
    */

    // Pull updates & send new Hanging GET request
    wingS.comet.requestUpdates();
};

wingS.comet.callbackObject = {
    success : function(request) { wingS.comet.handleSuccess(request); },
    failure : function(request) { wingS.comet.handleFailure(request); }
};

wingS.comet.sendHangingGetRequest = function() {
	uri = wingS.comet.cometPath 
		+ "-" + wingS.global.updateResource  
		+ "?_xhrID=" + new Date().getTime();
    wingS.comet.connectionObject = 
        YAHOO.util.Connect.asyncRequest("GET", uri, wingS.comet.callbackObject);
};

wingS.comet.streamTriggers = function() {

    var streamreq = new XMLHttpRequest();
    var byteoffset = 0;
    var buffer;
    var newdata;
    var url = "blub";
    var now = new Date();
    var t = now.getTime();
    url += "?nocache=" + t;

    streamreq.open("GET", url, true);

    streamreq.onreadystatechange = function() {

        if (streamreq.readyState == 3) {

            buffer = streamreq.responseText;
            newdata = buffer.substring(byteoffset);
            byteoffset = buffer.length;

            while (1) {
                var x = newdata.indexOf("<update>");
                if (x != -1) {
                    var y = newdata.indexOf("</update>");
                    if (y != -1) {
                        window.eval(newdata.substring((x+8),y));
                        newdata = newdata.substring(y+9);
                    } else {
                        // Last message is corrupt or incomplete. Ignore it and it will be fetched again.
                        break;
                    }
               } else {
               // No more messages.
               break;
               }
            }

            byteoffset = buffer.length - newdata.length;
        }
    }

    streamreq.send(null);
};

/*
wingS.comet.getCookie = function( name ) {
	var start = document.cookie.indexOf( name + "=" );
	var len = start + name.length + 1;
	if ( ( !start ) && ( name != document.cookie.substring( 0, name.length ) ) ) {
		return null;
	}
	if ( start == -1 ) return null;
	var end = document.cookie.indexOf( ';', len );
	if ( end == -1 ) end = document.cookie.length;
	return unescape( document.cookie.substring( len, end ) );
}

wingS.comet.setCookie = function( name, value, expires, path, domain, secure ) {
	var today = new Date();
	today.setTime( today.getTime() );
	if ( expires ) {
		expires = expires * 1000 * 60 * 60 * 24;
	}
	var expires_date = new Date( today.getTime() + (expires) );
	document.cookie = name+'='+escape( value ) +
		( ( expires ) ? ';expires='+expires_date.toGMTString() : '' ) +
		( ( path ) ? ';path=' + path : '' ) +
		( ( domain ) ? ';domain=' + domain : '' ) +
		( ( secure ) ? ';secure' : '' );
}

wingS.comet.deleteCookie = function( name, path, domain ) {
	if ( getCookie( name ) ) document.cookie = name + '=' +
			( ( path ) ? ';path=' + path : '') +
			( ( domain ) ? ';domain=' + domain : '' ) +
			';expires=Thu, 01-Jan-1970 00:00:01 GMT';
}
*/