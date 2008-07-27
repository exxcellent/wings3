package org.wings.adapter.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.wings.IntegrationFrame;
import org.wings.Resource;
import org.wings.adapter.AbstractTemplateIntegrationAdapter;
import org.wings.conf.Integration;
import org.wings.session.SessionManager;
import org.wings.template.StringTemplateSource;
import org.wings.template.TemplateSource;

/**
 * @author hengels
 * @version $Id
 */
public class LocalAdapter extends AbstractTemplateIntegrationAdapter {

    public LocalAdapter(IntegrationFrame frame, Integration integration) {
        super(frame, integration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.wings.session.ResourceMapper#mapResource(java.lang.String)
     */
    public Resource mapResource(String url) {
        if (!url.endsWith(".html"))
            return null;

        return super.mapResource(url);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.wings.adapter.AbstractIntegrationAdapter#navigate(java.lang.String)
     */
    protected void navigate(String url) throws IOException {
        String template = getFileContent(getFile(url));

        // Add template to cache
        TemplateSource templateSource = new StringTemplateSource(template);
        setTemplate(templateSource);
    }

    public Object getResource(String[] params) throws IOException {
        return getFileContent(getFile(integration.getResource().getUrlExtension(null).getReplacedValue(params)));
    }

    public Object getResource(String type, String[] params) throws IOException {
        return getResource(params);
    }
    
    private File getFile(String filePath) throws IOException {
        String basePath = integration.getBaseUrl().toExternalForm();
        if (!basePath.endsWith("/") || !basePath.endsWith("\\")) {
            basePath += File.separatorChar;
        }
        String path = basePath + filePath;
        String protocol = integration.getBaseUrl().getProtocol();
        path = path.replaceFirst(protocol + "://", "");
        path = path.replaceFirst(protocol + ":", "");
        return new File(SessionManager.getSession().getServletContext().getRealPath(path));
    }

    private String getFileContent(File file) throws IOException {
        StringBuilder contents = new StringBuilder();

        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        try {
            String line = null;
            String nl = System.getProperty("line.separator");
            while ((line = input.readLine()) != null) {
                contents.append(line).append(nl);
            }
        } finally {
            input.close();
        }

        return contents.toString();
    }

}