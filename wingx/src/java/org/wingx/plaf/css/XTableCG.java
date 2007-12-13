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
package org.wingx.plaf.css;

import org.wings.plaf.css.*;
import org.wings.plaf.CGManager;
import org.wings.plaf.Update;
import org.wings.*;
import org.wings.session.SessionManager;
import org.wings.util.SStringBuilder;
import org.wings.io.Device;
import org.wings.io.StringBuilderDevice;
import org.wings.table.*;
import org.wingx.XTable;
import org.wingx.table.*;

import java.io.IOException;
import java.awt.*;

import javax.swing.table.TableModel;

public class XTableCG
    extends AbstractComponentCG
    implements org.wings.plaf.TableCG
{
    private static final long serialVersionUID = 1L;
    protected String fixedTableBorderWidth;
    protected SIcon editIcon;
    protected String selectionColumnWidth = "22";
    protected final SLabel resetLabel = new SLabel(XTable.ICON_RESET);
    protected final SLabel refreshLabel = new SLabel(XTable.ICON_REFRESH);
    int horizontalOversize = 22;

    /**
     * Initialize properties from config
     */
    public XTableCG() {
        final CGManager manager = SessionManager.getSession().getCGManager();
        setFixedTableBorderWidth((String)manager.getObject("TableCG.fixedTableBorderWidth", String.class));
        setEditIcon(manager.getIcon("TableCG.editIcon"));
        selectionColumnWidth = (String)manager.getObject("TableCG.selectionColumnWidth", String.class);
    }

    public int getHorizontalOversize() {
        return horizontalOversize;
    }

    public void setHorizontalOversize(int horizontalOversize) {
        this.horizontalOversize = horizontalOversize;
    }

    /**
     * Tweak property. Declares a deprecated BORDER=xxx attribute on the HTML TABLE element.
     */
    public String getFixedTableBorderWidth() {
        return fixedTableBorderWidth;
    }

    /**
     * Tweak property. Declares a deprecated BORDER=xxx attribute on the HTML TABLE element.
     */
    public void setFixedTableBorderWidth(String fixedTableBorderWidth) {
        this.fixedTableBorderWidth = fixedTableBorderWidth;
    }

    /**
     * Sets the icon used to indicated an editable cell (if content is not direct clickable).
     */
    public void setEditIcon(SIcon editIcon) {
        this.editIcon = editIcon;
    }

    /**
     * @return Returns the icon used to indicated an editable cell (if content is not direct clickable).
     */
    public SIcon getEditIcon() {
        return editIcon;
    }

    /**
     * @return The width of the (optional) row selection column in px
     */
    public String getSelectionColumnWidth() {
        return selectionColumnWidth;
    }

    /**
     * The width of the (optional) row selection column in px
     *
     * @param selectionColumnWidth The width of the (optional) row selection column with unit
     */
    public void setSelectionColumnWidth(String selectionColumnWidth) {
        this.selectionColumnWidth = selectionColumnWidth;
    }

    public void installCG(final SComponent comp) {
        super.installCG(comp);

        final STable table = (STable)comp;
        final CGManager manager = table.getSession().getCGManager();
        Object value;

        value = manager.getObject("STable.defaultRenderer", STableCellRenderer.class);
        if (value != null) {
            table.setDefaultRenderer((STableCellRenderer)value);
            if (value instanceof SDefaultTableCellRenderer) {
                SDefaultTableCellRenderer cellRenderer = (SDefaultTableCellRenderer)value;
                cellRenderer.setEditIcon(editIcon);
            }
        }

        value = manager.getObject("XTable.headerRenderer", STableCellRenderer.class);
        if (value != null)
            table.setHeaderRenderer((STableCellRenderer)value);
        else
            table.setHeaderRenderer(new XTable.HeaderRenderer());

        value = manager.getObject("STable.rowSelectionRenderer", org.wings.table.STableCellRenderer.class);
        if (value != null) {
            if (value instanceof SDefaultTableRowSelectionRenderer) {
                SDefaultTableRowSelectionRenderer rowSelectionRenderer = (SDefaultTableRowSelectionRenderer)value;
                rowSelectionRenderer.setUseIcons(true);
            }
            table.setRowSelectionRenderer((org.wings.table.STableCellRenderer)value);
        }
    }

    public void uninstallCG(SComponent component) {
        super.uninstallCG(component);
        final STable table = (STable)component;
        table.setHeaderRenderer(null);
        table.setDefaultRenderer(null);
        table.setRowSelectionRenderer(null);
    }

    /**
     * write a specific cell to the device
     */
    protected void renderCellContent(final Device device, final STable table, final SCellRendererPane rendererPane,
                                     final int row, final int col)
        throws IOException
    {
        final boolean isEditingCell = table.isEditing() && row == table.getEditingRow() && col == table.getEditingColumn();
        final boolean editableCell = table.isCellEditable(row, col);
        final boolean selectableCell = table.getSelectionMode() != SListSelectionModel.NO_SELECTION && !table.isEditable();

        final SComponent component;
        if (isEditingCell) {
            component = table.getEditorComponent();
        }
        else {
            component = table.prepareRenderer(table.getCellRenderer(row, col), row, col);
        }

        final boolean isClickable = component instanceof SClickable;
        final boolean isEditableCellRenderer = component instanceof EditableTableCellRenderer;

        device.print("<td col=\"");
        device.print(col);
        device.print("\"");

        if (component == null) {
            device.print("></td>");
            return;
        }
        Utils.printTableCellAlignment(device, component, SConstants.LEFT, SConstants.TOP);
        Utils.optAttribute(device, "oversize", horizontalOversize);

        String parameter = null;
        if (table.isEditable() && editableCell)
            parameter = table.getEditParameter(row, col);
        else if (selectableCell)
            parameter = table.getToggleSelectionParameter(row, col);

        if (parameter != null && (selectableCell || editableCell) && !isClickable) {
            printClickability(device, table, parameter, table.getShowAsFormComponent());
            device.print(isEditingCell || isEditableCellRenderer ? " editing=\"true\"" : " editing=\"false\"");
            device.print(isEditingCell || isEditableCellRenderer     ? " class=\"cell\"" : " class=\"cell clickable\"");
        }
        else
            device.print(" class=\"cell\"");
        device.print(">");

        rendererPane.writeComponent(device, component, table);

        device.print("</td>");
        Utils.printNewline(device, component);
    }

    protected void writeHeaderCell(final Device device, final XTable table,
                                   final SCellRendererPane rendererPane,
                                   final int col)
        throws IOException
    {
        final SComponent comp = table.prepareHeaderRenderer(table.getHeaderRenderer(col), col);

        device.print("<th col=\"");
        device.print(col);
        device.print("\"");

        Utils.printTableCellAlignment(device, comp, SConstants.CENTER, SConstants.CENTER);

        String parameter = table.getToggleSortParameter(col);

        if (table.getModel() instanceof SortableTableModel) {
            STableColumn column = table.getColumnModel().getColumn(col);
            if (!(column instanceof XTableColumn) || ((XTableColumn)column).isSortable()) {
                Utils.printClickability(device, table, parameter, true, table.getShowAsFormComponent());
                device.print(" class=\"clickable head\"");
            }
            else
                device.print(" class=\"head\"");
        }
        else
            device.print(" class=\"head\"");

        device.print(">");

        rendererPane.writeComponent(device, comp, table);

        device.print("</th>");
        Utils.printNewline(device, comp);
    }

    protected void writeFilterCell(Device device, XTable table,
                                   SCellRendererPane rendererPane,
                                   int c)
        throws IOException
    {
        STableColumn column = table.getColumnModel().getColumn(c);

        if (column instanceof XTableColumn) {
            XTableColumn xTableColumn = (XTableColumn)column;
            if (!xTableColumn.isFilterable()) {
                device.print("<th class=\"filter\"></th>");
                return;
            }
        }

        EditableTableCellRenderer editableTableCellRenderer = table.getFilterRenderer(c);
        if (editableTableCellRenderer == null) {
            device.print("<th class=\"filter\"></th>");
            return;
        }

        SComponent comp = table.prepareFilterRenderer(editableTableCellRenderer, c);

        device.print("<th valign=\"middle\" class=\"filter\" col=\"");
        device.print(c);
        device.print("\"");

        //Utils.printTableCellAlignment(device, comp, SConstants.LEFT, SConstants.CENTER);
        device.print(" align=\"left\">");

        rendererPane.writeComponent(device, comp, table);
        device.print("</th>");
        Utils.printNewline(device, comp);
    }

    public final void writeInternal(final Device device, final SComponent _c) throws IOException {
        final XTable table = (XTable)_c;
        
        TableModel model = table.getModel();
        boolean empty = model.getRowCount() == 0;
        boolean filtered = isModelFiltered(model);

        device.print("<table");
        if (empty) table.addStyle("nodata");
        Utils.writeAllAttributes(device, table);
        if (empty) table.removeStyle("nodata");
        writeTableAttributes(device, table);
        device.print("><thead>");
        Utils.printNewline(device, table);

        Rectangle currentViewport = table.getViewportSize();
        Rectangle maximalViewport = table.getScrollableViewportSize();
        int startX = 0;
        int endX = table.getVisibleColumnCount();
        int startY = 0;
        int endY = table.getRowCount();
        int emptyIndex = maximalViewport != null ? maximalViewport.height : endY;

        if (currentViewport != null) {
            startX = currentViewport.x;
            endX = startX + currentViewport.width;
            startY = currentViewport.y;
            endY = startY + currentViewport.height;
        }

        writeColumnWidths(device, table, startX, endX);
        writeHeader(device, table, startX, endX);
        
        if (filtered)
            writeFilter(device, table, startX, endX);

        device.print("</thead>");
        Utils.printNewline(device, table);
        device.print("<tbody>");

        if (empty) {
            writeNoData(device, table, startX, endX, endY, filtered);
        } else
            writeBody(device, table, startX, endX, startY, endY, emptyIndex);

        writeFooter(device, table, startX, endX);

        device.print("</tbody></table>");
    }

    private boolean isModelFiltered(TableModel model) {
        return model instanceof FilterableTableModel;
    }

    private void writeTableAttributes(Device device, XTable table) throws IOException {
        final SDimension intercellPadding = table.getIntercellPadding();
        final SDimension intercellSpacing = table.getIntercellSpacing();
        Utils.writeEvents(device, table, null);

        // TODO: border="" should be obsolete
        // TODO: cellspacing and cellpadding may be in conflict with border-collapse
        /* Tweaking: CG configured to have a fixed border="xy" width */
        Utils.optAttribute(device, "border", fixedTableBorderWidth);
        Utils.optAttribute(device, "cellspacing", ((intercellSpacing != null) ? "" + intercellSpacing.getWidthInt() : null));
        Utils.optAttribute(device, "cellpadding", ((intercellPadding != null) ? "" + intercellPadding.getHeightInt() : null));
    }

    private void writeColumnWidths(Device device, XTable table, int startX, int endX) throws IOException {
        STableColumnModel columnModel = table.getColumnModel();
        if (columnModel != null && atLeastOneColumnWidthIsNotNull(columnModel)) {
            device.print("<colgroup>");
            if (isSelectionColumnVisible(table))
                writeCol(device, selectionColumnWidth);

            for (int i = startX; i < endX; ++i) {
                STableColumn column = columnModel.getColumn(i);
                if (!column.isHidden())
                    writeCol(device, column.getWidth());
                else
                    ++endX;
            }
            device.print("</colgroup>");
            Utils.printNewline(device, table);
        }
    }

    private void writeHeader(Device device, XTable table, int startX, int endX) throws IOException {
        if (!table.isHeaderVisible())
            return;

        final SCellRendererPane rendererPane = table.getCellRendererPane();
        STableColumnModel columnModel = table.getColumnModel();

        SStringBuilder headerArea = Utils.inlineStyles(table.getDynamicStyle(STable.SELECTOR_HEADER));
        device.print("<tr class=\"header\"");
        Utils.optAttribute(device, "style", headerArea);
        device.print(">");

        Utils.printNewline(device, table, 1);
        writeSelectionHeader(device, table);

        for (int i = startX; i < endX; ++i) {
            STableColumn column = columnModel.getColumn(i);
            if (!column.isHidden())
                writeHeaderCell(device, table, rendererPane, i);
            else
                ++endX;
        }
        device.print("</tr>");
        Utils.printNewline(device, table);
    }

    private void writeFilter(Device device, XTable table, int startX, int endX) throws IOException {
        if (!table.isFilterVisible() || !(table.getModel() instanceof FilterableTableModel))
            return;

        final SCellRendererPane rendererPane = table.getCellRendererPane();
        STableColumnModel columnModel = table.getColumnModel();

        device.print("<tr class=\"filter\">\n");
        Utils.printNewline(device, table, 1);
        writeSelectionFilter(device, table);

        for (int i = startX; i < endX; ++i) {
            STableColumn column = columnModel.getColumn(i);
            if (!column.isHidden())
                writeFilterCell(device, table, rendererPane, i);
            else
                ++endX;
        }

        device.print("</tr>");
        Utils.printNewline(device, table);
    }

    protected void writeBody(Device device, XTable table,
            int startX, int endX, int startY, int endY, int emptyIndex) throws IOException {
        final SListSelectionModel selectionModel = table.getSelectionModel();

        SStringBuilder selectedArea = Utils.inlineStyles(table.getDynamicStyle(STable.SELECTOR_SELECTED));
        SStringBuilder evenArea = Utils.inlineStyles(table.getDynamicStyle(STable.SELECTOR_EVEN_ROWS));
        SStringBuilder oddArea = Utils.inlineStyles(table.getDynamicStyle(STable.SELECTOR_ODD_ROWS));
        final SCellRendererPane rendererPane = table.getCellRendererPane();
        STableColumnModel columnModel = table.getColumnModel();

        for (int r = startY; r < endY; ++r) {
            writeTableRow(device, table, columnModel, selectionModel, rendererPane, r, 
                    startX, endX, emptyIndex, selectedArea, oddArea, evenArea);
        }
    }

    protected void writeTableRow(
            Device device, XTable table, STableColumnModel columnModel, 
            SListSelectionModel selectionModel, SCellRendererPane rendererPane,
            final int rowIndex, int startX, int endX, 
            int emptyIndex, SStringBuilder selectedArea, 
            SStringBuilder oddArea, SStringBuilder evenArea) throws IOException {
        if (rowIndex >= emptyIndex) {
            int colspan = endX - startX;
            device.print("<tr class=\"empty\">\n");
            if (isSelectionColumnVisible(table)) {
                device.print("  <td></td>\n");
            }
            device.print("  <td colspan=\"" + colspan + "\">&nbsp;</td>\n");
            device.print("</tr>\n");
            return;
        }
        
        String rowStyle = table.getRowStyle(rowIndex);
        SStringBuilder rowClass = new SStringBuilder(rowStyle != null ? rowStyle + " " : "");
        device.print("<tr");
        if (selectionModel.isSelectedIndex(rowIndex)) {
            Utils.optAttribute(device, "style", selectedArea);
            rowClass.append("selected ");
        }
        else if (rowIndex % 2 != 0)
            Utils.optAttribute(device, "style", oddArea);
        else
            Utils.optAttribute(device, "style", evenArea);

        rowClass.append(rowIndex % 2 != 0 ? "odd" : "even");
        Utils.optAttribute(device, "class", rowClass);
        device.print(">");

        writeSelectionBody(device, table, rendererPane, rowIndex);
        
        for (int c = startX; c < endX; ++c) {
            STableColumn column = columnModel.getColumn(c);
            if (!column.isHidden())
                renderCellContent(device, table, rendererPane, rowIndex, c);
            else
                ++endX;
        }
        
        device.print("</tr>");
        Utils.printNewline(device, table);
    }

    private void writeSelectionFilter(Device device, XTable table) throws IOException {
        if (isSelectionColumnVisible(table)) {
            device.print("<th valign=\"middle\"");
            Utils.optAttribute(device, "width", selectionColumnWidth);

            String parameter = table.getResetParameter();
            Utils.printClickability(device, table, parameter, true, table.getShowAsFormComponent());
            device.print(" class=\"num clickable\"");

            device.print(">");

            resetLabel.write(device);

            device.print("</th>");
        }
    }

    protected void writeSelectionHeader(Device device, XTable table) throws IOException {
        if (isSelectionColumnVisible(table)) {
            device.print("<th valign=\"middle\"");
            Utils.optAttribute(device, "width", selectionColumnWidth);
    
            if (table.getModel() instanceof RefreshableModel) {
                String parameter = table.getRefreshParameter();
                Utils.printClickability(device, table, parameter, true, table.getShowAsFormComponent());
                device.print(" class=\"num clickable\">");
    
                refreshLabel.write(device);
            }
            else {
                device.print(" class=\"num\">");
            }
            device.print("</th>");
        }
    }

    private boolean atLeastOneColumnWidthIsNotNull(STableColumnModel columnModel) {
        int columnCount = columnModel.getColumnCount();
        for (int i = 0; i < columnCount; i++)
            if (columnModel.getColumn(i).getWidth() != null)
                return true;
        return false;
    }

    private void writeCol(Device device, String width) throws IOException {
        device.print("<col");
        Utils.optAttribute(device, "width", width);
        device.print("/>");
    }

    protected void writeFooter(Device device, XTable table, int startX, int endX) throws IOException {
    }

    private void writeNoData(Device device, XTable table, int startX, int endX, int endY, boolean filtered) throws IOException {
        int colspan = endX - startX;
        int middle = endY / 2;

        // till middle
        for (int i = 0; i < middle; ++i) {
            device.print("<tr class=\"empty\"><td colspan=\"" + (colspan + 1) + "\">&nbsp;</td></tr>");
        }

        device.print("<tr class=\"nodata\">\n");
        if (isSelectionColumnVisible(table))
            device.print("  <td>&nbsp;</td>\n");

        device.print("  <td colspan=\"" + colspan + "\" align=\"center\" valign=\"middle\">");
        device.print(filtered ? table.getNoDataFoundLabel() : table.getNoDataAvailableLabel());
        device.print("</td>\n");
        device.print("</tr>\n");

        for (int i = middle + 1; i < endY; ++i) {
            device.print("<tr class=\"empty\"><td colspan=\"" + (colspan + 1) + "\">&nbsp;</td></tr>");
        }
    }

    /**
     * Renders the row sometimes needed to allow row selection.
     */
    protected void writeSelectionBody(final Device device, final STable table, final SCellRendererPane rendererPane,
                                         final int row)
        throws IOException
    {
        final STableCellRenderer rowSelectionRenderer = table.getRowSelectionRenderer();
        if (isSelectionColumnVisible(table)) {
            if (rowSelectionRenderer instanceof SDefaultTableRowSelectionRenderer) {
                SDefaultTableRowSelectionRenderer defaultTableRowSelectionRenderer = (SDefaultTableRowSelectionRenderer)rowSelectionRenderer;
                defaultTableRowSelectionRenderer.setUseIcons(table.getSelectionMode() != SListSelectionModel.NO_SELECTION);
            }
            final SComponent comp = rowSelectionRenderer.getTableCellRendererComponent(table,
                                                                                       table.getToggleSelectionParameter(row, -1),
                                                                                       table.isRowSelected(row),
                                                                                       row, -1);
            final String columnStyle = Utils.joinStyles(comp, "num");

            device.print("<td valign=\"top\" align=\"right\"");
            Utils.optAttribute(device, "width", selectionColumnWidth);

            String value = table.getToggleSelectionParameter(row, -1);
            if (table.getSelectionMode() != SListSelectionModel.NO_SELECTION) {
                printClickability(device, table, value, table.getShowAsFormComponent());
                device.print(" class=\"clickable ");
                device.print(columnStyle);
                device.print("\"");
            }
            else {
                device.print(" class=\"");
                device.print(columnStyle);
                device.print("\"");
            }
            device.print(">");

            // Renders the content of the row selection row
            rendererPane.writeComponent(device, comp, table);

            device.print("</td>");
        }
    }
    
    public static void printClickability(final Device device, final SComponent component, final String eventValue,
                                         final boolean formComponent) throws IOException {
        device.print(" onclick=\"return wingS.table.cellClick(");
        device.print("event,this,");
        device.print(formComponent + ",");
        device.print(!component.isReloadForced() + ",'");
        device.print(Utils.event(component));
        device.print("','");
        device.print(eventValue == null ? "" : eventValue);
        device.print("'");
        device.print(");\"");
    }

    private boolean isSelectionColumnVisible(STable table) {
        if (table.getRowSelectionRenderer() != null && table.getSelectionModel().getSelectionMode() != SListSelectionModel.NO_SELECTION)
            return true;
        return false;
    }
    

    public Update getTableScrollUpdate(STable table, Rectangle newViewport, Rectangle oldViewport) {
        return new ComponentUpdate(this, table);
    }

    public Update getEditCellUpdate(STable table, int row, int column) {
        return new EditCellUpdate((XTable)table, row, column);
    }

    public Update getRenderCellUpdate(STable table, int row, int column) {
        return new RenderCellUpdate((XTable)table, row, column);
    }

    protected class TableScrollUpdate
        extends AbstractUpdate {

        public TableScrollUpdate(SComponent component) {
            super(component);
        }

        public Handler getHandler() {
            XTable table = (XTable) component;

            Rectangle currentViewport = table.getViewportSize();
            Rectangle maximalViewport = table.getScrollableViewportSize();
            int startX = 0;
            int endX = table.getVisibleColumnCount();
            int startY = 0;
            int endY = table.getRowCount();
            int emptyIndex = maximalViewport != null ? maximalViewport.height : endY;

            if (currentViewport != null) {
                startX = currentViewport.x;
                endX = startX + currentViewport.width;
                startY = currentViewport.y;
                endY = startY + currentViewport.height;
            }

            String htmlCode = "";
            String exception = null;

            try {
                StringBuilderDevice htmlDevice = new StringBuilderDevice();
                writeBody(htmlDevice, table, startX, endX, startY, endY, emptyIndex);
                htmlCode = htmlDevice.toString();
            } catch (Throwable t) {
                exception = t.getClass().getName();
            }

            UpdateHandler handler = new UpdateHandler("tableScroll");
            handler.addParameter(table.getName());
            handler.addParameter(htmlCode);
            if (exception != null) {
                handler.addParameter(exception);
            }
            return handler;
        }
    }

    private class EditCellUpdate
        extends AbstractUpdate<XTable>
    {
        private int row;
        private int column;

        public EditCellUpdate(XTable table, int row, int column) {
            super(table);
            this.row = row;
            this.column = column;
        }

        public Handler getHandler() {
            XTable table = this.component;

            String htmlCode = "";
            String exception = null;

            try {
                StringBuilderDevice device = new StringBuilderDevice(512);
                /*
                Utils.printTableCellAlignment(device, component, SConstants.LEFT, SConstants.TOP);
                Utils.optAttribute(device, "oversize", horizontalOversize);
                device.print(" class=\"cell\">");
                */
                SComponent component = table.getEditorComponent();
                table.getCellRendererPane().writeComponent(device, component, table);
                htmlCode = device.toString();
            }
            catch (Throwable t) {
                exception = t.getClass().getName();
            }

            row = table.isHeaderVisible() ? this.row + 1 : this.row;
            row = table.isFilterVisible() ? this.row + 1 : this.row;
            column = columnInView(table, column);
            column = isSelectionColumnVisible(table) ? column + 1 : column;

            UpdateHandler handler = new UpdateHandler("tableCell");
            handler.addParameter(table.getName());
            handler.addParameter(row);
            handler.addParameter(column);
            handler.addParameter(true);
            handler.addParameter(htmlCode);
            if (exception != null) {
                handler.addParameter(exception);
            }
            return handler;
        }
    }

    private class RenderCellUpdate
        extends AbstractUpdate<XTable>
    {
        private int row;
        private int column;

        public RenderCellUpdate(XTable table, int row, int column) {
            super(table);
            this.row = row;
            this.column = column;
        }

        public Handler getHandler() {
            XTable table = this.component;

            String htmlCode = "";
            String exception = null;

            try {
                StringBuilderDevice device = new StringBuilderDevice(256);
                /*
                Utils.printTableCellAlignment(device, component, SConstants.LEFT, SConstants.TOP);
                Utils.optAttribute(device, "oversize", horizontalOversize);
                device.print(" class=\"cell\">");
                */
                SComponent component = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                table.getCellRendererPane().writeComponent(device, component, table);
                htmlCode = device.toString();
            }
            catch (Throwable t) {
                exception = t.getClass().getName();
            }

            row = table.isHeaderVisible() ? this.row + 1 : this.row;
            row = table.isFilterVisible() ? this.row + 1 : this.row;
            column = columnInView(table, column);
            column = isSelectionColumnVisible(table) ? column + 1 : column;

            UpdateHandler handler = new UpdateHandler("tableCell");
            handler.addParameter(table.getName());
            handler.addParameter(row);
            handler.addParameter(column);
            handler.addParameter(false);
            handler.addParameter(htmlCode);
            if (exception != null) {
                handler.addParameter(exception);
            }
            return handler;
        }

        public boolean equals(Object object) {
            if (!super.equals(object))
                return false;

            RenderCellUpdate other = (RenderCellUpdate) object;

            if (this.row != other.row)
                return false;
            if (this.column != other.column)
                return false;

            return true;
        }

        public int hashCode() {
            int hashCode = super.hashCode();
            int dispersionFactor = 37;

            hashCode = hashCode * dispersionFactor + row;
            hashCode = hashCode * dispersionFactor + column;

            return hashCode;
        }
    }

    private int columnInView(STable table, int column) {
        int viewColumn = 0;
        for (int i=0; i < column; i++) {
            STableColumn tableColumn = table.getColumnModel().getColumn(i);
            if (!tableColumn.isHidden())
                viewColumn++;
        }
        return viewColumn;
    }
}
