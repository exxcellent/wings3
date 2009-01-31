package org.wingx;

public class XRichTextEditorType {
    protected String yuiClassName;
    protected String config;

    public static XRichTextEditorType Simple = new XRichTextEditorType("SimpleEditor", "");
    public static XRichTextEditorType Normal = new XRichTextEditorType("Editor", "");

    public XRichTextEditorType(String yuiClassName, String config) {
        this.yuiClassName = yuiClassName;
        this.config = config;
    }

    public String getYuiClassName() {
        return yuiClassName;
    }

    public String getConfig() {
        return config;
    }
}
