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
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.MutableComboBoxModel;

/**
 * Default implementation of a model for {@link SComboBox} components.
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 */
public class SDefaultComboBoxModel<E>
        extends DefaultComboBoxModel<E>
        implements MutableComboBoxModel<E> {
    protected Object selectedItem = null;


    public SDefaultComboBoxModel() {}

    public SDefaultComboBoxModel(E items[]) {
        super(items);
    }

    public SDefaultComboBoxModel(Vector<E> vector) {
        super( vector );
    }

    public SDefaultComboBoxModel(Collection<E> collection) {
        super( new Vector<E>( collection ) );
    }
}
