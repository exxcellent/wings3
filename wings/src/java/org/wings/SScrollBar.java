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
package org.wings;

import org.wings.plaf.ScrollBarCG;
import org.wings.plaf.Update;

/**
 * Represents a scroll bar as used in a {@link SScrollPane}.
 * In contrast to {@link SPageScroller} this class is a graphical scroller component.
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 * @see SScrollPane
 */
public class SScrollBar extends SAbstractAdjustable {
    boolean marginVisible;
    boolean stepVisible;
    boolean blockVisible;

    /**
     * Creates a scrollbar with the specified orientation, value, extent, mimimum, and maximum.
     * The "extent" is the size of the viewable area. It is also known as the "visible amount".
     * <p/>
     * Note: Use <code>setBlockIncrement</code> to set the block
     * increment to a size slightly smaller than the view's extent.
     * That way, when the user jumps the knob to an adjacent position,
     * one or two lines of the original contents remain in view.
     *
     * @throws IllegalArgumentException if orientation is not one of VERTICAL, HORIZONTAL
     * @see #setOrientation
     * @see #setValue
     * @see #setVisibleAmount
     * @see #setMinimum
     * @see #setMaximum
     */
    public SScrollBar(int orientation, int value, int extent, int min, int max) {
        super(value, extent, min, max);
        setOrientation(orientation);
    }

    /**
     * Creates a scrollbar with the specified orientation
     * and the following initial values:
     * <pre>
     * minimum = 0
     * maximum = 100
     * value = 0
     * extent = 10
     * </pre>
     */
    public SScrollBar(int orientation) {
        this(orientation, 0, 10, 0, 100);
    }

    /**
     * Creates a vertical scrollbar with the following initial values:
     * <pre>
     * minimum = 0
     * maximum = 100
     * value = 0
     * extent = 10
     * </pre>
     */
    public SScrollBar() {
        this(SConstants.VERTICAL);
    }

    protected void adjust() {
        ScrollBarCG cg = (ScrollBarCG)getCG();
        if (cg != null) {
            Update update = cg.getAdjustmentUpdate(this, getValue(), getExtent(), getMaximum() - getMinimum());
            if (update != null)
                update(update);
        }
    }

    public boolean isMarginVisible() {
        return marginVisible;
    }

    public void setMarginVisible(boolean marginVisible) {
        boolean oldVal = this.marginVisible;
        this.marginVisible = marginVisible;
        propertyChangeSupport.firePropertyChange("marginVisible", oldVal, this.marginVisible);
    }

    public boolean isStepVisible() {
        return stepVisible;
    }

    public void setStepVisible(boolean stepVisible) {
        boolean oldVal = this.stepVisible;
        this.stepVisible = stepVisible;
        propertyChangeSupport.firePropertyChange("stepVisible", oldVal, this.stepVisible);
    }

    public boolean isBlockVisible() {
        return blockVisible;
    }

    public void setBlockVisible(boolean blockVisible) {
        boolean oldVal = this.blockVisible;
        this.blockVisible = blockVisible;
        propertyChangeSupport.firePropertyChange("blockVisible", oldVal, this.blockVisible);
    }

    public void setCG(ScrollBarCG cg) {
        super.setCG(cg);
    }
}
