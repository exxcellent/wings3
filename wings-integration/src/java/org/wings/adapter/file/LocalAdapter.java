package org.wings.adapter.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.mvel2.MVEL;
import org.wings.IntegrationFrame;
import org.wings.Resource;
import org.wings.TemplateIntegrationFrame;
import org.wings.adapter.AbstractTemplateIntegrationAdapter;
import org.wings.conf.Integration;
import org.wings.conf.UrlExtension;
import org.wings.io.IOUtil;
import org.wings.session.Session;
import org.wings.session.SessionManager;
import org.wings.template.StringTemplateSource;
import org.wings.util.HtmlParserUtils;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.Tag;

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
        String template = getFileContent(new File(prepareUrl(url)));
        setTemplate(new StringTemplateSource(process(template)));
    }

    public Object getResource(String[] params) throws IOException {
    	List<String> values = new ArrayList<String>();
		
		UrlExtension urlExtension = integration.getResource().getUrlExtension(null);
		
		int i = 0;
		for (String variable : urlExtension.getVariables()) {
			if (variable.startsWith("$session")) {
	        	Session session = SessionManager.getSession();
	        	String expression = variable.substring(9, variable.length());
	        	
	        	Object value = MVEL.getProperty(expression, session);
	        	values.add(value.toString());
	        }
			else {
				values.add(params[i]);
				i++;
			}
		}
    	
        String url = prepareUrl(integration.getResource().getUrlExtension(null).getReplacedValue(values.toArray(new String[0])));
        if (url == null) {
            throw new FileNotFoundException();
        }
        return process(getFileContent(new File(url)));
    }

    public Object getResource(String type, String[] params) throws IOException {
        return getResource(params);
    }
    
    private String getFileContent(File file) throws IOException {
        StringBuilder contents = new StringBuilder();
        
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file), IOUtil.getIOEncoding()));
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

    @Override
    protected String prepareUrl(String extensionUrl) {
        String baseUrl = integration.getBaseUrl().toExternalForm();
        if (!baseUrl.endsWith("/") || !baseUrl.endsWith("\\")) {
            baseUrl += File.separatorChar;
        }
        String url = baseUrl + extensionUrl;
        String protocol = integration.getBaseUrl().getProtocol();
        url = url.replaceFirst(protocol + "://", "");
        url = url.replaceFirst(protocol + ":", "");
        return SessionManager.getSession().getServletContext().getRealPath(url);
    }

    @Override
    protected String requestInclude(String url) {
        try {
            return getFileContent(new File(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public Source parseHead(Source headSource) {
        headSource = prepareTitle(headSource);
        headSource = HtmlParserUtils.removeAllTags(headSource, Tag.META);
        ((TemplateIntegrationFrame) frame).setHeadExtension(headSource.toString().trim());
        return headSource;
    }
    
    public Source parseBody(Source bodySource) {
        bodySource = HtmlParserUtils.removeAllTags(bodySource, Tag.FORM);
        return bodySource;
    }
    
    private Source prepareTitle(Source headSource) {
        Element titleElement = headSource.findNextElement(0, Tag.TITLE);
        if (titleElement != null) {
            String title = titleElement.getTextExtractor().toString();
            frame.setTitle(title);
            
            return HtmlParserUtils.removeAllTags(headSource, Tag.TITLE);
        }
        return headSource;
    }

}