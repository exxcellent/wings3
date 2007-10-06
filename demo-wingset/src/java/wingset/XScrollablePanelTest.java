package wingset;

import org.wings.*;
import org.wings.border.SEmptyBorder;
import org.wingx.XScrollablePanel;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:B.Schmid@eXXcellent.de">Benjamin Schmid</a>
 */
public class XScrollablePanelTest
    extends WingSetPane
{
    private final SComboBox selectComboBox = new SComboBox();
    private final SPanel mainPanel = new SPanel(new SGridLayout(1, 2, 10, 0));
    private Color[] colors = new Color[] { Color.red, Color.green, Color.yellow};

    protected SComponent createControls() {
        for(int i = 2; i< 10; i++) {
            selectComboBox.addItem(new Integer(i));
        }
        selectComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
        selectComboBox.setSelectedIndex(0);
        selectComboBox.setHorizontalAlignment(SConstants.LEFT_ALIGN);
        return selectComboBox;
    }

    protected SComponent createExample() {
        update();
        return mainPanel;
    }

    private void update() {
        mainPanel.removeAll();
        SPanel nestedPanel = nestPanel((Integer)selectComboBox.getSelectedItem());
        mainPanel.add(nestedPanel);
    }

    private SPanel nestPanel(int depth) {
        SPanel panel = new SPanel(new SGridLayout());
        panel.add(new SLabel("Depth "+depth));
        panel.setBackground(colors[depth % colors.length]);
        panel.setBorder(new SEmptyBorder(20,20,20,20));
        if (depth > 1)
            panel.add(nestPanel(depth-1));
        return panel;
    }

    public SScrollPane addScrollable(final SComponent component, final int height) {
        XScrollablePanel panel = new XScrollablePanel(component);
        panel.setPreferredSize(SDimension.FULLWIDTH);
        return panel.asScrollPane();
    }
}
