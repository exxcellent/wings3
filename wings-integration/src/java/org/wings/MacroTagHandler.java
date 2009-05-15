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
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

import org.wings.io.Device;
import org.wings.io.StringBuilderDevice;
import org.wings.macro.MacroContainer;
import org.wings.macro.MacroContext;
import org.wings.macro.impl.VelocityMacroProcessor;
import org.wings.plaf.IntegrationTableCG;
import org.wings.plaf.ComponentCG;
import org.wings.plaf.IntegrationComponentCG;
import org.wings.template.IntegrationTemplateParseContext;
import org.wings.template.PropertyManager;
import org.wings.template.parser.ParseContext;
import org.wings.template.parser.PositionReader;
import org.wings.template.parser.SGMLTag;
import org.wings.template.parser.SpecialTagHandler;

/**
 * <code>MacroTagHandler<code>.
 * <p/>
 * User: raedler
 * Date: 08.08.2007
 * Time: 14:35:57
 *
 * @author raedler
 * @version $Id
 */
public class MacroTagHandler implements SpecialTagHandler {
    boolean close_is_missing = false;

    long startPos;
    long endPos;
    Map<String, String> properties;
    String name;
    private String macroTemplate;

    public long getTagStart() {
        return startPos;
    }

    public long getTagLength() {
        return endPos - startPos;
    }

    private static void copy(Reader in, Device device, long length, char[] buf)
            throws IOException {
        int len;
        boolean limited = (length >= 0);
        int rest = limited ? (int) length : buf.length;

        while (rest > 0 &&
                (len = in.read(buf, 0,
                        (rest > buf.length) ? buf.length : rest)) > 0) {
            device.print(buf, 0, len);
            if (limited) rest -= len;
        }
    }

    public void executeTag(ParseContext context, Reader input) throws Exception {
        IntegrationTemplateParseContext tcontext = (IntegrationTemplateParseContext) context;
        Device sink = tcontext.getDevice();

        /*
         * get the component that is associated with this name. This has
         * been set as Layout Manager Constraint.
         */
        SComponent c = tcontext.getComponent(name);
        if (c == null) {
            sink.print("<!-- Template: Component '" + name + "' is unknown -->");
            input.skip(getTagLength());
        }
        else {
            properties(c);

            if (macroTemplate == null) {
                StringBuilderDevice d = new StringBuilderDevice();
                copy(input, d, getTagLength(), new char[512]);
                macroTemplate = d.toString();
                macroTemplate = macroTemplate.substring(macroTemplate.indexOf('>') + 1, macroTemplate.lastIndexOf('<'));
            }
            else
                input.skip(getTagLength());

//            String content = sb.toString().replaceAll(System.getProperty("line.separator"), "");
//            content = content.replaceAll("\n", "");
//            Pattern pattern = Pattern.compile("<object[\\s]*name[\\s]*=[\\s]*\\\".*\\\"[\\s]*>\\n*(.*\\n)*\\n*<[\\s]*/object[\\s]*>");
//            Matcher matcher = pattern.matcher(sb.toString());
//
//            String macroTemplate = null;
//            if (matcher.matches()) {
//                macroTemplate = matcher.group(0);
//                macroTemplate = macroTemplate.substring(macroTemplate.indexOf('>') + 1, macroTemplate.lastIndexOf('<'));
//            }


            if (macroTemplate != null && !"".equals(macroTemplate.trim())) {
                VelocityMacroProcessor macroProcessor = VelocityMacroProcessor.getInstance();

                MacroContainer macroContainer = macroProcessor.buildMacro(macroTemplate);

                MacroContext ctx = new MacroContext();
                ctx.setDevice(sink);
                ctx.setComponent(c);
                ctx.put(name, c);

                macroContainer.setContext(ctx);

                if (c instanceof STable) {
                    IntegrationTableCG cg = new IntegrationTableCG();
                    cg.setMacros(macroContainer);
                    if (c.getClientProperty("cg") == null)
                        c.putClientProperty("cg", c.getCG());
                    c.setCG(cg);
                    c.write(sink);
                } else {
                    ComponentCG cg = c.getCG();
                    if (cg instanceof IntegrationComponentCG) {
                       ((IntegrationComponentCG) cg).setMacros(macroContainer);
                    } else {
                        cg = new IntegrationComponentCG();
                        ((IntegrationComponentCG) cg).setMacros(macroContainer);
                        if (c.getClientProperty("cg") == null)
                            c.putClientProperty("cg", c.getCG());
                        c.setCG(cg);
                    }
                    c.write(sink);
                }
            }
            else {
                ComponentCG cg = (ComponentCG) c.getClientProperty("cg");
                if (cg != null)
                    c.setCG(cg);
                c.write(sink);
            }
        }
    }

    private void properties(SComponent c) {
        if (properties.size() > 0) {
            PropertyManager propManager =
                    TemplateIntegrationLayout.getPropertyManager(c.getClass());

            if (propManager != null) {
                Iterator<String> iter = properties.keySet().iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    String value = properties.get(key);
                    propManager.setProperty(c, key, value);
                }
            }
        }
    }

    public SGMLTag parseTag(ParseContext context, PositionReader input, long startPosition, SGMLTag startTag)
            throws IOException {
        IntegrationTemplateParseContext tcontext = (IntegrationTemplateParseContext) context;

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

        tcontext.addContainedComponent(name);

        endPos = input.getPosition();  // in case </component> is missing

        while (!startTag.finished()) {
            startTag = new SGMLTag(input, true);
            if (startTag.isNamed(endTagName) || startTag.isNamed(startTagName))
                break;
        }

        // Either EOF or newly opened COMPONENT (unexpectedly)
        if (startTag.finished() || startTag.isNamed(startTagName)) {
            close_is_missing = true;
        } else {
            // The current Position is after the closing '>'
            endPos = input.getPosition();
        }

        // remove properties, which are not necessary for the PropertyManager
        properties.remove("NAME");
        properties.remove("TYPE");
        context.setProperties(name, properties);

        return startTag;
    }
}
