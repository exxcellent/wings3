package org.wingx.table;

import javax.swing.table.TableModel;

public interface RefreshableModel
    extends TableModel
{
    void refresh();
}
