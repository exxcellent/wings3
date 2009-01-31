package wingset;

import org.wings.SComponent;
import org.wingx.XRichTextEditor;
import org.wingx.XRichTextEditorType;

public class XRichTextEditorSimpleExample extends WingSetPane {
    protected SComponent createControls() {
        return null;
    }

    protected SComponent createExample() {
        XRichTextEditor editor;
        editor = new XRichTextEditor(XRichTextEditorType.Simple);

        return editor;
    }
}
