package org.wings.sdnd;

import org.wings.SComponent;
import org.wings.SButton;
import org.wings.LowLevelEventListener;
import org.wings.STextComponent;
import org.wings.script.JavaScriptListener;
import org.wings.script.JavaScriptEvent;
import org.wings.session.SessionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TransferActionListener implements ActionListener, LowLevelEventListener {
    private SComponent focus;
    private final static String NAME = "datalistener";

    public TransferActionListener() {
        SessionManager.getSession().getDispatcher().register(this);
    }

    public static void installFocusListener(SComponent ... components) {
        for(SComponent component : components) {
            component.addScriptListener(getFocusListener(component));
        }
    }

    public static void installSendDataListener(SComponent ... components) {
        for(SComponent component : components) {
            component.addScriptListener(getSendDataListener(component));
        }
    }

    public static JavaScriptListener getFocusListener(SComponent component) {
        return new JavaScriptListener(JavaScriptEvent.ON_CLICK, "wingS.sdnd.focus('"+component.getName()+"');");
    }

    public static JavaScriptListener getSendDataListener(SComponent component) {
        return new JavaScriptListener(JavaScriptEvent.ON_MOUSE_DOWN, "wingS.sdnd.sendDataForClipboardAction('" + NAME + "')");
    }

    public void processLowLevelEvent(String name, String[] values) {
        if(values == null || values.length != 1)
            return;

        String[] vals = values[0].split(":");

        SComponent component = SessionManager.getSession().getComponentByName(vals[0]);
        setFocus(component);

        if(vals.length > 1 && component instanceof STextComponent) {
            STextComponent textComponent = (STextComponent)component;

            String[] selections = vals[1].split("-");
            if(selections.length > 1) {
                textComponent.setSelectionStart(Integer.parseInt(selections[0]));
                textComponent.setSelectionEnd(Integer.parseInt(selections[1]));
            } else {
                int position = Integer.parseInt(selections[0]);
                textComponent.setCaretPosition(position);
                textComponent.setSelectionStart(position);
                textComponent.setSelectionEnd(position);
            }
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
