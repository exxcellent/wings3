package org.wingx;

import org.wings.*;
import org.wings.event.SDocumentEvent;
import org.wings.script.ScriptListener;
import org.wings.plaf.css.Utils;
import org.wings.plaf.css.AbstractComponentCG;
import org.wings.plaf.TextFieldCG;
import org.wings.plaf.TextAreaCG;
import org.wings.header.SessionHeaders;
import org.wings.header.Header;
import org.wingx.plaf.RichTextEditorCG;

import java.util.List;
import java.util.LinkedList;

public class XRichTextEditor extends STextComponent {
    public final static int SIMPLE_EDITOR = 0;
    public final static int NORMAL_EDITOR = 1;

    private int editorType;
    private String title;

    public XRichTextEditor(String title) {
        this(SIMPLE_EDITOR);

        this.title = title;
    }

    private XRichTextEditor(int editorType) {
        super();
        
        this.editorType = editorType;
    }

    public int getEditorType() {
        return editorType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
