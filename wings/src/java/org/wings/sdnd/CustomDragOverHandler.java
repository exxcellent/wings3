package org.wings.sdnd;

import org.wings.SComponent;
import org.wings.event.SMouseEvent;

import java.util.Map;

/**
 * Interface that provides customizable methods that'd get called in case of a dragover (enter and leave)
 */
public interface CustomDragOverHandler {
    /**
     * Method that gets called in case of the mouse cursor entering a droptarget - returns false if the DragAndDropManager
     * shall set the mouse-icons for this transfer, true if the cursor is set by this method
     * @param source
     * @param target
     * @param action
     * @param event
     * @return
     */
    public abstract boolean dragOverEnter(SComponent source, SComponent target, int action, SMouseEvent event);
    /**
     * Method that gets called in case of the mouse cursor leaving droptarget - returns false if the DragAndDropManager
     * shall set the mouse-icons for this transfer, true if the cursor is set by this method
     * @param source
     * @param target
     * @param action
     * @param event
     * @return
     */
    public abstract boolean dragOverLeave(SComponent source, SComponent target, int action, SMouseEvent event);
}
