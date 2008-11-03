package org.wings.sdnd;

import org.wings.script.JavaScriptListener;
import org.wings.script.JavaScriptEvent;
import org.wings.STextComponent;

public class TextComponentChangeListener extends JavaScriptListener {
    public TextComponentChangeListener(STextComponent component) {
        super(JavaScriptEvent.ON_SELECT, null, "wingS.sdnd.installTextPositionHandler('" + component.getName() + "');");
    }

    public static void installTextChangeListener(STextComponent ... components) {
        for(STextComponent component : components) {
            component.addScriptListener(new TextComponentChangeListener(component));
        }
    }
}
