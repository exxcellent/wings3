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
package org.wings.plaf;

import org.wings.SComponent;
import org.wings.io.Device;

import java.io.IOException;
import java.io.Serializable;

public interface ComponentCG<COMPONENT_TYPE extends SComponent> extends Serializable {
    /**
     * Installs the CG.
     * <p/>
     * <p><b>Note</b>: Be very careful here since this method is called from
     * the SComponent constructor! Don't call any methods which rely on
     * something that will be constructed in a subconstructor later!
     */
    public void installCG(COMPONENT_TYPE c);

    /**
     * Uninstalls the CG.
     */
    public void uninstallCG(COMPONENT_TYPE c);

    /**
     * Writes the given component to the Device.
     * <p/>
     * <p>This renders the component according to this pluggable look and
     * feel; it reads the properties of the component and genereates the
     * HTML, XML or whatever representation that is written to the Device.
     * <p/>
     * <p>This method should be called from the write method in SComponent or
     * a subclass. It delegates
     *
     * @param device    the output device.
     * @param component the component to be rendered.
     */
    public void write(Device device, COMPONENT_TYPE component) throws IOException;

    /**
     * Returns an update for the complete component.
     *
     * @param component the component to be updated.
     */
    public Update getComponentUpdate(COMPONENT_TYPE component);
    
    /**
     * Options to pass to the Layout-Script for a component 
     */
    public static String RELAYOUT_FILL = "fill";
    public static String RELAYOUT_FIX = "fix";
    public static String RELAYOUT_SCROLLPANE = "scrollPane";
    
}
