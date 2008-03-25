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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.plaf.RootContainerCG;
import org.wings.style.Selector;

/**
 * A root container. The classes derived from this class ({@link SFrame} and {@link SInternalFrame}) render in the content pane of this
 * RootContainer.
 * <p/>
 * <p>The RootContainer has a stack of components. Ususally, the stack contains only <em>one</em> element, the content pane; this is the
 * bottom-most component. When dialogs are added to the RootContainer, then these dialogs are stacked on top of this content pane and only
 * <em>that</em> dialog is visible then. This emulates the behaviour of modal dialogs in a windowing system.
 *
 * @author Holger Engels
 * @author <a href="mailto:H.Zeller@acm.org">Henner Zeller</a>
 * @author <a href="mailto:Haaf@mercatis.de">Armin Haaf</a>
 */
public abstract class SRootContainer extends SContainer {
    private final static Log LOG = LogFactory.getLog(SRootContainer.class);

    /**
     * The container for the contentPane.
     */
    protected SContainer contentPane;

    /**
     * Contains all windows linked to this root container.
     */
    protected SContainer windowsPane;

    /**
     * default constructor initializes the stack layout system of this SRootContainer.
     */
    public SRootContainer() {
        super(new SRootLayout());
        initializeContentPane();
        initializeWindowsPane();
    }

    protected void initializeContentPane() {
        setContentPane(new SPanel(new SBorderLayout()));
    }

    protected void initializeWindowsPane() {
        SContainer windowsPane = new SContainer(new SFlowDownLayout());
        windowsPane.setStyle("SWindowsPane");
        setWindowsPane(windowsPane);
    }

    public void setCG(RootContainerCG cg) {
        super.setCG(cg);
    }

    /**
     * Push a new window on top of the stack.
     *
     * @param window the SDialog that is to be shown on top.
     */
    public void pushWindow(SWindow window) {
    	
    	getSession().getReloadManager().setSuppressMode(true);
        windowsPane.addComponent(window);
        getSession().getReloadManager().setSuppressMode(false);

        if (isUpdatePossible() && SRootContainer.class.isAssignableFrom(getClass())) {
            update(((RootContainerCG) getCG()).getAddWindowUpdate(windowsPane, window));
        }
        else {
        	windowsPane.reload();
        }
        
        LOG.debug("push window = " + window.getName());
    }

    /**
     * remove the dialog, that is on top of the stack.
     *
     * @return the dialog, that is popped from the stack.
     */
    public SWindow popWindow() {

        int count = windowsPane.getComponentCount();
        if (count < 1) {
            throw new IllegalStateException("there's no window left!");
        }

        SWindow window = (SWindow) windowsPane.getComponent(count - 1);
        removeWindow(window);

        LOG.debug("pop window = " + window.getName());
        return window;
    }

    public void removeWindow(SWindow window) {
    	
    	getSession().getReloadManager().setSuppressMode(true);
        windowsPane.remove(window);
        getSession().getReloadManager().setSuppressMode(false);

        if (isUpdatePossible() && SRootContainer.class.isAssignableFrom(getClass())) {
            update(((RootContainerCG) getCG()).getRemoveWindowUpdate(windowsPane, window));
        }
        else {
        	windowsPane.reload();
        }
    }

    /**
     * @return the number of dialogs that are on the stack currently.
     */
    public int getWindowCount() {
        return windowsPane.getComponentCount();
    }

    /**
     * returns the content pane of this RootContainer.
     */
    public SContainer getContentPane() {
        return contentPane;
    }

    /**
     * Returns the container that contains the windows linked to this root container.
     *
     * @return The container that contains the windows linked to this root container.
     */
    public SContainer getWindowsPane() {
        return windowsPane;
    }

    /**
     * Sets the container for the contentPane.
     *
     * @param container the container for the contentPane.
     */
    public void setContentPane(SContainer container) {
        SContainer oldVal = this.contentPane;
        if (this.contentPane != null) {
            super.remove(contentPane);
        }
        this.contentPane = container;
        super.addComponent(this.contentPane, null, 0);
        propertyChangeSupport.firePropertyChange("contentPane", oldVal, this.contentPane);
    }

    /**
     * Sets the container for the windowsPane. ! This is not like in swing. Each window object is bound to its frame or internal frame.
     *
     * @param windowsPane The container for the windowsPane.
     */
    protected void setWindowsPane(SContainer windowsPane) {
        SContainer oldVal = this.windowsPane;
        if (this.windowsPane != null) {
            super.remove(this.windowsPane);
        }
        this.windowsPane = windowsPane;
        super.addComponent(this.windowsPane, null, 1);

        propertyChangeSupport.firePropertyChange("windowsPane", oldVal, this.windowsPane);
    }

    /**
     * Adds the component to the content pane of the root container.
     */
    public SComponent addComponent(SComponent c, Object constraint, int index) {
        return contentPane.addComponent(c, constraint, index);
    }

    /**
     * Removes the component from the content pane.
     */
    public void remove(SComponent c) {
        contentPane.addComponent(c);
    }

    // allow frame.setBackground(Color.yellow);
    //    public void setAttribute(Selector selector, CSSProperty property, String propertyValue) {
    //        throw new IllegalArgumentException("use getContentPane().setAttribute()");
    //    }
}
