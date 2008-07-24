package org.wings.conf;

import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.wings.adapter.CmsAdapter;

/**
 * <code>Configuration<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 08:52:06
 *
 * @author rrd
 * @version $Id
 */
@XmlRootElement(name = "cms")
public class Cms {

    public static final String DEFAULT_PROTOCOL = "http";

    public static final String DEFAULT_SERVER = "localhost";

    public static final int DEFAULT_PORT = 80;

    @XmlAttribute(name = "adapter", required = true)
    private Class<? extends CmsAdapter> adapter;

    public Class<? extends CmsAdapter> getAdapter() {
        return adapter;
    }

    public void setAdapter(Class<? extends CmsAdapter> adapter) {
        this.adapter = adapter;
    }

    @XmlElement(name = "base-url", required = true)
    private URL baseUrl;

	/**
	 * @return the baseUrl
	 */
	public URL getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(URL baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	@XmlElement(name = "templates", required = true)
    private Templates templates;

	/**
	 * @return the templates
	 */
	public Templates getTemplates() {
		return templates;
	}

	/**
	 * @param templates the templates to set
	 */
	public void setTemplates(Templates templates) {
		this.templates = templates;
	}
	
	/**
	 * Returns the server path e.g. http://localhost:8080/ including port if set and
	 * a '/' at the end of the path.
	 * 
	 * @return The server path.
	 */
	public String getServerPath() {
		StringBuilder path = new StringBuilder();
		
		path.append(baseUrl.getProtocol()).append("://")
		.append(baseUrl.getHost());
		
		if (baseUrl.getPort() != -1) {
			path.append(":").append(baseUrl.getPort());
		}
		
		path.append("/");
		
		return path.toString();
	}
}
