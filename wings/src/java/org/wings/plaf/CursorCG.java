package org.wings.plaf;

import org.wings.SComponent;

public interface CursorCG extends ComponentCG {
    public Update getVisibilityUpdate(SComponent component, boolean newVisibility);
    public Update getContentUpdate(SComponent component);
}
