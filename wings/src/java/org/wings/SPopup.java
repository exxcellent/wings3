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
package org.wings;

import org.wings.event.SComponentAdapter;
import org.wings.event.SComponentEvent;

/**
 * Popups are used to display a <code>SComponent</code> to the user, typically
 * on top of all the other <code>SComponent</code>s in a particular containment
 * hierarchy.
 * <p>
 * The general contract is that if you need to change the size of the
 * <code>SComponent</code>, or location of the <code>SPopup</code>, you should
 * obtain a new <code>Popup</code>.
 * <p>
 *
 * @author Leon Chiver
 */
public class SPopup extends SWindow {

    public static final String TOP_RIGHT = "tr";

    public static final String TOP_LEFT = "tl";

    public static final String BOTTOM_RIGHT = "br";

    public static final String BOTTOM_LEFT = "bl";

    private SComponent anchor;

    private String corner;

    private SComponent focusedComponent;

    private final SComponentAdapter componentListener;

    /**
     * Creates a <code>SPopup</code> which will pe absolutely placed at the specified
     * coordinates.
     * @param x initial x screen coordinate
     * @param y initial y screen coordinate
     * @exception IllegalArgumentException if contents is null
     */
    public SPopup(int x, int y) {
        this(null, null, x, y);
    }

    /**
     * Creates a <code>SPopup</code> which will be placed relative to the given anchor component's corner.
     * @param anchor the anchor component which will be used as a reference when relatively placing the popup.
     * When the anchor is hidden or removed from the component hierarchy, the popup will also be removed.
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
    public SPopup(SComponent anchor, String corner, int offsetX, int offsetY) {
        this.corner = corner;
        this.componentListener = new SComponentAdapter() {
            @Override
            public void componentHidden(SComponentEvent e) {
                setVisible(false);
            }
        };

        setAnchor(anchor);
        setX(offsetX);
        setY(offsetY);
        super.setVisible(false);
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

    public void setAnchor(SComponent anchor) {
        if (this.anchor != null)
            this.anchor.removeComponentListener(componentListener);
        this.anchor = anchor;
        if (this.anchor != null)
            this.anchor.addComponentListener(componentListener);

        super.setOwner(anchor);
    }

    @Override
    public void setLayout(SLayoutManager l) {
        // XXX - this is a workaround for a side effect which occurs
        // when setting the layout manager: the component's preferred size will
        // be changed. But that's not what we want here
        SDimension preferredSize = getPreferredSize();
        super.setLayout(l);
        if (preferredSize != null) {
            setPreferredSize(preferredSize);
        }
    }

    @Override
    public void requestFocus() {
        if (focusedComponent != null) {
            focusedComponent.requestFocus();
        } else {
            super.requestFocus();
        }
    }

    /**
     * Specifies which component should get the focus after the popup has been shown.
     * @param component the component which should get the focus after the popup has been shown.
     */
    public void setFocusedComponent(SComponent component) {
        this.focusedComponent = component;
    }

    /**
     * Returns the component which should get the focus after the popup has been shown.
     * @return the component which should get the focus after the popup has been shown.
     */
    public SComponent getFocusedComponent() {
        return focusedComponent;
    }
}
