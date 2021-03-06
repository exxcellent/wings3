package org.wingx;

import org.wings.SDimension;
import org.wings.STextComponent;
import org.wings.io.Device;
import org.wings.event.SDocumentEvent;
import org.wings.script.JavaScriptListener;
import org.wings.script.ScriptListener;
import org.wings.script.JavaScriptEvent;
import org.wingx.plaf.RichTextEditorCG;

import java.io.IOException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class XRichTextEditor extends STextComponent {
    private String configuration;
    private XRichTextEditorType type;
    private ScriptListener listener;
    private boolean editable;

    public XRichTextEditor() {
        this(XRichTextEditorType.Simple);
    }

    public XRichTextEditor(XRichTextEditorType type) {
        super();

        this.type = type;
        updateListener();
        addPropertyChangeListener("enabled", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
            }
        });
    }

    public String getEditorName() {
        return "editor_" + getName();
    }

    private void updateListener() {
        if(listener != null)
            removeScriptListener(listener);

        String name = getEditorName();
        String height = "";
        String width = "";

        if(getPreferredSize() != null) {
            height = getPreferredSize().getHeight();
            width = getPreferredSize().getWidth();
        } else {
            height = "100%";
            width = "100%";
        }

        final StringBuilder builder = new StringBuilder();
        builder.append("wingS.global.onHeadersLoaded(function() {");

            String editor = type.getYuiClassName();
            final String config = getConfiguration();
            builder.append("window.").append(name).append(" = new YAHOO.widget.").append(editor).append("('").append(getName()).append("', " +
                    "{" +
                        "height:\"").append(height).append("\"," +
                        "width:\"").append(width).append("\",")
                        .append("allowNoEdit : ").append("true") // last , is added by next line
                        .append((config.trim().length() > 0 ? "," + config : ""))
                    .append("});\n");
            //disable titlebar
            builder.append(name).append("._defaultToolbar.titlebar = false;\n");

            //todo if necessary increase number of undos
            builder.append(name).append(".maxUndo = 250;\n");

            builder.append(name).append(".on('afterNodeChange', function(o) { var elt = document.getElementById('").append(getName()).append("'); " + name + ".saveHTML(); }, ").append(name).append(", true);\n");
            builder.append(name).append(".on('editorKeyUp', function(o) { var elt = document.getElementById('").append(getName()).append("'); " + name + ".saveHTML(); }, ").append(name).append(", true);\n");
            builder.append(name).append(".on('afterRender', function(o) { ").append("document.getElementById(").append(name).append(".get('iframe').get('id')).tabIndex = '").append(getFocusTraversalIndex()).append("'; }, true);\n");
            builder.append(name).append(".on('afterRender', function(o) { ").append(name).append(".setEditorHTML('").append(doubleEscape(getCG().getText(this))).append("</div>'); }, true);\n");
            builder.append(name).append(".on('afterRender', function(o) { ").append(name).append(".set('disabled', ").append(!isEnabled()).append("); }, true);\n");
        builder.append(name).append(".render();\n");
        builder.append("});");

        listener = new JavaScriptListener("special", null, builder.toString());
        addScriptListener(listener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        boolean oldVal = this.enabled;
        this.enabled = enabled;
        if(oldVal != enabled)
            update(getCG().getEnabledAndWritabilityUpdate(XRichTextEditor.this));
        // editor_x.set("disabled", !enabled)
    }

    @Override
    public void setEditable(boolean ed) {
        this.editable = ed;
    }

    @Override
    public boolean isEditable() {
        return this.editable;
    }

    private String doubleEscape(String text) {
        text = text.replaceAll("\\\'", "\\\\'");
        return text;
    }

    @Override
    public void write(Device s) throws IOException {
        updateListener();
        
        super.write(s);
    }

    @Override
    public void setText(String text) {
        if(text == null) {
            text = "";
        }
        
        text = text.replaceAll("\r", "");
        text = text.replaceAll("\n", "");

        super.setText(text);
    }

    public void setPlainText(String text) {
        setText(convertFromPlain(text));
    }

    private String convertFromPlain(String text) {
        if(text == null)
            text = "";
        
        text = text.replaceAll("\r", "");
        text = text.replaceAll("\n", "<BR />");
        text = text.replaceAll("<", "%lt;");
        text = text.replaceAll(">", "%gt;");

        return text;
    }
    
    @Override
    public RichTextEditorCG getCG() {
        return (RichTextEditorCG)super.getCG();
    }

    @Override
    public void setPreferredSize(SDimension preferredSize) {
        super.setPreferredSize(preferredSize);

        updateListener();
    }

    @Override
    public void changedUpdate(SDocumentEvent e) {
        if(isUpdatePossible()) {
            update(getCG().getTextUpdate(this));
        } 
    }

    /**
    * Contains the configuration for the RichTextEditor
    *
    * @return
    */
    public String getConfiguration() {
        return configuration != null ? configuration : type.getConfig();
    }

    /**
     * Sets the configuration for the RichTextEditor
     *
     * @param configuration
     */
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    @Override
    public void processLowLevelEvent(String action, String[] values) {
        if(isEditable()) {
            super.processLowLevelEvent(action, values);
        }
        // otherwise ignore the texts sent from clients
    }
}
