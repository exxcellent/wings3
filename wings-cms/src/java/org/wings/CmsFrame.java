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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.wings.adapter.CmsAdapter;
import org.wings.conf.Cms;
import org.wings.conf.Templates;
import org.wings.session.SessionManager;

/**
 * <code>CmsFrame<code>.
 * <p/>
 * User: raedler
 * Date: 08.08.2007
 * Time: 09:27:35
 *
 * @author raedler
 * @version $Id
 */
public class CmsFrame extends SFrame {

    private CmsLayout layout = new CmsLayout();

    /**
     * Default constructor
     */
    public CmsFrame() {
        setContentPane(new CmsForm());
        getContentPane().setLayout(layout);

        Cms cms = readCms();

        CmsAdapter adapter = createAdapter(cms);
        adapter.setFrame(this);
        adapter.setCms(cms);

        // Set the resource mapper which is the distinguishable cms adapter.
        SessionManager.getSession().setResourceMapper(adapter);
    }

    /**
     * Reads the wings to cms configuration file and returns an object containing all required data.
     *
     * @return A configuration object with all required data to access the cms.
     */
    private Cms readCms() {
        try {
            JAXBContext ctx = JAXBContext.newInstance(Cms.class, Templates.class);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            return (Cms) unmarshaller.unmarshal(getClass().getClassLoader().getResourceAsStream("wings-2-cms.xml"));
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    private CmsAdapter createAdapter(Cms cms) {
        Class<? extends CmsAdapter> type = cms.getAdapter();

        assert type != null : "Adapter class cannot be null.";

        try {
            if (CmsAdapter.class.isAssignableFrom(type)) {
                Constructor<? extends CmsAdapter> constructor = type.getConstructor(SFrame.class, STemplateLayout.class, Cms.class);
                return (CmsAdapter) constructor.newInstance(this, layout, cms);
            }
            else {
                return (CmsAdapter) type.newInstance();
            }
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public final SComponent add(SComponent c) {
        throw new UnsupportedOperationException("This method won't be supported by CmsFrame. Use add(SComponent c, Object constraint) instead.");
    }

    @Override
    public void add(SComponent c, Object constraint) {
        getContentPane().add(c, constraint);
    }

    @Override
    public SComponent add(SComponent c, int index) {
        throw new UnsupportedOperationException("This method won't be supported by CmsFrame. Use add(SComponent c, Object constraint) instead.");
    }

    @Override
    public void add(SComponent c, Object constraint, int index) {
        throw new UnsupportedOperationException("This method won't be supported by CmsFrame. Use add(SComponent c, Object constraint) instead.");
    }
}
