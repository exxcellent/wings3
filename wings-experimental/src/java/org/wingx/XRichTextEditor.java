package org.wingx;

import org.wings.STextArea;
import org.wings.SDialog;
import org.wings.STextComponent;
import org.wings.script.ScriptListener;
import org.wings.plaf.css.Utils;
import org.wings.plaf.css.AbstractComponentCG;
import org.wings.header.SessionHeaders;
import org.wings.header.Header;

import java.util.List;
import java.util.LinkedList;

public class XRichTextEditor extends STextComponent {
    public final static int SIMPLE_EDITOR = 0;
    public final static int NORMAL_EDITOR = 1;

    private int editorType;

    public XRichTextEditor() {
        this(SIMPLE_EDITOR);
    }

    public XRichTextEditor(int editorType) {
        super();

        this.editorType = editorType;
    }

    public int getEditorType() {
        return editorType;
    }
}
