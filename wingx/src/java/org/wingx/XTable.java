package org.wingx;

import org.wings.*;
import org.wings.table.*;
import org.wings.event.SMouseListener;
import org.wings.event.SMouseEvent;
import org.wingx.table.*;
import org.wingx.plaf.css.XTableCG;

import javax.swing.table.TableModel;
import java.util.*;

public class XTable extends STable
{
    private static final SIcon ICON_NONE = new SResourceIcon("org/wingx/table/images/sort_none.png");
    private static final SIcon ICON_ASCENDING = new SResourceIcon("org/wingx/table/images/sort_up.png");
    private static final SIcon ICON_DESCENDING = new SResourceIcon("org/wingx/table/images/sort_down.png");
    public static final SIcon ICON_REFRESH = new SResourceIcon("org/wingx/table/images/table_refresh.png");
    public static final SIcon ICON_RESET = new SResourceIcon("org/wingx/table/images/table_clear_filter.png");

    private Map column2Listeners = new HashMap();
    protected boolean filterVisible = true;
    protected boolean resetFilter = false;
    protected int delayedSortColumn = -1;
    private EditableTableCellRenderer filterRenderer;
    private boolean refresh;
    private SMouseListener linkMouseListener;
    private StringBuilder nameBuffer = new StringBuilder();
    
    public XTable() {
    }

    public XTable(TableModel model, STableColumnModel columnModel) {
        super(model, columnModel);
    }

    public XTable(TableModel tableModel) {
        super(tableModel);
    }

    /**
     * Returns a <code>STableColumnModel</code> that contains information
     * about all columns  of this table.
     *
     * @return  the object that provides the column state of the table
     * @see     #setColumnModel
     */
    public XTableColumnModel getColumnModel() {
        return (XTableColumnModel)columnModel;
    }

    public EditableTableCellRenderer getFilterRenderer() {
        return filterRenderer;
    }

    public void setFilterRenderer(EditableTableCellRenderer filterRenderer) {
        EditableTableCellRenderer oldVal = this.filterRenderer;
        this.filterRenderer = filterRenderer;
        propertyChangeSupport.firePropertyChange("filterRenderer", oldVal, this.filterRenderer);
    }

    protected void nameFilterComponent(final SComponent component, final int col, final int num) {
        nameBuffer.setLength(0);
        nameBuffer
            .append(this.getName())
            .append("_f_")
            .append(col)
            .append("_")
            .append(num);
        component.setNameRaw(nameBuffer.toString());
    }

//    private int nthVisibleColumn(int n) {
//        n += 1;
//        int c = 0;
//        for (Iterator iterator = columnModel.getColumns().iterator(); iterator.hasNext();) {
//            STableColumn column = (STableColumn)iterator.next();
//            if (!column.isHidden())
//                n --;
//
//            if (n == 0)
//                return c;
//
//            c ++;
//        }
//        throw new RuntimeException("There is no " + n + "'th visible column.");
//    }
    
    protected boolean filtersDifferent(Object filter, Object value) {
        if (filter instanceof Object[] && value instanceof Object[]) {
            return !arraysEqual((Object[]) filter, (Object[]) value);
        } else {
            return SComponent.isDifferent(filter, value);
        }
    }

    /**
     * Arrays.equals() doesn't work when the array elements are arrays
     * and so on...
     */
    private boolean arraysEqual(Object[] arr1, Object[] arr2) {
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            Object o1 = arr1[i];
            Object o2 = arr2[i];
            if (o1 == o2) {
                continue;
            }
            if (o1 instanceof Object[] && o2 instanceof Object[]) {
                boolean b = arraysEqual((Object[]) o1, (Object[]) o2);
                if (b) {
                    continue;
                } else {
                    return false;
                }
            }
            if (o1 == null || o2 == null || !o1.equals(o2)) {
                return false;
            }
        }
        return true;
    }

    public void processLowLevelEvent(String action, String[] values) {
        if (action.endsWith("_keystroke")) {
            processKeyEvents(values);
            return;
        }
        
        if (model instanceof FilterableTableModel && action.indexOf("_f_") != -1) {
            FilterableTableModel filterableTableModel = (FilterableTableModel)getModel();

            StringTokenizer tokens = new StringTokenizer(action, "_");
            tokens.nextToken(); // tableName
            tokens.nextToken(); // f
            int col = Integer.parseInt(tokens.nextToken()); // col

            EditableTableCellRenderer editableTableCellRenderer = getFilterRenderer(col);
            editableTableCellRenderer.getLowLevelEventListener(this, -1, col).processLowLevelEvent(action, values);
            Object value = editableTableCellRenderer.getValue();

            Object filter = filterableTableModel.getFilter(convertColumnIndexToModel(col));
            if (filtersDifferent(filter, value)) {
                filterableTableModel.setFilter(convertColumnIndexToModel(col), value);
                SForm.addArmedComponent(this);
                refresh = true;
            }
        }
        else if (isEditing() && action.indexOf("_e_") != -1 && cellEditorComponent != null) {
            cellEditorComponent.processLowLevelEvent(action, values);
        }
        else if (action.indexOf("_") != -1) {
            StringTokenizer tokens = new StringTokenizer(action, "_");
            tokens.nextToken(); // tableName
            int row = Integer.parseInt(tokens.nextToken()); // row
            int col = Integer.parseInt(tokens.nextToken()); // col

            STableCellRenderer cellRenderer = getCellRenderer(row, col);
            if (cellRenderer instanceof EditableTableCellRenderer) {
                EditableTableCellRenderer editableCellRenderer = (EditableTableCellRenderer)cellRenderer;
                LowLevelEventListener listener = editableCellRenderer.getLowLevelEventListener(this, row, col);
                if (listener.isEnabled()) {
                    listener.processLowLevelEvent(action, values);
                    Object value = editableCellRenderer.getValue();
                    getModel().setValueAt(value, row, convertColumnIndexToModel(col));
                }
            }
        }
        else {
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                char modus = value.charAt(0);
                try {
                    // editor event
                    switch (modus) {
                        case 'o':
                            delayedSortColumn = Integer.parseInt(value.substring(1));
                            SForm.addArmedComponent(this);
                            break;
                        case 'c':
                            resetFilter = true;
                            break;
                        case 'r':
                            refresh = true;
                            break;
                    }
                }
                catch (NumberFormatException ex) {
                    // ignored
                }
            }
            super.processLowLevelEvent(action, values);
        }
    }

    public void fireFinalEvents() {
        super.fireFinalEvents();
        /*
        * check for sort
        */
        if (delayedSortColumn != -1) {
            SortableTableModel sortableModel = (SortableTableModel)model;
            int order = sortableModel.getSort(delayedSortColumn);
            order += 1;
            order %= 3;
            sortableModel.setSort(delayedSortColumn, order);
            delayedSortColumn = -1;
            refresh = true;
        }
        /*
        * check for filter reset
        */
        if (resetFilter) {
            resetFilter = false;
            resetFilter();
            refresh = true;
        }

        if (refresh) {
            refresh = false;
            refresh();
        }
    }

    /**
     * refresh the table
     */
    public void refresh() {
        if (getModel() instanceof RefreshableModel)
            ((RefreshableModel)getModel()).refresh();
    }

    public void resetFilter() {
        FilterableTableModel filterableTableModel = (FilterableTableModel)getModel();
        for (int i=0; i < filterableTableModel.getColumnCount(); i++)
            filterableTableModel.setFilter(i, null);
    }

    public void updateCG() {
        setCG(new XTableCG());
    }

    public String getToggleSortParameter(int col) {
        return "o" + col;
    }

    public String getRefreshParameter() {
        return "r";
    }

    public String getResetParameter() {
        return "c";
    }

    /**
     * Returns the header renderer for the given header cell.
     * @param col Table column
     * @return The header renderer for the given header cell.
     */
    public EditableTableCellRenderer getFilterRenderer( int col ) {
        STableColumnModel columnModel = getColumnModel();
        if (columnModel != null) {
            STableColumn column  = columnModel.getColumn(col);
            if (column != null) {
                STableCellRenderer renderer = column instanceof XTableColumn ? ((XTableColumn)column).getFilterRenderer() : column.getCellRenderer();
                if (renderer instanceof EditableTableCellRenderer)
                   return (EditableTableCellRenderer)renderer;
            }
        }
        return getFilterRenderer();
    }

    /**
     * Prepares and returns the renderer to render the column filter
     * @param col Column number to render. Starts with <code>0</code>. May be <code>-1</code> for row selection column.
     * @return The renderer to render the column filter
     */
    public SComponent prepareFilterRenderer(EditableTableCellRenderer filterRenderer, int col ) {
        Object filterValue = col >= 0 ? ((FilterableTableModel)model).getFilter(convertColumnIndexToModel(col)) : null;
        SComponent component = filterRenderer.getTableCellRendererComponent(this, filterValue, false, -1, col);
        nameFilterComponent(component, col);
        return component;
    }

    protected void nameFilterComponent(final SComponent component, final int col) {
        nameBuffer.setLength(0);
        nameBuffer.append(this.getName()).append("_f_");
        nameBuffer.append(col);
        component.setNameRaw(nameBuffer.toString());
    }
    
    public final void removeClickListener(final XTableClickListener listener) {
        for (Iterator iterator = column2Listeners.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry colAndListeners = (Map.Entry)iterator.next();
            List l = (List)colAndListeners.getValue();
            if (l == null) {
                continue;
            }
            l.remove(listener);
        }
    }

    public void addClickListener(int index, XTableClickListener listener) {
        List l = (List) column2Listeners.get(Integer.valueOf(index));
        if (l == null) {
            l = new ArrayList();
            column2Listeners.put(Integer.valueOf(index), l);
        }
        l.add(listener);
        if (linkMouseListener == null) {
            linkMouseListener = new SMouseListener() {
                public void mouseClicked(SMouseEvent e) {
                    SPoint point = e.getPoint();
                    int col = XTable.this.columnAtPoint(point);
                    List listeners = (List) column2Listeners.get(Integer.valueOf(col));
                    if (listeners == null) {
                        return;
                    }
                    for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
                        XTableClickListener listener  = (XTableClickListener) iterator.next();
                        listener.clickOccured(XTable.this.rowAtPoint(point), col);
                    }
                }
            };
            addMouseListener(linkMouseListener);
        }
    }

    public boolean isCellEditable(int row, int col) {
        if (isClickListenerSet(col)) {
            return true;
        }
        return super.isCellEditable(row, col);
    }

    public boolean isColumnSortable(int col) {
        STableColumn column = getColumnModel().getColumn(col);
        if (column instanceof XTableColumn) {
            XTableColumn xTableColumn = (XTableColumn)column;
            return xTableColumn.isSortable();
        }
        return false;
    }

    public boolean isFilterVisible() {
        return filterVisible;
    }

    public void setFilterVisible(boolean filterVisible) {
        boolean oldVal = this.filterVisible;
        this.filterVisible = filterVisible;
        propertyChangeSupport.firePropertyChange("filterVisible", oldVal, this.filterVisible);
    }

    private boolean isClickListenerSet(int col) {
        List l = (List) column2Listeners.get(Integer.valueOf(col));
        return (l != null && !l.isEmpty());
    }

    public SComponent prepareRenderer(STableCellRenderer r, int row, int col) {
        SComponent component = super.prepareRenderer(r, row, col);
        if (isClickListenerSet(col)) {
            if (!(component.getStyle().indexOf(" link ") > 0)) {
                component.setStyle(component.getStyle() + " link ");
            }
        } else if (component.getStyle() != null) {
            component.setStyle(component.getStyle().replaceAll(" link ", " "));
        }
        return component;
    }

    public boolean editCellAt(int row, int column, EventObject eo) {
        if (isClickListenerSet(column)) {
            return false;
        }
        return super.editCellAt(row, column, eo);
    }

    public static class HeaderRenderer
        extends SDefaultTableCellRenderer
    {
        public HeaderRenderer() {
            setHorizontalTextPosition(SConstants.LEFT);
            setPreferredSize(SDimension.FULLWIDTH);
        }

        public SComponent getTableCellRendererComponent(STable table, Object value, boolean selected, int row, final int col) {
            if (table.getModel() instanceof SortableTableModel) {
                SortableTableModel sortableTableModel = (SortableTableModel)table.getModel();

                setIcon(null);
                setText(value != null ? value.toString() : null);

                if (!((XTable)table).isColumnSortable(col)) {
                    setIcon(null);
                }
                else switch (sortableTableModel.getSort(col)) {
                    case SortableTableModel.SORT_NONE:
                        setIcon(ICON_NONE);
                        break;
                    case SortableTableModel.SORT_ASCENDING:
                        setIcon(ICON_ASCENDING);
                        break;
                    case SortableTableModel.SORT_DESCENDING:
                        setIcon(ICON_DESCENDING);
                        break;
                }

                return this;
            }
            else
                return super.getTableCellRendererComponent(table, value, selected, row, col);
        }
    }

    public void createDefaultColumnsFromModel() {
        TableModel tm = getModel();

        if (tm != null) {
            STableColumnModel columnModel = getColumnModel();
            while (columnModel.getColumnCount() > 0)
                columnModel.removeColumn(columnModel.getColumn(0));

            for ( int i = 0; i < tm.getColumnCount(); i++ ) {
                XTableColumn column = new XTableColumn( i );
                String columnName = tm.getColumnName( i );
                column.setHeaderValue( columnName );
                this.columnModel.addColumn( column );
            }
        }
    }

    @Override
    protected XTableColumnModel createDefaultColumnModel() {
        return new XDefaultTableColumnModel();
    }
}
