/**
 * 
 */
package org.wings.adapter;

import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.wings.Resource;
import org.wings.SFrame;
import org.wings.conf.Integration;
import org.wings.resource.DynamicResource;
import org.wings.resource.ReloadResource;

/**
 * <code>AbstractIntegrationAdapter</code>.
 * 
 * <pre>
 * Date: Jul 25, 2008
 * Time: 6:36:21 PM
 * </pre>
 *
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public abstract class AbstractIntegrationAdapter implements IntegrationAdapter {

	protected SFrame frame;
	
	protected Integration integration;
	
	private DynamicResource defaultResource;
	
	/**
	 * @param frame
	 * @param integration
	 */
	public AbstractIntegrationAdapter(SFrame frame, Integration integration) {
		this.frame = frame;
		this.integration = integration;
		
		defaultResource = frame.getDynamicResource(ReloadResource.class);
	}

	/* (non-Javadoc)
	 * @see org.wings.adapter.IntegrationAdapter#setFrame(org.wings.SFrame)
	 */
	public void setFrame(SFrame frame) {
		this.frame = frame;
	}

	/* (non-Javadoc)
	 * @see org.wings.adapter.IntegrationAdapter#setIntegration(org.wings.conf.Integration)
	 */
	public void setIntegration(Integration integration) {
		this.integration = integration;
	}

	/* (non-Javadoc)
	 * @see org.wings.session.ResourceMapper#mapResource(java.lang.String)
	 */
	public Resource mapResource(String url) {
		try {
			navigate(url);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return defaultResource;
	}
	
	/**
	 * @param url
	 */
	protected abstract void navigate(String url) throws Exception;
}
