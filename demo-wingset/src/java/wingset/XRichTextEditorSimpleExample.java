package wingset;

import org.wings.*;
import org.wingx.XRichTextEditor;
import org.wingx.XRichTextEditorType;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class XRichTextEditorSimpleExample extends WingSetPane {
    protected SComponent createControls() {
        SPanel panel = new SPanel();

        SButton button = new SButton("Update Textarea");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText(editor.getText());
            }
        });
        panel.add(button);

        SButton button2 = new SButton("Disable/Enable");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editor.setEnabled(!editor.isEnabled());
            }
        });
        panel.add(button2);

        return panel;
    }

    private XRichTextEditor editor;
    private STextArea textArea;

    private String testText = "'Hello World',\nThis is a RichTextEditor \"Test\"";
    protected SComponent createExample() {
        SPanel panel = new SPanel(new SFlowDownLayout());
        panel.setPreferredSize(SDimension.FULLWIDTH);
        editor = new XRichTextEditor(XRichTextEditorType.Simple);
        editor.setPreferredSize(SDimension.FULLAREA);
        editor.setText(testText);


        textArea = new STextArea();
        textArea.setText(editor.getText());
        textArea.setPreferredSize(SDimension.FULLAREA);
        
        panel.add(editor);
        panel.add(new SSpacer("100%", "5px"));
        panel.add(textArea);
        
        return panel;
    }
}
