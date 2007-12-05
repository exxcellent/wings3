package org.wings.portlet;

/**
 * 
 * Codec for encoding and decoding parameter names set in a SPortletAnchor. The
 * parameter names have to be encoded to seperate them from the normal wingS
 * parameters. A parameter must be encoded manually if it is set in the portlet
 * and retrieved in a wingS class using the {@link PortletParameterProvider}
 * 
 * @author <a href="mailto:marc.musch@mercatis.com">Marc Musch</a>
 * 
 */
public class PortletParameterCodec {

	/**
	 * Encodes a parameter name
	 * 
	 * @param parameterName
	 *            the parameter name to encode
	 * @return the encoded parameter name
	 */
	public synchronized static String encode(String parameterName) {
		return Const.WINGS_PORTLET_URL_CODE_STRING + parameterName;

	}

	/**
	 * Decodes a parameter name
	 * 
	 * @param codedParameterName
	 *            the encoded paramter name to decode
	 * @return the decoded parameter name
	 */
	public synchronized static String decode(String codedParameterName) {
		return codedParameterName.substring(Const.WINGS_PORTLET_URL_CODE_STRING
				.length());
	}

}
