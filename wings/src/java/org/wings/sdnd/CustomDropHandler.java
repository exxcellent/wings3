package org.wings.sdnd;

import org.wings.SComponent;
import org.wings.event.SMouseEvent;

/**
 * Interface that provides the possibility to implemenet a customized drop-handler
 */
public interface CustomDropHandler {
    public abstract boolean drop(SComponent source, SComponent target, int action, SMouseEvent event);
}
