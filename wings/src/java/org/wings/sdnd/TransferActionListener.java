package org.wings.sdnd;

import org.wings.SComponent;
import org.wings.SButton;
import org.wings.LowLevelEventListener;
import org.wings.script.JavaScriptListener;
import org.wings.script.JavaScriptEvent;
import org.wings.session.SessionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransferActionListener implements ActionListener, LowLevelEventListener {
    private SComponent focus;

    public TransferActionListener() {
        SessionManager.getSession().getDispatcher().register(this);
    }

    public void processLowLevelEvent(String name, String[] values) {
        if(values == null || values.length == 0)
            return;

        SComponent component = SessionManager.getSession().getComponentByName(values[0]);
        setFocus(component);
    }

    public String getLowLevelEventId() {
        return "focuslistener";
    }

    public String getName() {
        return "focuslistener";
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

    public void installFocusListener(SComponent ... components) {
        for(SComponent component : components) {
            component.addScriptListener(getScriptListener(component));
        }
    }

    public JavaScriptListener getScriptListener(SComponent component) {
        return new JavaScriptListener(JavaScriptEvent.ON_CLICK, "wingS.sdnd.focus('"+component.getName()+"');");
    }

    public void actionPerformed(ActionEvent e) {
        SComponent focus = getFocus();
        if(focus == null)
            return;

        Object source = e.getSource();
        if(source instanceof SButton) {
            String text = ((SButton)source).getText();
            Action a = focus.getActionMap().get(text);
            if(a == null)
                return;

            a.actionPerformed(new ActionEvent(focus, ActionEvent.ACTION_PERFORMED, null));
        }
    }

    public SComponent getFocus() {
        return focus;
    }

    public void setFocus(SComponent focus) {
        this.focus = focus;
    }
}
