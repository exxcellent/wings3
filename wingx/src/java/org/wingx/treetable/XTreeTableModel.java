package org.wingx.treetable;

import java.util.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeModel;

import org.apache.commons.logging.*;
import org.wingx.tree.LazyNode;

/**
 * the table model for an OTableTree
 * 
 */
public class XTreeTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 4071940504892898468L;

    private final static Log logger = LogFactory.getLog(XTreeTableModel.class);

    private final TreeModel treeModel;

    private final List nodeModel = new ArrayList();

    /**
     * construct a table model
     */
    public XTreeTableModel(TreeModel treeModel) {
        super();
        this.treeModel = treeModel;
        expandNode(getRoot(), -1);
    }

    /**
     * @inheritDoc
     */
    public boolean isCellEditable(int row, int column) {
        return (column > 0);
    }

    /**
     * toggle the expanded state for the node at the given row
     */
    public void toggleExpanded(int row) {
        if (row < 0 || row >= getNodeModel().size()) {
            logger.warn("bad row: " + row + " size is " + getNodeModel().size());
            return;
        }
        XTreeTableNode node = (XTreeTableNode) getNodeModel().get(row);
        logger.info("toggle expanded row=" + row + " expanded=" + node.isExpanded() + " model-size=" + getNodeModel().size());
        if (!node.isExpanded()) {
            int insertPos = expandNode(node, row);
            fireTableRowsInserted(row + 1, row + insertPos);
        }
        else {
            int delcount = collapseNode(node, row);
            fireTableRowsDeleted(row + 1, row + delcount);
        }
    }

    /**
     * expand the given node.
     * if child nodes already have their expanded flag set to true,
     * then expand them as well
     * 
     * @return the insert position
     */
    private int expandNode(XTreeTableNode node, int row) {
        if (node instanceof LazyNode)
            ((LazyNode)node).initialize();

        int count = node.getChildCount();
        int insertPosition = row;
        for ( int i = 0 ; i < count ; i++ ) {
            XTreeTableNode child = (XTreeTableNode) node.getChildAt(i);
            insertPosition++;
            getNodeModel().add(insertPosition, child);
            if (child.isExpanded()) {
                insertPosition = expandNode(child, insertPosition);
            }
        }
        node.setExpanded(true);
        return insertPosition;
    }

    /**
     * collapse the given node,
     * remove all descendants which follow the node
     */
    private int collapseNode(XTreeTableNode node, int row) {
        int delcount = 0;
        while ( row + 1 < getNodeModel().size() ) {
            XTreeTableNode candidate = (XTreeTableNode) getNodeModel().get(row + 1);
            /*
             * remove the candidate if the depth is greater than the depth
             * of the parent to be collapsed
             */
            if (candidate.getDepth() > node.getDepth()) {
                getNodeModel().remove(row + 1);
                delcount++;
            }
            else {
                break;
            }
        }
        node.setExpanded(false);
        return delcount;
    }

    /**
     * @inheritDoc
     */
    public int getRowCount() {
        return (getNodeModel() != null) ? getNodeModel().size() : 0;
    }

    /**
     * @param row
     * @return the value of the first column
     */
    public String getFirstColumnValue(int row) {
        return getNodeModel().get(row).toString();
    }

    /**
     * @inheritDoc
     */
    public final Object getValueAt(int row, int column) {
        if (row >= 0 && row < getNodeModel().size()) {
            if (column == 0) {
                return getFirstColumnValue( row );
            }
            return ((XTreeTableNode)(getNodeModel().get(row))).getValueAt(column);
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    public final void setValueAt(Object aValue, int row, int column) {
        if (row >= 0 && row < getNodeModel().size()) {
                ((XTreeTableNode)getNodeModel().get(row)).setValueAt(aValue, column);
        }
        fireTableCellUpdated(row, column);
    }

    /**
     * @return Returns the treeModel.
     */
    public TreeModel getTreeModel() {
        return this.treeModel;
    }

    /**
     * @return Returns the parentNode.
     */
    public XTreeTableNode getRoot() {
        return (XTreeTableNode) getTreeModel().getRoot();
    }

    /**
     * @return Returns the nodeModel.
     */
    public List getNodeModel() {
        return this.nodeModel;
    }
}
