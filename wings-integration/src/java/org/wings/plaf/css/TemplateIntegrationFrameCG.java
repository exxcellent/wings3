package org.wings.plaf.css;


import java.io.IOException;

import org.wings.SFrame;
import org.wings.TemplateIntegrationFrame;
import org.wings.io.Device;

/**
 * <code>TemplateIntegrationFrameCG<code>.
 * <p/>
 * User: rrd
 * Date: 10.08.2007
 * Time: 16:43:09
 *
 * @author rrd
 * @version $Id
 */
public class TemplateIntegrationFrameCG extends IntegrationFrameCG implements org.wings.plaf.TemplateIntegrationFrameCG {

    private static final long serialVersionUID = 1L;

    @Override
    protected void writeHeadExtension(Device out, SFrame frame) throws IOException {
        String extension = ((TemplateIntegrationFrame) frame).getHeadExtension();
        if (extension != null) {
            out.print(extension);
        }
    }
    
    @Override
    protected void wirteBodyExtension(Device out, SFrame frame) throws IOException {
        String extension = ((TemplateIntegrationFrame) frame).getBodyExtension();
        if (extension != null) {
            out.print(extension);
        }
    }

}
