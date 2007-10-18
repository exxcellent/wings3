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

import java.awt.Color;
import javax.swing.event.ChangeEvent;
import org.wings.LowLevelEventListener;
import org.wings.SComponent;
import org.wings.ReloadManager;
import org.wings.SForm;
import org.wings.event.SAjaxChangeListener;
import org.wings.session.Session;
import org.wings.session.SessionManager;
import org.wingx.plaf.css.ColorPickerCG;

/**
 * A component to choose a color. Adopted and enhanced from the YUI library examples
 * (see http://developer.yahoo.com/yui/slider/).
 */
public class XColorPicker extends SComponent implements LowLevelEventListener, XColorPickerInterface {

    /**
     * red RGB part
     */
    private int red   = 255;

    /**
     * green RGB part
     */
    private int green = 255;

    /**
     * blue RGB part
     */
    private int blue  = 255;

    /**
     * @see #setImmediateUpdate(boolean b)
     */
    private boolean immediateUpdate = true;

    /**
     * Ajax request timeout in ms
     */
    private int timeout = 5000;

    private ReloadManager reloadManager = SessionManager.getSession().getReloadManager();

    /**
     * Creates a color picker with the default color (white) and immediate
     * updates.
     * @see #setImmediateUpdate(boolean b)
     */
    public XColorPicker() {
        ((ColorPickerCG)getCG()).prepareIds(this);
    }

    /**
     * Creates a color picker with the given color set as default and
     * immediate updates.
     * @param red
     * @param green
     * @param blue
     * @see #setImmediateUpdate(boolean b)
     */
    public XColorPicker(int red, int green, int blue) {
        this();
        this.red   = red;
        this.green = green;
        this.blue  = blue;
    }

    /**
     * Creates a color picker with the given color and immediate update
     * option set.
     * @param red
     * @param green
     * @param blue
     * @param immediateUpdate see setImmediateUpdate(boolean b)
     */
    public XColorPicker(int red, int green, int blue, boolean immediateUpdate) {
        this(red, green, blue);
        this.immediateUpdate = immediateUpdate;
    }

    /**
     * @return color selected by user
     */
    public Color getSelectedColor() {
        return new Color(red, green, blue);
    }
    
    public boolean setSelectedColor(int red, int green, int blue) {
        Color oldColor = this.getSelectedColor();

        this.red   = red;
        this.green = green;
        this.blue  = blue;

        Color newColor = this.getSelectedColor();

        if (newColor != oldColor) {
            fireStateChanged();
            if (this.immediateUpdate &&
                    reloadManager.getDirtyFrames().contains(this.getParentFrame())) return true;
        }

        return false;
    }

    /**
     * If true (which is the default) all dirty components will be requested to
     * refresh after a change of this component.
     * @see #setSelectedColor(int red, int green, int blue)
     */
    public void setImmediateUpdate(boolean b) {
        this.immediateUpdate = b;
    }

    /**
     * @see #setImmediateUpdate(boolean b)
     */
    public boolean isImmediateUpdate() {
        return this.immediateUpdate;
    }

    /**
     * Adds the specified <code>SAjaxChangeListener</code> to the color picker.
     * @param l the <code>SAjaxChangeListener</code> to add
     */
    public void addAjaxChangeListener(SAjaxChangeListener l) {
        addEventListener(SAjaxChangeListener.class, l);
    }

    /**
     * Removes a <code>SAjaxChangeListener</code> from the color picker.
     *
     * @param l the <code>SAjaxChangeListener</code> to remove
     */
    public void removeAjaxChangeListener(SAjaxChangeListener l) {
        removeEventListener(SAjaxChangeListener.class, l);
    }

    /**
     * Returns an array of all the <code>SAjaxChangeListener</code>s added
     * to this color picker with <code>addAjaxChangeListener</code>.
     *
     * @return all of the <code>SAjaxChangeListener</code>s added or an empty
     *         array if no listeners have been added
     */
    public SAjaxChangeListener[] getAjaxChangeListeners() {
        return (SAjaxChangeListener[]) getListeners(SAjaxChangeListener.class);
    }

    public boolean hasAjaxChangeListener() {
        return (getListenerCount(SAjaxChangeListener.class) > 0) ? true : false;
    }

    /**
     * Notifies all listeners that have registered interest in
     * <code>ChangeEvent</code>s.
     */
    protected void fireStateChanged() {
        SAjaxChangeListener[] listeners = (SAjaxChangeListener[]) getListeners(SAjaxChangeListener.class);
        for (int i = 0; i < listeners.length; i++) {
            SAjaxChangeListener listener = listeners[i];
            listener.stateChanged(new ChangeEvent(this));
        }
    }

    public void fireFinalEvents() {
        fireStateChanged();
    }

    public void fireIntermediateEvents() {
        // nothing to do
    }

    public boolean isEpochCheckEnabled() {
        return true;
    }

    public void processLowLevelEvent(String action, String[] values) {
        processKeyEvents(values);
        if (action.endsWith("_keystroke"))
            return;

        Color oldColor = this.getSelectedColor();

        String rAction = (String)this.getClientProperty("pickerRvalId");
        String gAction = (String)this.getClientProperty("pickerGvalId");
        String bAction = (String)this.getClientProperty("pickerBvalId");

        int newRed   = this.red;
        int newGreen = this.green;
        int newBlue  = this.blue;

        if (action.compareTo(rAction) == 0) {
            newRed = Integer.parseInt(values[0]);
        } else if (action.compareTo(gAction) == 0) {
            newGreen = Integer.parseInt(values[0]);
        } else if (action.compareTo(bAction) == 0) {
            newBlue = Integer.parseInt(values[0]);
        }

        Color newColor = new Color(newRed, newGreen, newBlue);

        if (oldColor != newColor) {
            this.red = newRed;
            this.green = newGreen;
            this.blue = newBlue;
            SForm.addArmedComponent(this);
        }

    }

    /**
     * Set Ajax request timeout.
     * @param timeout in ms
     */
    public void setTimeout(int timeout) {
        int oldTimeout = this.timeout;
        this.timeout = timeout;
        reloadIfChange(oldTimeout, timeout);
    }

    /**
     * @see org.wingx.XSuggest#setTimeout(int timeout)
     */
    public int getTimeout() {
        return timeout;
    }

}
