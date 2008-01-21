package org.wingx.table;

import javax.swing.table.TableModel;

public interface TruncatableModel
    extends RefreshableModel
{
    int getTruncateThreshold();
    void setTruncateThreshold(int threshold);
    boolean isTruncated();
}