/*
 * Copyright 2000,2005 wingS development team.
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
package org.wings;

import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 * Default implementation of a {@link ListModel}.
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 */
public class SDefaultListModel<E>
        extends DefaultListModel<E> {

    public SDefaultListModel(Collection<E> collection) {
    	for( E element : collection )
    		addElement(element);
    }


    public SDefaultListModel(E[] elements) {
    	for( E element : elements )
    		addElement(element);
    }


    public SDefaultListModel() {
        // default constructor
    }
}


