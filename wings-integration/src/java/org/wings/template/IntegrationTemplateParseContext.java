package org.wings.template;

import org.wings.STemplateLayout;
import org.wings.SComponent;
import org.wings.io.Device;

/**
 * <code>IntegrationTemplateParseContext<code>.
 * <p/>
 * User: raedler
 * Date: 03.12.2007
 * Time: 13:23:45
 *
 * @author raedler
 * @version $Id
 */
public class IntegrationTemplateParseContext extends TemplateParseContext {

    public IntegrationTemplateParseContext(final Device sink, STemplateLayout layout) {
        super(sink, layout);
    }

    public SComponent[] getComponents() {
        return layout.getContainer().getComponents();
    }
}
