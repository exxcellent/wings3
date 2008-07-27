package org.wings.plaf.css;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.wings.DebugTagHandler;
import org.wings.MacroTagHandler;
import org.wings.SLayoutManager;
import org.wings.TemplateIntegrationLayout;
import org.wings.io.Device;
import org.wings.io.NullDevice;
import org.wings.template.IntegrationTemplateParseContext;
import org.wings.template.TemplateSource;
import org.wings.template.parser.PageParser;

/**
 * <code>TemplateIntegrationLayoutCG<code>.
 * <p/>
 * User: raedler
 * Date: 08.08.2007
 * Time: 13:00:00
 *
 * @author raedler
 * @version $Id
 */
public class TemplateIntegrationLayoutCG implements org.wings.plaf.TemplateIntegrationLayoutCG {

    private static final long serialVersionUID = 1L;

    private static final PageParser parser = new PageParser();

    /**
     * The parser looks for the '<OBJECT></OBJECT>' - tags.
     */
    static {
        parser.addTagHandler("OBJECT", MacroTagHandler.class);
        parser.addTagHandler("DEBUG", DebugTagHandler.class);
    }

    private void write(Device device, TemplateIntegrationLayout layout)
            throws IOException {
        final TemplateSource source = layout.getTemplateSource();

        if (source == null) {
            device.print("The cms server is not reachable at the moment or the connection data is wrong. Please check your <em>integration.xml</em>");
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
        write(device, (TemplateIntegrationLayout) manager);
    }

    public static Set<String> getContainedComponents(TemplateIntegrationLayout layout) throws IOException {
        final TemplateSource source = layout.getTemplateSource();
        IntegrationTemplateParseContext context = new IntegrationTemplateParseContext(new NullDevice(), layout);
        return TemplateIntegrationLayoutCG.parser.getContainedComponents(source, context);
    }

    public static Map<String, Map<String, String>> getComponentProperties(TemplateIntegrationLayout layout) throws IOException {
        final TemplateSource source = layout.getTemplateSource();
        IntegrationTemplateParseContext context = new IntegrationTemplateParseContext(new NullDevice(), layout);
        return TemplateIntegrationLayoutCG.parser.getComponentProperties(source, context);
    }
}
