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

import org.wings.SBorderLayout;
import org.wings.SComponent;
import org.wings.SDimension;
import org.wings.SWindow;

/**
 * @author leon
 */
public class XPopup extends SWindow {

    /**
     * The form representing the Popup.
     */
    private SComponent contents;

    public static final String TOP_RIGHT = "tr";

    public static final String TOP_LEFT = "tl";

    public static final String BOTTOM_RIGHT = "br";

    public static final String BOTTOM_LEFT = "bl";

    private SComponent anchor;

    private String corner;

    /**
     * Creates a <code>XPopup</code> which will pe absolutely placed at the specified
     * coordinates.
     * @param contents contents of the popup
     * @param x initial x screen coordinate
     * @param y initial y screen coordinate
     * @exception IllegalArgumentException if contents is null
     */
    public XPopup(SComponent contents, int x, int y) {
        this(contents, null, null, x, y);
    }

    /**
     * Creates a <code>XPopup</code> which will be placed relative to the given anchor component's corner.
     * @param contents the contents of the popup.
     * @param anchor the anchor component which will be used as a reference when relatively placing the popup.
     * @param corner the corner of the anchor component relative to which the popup will be placed.
     * Valid values are:
     * <ul>
     * <li>{@link #BOTTOM_LEFT}</li>
     * <li>{@link #BOTTOM_RIGHT}</li>
     * <li>{@link #TOP_LEFT}</li>
     * <li>{@link #TOP_RIGHT}</li>
     * </ul>
     * @param offsetX horizontal offset.
     * @param offsetY vertical offset.
     */
    public XPopup(SComponent contents, SComponent anchor, String corner, int offsetX, int offsetY) {
        SBorderLayout layout = new SBorderLayout();
        setLayout(layout);
        add(contents, SBorderLayout.CENTER);
        if (contents == null) {
            throw new IllegalArgumentException("Contents must be non-null.");
        }
        this.anchor = anchor;
        this.contents = contents;
        this.corner = corner;
        setX(offsetX);
        setY(offsetY);
        setVisible(false);
    }

    public String getCorner() {
        return corner;
    }

    public boolean isAnchored() {
        return anchor != null;
    }

    public SComponent getAnchor() {
        return anchor;
    }

    public SComponent getContents() {
        return contents;
    }

    @Override
    public void setPreferredSize(SDimension preferredSize) {
        super.setPreferredSize(preferredSize);
        if (contents != null) {
            contents.setPreferredSize(preferredSize);
        }
    }

}
