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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.template.StringTemplateSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Default layout for all {@link SRootContainer} derivates.
 * <p/>
 * There might be situations when you want to use a custom SRootLayout. Why?
 * Because by setting an SFrame's layout you can change the look of a whole
 * application. Since only the object called "frame" will be replaced with
 * the content element, you can statically define XHTML elements such as a
 * border, a title or a footer line by defining your own template with a STemplateLayout.
 */
public class SRootLayout extends STemplateLayout {
    private final static Log LOG = LogFactory.getLog(SRootLayout.class);

    /**
     * Use the default template.
     */
    public SRootLayout() {
        try {
			setTemplate(new StringTemplateSource(
			        "<object name=\"frame\"></object>\n"
			        +"<object name=\"windows\"></object>"));
		} catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
    }

    /**
     * Read the template from a file.
     *
     * @throws java.io.IOException
     */
    public SRootLayout(String tmplFileName) throws IOException {
        setTemplate(new File(tmplFileName));
    }

    /**
     * Read the template from a file.
     *
     * @throws java.io.IOException
     */
    public SRootLayout(File tmplFile) throws IOException {
        setTemplate(tmplFile);
    }

    /**
     * Read the template from an URL.
     * The content is cached.
     */
    public SRootLayout(URL url) throws java.io.IOException {
        setTemplate(url);
    }

    public void addComponent(SComponent c, Object constraint, int index) {}

    public void removeComponent(SComponent comp) {}

    public SComponent getComponent(String name) {
        if ("frame".equals(name)) {
            int count = container.getComponentCount();
            if (count > 0) {
                return container.getComponent(0);
            }
            else {
                throw new IllegalStateException("The root container contains " + count + " components but a root container can not contain more than 1 component.");
            }
        }
        else if ("windows".equals(name)) {
            if (container instanceof SRootContainer) {
                return ((SRootContainer) container).getWindowsPane();
            }
        }
        return null;
    }

    // this has been overridden as noop in STemplateLayout
    // give it back the original behaviour
    public void setContainer(SContainer container) {
        this.container = container;
    }
}
