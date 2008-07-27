package org.wings;


public class TemplateIntegrationFrame extends IntegrationFrame {

    private static final long serialVersionUID = 1L;

    private TemplateIntegrationLayout templateLayout;

    private String headExtension;

    private String bodyExtension;
    
    @Override
    protected void initialize() {
        templateLayout = new TemplateIntegrationLayout();
        setContentPane(new TemplateIntegrationForm(templateLayout));
        super.initialize();
    }

    public TemplateIntegrationLayout getTemplateIntegrationLayout() {
        return templateLayout;
    }
    
    public String getHeadExtension() {
        return headExtension;
    }

    public void setHeadExtension(String headExtension) {
        this.headExtension = headExtension;
    }

    public String getBodyExtension() {
        return bodyExtension;
    }

    public void setBodyExtension(String bodyExtension) {
        this.bodyExtension = bodyExtension;
    }
    
    @Override
    public final SComponent add(SComponent c) {
        throw new UnsupportedOperationException("This method won't be supported by CmsFrame. Use add(SComponent c, Object constraint) instead.");
    }

    @Override
    public void add(SComponent c, Object constraint) {
        getContentPane().add(c, constraint);
    }

    @Override
    public SComponent add(SComponent c, int index) {
        throw new UnsupportedOperationException("This method won't be supported by CmsFrame. Use add(SComponent c, Object constraint) instead.");
    }

    @Override
    public void add(SComponent c, Object constraint, int index) {
        throw new UnsupportedOperationException("This method won't be supported by CmsFrame. Use add(SComponent c, Object constraint) instead.");
    }
}
