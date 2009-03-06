/*
 * $Id: SOURCEHEADER 1313 2007-09-11 13:25:48Z hengels $
 * (c) Copyright 2007 osbl development team.
 *
 * This file is part of the OSBL (http://concern.sf.net).
 *
 * The OSBL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package calendar;

import org.wings.sdnd.DefaultTransferable;

import java.awt.datatransfer.DataFlavor;
import java.util.Collection;

public class AppointmentTransferable extends DefaultTransferable {
    private static DataFlavor[] flavors;
    private Collection collection;

    static {
        try
        {
            flavors = new DataFlavor[] {
                new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=java.util.Collection")
            };
        } catch(ClassNotFoundException e) {
        }
    }

    public AppointmentTransferable(Collection appointments) {
        super(flavors);

        this.collection = appointments;
    }

    protected Object getDataForClass(DataFlavor dataFlavor, Class<?> aClass) {
        if(dataFlavor.getPrimaryType().equals("application")) {
            // application/x-java-jvm-local-objectref
            if(dataFlavor.getMimeType().startsWith(DataFlavor.javaJVMLocalObjectMimeType)) {
                return collection;
            }
        }

        return null;
    }
}
