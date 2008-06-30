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

import org.wings.io.Device;
import org.wings.template.CmsTemplateParseContext;
import org.wings.template.parser.ParseContext;
import org.wings.template.parser.PositionReader;
import org.wings.template.parser.SGMLTag;
import org.wings.template.parser.SpecialTagHandler;
import org.wingx.XDivision;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * <code>DebugTagHandler<code>.
 * <p/>
 * User: raedler
 * Date: 03.12.2007
 * Time: 14:35:57
 *
 * @author raedler
 * @version $Id
 */
public class DebugTagHandler implements SpecialTagHandler {
    boolean close_is_missing = false;

    long startPos;
    long endPos;
    Map properties;
    String name;

    public long getTagStart() {
        return startPos;
    }

    public long getTagLength() {
        return endPos - startPos;
    }

    public void executeTag(ParseContext context, InputStream input) throws Exception {
        CmsTemplateParseContext tcontext = (CmsTemplateParseContext) context;
        Device sink = tcontext.getDevice();

        /*
         * get the component that is associtated with this name. This has
         * been set as Layout Manager Constraint.
         */
        SComponent[] components = tcontext.getComponents();

        StringBuilder sb = new StringBuilder();
        for (long i = getTagLength(); i > 0; i--) {
            sb.append((char) input.read());
        }

        SPanel container = new SPanel(new SFlowDownLayout());
        XDivision division;
        for (SComponent c : components) {
            division = new XDivision();
            division.add(c);
            container.add(division);
        }
        container.write(sink);
    }

    public SGMLTag parseTag(ParseContext context, PositionReader input, long startPosition, SGMLTag startTag)
            throws IOException {

        final String startTagName = startTag.getName();
        final String endTagName = "/" + startTagName;

        /*
         * parse the full tag to get all parameters
         * (i.e. an optional 'format'-parameter)
         * and to place the Reader at the position
         * after the closing '>'
         */
        startTag.parse(input);

        /*
         * The Offset is the space the reader skipped
         * before it reached the opening '<'
         *   <!-- a comment --> some garbage <DATE>
         * ^----- offset --------------------^
         */
        startPos = startPosition + startTag.getOffset();

        /*
         * get properties
         */
        properties = startTag.getAttributes();

        name = startTag.value("NAME", null);
        if (name == null)
            return null;

        context.addContainedComponent(name);

        endPos = input.getPosition();  // in case </component> is missing

        while (!startTag.finished()) {
            startTag = new SGMLTag(input, true);
            if (startTag.isNamed(endTagName) || startTag.isNamed(startTagName))
                break;
        }

        // Either EOF or newly opened COMPONENT (unexpectedly)
        if (startTag.finished() || startTag.isNamed(startTagName)) {
            close_is_missing = true;
        }
        else {
            // The current Position is after the closing '>'
            endPos = input.getPosition();
        }

        // remove properties, which are not necessary for the PropertyManager
        properties.remove("NAME");
        properties.remove("TYPE");

        return startTag;
    }
}
