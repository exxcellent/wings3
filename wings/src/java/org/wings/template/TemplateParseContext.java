/*
 * Copyright 2000,2005 wingS development team.
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
package org.wings.template;

import org.wings.SComponent;
import org.wings.STemplateLayout;
import org.wings.io.Device;
import org.wings.io.DeviceOutputStream;
import org.wings.template.parser.ParseContext;

import java.io.OutputStream;
import java.util.*;

/**
 * @author <a href="mailto:H.Zeller@acm.org">Henner Zeller</a>
 */
public class TemplateParseContext implements ParseContext {
    private final OutputStream myOut;
    private final Device sink;
    protected final STemplateLayout layout;
    private final Set<String> containedComponents = new HashSet<String>();
    private final Map<String,Map<String,String>> componentProperties = new HashMap<String, Map<String, String>>();

    public TemplateParseContext(final Device sink, STemplateLayout layout) {
        this.sink = sink;
        this.layout = layout;
        myOut = new DeviceOutputStream(sink);
    }

    public OutputStream getOutputStream() {
        return myOut;
    }

    public void startTag(int number) {
    }

    public void doneTag(int number) {
    }

    public void addContainedComponent(String component) {
        containedComponents.add(component);
    }

    public Set<String> getContainedComponents() {
        return containedComponents;
    }

    /*
     * important for the template: the components write to this sink
     */
    public Device getDevice() {
        return sink;
    }

    public SComponent getComponent(String name) {
        return layout.getComponent(name);
    }

    public void setProperties(String name, Map<String,String> properties) {
        componentProperties.put(name, properties);
    }

    public Map<String, Map<String, String>> getComponentProperties() {
        return componentProperties;
    }
}


