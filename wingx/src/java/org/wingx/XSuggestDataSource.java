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

/**
 *
 * @author Christian Schyma
 */
public interface XSuggestDataSource {
    
    /**
     * Generates and returns a list of suggestions.
     * @param lookupText string to filter the suggestions
     */
    List generateSuggestions(String lookupText);
    
    /**
     * Sets the intermediate text of the suggest field.
     * Used by an Ajax call from CG only.
     */
    public void setAjaxText(String newText);
        
    /**
     * Returns the intermediate text of the suggest field.
     * Used by an Ajax call from CG only.
     */
    public String getAjaxText();        
    
}
