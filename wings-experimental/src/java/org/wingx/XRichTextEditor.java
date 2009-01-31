package org.wingx;

import org.wings.SDimension;
import org.wings.STextComponent;
import org.wings.event.SDocumentEvent;
import org.wings.script.JavaScriptListener;
import org.wings.script.ScriptListener;
import org.wingx.plaf.RichTextEditorCG;

public class XRichTextEditor extends STextComponent {
    private String configuration;
    private XRichTextEditorType type;
    private ScriptListener listener;

    public XRichTextEditor() {
        this(XRichTextEditorType.Simple);
    }

    public XRichTextEditor(XRichTextEditorType type) {
        super();

        this.type = type;
        updateListener();
    }

    private void updateListener() {
        if(listener != null)
            removeScriptListener(listener);

        final StringBuilder builder = new StringBuilder();
        builder.append("wingS.global.onHeadersLoaded(function() {");
        String name = "editor_" + getName();
        String height = "";
        String width = "";
        if(getPreferredSize() != null) {
            height = getPreferredSize().getHeight();
            width = getPreferredSize().getWidth();
        } else {
            height = "100%";
            width = "100%";
        }
        String editor = type.getYuiClassName();
        final String config = getConfiguration();
        builder.append("window." + name + " = new YAHOO.widget." + editor + "('" + getName() + "', " +
                "{" +
                    "height:\"" + height + "\"," +
                    "width:\"" + width + "\"" + (config.trim().length() > 0 ? "," + config : "") +
                "});\n");
        //disable titlebar
        builder.append(name + "._defaultToolbar.titlebar = false;");

        //todo if necessary increase number of undos
        builder.append(name + ".maxUndo = 250;");

        builder.append(name + ".on('afterNodeChange', function(o) { var elt = document.getElementById('" + getName() + "'); " + name + ".saveHTML(); }, " + name + ", true);");
        builder.append(name + ".on('editorKeyUp', function(o) { var elt = document.getElementById('" + getName() + "'); " + name + ".saveHTML(); }, " + name + ", true);");
        
        builder.append(name + ".on('afterRender', function(o) { var elt = document.getElementById('" + getName() + "'); " + name + ".setEditorHTML('" + getText(this) + "'); }, true);");
        builder.append(name + ".render();");
        builder.append("});");

        listener = new JavaScriptListener(null, null, builder.toString());
        addScriptListener(listener);
    }

    @Override
    public void setPreferredSize(SDimension preferredSize) {
        super.setPreferredSize(preferredSize);

        updateListener();
    }

    public String getText(XRichTextEditor component) {
        String text = component.getText();
        text = text.replaceAll("\n", "<BR />");
        text = text.replaceAll("'", "\'");

        return text;
    }

    @Override
    public void changedUpdate(SDocumentEvent e) {
        if(isUpdatePossible()) {
            update(((RichTextEditorCG)getCG()).getTextUpdate(this));
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
        super.processLowLevelEvent(action, values);
    }
}
