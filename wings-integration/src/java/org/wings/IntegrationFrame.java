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

import org.wings.adapter.IntegrationAdapter;
import org.wings.conf.Integration;
import org.wings.conf.Resource;
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
public class IntegrationFrame extends SFrame {

    private static final long serialVersionUID = 1L;

    private IntegrationLayout layout = new IntegrationLayout();
    
    private String headExtension;
    
    private String bodyExtension;

    /**
     * Default constructor
     */
    public IntegrationFrame() {
        setContentPane(new IntegrationForm());
        getContentPane().setLayout(layout);

        Integration integration = readCms();

        IntegrationAdapter adapter = createAdapter(integration);
        adapter.setFrame(this);
        adapter.setIntegration(integration);

        // Set the resource mapper which is the distinguishable cms adapter.
        SessionManager.getSession().setResourceMapper(adapter);
    }

    /**
     * Reads the wings to cms configuration file and returns an object containing all required data.
     *
     * @return A configuration object with all required data to access the cms.
     */
    private Integration readCms() {
        try {
            JAXBContext ctx = JAXBContext.newInstance(Integration.class, Resource.class);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            return (Integration) unmarshaller.unmarshal(getClass().getClassLoader().getResourceAsStream("integration.xml"));
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    private IntegrationAdapter createAdapter(Integration integration) {
        Class<? extends IntegrationAdapter> type = integration.getAdapter();

        assert type != null : "Adapter class cannot be null.";

        try {
            if (IntegrationAdapter.class.isAssignableFrom(type)) {
                Constructor<? extends IntegrationAdapter> constructor = type.getConstructor(SFrame.class, Integration.class, STemplateLayout.class);
                return (IntegrationAdapter) constructor.newInstance(this, integration, layout);
            }
            else {
                return (IntegrationAdapter) type.newInstance();
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
    
    public String getHeadExtension() {
        return headExtension;
    }

    public void setHeadExtension(String headExtension) {
        this.headExtension = headExtension;
    }

    public String getBodyExtension() {
        return bodyExtension;
    }

    public void setBodyExtension(String bodyExtension) {
        this.bodyExtension = bodyExtension;
    }
}
