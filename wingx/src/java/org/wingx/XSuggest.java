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
import java.util.Map;

import org.wings.SDimension;
import org.wings.SResourceIcon;
import org.wings.STextField;
import org.wingx.plaf.SuggestCG;

/**
 * Enhanced STextField that supports the user input by displaying suggestions.
 * @author Christian Schyma
 */
public class XSuggest extends STextField implements XSuggestDataSource {

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
        XSuggestDataSource oldVal = this.dataSource;
        this.dataSource = source;
        propertyChangeSupport.firePropertyChange("dataSource", oldVal, this.dataSource);
    }

    /**
     * returns the currently used data source
     * @see #setDataSource(XSuggestDataSource source)
     */
    public XSuggestDataSource getDataSource() {
        return this.dataSource;
    }

    /**
     * Set Ajax request timeout.
     * @param timeout in ms
     */
    public void setTimeout(int timeout) {
        int oldTimeout = this.timeout;
        this.timeout = timeout;
        reloadIfChange(oldTimeout, timeout);
        propertyChangeSupport.firePropertyChange("timeout", oldTimeout, this.timeout);
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
        propertyChangeSupport.firePropertyChange("inputDelay", oldDelay, this.inputDelay);
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
        propertyChangeSupport.firePropertyChange("suggestBoxWidth", oldSuggestBoxWidth, this.suggestBoxWidth);
    }

    public SDimension getSuggestBoxWidth() {
        return this.suggestBoxWidth;
    }

    /**
     * Obtaines the list of suggestions from the data source, if there is one.
     * @see {org.wingx.XSuggestDataSource.generateSugestions()}
     * @param lookupText
     * @return suggestions list with map entries or null, if there is no data source
     */
    public List<Map.Entry<String,String>> generateSuggestions(String lookupText) {
        if (getDataSource() != null) {
            return getDataSource().generateSuggestions(lookupText);
        } else{
            return null;
        }
    }

    @Override
    public void processLowLevelEvent(String action, String[] values) {
        String value = values[0];
        if (value.startsWith("q:")) {
            String query = value.substring(2);
            List<Map.Entry<String,String>> suggestions = generateSuggestions(query);
            update(((SuggestCG)getCG()).getSuggestionsUpdate(this, query, suggestions));
        }
        else
            super.processLowLevelEvent(action, values);
    }
}
