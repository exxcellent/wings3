package org.wings.sdnd;

import org.wings.event.SMouseEvent;
import org.wings.SComponent;

/**
 * Interface that provides the possibility to implement a customized dragstart handler
 */
public interface CustomDragHandler {
    /**
     * If this interface is implemented, this method is called upon a dragstart - if it returns true, nothing will be
     * done by the SDragAndDropManager, you'll have to call exportAsDrag, set icons etc. yourself - if it returns false
     * exportAsDrag and setting the icons will be done by the SDragAndDropManager 
     * @param source
     * @param target
     * @param action
     * @param event
     * @return
     */
    public abstract boolean dragStart(SComponent source, SComponent target, int action, SMouseEvent event);
}
