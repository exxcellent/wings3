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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.SComponent;
import org.wings.SWindow;
import org.wings.header.Header;
import org.wings.header.SessionHeaders;
import org.wings.io.StringBuilderDevice;
import org.wings.plaf.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>WindowCG</code>.
 * User: raedler
 * Date: Oct 5, 2007
 * Time: 1:30:02 AM
 *
 * @author raedler
 * @version $Id
 */
public class WindowCG extends FormCG implements org.wings.plaf.WindowCG {

    private final static Log log = LogFactory.getLog(WindowCG.class);

    protected final List<Header> headers = new ArrayList<Header>();

    public WindowCG() {
        headers.add(Utils.createExternalizedCSSHeader("org/wings/js/yui/container/assets/container.css"));
    }

    public void installCG(SComponent component) {
        SessionHeaders.getInstance().registerHeaders(headers);
        super.installCG(component);
    }

    public void uninstallCG(SComponent component) {
        super.uninstallCG(component);
        SessionHeaders.getInstance().deregisterHeaders(headers);
    }

    public Update getWindowAddedUpdate(final SWindow window) {
        return new WindowAddedUpdate(window);
    }

    protected class WindowAddedUpdate extends AbstractUpdate {

        public WindowAddedUpdate(final SWindow window) {
            super(window);
        }

        public Handler getHandler() {
            String htmlCode = "";
            String exception = null;

            try {
                StringBuilderDevice htmlDevice = new StringBuilderDevice();
                write(htmlDevice, component);
                htmlCode = htmlDevice.toString();
            } catch (Throwable t) {
                log.fatal("An error occured during rendering", t);
                exception = t.getClass().getName();
            }

            UpdateHandler handler = new UpdateHandler("addChildComponent");
            handler.addParameter(component.getParent().getName());
            handler.addParameter(htmlCode);
            if (exception != null) {
                handler.addParameter(exception);
            }
			return handler;
        }
    }

    public Update getWindowClosedUpdate(final SWindow window) {
        return new WindowClosedUpdate(window);
    }

    protected class WindowClosedUpdate extends AbstractUpdate {

        public WindowClosedUpdate(final SWindow window) {
            super(window);
        }

        public Handler getHandler() {
            UpdateHandler handler = new UpdateHandler("remove");
            handler.addParameter(component.getName() + "_c");
            return handler;
        }
    }
}
