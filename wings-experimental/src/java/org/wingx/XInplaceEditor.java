/*
 * Copyright 2006 wingS development team.
 *
 * This file is part of wingS (http://wingsframework.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package org.wingx;

import java.io.IOException;
import org.wings.SLabel;
import org.wings.event.SDocumentListener;
import org.wings.io.StringBuilderDevice;
import org.wings.plaf.css.Utils;
import org.wings.text.DefaultDocument;
import org.wings.text.SDocument;

/**
 * A in place editor is a simple label that becomes editable when activated
 * by a mouse click.
 * @author Christian Schyma
 */
public class XInplaceEditor extends SLabel implements XInplaceEditorInterface {
    
    /**
     * number of editor columns
     */
    private int cols = 40;
    
    /**
     * numer of editor rows
     */
    private int rows = 1;
    
    /**
     * DWR request timeout in ms
     */
    private int timeout = 5000;
    
    /**
     * intermediate text of a DWR (Ajax) request
     */
    private SDocument ajaxDocument;
    
    /**
     * what to display, when the intermediateDocument gets ""?
     */
    private String emptyString = "click here to name";
    
    /**
     * text to be displayed while hovering with the mouse over the editable text
     */
    private String clickNotificationText = "click here to edit";
    
    /**
     * Creates a new <code>XInplaceEditor</code> instance with the specified text.
     *
     * @param text The text to be displayed by the label.
     */
    public XInplaceEditor(String text) {
        super(text);
        init();
    }
    
    /**
     * Creates a new <code>XInplaceEditor</code> instance with the specified text
     * and number of editor columns.
     *
     * @param text The text to be displayed by the label.
     * @param cols number of editor columns
     */
    public XInplaceEditor(String text, int cols) {
        super(text);
        init();
        this.cols = cols;
    }
    
    /**
     * Creates a new <code>XInplaceEditor</code> instance with the specified text,
     * numer of editor columns and rows.
     *
     * @param text The text to be displayed by the label.
     * @param cols number of editor columns
     * @param rows number of editor rows
     */
    public XInplaceEditor(String text, int cols, int rows) {
        super(text);
        init();
        this.cols = cols;
        this.rows = rows;
    }
    
    private void init() {
        ajaxDocument = new DefaultDocument(text);
        setWordWrap(true);
    }
    
    /**
     * @return formatted version of the intermediate text. Used by the
     * client-side JavaScript.
     */
    private String getAjaxFormattedText() {
        StringBuilderDevice device = new StringBuilderDevice(32);
        try {
            Utils.quote(device, ajaxDocument.getText(), true, !isWordWrap(), false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return device.toString();
    }
        
    public String getAjaxText() {
        return ajaxDocument.getText();
    }
        
    public String setAjaxText(String text) {
        this.ajaxDocument.setText(text);
        this.text = text;
        
        // to avoid a invisible label
        if (text.compareTo("") == 0) {
            return this.emptyString;
        } else {
            return this.getAjaxFormattedText();
        }
    }
    
    public void setText(String t) {
        super.setText(t);
        if (this.ajaxDocument != null) {
            setAjaxText(t);
        }
    }
    
    /**
     * set number of columns of the editor
     */
    public void setCols(int cols) {
        int oldValue = this.cols;
        this.cols = cols;
        reloadIfChange(cols, oldValue);
    }
    
    /**
     * @see #setCols
     */
    public int getCols() {
        return cols;
    }
    
    /**
     * set number of rows of the editor
     */
    public void setRows(int rows) {
        int oldValue = this.rows;
        this.rows = rows;
        reloadIfChange(rows, oldValue);
    }
    
    /**
     * @see #setRows
     */
    public int getRows() {
        return rows;
    }
    
    /**
     * Set Ajax request timeout.
     * @param timeout in ms
     */
    public void setTimeout(int timeout) {
        int oldValue = this.timeout;
        this.timeout = timeout;
        reloadIfChange(timeout, oldValue);
    }
    
    /**
     * @see #setTimeout(int timeout)
     */
    public int getTimeout() {
        return timeout;
    }
    
    public SDocumentListener[] getAjaxDocumentListeners() {
        return ajaxDocument.getDocumentListeners();
    }
    
    public void addAjaxDocumentListener(SDocumentListener listener) {
        ajaxDocument.addDocumentListener(listener);
    }
    
    public void removeAjaxDocumentListener(SDocumentListener listener) {
        ajaxDocument.removeDocumentListener(listener);
    }
    
    /**
     * @param text to be displayed while hovering with the mouse over
     * the editable text
     */
    public void setClickNotificationText(String text) {
        String oldValue = this.clickNotificationText;
        this.clickNotificationText = text;
        reloadIfChange(text, oldValue);
    }
    
    /**
     * @return returns the text to be displayed while hovering with the
     * mouse over the editable text
     */
    public String getClickNotificationText() {
        return this.clickNotificationText;
    }
       
}
