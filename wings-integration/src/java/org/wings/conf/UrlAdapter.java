/**
 * 
 */
package org.wings.conf;

import java.net.URL;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * <code>StringToUrlAdapter</code>.
 * 
 * <pre>
 * Date: Jul 23, 2008
 * Time: 6:15:26 PM
 * </pre>
 *
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public class UrlAdapter extends XmlAdapter<String, URL> {

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(URL url) throws Exception {
		return url.toExternalForm();
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public URL unmarshal(String value) throws Exception {
		return new URL(value);
	}
}
