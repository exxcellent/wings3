package org.wings.sdnd;

import org.wings.SComponent;
import org.wings.event.SMouseEvent;

import java.util.Map;

/**
 * Interface that provides the possiblity to implement handlers for when the mouse cursor stayed on a drop-element  
 */
public interface CustomDropStayHandler {
    /**
     * Method that is called when the mouse cursor stayed on a element for the time period defined in the configuration
     * @param source
     * @param target
     * @param action
     * @param event
     */
    public abstract void dropStay(SComponent source, SComponent target, int action, SMouseEvent event);

    /**
     * Returns a configuration Mapping, which must contain 'stayOnElementTimeout' and a Integer definining the timeout
     * Additionally it can contain 'stayOnElementTimeoutInterval', if the stayedOnElement Handler should be called in
     * intervals, while the mouse stays on the element         
     * @return
     */
    public abstract Map<String, Object> getDropStayConfiguration();
}
