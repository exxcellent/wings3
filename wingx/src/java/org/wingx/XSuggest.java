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

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.ReloadManager;
import org.wings.SDimension;
import org.wings.SResourceIcon;
import org.wings.STextField;
import org.wings.event.SDocumentListener;
import org.wings.text.DefaultDocument;
import org.wings.text.SDocument;

/**
 * Enhanced STextField that supports the user input by displaying suggestions.
 * @author Christian Schyma
 */
public class XSuggest extends STextField implements XSuggestDataSource {

    private final transient static Log log = LogFactory.getLog(XSuggest.class);

    /**
     * data source from which suggestions are generated
     */
    private XSuggestDataSource dataSource = null;

    /**
     * Ajax request timeout in ms
     */
    private int timeout = 10000;

    /**
     * @see org.wingx.XSuggest#setInputDelay(int delay)
     */
    private int inputDelay = 500;

    /**
     * an icon to indicate the Ajax activity
     */
    private SResourceIcon activityIcon = new SResourceIcon("org/wings/icons/AjaxActivityIndicatorSmall.gif");

    /**
     * @see org.wingx.XSuggest#setSuggestBoxWidth(SDimension dim)
     */
    private SDimension suggestBoxWidth = new SDimension(SDimension.AUTO, SDimension.AUTO);
    
    {
        activityIcon.getId(); // hack to externalize
    }

    /**
     * Intermediate document which holds the current text of the suggest field
     * at the client. This text can differ from the original text (this.text)
     * due to the use of Ajax.
     */
    private SDocument ajaxDocument = new DefaultDocument();

    /**
     * Creates a text field that can display suggestions.
     */
    public XSuggest() {
    }

    /**
     * Creates a text field that can display suggestions.
     * @param text initial content of the text field
     */
    public XSuggest(String text) {
        super(text);
    }

    /**
     * set a new data source from which suggestions are generated
     * @param source
     */
    public void setDataSource(XSuggestDataSource source) {
        this.dataSource = source;
    }

    /**
     * returns the currently used data source
     * @param data source or null if no is set
     * @see #setDataSource(XSuggestDataSource source)
     */
    public XSuggestDataSource getDataSource() {
        return this.dataSource;
    }

    public void setAjaxText(String newText) {
        ajaxDocument.setText(newText);
    }

    public String getAjaxText() {
        return ajaxDocument.getText();
    }

    /**
     * Returns an array of all the <code>SDocumentListener</code>s added
     * to this component with <code>addAjaxDocumentListener</code>.
     *
     * @return all of the <code>SDocumentListener</code>s added or an empty
     *         array if no listeners have been added
     */
    public SDocumentListener[] getAjaxDocumentListeners() {
        return ajaxDocument.getDocumentListeners();
    }

    /**
     * Adds the specified <code>SDocumentListener</code> to the suggest field.
     * An event is fired immediately (using Ajax) whenever the field is
     * changed at the client.
     *
     * @param l the <code>SDocumentListener</code> to add
     */
    public void addAjaxDocumentListener(SDocumentListener listener) {
        ajaxDocument.addDocumentListener(listener);
    }

    /**
     * Removes a <code>SDocumentListener</code>.
     *
     * @param l the <code>SDocumentListener</code> to remove
     */
    public void removeAjaxDocumentListener(SDocumentListener listener) {
        ajaxDocument.removeDocumentListener(listener);
    }

    /**
     * Set Ajax request timeout.
     * @param timeout in ms
     */
    public void setTimeout(int timeout) {
        int oldTimeout = this.timeout;
        this.timeout = timeout;
        reloadIfChange(oldTimeout, timeout);
    }

    /**
     * @see org.wingx.XSuggest#setTimeout(int timeout)
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Fast typists can produce a lot of requests in a short time; this delay
     * limits the requests to a certain time.
     * @param delay in ms
     */
    public void setInputDelay(int delay) {
        int oldDelay = this.inputDelay;
        this.inputDelay = delay;
        reloadIfChange(oldDelay, delay);
    }

    /**
     * @see org.wingx.XSuggest#setInputDelay(int delay)
     */
    public int getInputDelay() {
        return inputDelay;
    }

    /**
     * Configures the width of the suggest box.  There are three possibilites:<br>
     * - <code>SDimension.AUTO</code> automatically set the size in accordance to the content (default)<br>
     * - <code>SDimension.INHERIT</code> inherits width from the suggest input box<br>
     * - integer value
     * @param dim width of suggest box, height is ignored     
     */
    public void setSuggestBoxWidth(SDimension dim) {  // setSuggestBoxWidth inherit, auto, pixel width
        SDimension oldSuggestBoxWidth = this.suggestBoxWidth;
        this.suggestBoxWidth = dim;
        reloadIfChange(oldSuggestBoxWidth, dim);                
    }
    
    public SDimension getSuggestBoxWidth() {
        return this.suggestBoxWidth;
    }
    
    /**
     * This method is used by DWR. It returns to the client a list of
     * suggestions in accordance with the lookupText. The list contains pairs,
     * the first entry represents the suggestion to display, the second entry
     * the value to be used on selection.
     * @param lookupText
     * @return suggestions list
     */
    public List generateSuggestions(String lookupText) {
        setAjaxText(lookupText);
        if (getDataSource() != null) {
            return getDataSource().generateSuggestions(lookupText);
        } else{
            return null;
        }
    }
      
}


