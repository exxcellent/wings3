/*
 * Copyright 2007 wingS development team.
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

/**
 *
 * @author Christian Schyma
 */
public interface XInplaceEditorInterface {
       
    /**
     * Set the intermediate text. Used by the client-side JavaScript.
     * @param text
     * @return formatted text to display at the client-side
     */
    public String setAjaxText(String text);
    
    /**
     * Return the indermediate text. Used by the client-side JavaScript.
     * @return intermediate text
     */
    public String getAjaxText();
    
}
