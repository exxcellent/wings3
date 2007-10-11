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

import org.wings.SWindow;

/**
 * <code>WindowCG</code>.
 * <p/>
 * User: raedler
 * Date: Oct 5, 2007
 * Time: 1:29:48 AM
 *
 * @author raedler
 * @version $Id
 */
public interface WindowCG extends ComponentCG {
    public Update getWindowAddedUpdate(SWindow window);
    public Update getWindowClosedUpdate(SWindow window);
}
