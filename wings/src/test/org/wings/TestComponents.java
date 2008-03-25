package org.wings;

import static org.junit.Assert.*;
import org.junit.Test;
import org.wings.*;
import org.wings.test.WingsTestCase;

import javax.swing.tree.TreePath;
import java.awt.*;


public class TestComponents extends WingsTestCase {

    @Test
    public void testSButton(){
        SButton button = new SButton();
        button.setText("test");

        assertEquals("test", button.getText());
    }



    @Test
    public void testSButton2(){
        SButton button = new SButton();
        button.setText("test");

        assertEquals("test-foo", button.getText());
    }


    @Test
    public void testSTextField(){
        STextField textfield = new STextField();
        textfield.setText("text-textfield");

        assertEquals("text-label", textfield.getText());
    }

    @Test
    public void testSLabel(){
        SLabel label2 = new SLabel();
        label2.setVerticalTextPosition(5);

        assertEquals(5, label2.getVerticalTextPosition());
    }


    @Test
    public void testSFrame(){
        SFrame frame = new SFrame();
        frame.setTitle("test");

        assertEquals("test", frame.getTitle());
    }


    @Test
    public void testSLabel2(){
        SLabel label = new SLabel();
        Color color = Color.RED;
        label.setForeground(color);

        assertEquals(color, label.getForeground());
    }

    

    @Test
    public void testSendEventWithSCheckbox(){
        SCheckBox checkbox = new SCheckBox();
        checkbox.setSelected(false);

        String[] values = {"1"};
        sendEvent(checkbox, values);

        assertTrue(checkbox.isSelected());
    }



    @Test
    public void testSendEventWithSButton(){
        SButton button = new SButton();

        String[] values = {"1"};
        sendEvent(button, values);

        assertNotNull(button);
    }

    

    @Test
    public void testSendEventWithSScrollBar(){
        SScrollBar scrollbar = new SScrollBar();

        String[] values = {"20"};
        sendEvent(scrollbar, values);

        assertEquals(scrollbar.getValue(), Integer.parseInt(values[0]));
    }


    @Test(expected=IllegalArgumentException.class)
    public void testSendEventWithSLabel(){
        SLabel label = new SLabel();

        String[] values = {"foo"};
        sendEvent(label, values);

        assertNotNull(label);
    }

    @Test
    public void testIntermediateEventSTree(){
       STree tree = new STree();

       String[] values = {"a1"};
       sendEvent(tree, values);
       Object oldPath = tree.getSelectionPath().getPath();

       String[] values2 = {"b2"};
       sendEvent(tree, values2);

       Object newPath = tree.getSelectionPath().getPath();

       assertNotSame(oldPath, newPath);
        
    }

 }
