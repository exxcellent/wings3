/*
 * Copyright 2007 wingS development team.
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

package org.wingx;

/**
 *
 * @author Christian Schyma
 */
public interface XColorPickerInterface {
    
    /**
     * Color selection.
     * @param red
     * @param green
     * @param blue
     * @return true if a component update request is needed (this is used by
     * the client-side JavaScript to keep all components in sync)
     */
    public boolean setSelectedColor(int red, int green, int blue);
    
}
