package wingscms;

import org.wings.*;
import org.wings.session.SessionManager;
import org.wingx.table.EditableTableCellRenderer;

import java.text.NumberFormat;

class IntegerCellRenderer
    extends SFormattedTextField
    implements EditableTableCellRenderer
{
    IntegerCellRenderer() {
        super(NumberFormat.getIntegerInstance(SessionManager.getSession().getLocale()));
        addActionListener(new NoAction());
    }

    public Object getValue() {
        String s = getText();
        if ("".equals(s))
            return null;
        return ((Long)super.getValue()).intValue();
    }

    public SComponent getTableCellRendererComponent(STable table, Object value, boolean isSelected, int row, int column) {
        setText(value != null ? value.toString() : null);
        return this;
    }

    public LowLevelEventListener getLowLevelEventListener(STable table, int row, int column) {
        return this;
    }
}
