/*
 * Copyright 2006 wingS development team.
 *
 * This file is part of wingS (http://wingsframework.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package org.wingx.plaf.css;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.wings.SComponent;
import org.wings.event.SParentFrameEvent;
import org.wings.event.SParentFrameListener;
import org.wings.header.SessionHeaders;
import org.wings.io.Device;
import org.wings.plaf.css.TextFieldCG;
import org.wings.plaf.css.Utils;
import org.wings.plaf.css.dwr.CallableManager;
import org.wings.plaf.css.script.OnHeadersLoadedScript;
import org.wings.session.ScriptManager;
import org.wings.util.SStringBuilder;
import org.wingx.XSuggest;
import org.wingx.XSuggestDataSource;

/**
 *
 * @author Christian Schyma
 */
public class SuggestCG extends TextFieldCG implements SParentFrameListener {

    protected final List headers = new ArrayList();
    
    public SuggestCG() {
        // TODO move to properties
        headers.add(Utils.createExternalizedCSSHeader("org/wingx/suggest/suggest.css"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/suggest/suggest.js"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/js/MochiKit/Base.js"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/js/MochiKit/Iter.js"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/js/MochiKit/DOM.js"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/js/MochiKit/Logging.js"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/js/MochiKit/Style.js"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/js/MochiKit/Color.js"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/js/MochiKit/Visual.js"));
    }

    public void writeInternal(Device device, SComponent component) throws IOException {
        super.writeInternal(device, component);

        String script = generateInitScript((XSuggest) component);
        ScriptManager.getInstance().addScriptListener(new OnHeadersLoadedScript(script, false));
    }

    public void installCG(final SComponent comp) {
        super.installCG(comp);
        comp.addParentFrameListener(this);
    }

    private String generateInitScript(XSuggest suggest) {
        SStringBuilder options = new SStringBuilder();
        options
                .append("{ inputDelayTime: ").append(suggest.getInputDelay())
                .append(", timeout: ").append(suggest.getTimeout())
                .append(", suggestBoxWidth: '").append(suggest.getSuggestBoxWidth().getWidth())
                .append("'}");

        return "function() {new wingS.Suggest(\""+
                suggest.getName() +"\", "+
                suggest.getClientProperty("javaScriptObject") +", "+                
                options.toString() +");}";
    }

    public void parentFrameAdded(SParentFrameEvent e) {
        SessionHeaders.getInstance().registerHeaders(headers);

        XSuggest component = (XSuggest) e.getComponent();

        String javaScriptObject = component.getName() + "_suggest";
        component.putClientProperty("javaScriptObject", javaScriptObject);
        
        // expose methods to JavaScript by using DWR
        CallableManager.getInstance().registerCallable(javaScriptObject, component, XSuggestDataSource.class);
    }

    public void parentFrameRemoved(SParentFrameEvent e) {
        SessionHeaders.getInstance().deregisterHeaders(headers);

        String javaScriptObject = (String)e.getComponent().getClientProperty("javaScriptObject");
        CallableManager.getInstance().unregisterCallable(javaScriptObject);
    }

}
