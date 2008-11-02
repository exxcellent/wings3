package org.wings.sdnd;

import org.wings.script.JavaScriptListener;
import org.wings.script.JavaScriptEvent;
import org.wings.LowLevelEventListener;
import org.wings.STextComponent;
import org.wings.session.SessionManager;

public class TextComponentChangeListener extends JavaScriptListener implements LowLevelEventListener {
    private final static String NAME = "textpos";
    private STextComponent textComponent;

    public TextComponentChangeListener(STextComponent component) {
        super(JavaScriptEvent.ON_SELECT, null, "wingS.sdnd.installTextPositionHandler('" + component.getName() + "', '" + NAME + "');");

        this.textComponent = component;
        SessionManager.getSession().getDispatcher().register(this);
    }

    public void processLowLevelEvent(String name, String[] values) {
        if(values == null || values.length != 1)
            return;

        int index = values[0].indexOf("-");
        if(index != -1) {
            textComponent.setSelectionStart(Integer.parseInt(values[0].substring(0, index)));
            textComponent.setSelectionEnd(Integer.parseInt(values[0].substring(index+1, values[0].length())));
        } else {
            int position = Integer.parseInt(values[0]);
            textComponent.setCaretPosition(position);
            textComponent.setSelectionStart(position);
            textComponent.setSelectionEnd(position);
        }
    }

    public String getLowLevelEventId() {
        return NAME;
    }

    public String getName() {
        return NAME;
    }

    public void fireIntermediateEvents() {
    }

    public void fireFinalEvents() {
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isEpochCheckEnabled() {
        return false;
    }
}
