package org.wings.plaf.css;

import org.wings.io.Device;
import org.wings.SLayoutManager;
import org.wings.template.TemplateParseContext;
import org.wings.template.TemplateSource;
import org.wings.template.parser.PageParser;
import org.wings.MacroTagHandler;
import org.wings.CmsLayout;

import java.io.IOException;

/**
 * <code>CmsLayoutCG<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 13:00:00
 *
 * @author rrd
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
    }


    private void write(Device device, CmsLayout layout)
            throws IOException {
        final TemplateSource source = layout.getTemplateSource();

        if (source == null) {
            device.print("Unable to open template-file <em>null</em> in '" + layout);
        } else {
            parser.process(source, new TemplateParseContext(device, layout));
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
