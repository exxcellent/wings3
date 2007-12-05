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

import org.wings.CmsLayout;
import org.wings.DebugTagHandler;
import org.wings.MacroTagHandler;
import org.wings.SLayoutManager;
import org.wings.io.Device;
import org.wings.template.CmsTemplateParseContext;
import org.wings.template.TemplateSource;
import org.wings.template.RangeTagHandler;
import org.wings.template.parser.PageParser;

import java.io.IOException;

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
public class CmsLayoutCG implements org.wings.plaf.CmsLayoutCG {

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


    private void write(Device device, CmsLayout layout)
            throws IOException {
        final TemplateSource source = layout.getTemplateSource();

        if (source == null) {
            device.print("The cms server is not reachable at the moment or the connection data is wrong. Please check your <em>wings-2-cms.xml</em>");
        }
        else {
            parser.process(source, new CmsTemplateParseContext(device, layout));
        }
    }

    /**
     * @param device  the device to write the code to
     * @param manager the layout manager
     * @throws IOException
     */
    public void write(Device device, SLayoutManager manager)
            throws IOException {
        write(device, (CmsLayout) manager);
    }
}
