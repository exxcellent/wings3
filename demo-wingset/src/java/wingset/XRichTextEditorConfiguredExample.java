package wingset;

import org.wings.SComponent;
import org.wings.SForm;
import org.wingx.XRichTextEditor;
import org.wingx.XRichTextEditorType;

public class XRichTextEditorConfiguredExample extends WingSetPane {

    protected SComponent createControls() {
        return null;
    }

    protected SComponent createExample() {
        XRichTextEditor editor;
        editor = new XRichTextEditor(MyRichTextEditorType.NormalWithUndoRedoEnglish);
        editor.setText("A quick brown <b>fox</b><br/>jumps over the <i>lazy</i> dog");
        editor.addDocumentListener(editor);

        return editor;
    }

    public static class MyRichTextEditorType extends XRichTextEditorType {
        public MyRichTextEditorType(String yuiClassName, String config) {
            super(yuiClassName, config);
        }

        /**
         * Example Configuration
         */
        public static XRichTextEditorType NormalWithUndoRedoEnglish = new XRichTextEditorType("Editor",  "toolbar: { collapse: false," +
                  "titlebar: 'Editor'," +
                  "draggable: false," +
                  "buttonType: 'advanced'," +
                    "focusAtStart: true," +
                    "autoHeight: true," +
                  "buttons: [" +
                      "    { group: 'fontstyle', label: 'Font'," +
                      "        buttons: [" +
                      "            { type: 'select', label: 'Arial', value: 'fontname', disabled: true," +
                      "                menu: [" +
                      "                    { text: 'Arial', checked: true }," +
                      "                    { text: 'Courier New' }," +
                      "                    { text: 'Times New Roman' }" +
                      "                ]" +
                      "            }," +
                      "            { type: 'spin', label: '14', value: 'fontsize', range: [ 8, 36 ], disabled: true }" +
                      "        ]" +
                      "    }," +
                      "    { type: 'separator' }," +
                      "    { group: 'textstyle', label: 'Format'," +
                      "        buttons: [" +
                      "            { type: 'push', label: 'Bold', value: 'bold' }," +
                      "            { type: 'push', label: 'Italic', value: 'italic' }," +
                      "            { type: 'push', label: 'Underlined', value: 'underline' }," +
                      "            { type: 'separator' }," +
                      "            { type: 'color', label: 'Color', value: 'forecolor', disabled: true }," +
                      "            { type: 'separator' }," +
                      "            { type: 'push', label: 'Remove formatting', value: 'removeformat', disabled: true }" +
                      "        ]" +
                      "    }," +
                      "    { type: 'separator' }," +
                      "    { group: 'undoredo', label: 'Undo/Redo'," +
                      "        buttons: [" +
                      "            { type: 'push', label: 'Undo', value: 'undo' }," +
                      "            { type: 'push', label: 'Redo', value: 'redo' }" +
                      "        ]" +
                      "    }," +
                      "    { type: 'separator' }," +
                      "    { group: 'alignment', label: 'Alignment'," +
                      "        buttons: [" +
                      "            { type: 'push', label: 'Left', value: 'justifyleft' }," +
                      "            { type: 'push', label: 'Center', value: 'justifycenter' }," +
                      "            { type: 'push', label: 'Right', value: 'justifyright' }" +
                      "        ]" +
                      "    }," +
                      "    { type: 'separator' }," +
                      "            { group: 'list', label: 'Lists'," +
                      "                buttons: [" +
                      "                    { type: 'push', label: 'Unordered', value: 'insertunorderedlist' }," +
                      "                    { type: 'push', label: 'Numbered', value: 'insertorderedlist' }" +
                      "                ]" +
                      "            }," +
                      "            { type: 'separator' }"+

                  "]" +
                  "}"
            );
    }
}
