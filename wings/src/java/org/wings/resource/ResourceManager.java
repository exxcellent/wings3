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
package org.wings.resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.plaf.ResourceDefaults;
import org.wings.util.PropertyDiscovery;

import java.io.IOException;
import java.util.Properties;

/**
 * For accessing static resources
 *
 * @author <a href="mailto:ole@freiheit.com">Ole Langbehn</a>
 */
public class ResourceManager {
    private final transient static Log log = LogFactory.getLog(ResourceManager.class);

    private static final String PROPERTIES_FILENAME = "org/wings/resource/resource.properties";

    public static final ResourceDefaults RESOURCES;

    private ResourceManager() {
    }

    static {
        Properties properties;
        try {
            properties = PropertyDiscovery.loadRequiredProperties(PROPERTIES_FILENAME);
        } catch (IOException e) {
            log.fatal("Cannot open resource properties file at location " + PROPERTIES_FILENAME, e);
            properties = new Properties();
        }
        RESOURCES = new ResourceDefaults(null, properties);
    }

    public static Object getObject(String key, Class clazz) {
        return RESOURCES.get(key, clazz);
    }
}
