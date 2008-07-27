/**
 * 
 */
package org.wings.adapter.cms.joomla;

import java.io.IOException;

import org.apache.commons.httpclient.methods.GetMethod;
import org.wings.IntegrationFrame;
import org.wings.adapter.cms.AbstractCmsAdapter;
import org.wings.conf.Integration;

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
	public AbstactJoomlaAdapter(IntegrationFrame frame, Integration integration) {
		super(frame, integration);
	}
	
	public Object getResource(String[] params) throws IOException {
		return getResource(null, params);
	}
	
	public Object getResource(String type, String[] params) throws IOException {
	    String baseUrl = integration.getBaseUrl().toExternalForm();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        String extensionUrl = integration.getResource().getUrlExtension(type).getReplacedValue(params);
		return request(new GetMethod(baseUrl + extensionUrl));
	}
}
