package org.wings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.wings.SCheckBox;
import org.wings.SLabel;
import org.wings.STextField;
import org.wings.test.WingsTestCase;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TestPropertyChange extends WingsTestCase {
    public TestPropertyChange(){
    }

    @Test
    public void testPropertyChangeEventKonstruktor1(){
        Object source = new Object();
        String propertyName = "property";
        int oldValue = 1;
        int newValue = 2;
        PropertyChangeEvent event = new PropertyChangeEvent(source, propertyName, oldValue, newValue);

        assertEquals(event.getPropertyName(), propertyName);
        assertEquals(event.getOldValue(), oldValue);
        assertEquals(event.getNewValue(), newValue);
    }

    @Test
    public void testPropertyChangeEventKonstruktorPropertyNameIsEmpty(){
        Object source = new Object();
        String propertyName = "";
        int oldValue = 1;
        int newValue = 2;
        PropertyChangeEvent event = new PropertyChangeEvent(source, propertyName, oldValue, newValue);

        assertEquals(event.getPropertyName(), propertyName);
        assertEquals(event.getOldValue(), oldValue);
        assertEquals(event.getNewValue(), newValue);
    }

    @Test
    public void testPropertyChangeEventKonstruktornewValueIsNull(){
        Object source = new Object();
        String propertyName = "property";
        int oldValue = 1;
        Integer newValue = null;
        PropertyChangeEvent event = new PropertyChangeEvent(source, propertyName, oldValue, newValue);

        assertEquals(event.getPropertyName(), propertyName);
        assertEquals(event.getOldValue(), oldValue);
        assertEquals(event.getNewValue(), newValue);
    }

    @Test
    public void testPropertyChangeEventKonstruktoroldValueIsNull(){
        Object source = new Object();
        String propertyName = null; 
        Integer oldValue = null;
        Integer newValue = null;
        PropertyChangeEvent event = new PropertyChangeEvent(source, propertyName, oldValue, newValue);

        assertEquals(event.getPropertyName(), propertyName);
        assertEquals(event.getOldValue(), oldValue);
        assertEquals(event.getNewValue(), newValue);
        assertEquals(event.getNewValue(), newValue);
    }

    @Test
    public void testSendEvent(){
        final STextField text1 = new STextField();
        final SLabel label1 = new SLabel();

        final PropertyChangeListener propertyChangeListener = new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                label1.setText(""+propertyChangeEvent.getNewValue());
            }
        };

        text1.addPropertyChangeListener("text",propertyChangeListener);

        String[] values = {"test"};
        sendEvent(text1, values);

        assertEquals(label1.getText(), values[0]);
    }

    @Test
    public void testSendEvent2(){
        final STextField text1 = new STextField();
        final SLabel label1 = new SLabel();

        final PropertyChangeListener propertyChangeListener = new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                label1.setText(""+propertyChangeEvent.getNewValue());
            }
        };

        text1.addPropertyChangeListener(propertyChangeListener);

        String[] values = {"test"};
        sendEvent(text1, values);

        assertEquals(label1.getText(), values[0]);
    }

    @Test
    public void testSendEvent3(){
        final SCheckBox box1 = new SCheckBox();
        final SCheckBox box2 = new SCheckBox();

        final PropertyChangeListener propertyChangeListener = new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                box2.setSelected(true);
            }
        };

        box1.addPropertyChangeListener("selected", propertyChangeListener);

        String[] values = {"1"};
        sendEvent(box1, values);

        assertTrue(box2.isSelected());
    }
}


