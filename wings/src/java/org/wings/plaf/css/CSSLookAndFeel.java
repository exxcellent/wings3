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
package org.wings.plaf.css;

import org.wings.session.Browser;
import org.wings.session.SessionManager;
import org.wings.util.PropertyDiscovery;

import java.io.IOException;
import java.util.Properties;

public class CSSLookAndFeel  extends org.wings.plaf.LookAndFeel {
    private static final long serialVersionUID = 1L;
    private static final String PROPERTIES_DEFAULTFILE_PREFIX = "org/wings/plaf/css/default";

    public CSSLookAndFeel() throws IOException {
        super(loadProperties());
    }

    private static Properties loadProperties() throws IOException {
        final StringBuilder propertyFile = new StringBuilder();

        // check for default PLAF properties under org/wings/plaf/css/default.properties
        propertyFile.append(PROPERTIES_DEFAULTFILE_PREFIX).append(".properties");
        Properties properties = PropertyDiscovery.loadRequiredProperties(propertyFile.toString());

        // check for browser dependent properties under org/wings/plaf/css/default_msie.properties
        final Browser userAgent = SessionManager.getSession().getUserAgent();
        final String browserType = userAgent.getBrowserType().getName().toLowerCase();
        propertyFile.setLength(0);
        propertyFile.append(PROPERTIES_DEFAULTFILE_PREFIX).append("_").append(browserType).append(".properties");
        properties.putAll(PropertyDiscovery.loadOptionalProperties(propertyFile.toString()));

        // check for browser dependent and VERSION dependen properties under org/wings/plaf/css/default_msie7.properties
        propertyFile.setLength(0);
        propertyFile.append(PROPERTIES_DEFAULTFILE_PREFIX).append("_").append(browserType).append(userAgent.getMajorVersion()).append(".properties");
        properties.putAll(PropertyDiscovery.loadOptionalProperties(propertyFile.toString()));

        return properties;
    }
}


