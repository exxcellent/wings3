package wingscms;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import java.lang.reflect.Method;

public class ObjectTableModel<T>
    extends AbstractTableModel
{
    List<String> properties = new ArrayList<String>();
    List<T> rows = new ArrayList<T>();

    public List<String> getProperties() {
        return properties;
    }

    public List<T> getRows() {
        return rows;
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return properties.size();
    }

    public String getColumnName(int column) {
        return properties.get(column);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object row = rows.get(rowIndex);
        String property = properties.get(columnIndex);
        try {
            Method getter = row.getClass().getMethod("get" + Character.toUpperCase(property.charAt(0)) + property.substring(1));
            return getter.invoke(row);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Object row = rows.get(rowIndex);
        String property = properties.get(columnIndex);
        try {
            Method getter = row.getClass().getMethod("get" + Character.toUpperCase(property.charAt(0)) + property.substring(1));
            Method setter = row.getClass().getMethod("set" + Character.toUpperCase(property.charAt(0)) + property.substring(1), getter.getReturnType());
            setter.invoke(row, value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
