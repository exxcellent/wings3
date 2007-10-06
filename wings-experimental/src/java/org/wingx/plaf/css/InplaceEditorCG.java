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

import org.wings.SComponent;
import org.wings.SIcon;
import org.wings.event.SParentFrameEvent;
import org.wings.event.SParentFrameListener;
import org.wings.header.SessionHeaders;
import org.wings.io.Device;
import org.wings.plaf.css.LabelCG;
import org.wings.plaf.css.Utils;
import org.wings.plaf.css.dwr.CallableManager;
import org.wings.plaf.css.script.OnHeadersLoadedScript;
import org.wings.resource.ResourceManager;
import org.wings.session.ScriptManager;
import org.wings.util.SStringBuilder;
import org.wingx.XInplaceEditor;
import org.wingx.XInplaceEditorInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Christian Schyma
 */
public class InplaceEditorCG extends LabelCG implements SParentFrameListener {

    protected final List headers = new ArrayList();
   
    // these graphics are needed for inplaceeditor.js
    static {
        ((SIcon) ResourceManager.getObject("InplaceEditorCG.okButton", SIcon.class)).getURL();
        ((SIcon) ResourceManager.getObject("InplaceEditorCG.cancelButton", SIcon.class)).getURL();
    }

    public InplaceEditorCG() {
        // TODO move to properties file
        headers.add(Utils.createExternalizedJSHeader("org/wingx/inplaceeditor/inplaceeditor.js"));
        headers.add(Utils.createExternalizedCSSHeader("org/wingx/inplaceeditor/inplaceeditor.css"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/js/MochiKit/Base.js"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/js/MochiKit/Iter.js"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/js/MochiKit/DOM.js"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/js/MochiKit/Style.js"));
    }

    public void installCG(final SComponent comp) {
        super.installCG(comp);
        comp.addParentFrameListener(this);
    }

    public void writeInternal(Device device, SComponent component) throws IOException {
        super.writeInternal(device, component);

        String script = generateInitScript((XInplaceEditor) component);
        ScriptManager.getInstance().addScriptListener(new OnHeadersLoadedScript(script, false));
    }

    private String generateInitScript(XInplaceEditor inplace) {
        SStringBuilder options = new SStringBuilder("{");
        options
                .append("cols: ").append(inplace.getCols()).append(",")
                .append("rows: ").append(inplace.getRows()).append(",")
                .append("timeout: ").append(inplace.getTimeout()).append(",")
                .append("clickNotificationText: '").append(inplace.getClickNotificationText()).append("'}");

        SStringBuilder script = new SStringBuilder("function () {new wingS.InplaceEditor(\"");
        script
                .append(inplace.getName()).append("\", ")
                .append(inplace.getClientProperty("DWR_JS_OBJECT")).append(", ")                
                .append(options.toString()).append(");}");        
        
        return script.toString();
    }

    public void parentFrameAdded(SParentFrameEvent e) {
        SessionHeaders.getInstance().registerHeaders(headers);

        SComponent c = e.getComponent();
        String DWR_JS_OBJECT = c.getName() + "_inplace";
        c.putClientProperty("DWR_JS_OBJECT", DWR_JS_OBJECT);

        // expose data source to java script by using DWR        
        CallableManager.getInstance().registerCallable(DWR_JS_OBJECT, c, XInplaceEditorInterface.class);
    }

    public void parentFrameRemoved(SParentFrameEvent e) {
        SessionHeaders.getInstance().deregisterHeaders(headers);

        String DWR_JS_OBJECT = (String)e.getComponent().getClientProperty("DWR_JS_OBJECT");
        CallableManager.getInstance().unregisterCallable(DWR_JS_OBJECT);
    }

}
