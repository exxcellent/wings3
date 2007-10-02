package org.wingx.table;

import org.wings.table.STableColumn;
import org.wings.table.SDefaultTableColumnModel;

import java.util.*;

public class XDefaultTableColumnModel
    extends SDefaultTableColumnModel
    implements XTableColumnModel
{
    private Map columnByName = new HashMap();

    public XTableColumn getColumn(int columnIndex) {
        return (XTableColumn)super.getColumn(columnIndex);
    }

    public XTableColumn getColumn(String identifier) {
        XTableColumn column = (XTableColumn)columnByName.get(identifier);
        if (column == null) {
            for (Iterator iterator = getColumns().iterator(); iterator.hasNext();) {
                XTableColumn tableColumn = (XTableColumn)iterator.next();
                if (identifier.equals(tableColumn.getIdentifier())) {
                    columnByName.put(identifier, tableColumn);
                    column = tableColumn;
                    break;
                }
            }
        }
        return column;
    }
}
