package org.wings.adapter;

import java.io.IOException;

import org.wings.IntegrationFrame;
import org.wings.STemplateLayout;
import org.wings.TemplateIntegrationFrame;
import org.wings.conf.Integration;
import org.wings.template.TemplateSource;

public abstract class AbstractTemplateIntegrationAdapter extends
        AbstractIntegrationAdapter implements TemplateIntegrationAdapter {

    private STemplateLayout layout;
    
    public AbstractTemplateIntegrationAdapter(IntegrationFrame frame, Integration integration) {
        super(frame, integration);
    }

    public void initialize() {
        layout = ((TemplateIntegrationFrame) frame).getTemplateIntegrationLayout();
    }
    
    public void setTemplate(TemplateSource templateSource) throws IOException {
        layout.setTemplate(templateSource);
    }

}
