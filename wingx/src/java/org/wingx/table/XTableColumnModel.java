package org.wingx.table;

import org.wings.table.STableColumnModel;
import org.wings.table.STableColumn;

public interface XTableColumnModel extends STableColumnModel
{
    XTableColumn getColumn(int columnIndex);
    XTableColumn getColumn(String identifier);
}
