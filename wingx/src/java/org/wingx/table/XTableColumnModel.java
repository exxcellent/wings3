package org.wingx.table;

import org.wings.table.STableColumnModel;

import java.util.List;

public interface XTableColumnModel extends STableColumnModel
{
    XTableColumn getColumn(int columnIndex);
    XTableColumn getColumn(String identifier);
    List<XTableColumn> getColumns();

    void setColumnHidden(String identifier, boolean hidden);
}
