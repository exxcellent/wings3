package org.wingx.plaf;

import org.wingx.XRichTextEditor;
import org.wings.plaf.ComponentCG;
import org.wings.plaf.Update;

public interface RichTextEditorCG<COMPONENT_TYPE extends XRichTextEditor> extends ComponentCG<COMPONENT_TYPE> {
    public Update getEnabledAndWritabilityUpdate(XRichTextEditor component);
    public Update getTextUpdate(XRichTextEditor component);
    public String getText(XRichTextEditor component);
}
