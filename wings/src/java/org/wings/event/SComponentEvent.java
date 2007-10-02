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
package org.wings.event;

import org.wings.SComponent;
import org.wings.SDimension;

/**
 * A low-level event which indicates that a component moved, changed
 * size, or changed visibility (also, the root class for the other
 * component-level events).
 * <p/>
 * Component events are provided for notification purposes ONLY;
 * WingS will automatically handle component moves and resizes
 * internally so that GUI layout works properly regardless of
 * whether a program is receiving these events or not.
 * <p/>
 * In addition to serving as the base class for other component-related
 * events (InputEvent, FocusEvent, WindowEvent, ContainerEvent),
 * this class defines the events that indicate changes in
 * a component's size, position, or visibility.
 * <p/>
 * This low-level event is generated by a component object (such as a
 * SList) when the component is moved, resized, rendered invisible, or made
 * visible again. The event is passed to every ComponentListener or
 * ComponentAdapter object which registered to receive such
 * events using the component's addComponentListener method.
 * (ComponentAdapter objects implement the
 * ComponentListener interface.) Each such listener object
 * gets this ComponentEvent when the event occurs.
 *
 * @author <a href="mailto:andre@lison.de">Andre Lison</a>
 * @see org.wings.event.SComponentAdapter
 * @see org.wings.event.SComponentListener
 */
public class SComponentEvent
        extends java.awt.AWTEvent {
    /**
     * The first number in the range of ids used for component events.
     */
    public static final int COMPONENT_FIRST = 10000;

    /**
     * This event indicates that the component was rendered invisible.
     */
    public static final int COMPONENT_HIDDEN = COMPONENT_FIRST;

    /**
     * This event indicates that the component's position changed.
     */
    public static final int COMPONENT_MOVED = COMPONENT_FIRST + 1;

    /**
     * This event indicates that the component's size changed.
     */
    public static final int COMPONENT_RESIZED = COMPONENT_FIRST + 2;

    /**
     * This event indicates that the component was made visible.
     */
    public static final int COMPONENT_SHOWN = COMPONENT_FIRST + 3;

    /**
     * The last number in the range of ids used for component events.
     */
    public static final int COMPONENT_LAST = COMPONENT_SHOWN; // take last.

    /**
     * Constructs a ComponentEvent object.
     *
     * @param aSource the Component object that originated the event
     * @param anId    an integer indicating the type of event
     */
    public SComponentEvent(SComponent aSource, int anId) {
        super(aSource, anId);
    }

    /**
     * Returns the originator of the event.
     *
     * @return the Component object that originated the event
     */
    public SComponent getComponent() {
        return (SComponent) source;
    }

    /**
     * Returns a string representing the state of this event. This
     * method is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may
     * not be <tt>null</tt>.
     */
    public String paramString() {
        if (source == null)
            return "no source";

        String typeStr;
        SDimension d = ((SComponent) source).getPreferredSize();

        switch (id) {
            case COMPONENT_SHOWN:
                typeStr = "COMPONENT_SHOWN";
                break;
            case COMPONENT_HIDDEN:
                typeStr = "COMPONENT_HIDDEN";
                break;
            case COMPONENT_MOVED:
                typeStr = "COMPONENT_MOVED (" + d.getWidthInt() + "x" + d.getHeightInt()+ ")";
                break;
            case COMPONENT_RESIZED:
                typeStr = "COMPONENT_RESIZED (" + d.getWidthInt() + "x" + d.getHeightInt() + ")";
                break;
            default:
                typeStr = "unknown type";
        }
        return typeStr;
    }

    public String toString() {
        return "ComponentEvent[source=" + source + "; " + paramString() + "]";
    }
}
