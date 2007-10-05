package org.wings;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.io.Device;
import org.wings.portlet.Const;
import org.wings.session.Session;
import org.wings.session.SessionManager;
import org.wings.util.SStringBuilder;

/**
 * 
 * This class extends the RequestURL to make it possible the the actionURL from
 * the portlet can be written out without any additional parameters or sites.
 * The write method from the RequestURL is changed and the Clone Method.
 * 
 * @author <a href="mailto:marc.musch@mercatis.com">Marc Musch</a>
 */
public class PortletRequestURL extends RequestURL {

	private final transient static Log log = LogFactory
			.getLog(PortletRequestURL.class);

	private static final String DEFAULT_RESOURCE_NAME = "_";

	/**
	 * Current session encoding used for URLEncoder.
	 */
	private final String characterEncoding;

	private String baseParameters;

	private boolean hasQuestMark;

	private String eventEpoch;

	private String resource;

	private SStringBuilder parameters = null;

	public PortletRequestURL() {
		super();
		this.characterEncoding = determineCharacterEncoding();
	}

	public PortletRequestURL(String baseURL, String encodedBaseURL) {
		super(baseURL, encodedBaseURL);
		this.characterEncoding = determineCharacterEncoding();
	}

	/**
	 * copy constructor.
	 */
	private PortletRequestURL(PortletRequestURL other) {
		this.characterEncoding = determineCharacterEncoding();
		this.baseURL = other.baseURL;
		this.baseParameters = other.baseParameters;
		this.hasQuestMark = other.hasQuestMark;
		this.eventEpoch = other.eventEpoch;
		this.resource = other.resource;
		SStringBuilder params = other.parameters;
		parameters = (params == null) ? null : new SStringBuilder(params
				.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wings.RequestURL#write(org.wings.io.Device) The
	 *      PortletRequestWrite Method only writes the portletURL
	 */
	@Override
	public void write(Device d) throws IOException {

		boolean printed = false;
		String epoResString = "";
		
		if(resource!=null && eventEpoch!=null) {
			epoResString = eventEpoch + SConstants.UID_DIVIDER;
		}
		
		if(resource!=null) {
			if(resource.endsWith("xml") || resource.endsWith("html")) {
				addParameter(Const.REQUEST_PARAM_RESOURCE_AS_PARAM, epoResString + resource);
			}
			else if(resource.startsWith("-")) {
				d.print(resource);
				printed = true;
			}
			
		}
		else {
			addParameter(Const.REQUEST_PARAM_RESOURCE_AS_PARAM, DEFAULT_RESOURCE_NAME);
		}
		
		if (baseURL != null && !printed) {
			d.print(baseURL);
		}
		

		// if (resource != null && epoch != null) {
		// d.print(epoch);
		// d.print(SConstants.UID_DIVIDER);
		// }
		//
		// if (resource != null) {
		// d.print(resource);
		// } else {
		// /*
		// * The default resource name. Work around a bug in some
		// * browsers that fail to assemble URLs.
		// * (TODO: verify and give better explanation here).
		// */
		// d.print(DEFAULT_RESOURCE_NAME);
		// }

		if (baseParameters != null) {
			d.print(baseParameters);
		}

		if (parameters != null && parameters.length() > 0) {
			d.print(hasQuestMark ? "&amp;" : "?");
			d.print(parameters.toString());
		}
	}

	@Override
	public void setBaseURL(String b, String encoded) {
		baseURL = b;

		baseParameters = encoded.substring(b.length());
		if (baseParameters.length() == 0)
			baseParameters = null;

		if (baseParameters != null)
			hasQuestMark = baseParameters.indexOf('?') >= 0;
		else
			hasQuestMark = false;
	}

	@Override
	public void setEventEpoch(String e) {
		eventEpoch = e;
	}

	@Override
	public String getEventEpoch() {
		return eventEpoch;
	}

	@Override
	public String getResource() {
		return resource;
	}

	@Override
	public void setResource(String r) {
		resource = r;
	}

	@Override
	public PortletRequestURL addParameter(String parameter) {
		if (parameter != null) {
			if (parameters == null)
				parameters = new SStringBuilder();
			else
				parameters.append("&amp;");
			parameters.append(recode(parameter));
		}
		return this;
	}

	@Override
	public PortletRequestURL addParameter(String name, String value) {
		addParameter(name);
		parameters.append("=").append(recode(value));
		return this;
	}

	@Override
	public PortletRequestURL addParameter(LowLevelEventListener comp,
			String value) {
		addParameter(comp.getLowLevelEventId(), value);
		return this;
	}

	@Override
	public PortletRequestURL addParameter(String name, int value) {
		addParameter(name);
		parameters.append("=").append(value);
		return this;
	}

	@Override
	public void clear() {
		if (parameters != null) {
			parameters.setLength(0);
		}
		setEventEpoch(null);
		setResource(null);
	}

	@Override
	public Object clone() {
		return new PortletRequestURL(this);
	}

	/**
	 * Determine the current character encoding.
	 * 
	 * @return Character encoding string or <code>null</code>
	 */
	private String determineCharacterEncoding() {
		String characterEncoding = null;
		final Session session = SessionManager.getSession();
		if (session != null) {
			characterEncoding = session.getCharacterEncoding();
		}
		return characterEncoding;
	}

	/**
	 * Recoded passes string to URL with current encoding.
	 * 
	 * @param parameter
	 *            String to recode
	 * @return Encoded parameter or same if an error occured
	 */
	private String recode(String parameter) {
		try {
			return URLEncoder.encode(parameter, characterEncoding);
		} catch (UnsupportedEncodingException e) {
			log.warn("Unknown character encoding?! " + characterEncoding, e);
			return parameter;
		}
	}

	private final boolean eq(Object a, Object b) {
		return (a == b) || (a != null && a.equals(b));
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!super.equals(o))
			return false;
		PortletRequestURL other = (PortletRequestURL) o;
		return (hasQuestMark == other.hasQuestMark
				&& eq(baseParameters, other.baseParameters)
				&& eq(eventEpoch, other.eventEpoch) && eq(resource, other.resource) && eq(
				parameters, other.parameters));
	}

	@Override
	public int hashCode() {
		return baseURL != null ? baseURL.hashCode() : 0;
	}

	@Override
	public String toString() {
		SStringBuilder erg = new SStringBuilder();

		boolean printed = false;
		String epoResString = "";
		
		if(resource!=null && eventEpoch!=null) {
			epoResString = eventEpoch + SConstants.UID_DIVIDER;
		}
		
		if(resource!=null) {
			if(resource.endsWith("xml") || resource.endsWith("html")) {
				addParameter(Const.REQUEST_PARAM_RESOURCE_AS_PARAM, epoResString + resource);
			}
			else if(resource.startsWith("-")) {
				erg.append(resource);
				printed = true;
			}
			
		}
		else {
			addParameter(Const.REQUEST_PARAM_RESOURCE_AS_PARAM, DEFAULT_RESOURCE_NAME);
		}
		
		if (baseURL != null && !printed) {
			erg.append(baseURL);
		}

		// if (resource != null && epoch != null) {
		// erg.append(epoch);
		// erg.append(SConstants.UID_DIVIDER);
		// }

		// if (resource != null) {
		// erg.append(resource);
		// } else {
		// erg.append(DEFAULT_RESOURCE_NAME);
		// }

		if (baseParameters != null) {
			erg.append(baseParameters);
		}

		if (parameters != null && parameters.length() > 0) {
			erg.append(hasQuestMark ? "&" : "?");
			erg.append(parameters.toString());
		}

		return erg.toString();
	}

}
