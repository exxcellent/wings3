package org.wings.adapter;

import java.io.IOException;

import org.wings.template.TemplateSource;

public interface TemplateIntegrationAdapter extends IntegrationAdapter {
    
    void setTemplate(TemplateSource templateSource) throws IOException;

}
