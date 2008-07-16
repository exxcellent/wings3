/*
 * Copyright 2000,2008 wingS development team.
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

package org.wings.plaf;

import org.wings.sdnd.SDragAndDropManager;
import org.wings.SComponent;

/**
 * SDragAndDropManager CG Interface
 * @author Florian Roks
 */
public interface SDragAndDropManagerCG extends ComponentCG {
    /**
     * Gets a Update for a Registration (add/remove) of a dragsource or droptarget
     * @param component
     * @param event
     * @return
     */
    public Update getRegistrationUpdate(SComponent component, SDragAndDropManager.DragAndDropRegistrationEvent event);
}
