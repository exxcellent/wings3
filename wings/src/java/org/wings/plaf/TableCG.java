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
import java.util.List;

import org.wings.STable;


public interface TableCG extends ComponentCG {

    Update getTableScrollUpdate(STable table, Rectangle newViewport, Rectangle oldViewport);

    Update getSelectionUpdate(STable table, List deselectedIndices, List selectedIndices);

    Update getEditCellUpdate(STable table, int row, int column);

    Update getRenderCellUpdate(STable table, int row, int column);

    //Update getDataUpdate(STable table);
}