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

import org.wings.*;
import org.wings.io.Device;
import org.wings.io.NullDevice;
import org.wings.template.IntegrationTemplateParseContext;
import org.wings.template.TemplateSource;
import org.wings.template.RangeTagHandler;
import org.wings.template.parser.PageParser;

import java.io.IOException;
import java.util.*;

/**
 * <code>CmsLayoutCG<code>.
 * <p/>
 * User: raedler
 * Date: 08.08.2007
 * Time: 13:00:00
 *
 * @author raedler
 * @version $Id
 */
public class IntegrationLayoutCG implements org.wings.plaf.IntegrationLayoutCG {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final PageParser parser = new PageParser();

    /**
     * The parser looks for the '<OBJECT></OBJECT>' - tags.
     */
    static {
        parser.addTagHandler("OBJECT", MacroTagHandler.class);
        parser.addTagHandler("DEBUG", DebugTagHandler.class);
    }

    private void write(Device device, IntegrationLayout layout)
            throws IOException {
        final TemplateSource source = layout.getTemplateSource();

        if (source == null) {
            device.print("The cms server is not reachable at the moment or the connection data is wrong. Please check your <em>wings-2-cms.xml</em>");
        }
        else {
            IntegrationTemplateParseContext context = new IntegrationTemplateParseContext(device, layout);
            parser.process(source, context);
        }
    }

    /**
     * @param device  the device to write the code to
     * @param manager the layout manager
     * @throws IOException
     */
    public void write(Device device, SLayoutManager manager)
            throws IOException {
        write(device, (IntegrationLayout) manager);
    }

    public static Set<String> getContainedComponents(IntegrationLayout layout) throws IOException {
        final TemplateSource source = layout.getTemplateSource();
        IntegrationTemplateParseContext context = new IntegrationTemplateParseContext(new NullDevice(), layout);
        return IntegrationLayoutCG.parser.getContainedComponents(source, context);
    }

    public static Map<String, Map<String, String>> getComponentProperties(IntegrationLayout layout) throws IOException {
        final TemplateSource source = layout.getTemplateSource();
        IntegrationTemplateParseContext context = new IntegrationTemplateParseContext(new NullDevice(), layout);
        return IntegrationLayoutCG.parser.getComponentProperties(source, context);
    }
}
