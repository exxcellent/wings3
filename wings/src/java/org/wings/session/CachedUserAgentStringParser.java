package org.wings.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

final public class CachedUserAgentStringParser implements UserAgentStringParser {

	final private UserAgentStringParser parser = UADetectorServiceFactory.getCachingAndUpdatingParser();
	
	final private ConcurrentMap<String,ReadableUserAgent> cache = new ConcurrentHashMap<String,ReadableUserAgent>();

	static final private UserAgentStringParser instance = new CachedUserAgentStringParser();
	
	private CachedUserAgentStringParser() {}
	
	static public UserAgentStringParser getParser() {
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getDataVersion() {
		return parser.getDataVersion();
	}

	/**
	 * @⁄<code>null</code>
	 */
	public ReadableUserAgent parse(final String userAgentString) {
		ReadableUserAgent result = cache.get(userAgentString);
		if (result == null) {
			result = parser.parse(userAgentString);
			cache.putIfAbsent(userAgentString, result);
		}
		return result;
	}
}
