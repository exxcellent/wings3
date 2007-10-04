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

package org.wings.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.session.SessionManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Utility class to discover all properties that can be found in retrievable files either via Classpath loader or as file in the
 * <code>WEB-INF</code> directory.
 */
public final class PropertyDiscovery {
    private final static Log log = LogFactory.getLog(PropertyDiscovery.class);
    private static final String WEB_INF = "WEB-INF/";

    /**
     * Loads all properties from either the class path or with modified name in the WEB-INF directory. Requires something to be found.
     * <p/>
     * First looks in the classPath for the file. Then replace all path-separators with . and look for a named file in the WEB-INF
     * directory. Throws an exception if nothings was found and required is true! <p>Example: <code>org/wings/myprops.properties</code>
     * first checks for that file on the classpath and then for a file  <code>WEB-INF/org.wings.myprops.properties</code>.
     *
     * @param propertyPath the name of the properties file i.e. <code>org/wings/myprops.properties</code>
     * @return The Properties loaded
     * @throws java.io.IOException if something fails or nothing was found
     */
    public static Properties loadRequiredProperties(String propertyPath) throws IOException {
        return loadProperties(propertyPath, true);
    }

    /**
     * Loads all properties from either the class path or with modified name in the WEB-INF directory.
     * <p/>
     * First looks in the classPath for the file. Then replace all path-separators with . and look for a named file in the WEB-INF
     * directory. Throws an exception if nothings was found and required is true! <p>Example: <code>org/wings/myprops.properties</code>
     * first checks for that file on the classpath and then for a file  <code>WEB-INF/org.wings.myprops.properties</code>.
     *
     * @param propertyPath the name of the properties file i.e. <code>org/wings/myprops.properties</code>
     * @return The Properties loaded
     */
    public static Properties loadOptionalProperties(String propertyPath) {
        try {
            return loadProperties(propertyPath, false);
        } catch (IOException e) {
            log.info("Error", e);
            return new Properties();
        }
    }

    private static Properties loadProperties(String propertyPath, boolean required) throws IOException {
        final Properties properties = new Properties();
        // first load defaults from classpath, and if it fails, throw Exception
        boolean somethingFound = loadPropertiesFromClasspath(properties, propertyPath);
        // now load from webapp folder, log if fails.
        String webappUrl = WEB_INF + propertyPath.replace('/', '.');
        somethingFound |= (loadPropertiesFromContainer(properties, webappUrl));
        if (required && !somethingFound) {
            throw new IOException("No properties found under: " + propertyPath);
        }

        return properties;
    }

    /**
     * Loads a file from the webapp's dir into a properties file.
     *
     * @param webappUrl the file's url
     * @return true if something was found
     */
    private static boolean loadPropertiesFromContainer(final Properties properties, final String webappUrl) {
        InputStream in;
        try {
            in = SessionManager.getSession().getServletContext().getResourceAsStream(webappUrl);
            properties.load(in);
            in.close();
            if (log.isDebugEnabled()) {
                log.debug("Loaded properties from servlet container file '" + webappUrl + "'");
            }
        } catch (Exception e) {
            log.debug("No custom " + webappUrl + "found. Using defaults.");
            return false;
        }
        return true;
    }

    /**
     * Loads a file from the webapp's classpath into a properties file.
     *
     * @param propertyFileClasspath the file's classpath
     * @return <code>true</code> if something was found and loaded
     */
    private static boolean loadPropertiesFromClasspath(final Properties properties,
                                                       final String propertyFileClasspath) throws IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Enumeration<URL> filesFound = classLoader.getResources(propertyFileClasspath);
        boolean somethingLoaded = false;
        while (filesFound.hasMoreElements()) {
            final URL propertyFile = filesFound.nextElement();
            try {
                InputStream content = propertyFile.openStream();
                properties.load(content);
                content.close();
                somethingLoaded = true;
                if (log.isDebugEnabled()) {
                    log.debug("Loaded properties from classpath file '" + propertyFileClasspath + "'");
                }
            } catch (Exception e) {
                final String error = "Unable to open " + propertyFile.toExternalForm() +
                        " from classpath due " + e + ". Please check deployment!";
                throw new IOException(error);
            }
        }
        return somethingLoaded;
    }
}
