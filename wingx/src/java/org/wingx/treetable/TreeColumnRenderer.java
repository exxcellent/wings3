package org.wingx.treetable;

import org.wings.*;
import org.wings.table.STableCellRenderer;

import org.wingx.XTreeTable;

public class TreeColumnRenderer
    extends SPanel
    implements STableCellRenderer
{
    public static final SIcon ARROW_DOWN = new SResourceIcon("org/wings/icons/ArrowDown.gif");
    public static final SIcon ARROW_RIGHT = new SResourceIcon("org/wings/icons/ArrowRight.gif");

    protected final SLabel indention = new SLabel();
    protected final SLabel label = new SLabel();

    public TreeColumnRenderer() {
        super(new SBorderLayout());
        setHorizontalAlignment(SConstants.LEFT_ALIGN);
        add(indention, SBorderLayout.WEST);
        add(label, SBorderLayout.CENTER);
        label.setHorizontalAlignment(SConstants.LEFT_ALIGN);
        label.setWordWrap(true);
        label.setIconTextGap(7);
    }

    public SComponent getTableCellRendererComponent(STable table, Object value, boolean selected, final int row, final int col) {
        if (value instanceof SComponent)
            return (SComponent) value;

        XTreeTable treeTable = (XTreeTable) table;
        XTreeTableModel tableModel = treeTable.getTreeTableModel();

        if (col == 0 && row >= 0 && row < tableModel.getNodeModel().size()) {
            XTreeTableNode node = (XTreeTableNode) tableModel.getNodeModel().get(row);

            indention.setText(indention(node.getDepth()));
            label.setIcon((!node.getAllowsChildren()) ? null : ((node.isExpanded()) ? ARROW_DOWN : ARROW_RIGHT));
            label.setText(value.toString());
            return this;
        }

        label.setText( value != null ? value.toString() : null);
        return label;
    }

    private String indention(int depth) {
        StringBuilder indent = new StringBuilder("<html>");
        for (int i = 1; i < depth; i++) {
            indent.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        }
        return indent.toString();
    }

    protected String nonBreakingSpaces(String value) {
        return "<html>" + value.replaceAll(" ", "&nbsp;");
    }
}
