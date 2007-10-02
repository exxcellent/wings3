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

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Default implementation of a {@link ListModel}.
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 */
public class SDefaultListModel
        extends AbstractListModel {
    protected final ArrayList data = new ArrayList(2);


    public SDefaultListModel(List d) {
        data.clear();
        if (d != null) {
            for (int i = 0; i < d.size(); i++)
                data.add(d.get(i));
        }
    }


    public SDefaultListModel(Object[] d) {
        data.clear();
        if (d != null) {
            for (int i = 0; i < d.length; i++)
                data.add(d[i]);
        }
    }


    public SDefaultListModel() {
        // default constructor
    }


    public int getSize() {
        return data.size();
    }


    public Object getElementAt(int i) {
        return data.get(i);
    }

    public int indexOf(Object element) {
        return data.indexOf(element);
    }

    public Enumeration elements() {
        return Collections.enumeration(data);
    }

    public void clear() {
        data.clear();
    }

    public void addElement(Object element) {
        data.add(element);
    }
}


