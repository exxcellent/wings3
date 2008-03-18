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
package org.wings.plaf;

import org.wings.SContainer;
import org.wings.SRootContainer;
import org.wings.SWindow;

/**
 * <code>RootContainerCG</code>.
 * <p/>
 * User: raedler
 * Date: Oct 4, 2007
 * Time: 5:28:10 PM
 *
 * @author raedler
 * @version $Id
 */
public interface RootContainerCG extends ContainerCG {
    public Update getAddWindowUpdate(SContainer container, SWindow window);
    public Update getRemoveWindowUpdate(SContainer container, SWindow window);
}
