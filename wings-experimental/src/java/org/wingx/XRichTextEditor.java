package org.wingx;

import org.wings.*;
import org.wings.event.SDocumentEvent;
import org.wingx.plaf.RichTextEditorCG;

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

    @Override
    public void changedUpdate(SDocumentEvent e) {
        if(isUpdatePossible()) {
            update(((RichTextEditorCG)getCG()).getTextUpdate(this));
        }
    }

    public int getEditorType() {
        return editorType;
    }
}
