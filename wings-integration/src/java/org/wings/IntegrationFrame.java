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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.wings.adapter.AbstractIntegrationAdapter;
import org.wings.adapter.IntegrationAdapter;
import org.wings.conf.Integration;
import org.wings.conf.Resource;
import org.wings.session.SessionManager;

/**
 * <code>IntegrationFrame<code>.
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
    
    protected IntegrationAdapter adapter;

    /**
     * Default constructor
     */
    public IntegrationFrame() {
        Integration integration = readIntegration();
        
        adapter = createAdapter(integration);
        adapter.setFrame(this);
        adapter.setIntegration(integration);

        // Set the resource mapper which is the distinguishable cms adapter.
        SessionManager.getSession().setResourceMapper(adapter);
        
        initialize();
    }
    
    protected void initialize() {
        adapter.initialize();
    }

    /**
     * Reads the integration.xml configuration file and returns an object containing all required data.
     *
     * @return A configuration object with all required data.
     */
    private Integration readIntegration() {
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

    protected IntegrationAdapter createAdapter(Integration integration) {
        Class<? extends IntegrationAdapter> type = integration.getAdapter();

        assert type != null : "Adapter class cannot be null.";

        try {
            if (AbstractIntegrationAdapter.class.isAssignableFrom(type)) {
                Constructor<? extends IntegrationAdapter> constructor = type.getConstructor(IntegrationFrame.class, Integration.class);
                return (IntegrationAdapter) constructor.newInstance(this, integration);
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
    
    public Object getResource(String param) throws IOException {
        return getResource(new String[] {param});
    }

    public Object getResource(String[] params) throws IOException {
        return adapter.getResource(params);
    }

    public Object getResource(String type, String[] params) throws IOException {
        return adapter.getResource(type, params);
    }

    public org.wings.Resource mapResource(String url) {
        return adapter.mapResource(url);
    }
}
