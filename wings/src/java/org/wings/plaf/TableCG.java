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

import java.awt.Rectangle;

import org.wings.STable;


public interface TableCG extends ComponentCG {
    
    Update getTableCellUpdate(STable table, int row, int col);
    Update getTableScrollUpdate(STable table, Rectangle newViewport, Rectangle oldViewport);

}