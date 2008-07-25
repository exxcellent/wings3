/**
 * 
 */
package org.wings.adapter.cms.joomla;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.httpclient.methods.GetMethod;
import org.wings.SFrame;
import org.wings.STemplateLayout;
import org.wings.adapter.cms.AbstractCmsAdapter;
import org.wings.conf.Integration;
import org.wings.conf.UrlExtension;

import au.id.jericho.lib.html.Source;

/**
 * <code>AbstactJoomlaAdapter</code>.
 * 
 * <pre>
 * Date: Jul 25, 2008
 * Time: 6:23:14 PM
 * </pre>
 *
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public abstract class AbstactJoomlaAdapter extends AbstractCmsAdapter {

	/**
	 * @param frame
	 * @param layout
	 * @param integration
	 */
	public AbstactJoomlaAdapter(SFrame frame, Integration integration, STemplateLayout layout) {
		super(frame, integration, layout);
	}

	/* (non-Javadoc)
	 * @see org.wings.template.ResourceResolver#getResource(java.lang.Object[])
	 */
	public Object getResource(Object... params) throws IOException {
		return getResource(null, params);
	}

	/* (non-Javadoc)
	 * @see org.wings.template.TemplateResolver#getTemplate(java.lang.String)
	 */
	public Object getResource(String type, Object... params) throws IOException {

		URL baseUrl = integration.getBaseUrl();
		
		UrlExtension urlExtension = integration.getResource().getUrlExtension(type);
		
		String url = baseUrl.toExternalForm() + "/" + urlExtension.getReplacedValue((String[]) params);
		
		return request(new GetMethod(url));
	}
}
