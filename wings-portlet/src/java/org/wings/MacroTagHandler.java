package org.wings;

import org.wings.template.parser.SGMLTag;
import org.wings.template.parser.ParseContext;
import org.wings.template.parser.PositionReader;
import org.wings.template.parser.SpecialTagHandler;
import org.wings.template.TemplateParseContext;
import org.wings.template.PropertyManager;
import org.wings.io.Device;
import org.wings.plaf.CGManager;
import org.wings.plaf.ComponentCG;
import org.wings.session.SessionManager;
import org.wings.macro.MacroProcessor;
import org.wings.macro.MacroContext;
import org.wings.macro.Macro;
import org.wings.macro.MacroContainer;
import org.wings.plaf.CmsTableCG;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Iterator;

/**
 * <code>CmsObjectTagHandler<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 14:35:57
 *
 * @author rrd
 * @version $Id
 */
public class MacroTagHandler implements SpecialTagHandler {
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
        TemplateParseContext tcontext = (TemplateParseContext) context;
        Device sink = tcontext.getDevice();

        /*
         * get the component that is associtated with this name. This has
         * been set as Layout Manager Constraint.
         */
        SComponent c = tcontext.getComponent(name);
        if (c == null) {
            sink.print("<!-- Template: '" + name + "' Component not given -->");
        } else {

            StringBuilder sb = new StringBuilder();
            for (long i = getTagLength(); i > 0; i--) {
                sb.append((char) input.read());
            }
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

            String macroTemplate = sb.toString();
            macroTemplate = macroTemplate.substring(macroTemplate.indexOf('>') + 1, macroTemplate.lastIndexOf('<'));

            if (macroTemplate != null && !"".equals(macroTemplate.trim())) {
                MacroProcessor macroProcessor = MacroProcessor.getInstance();

                MacroContainer macroContainer = macroProcessor.buildMacro(macroTemplate);

                MacroContext ctx = new MacroContext();
                ctx.setDevice(sink);
                ctx.setComponent(c);
                ctx.put(name, c);

                macroContainer.setContext(ctx);

//                if (c instanceof CmsForm) {
//                    SComponent child = ((CmsForm) c).getComponent();
//
//                    if (child instanceof STable) {
//                        CmsTableCG cg = new CmsTableCG();
//                        cg.setMacros(macroContainer);
//                        child.setCG(cg);
//                        c.write(sink);
//                    }
//                }

                if (c instanceof STable) {
                    CmsTableCG cg = new CmsTableCG();
                    cg.setMacros(macroContainer);
                    c.setCG(cg);
                    c.write(sink);
                }

                //String result = macroProcessor.handle(macroTemplate, name, tcontext);
                //sink.print(result);
            } else {

                CGManager cgManager = SessionManager.getSession().getCGManager();
                ComponentCG cg = cgManager.getCG(c);
                c.setCG(cg);

                // set properties; the STemplateLayout knows how
                if (properties.size() > 0) {
                    PropertyManager propManager =
                            CmsLayout.getPropertyManager(c.getClass());

                    if (propManager != null) {
                        Iterator iter = properties.keySet().iterator();
                        while (iter.hasNext()) {
                            String key = (String) iter.next();
                            String value = (String) properties.get(key);
                            // System.out.println("set Property " + key + "=" +value + "  for " + name);
                            propManager.setProperty(c, key, value);
                        }
                    }
                }
                c.write(sink);
            }
//            input.skip(getTagLength());
        }
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

        return startTag;
    }
}
