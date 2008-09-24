/**
 * 
 */
package org.wings.adapter.cms.joomla;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.methods.GetMethod;
import org.mvel.MVEL;
import org.wings.IntegrationFrame;
import org.wings.adapter.cms.AbstractCmsAdapter;
import org.wings.conf.Integration;
import org.wings.conf.UrlExtension;
import org.wings.session.Session;
import org.wings.session.SessionManager;

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
		
		List<String> values = new ArrayList<String>();
		
		UrlExtension urlExtension = integration.getResource().getUrlExtension(type);
		
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
		
	    String url = prepareUrl(urlExtension.getReplacedValue(values.toArray(new String[0])));
		return process(request(new GetMethod(url)));
	}
}
