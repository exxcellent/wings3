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

package org.wings.script;

import org.wings.SComponent;
import org.wings.SPopup;
import org.wings.plaf.css.PopupCG;

/**
 * A JavaScript listener to handle SPopup.
 * @author Christian Schyma
 */
public class PopupListener extends JavaScriptDOMListener {
    
    public static final int SHOW = 0;
    public static final int HIDE = 1;
    
    /**
     * This one is needed because SPopup is not nested in the component 
     * hierarchy and would otherwise be collected by the GC.
     */
    private final SPopup popup;
    
    /**
     * @param event one of JavaScriptEvent (e.g. JavaScriptEvent.ON_CLICK) to act on
     * @param popup SPopup component
     * @param action PopupListener.SHOW | PopupListener.HIDE
     * @param component the component this listener is associated with
     */
    public PopupListener(String event, SPopup popup, int action, SComponent component) {        
        super(event, selectFunction(((PopupCG)popup.getCG()), action), component);
        this.popup = popup;        
    }

    private static String selectFunction(PopupCG popupCG, int action) {
        switch (action) {
            case SHOW:
                return popupCG.getJsShowFunction();
            case HIDE:
                return popupCG.getJsHideFunction();
            default:
                return popupCG.getJsHideFunction();
        }
    }
    
}
