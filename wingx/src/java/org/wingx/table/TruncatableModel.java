package org.wingx.table;

import javax.swing.table.TableModel;

public interface TruncatableModel
    extends TableModel
{
    boolean isTruncated();
}