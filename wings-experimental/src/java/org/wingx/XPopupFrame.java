/*
 * Copyright 2000,2006 wingS development team.
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

import org.wings.SComponent;
import org.wings.SFrame;

public class XPopupFrame extends SComponent {

    private SFrame frame;
    private int width;
    private int height;

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    // TODO
    public XPopupFrame(String url) {

    }

    public XPopupFrame(SFrame frame, int width, int height) {
        this.frame = frame;
        this.width = width;
        this.height = height;
    }

    public String getFrameUrl() {
        this.frame.show();
        return this.frame.getRequestURL().toString();
    }

    public String showScript() {
        return "function() {popupFrame.show();}";
    }

}
