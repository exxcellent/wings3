/*
 * Copyright 2000,2005 wingS development team.
 *
 * This file is part of wingS (http://wingsframework.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package org.wings.session;

import java.io.Serializable;
import java.util.Locale;

import net.sf.uadetector.OperatingSystemFamily;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentFamily;
import net.sf.uadetector.VersionNumber;
import net.sf.uadetector.internal.data.domain.BrowserType;


/**
 * Detect the browser from the user-agent string passed in the HTTP header.
 *
 * @author <a href="mailto:andre@lison.de">Andre Lison</a>
 */
public class Browser implements Serializable {
	
	final private ReadableUserAgent ua;
    
	final private int majorVersion;
	final private double minorVersion;
	final private String release;
	final private Locale browserLocale;

    /**
     * Create a new browser object and start scanning for
     * browser, os and client language in given string.
     *
     * @param agent the "User-Agent" string from the request.
     */
    public Browser(String agent,Locale locale) {
    	this.ua = CachedUserAgentStringParser.getParser().parse(agent);
        
    	this.majorVersion = Integer.parseInt( this.ua.getVersionNumber().getMajor() );
    	this.minorVersion = Integer.parseInt( this.ua.getVersionNumber().getMinor() );
    	this.release = this.ua.getVersionNumber().getExtension();
    	this.browserLocale = locale;
    }

    /**
     * Get the browser browserName (Mozilla, MSIE, Opera etc.).
     */
    public String getBrowserName() {
        return this.ua.getName();
    }

    /**
     * Gets the classification of the browser, this can be either GECKO, IE, KONQUEROR, MOZILLA, OPERA or UNKNOWN.
     *
     * @return A classification of the browser {@link BrowserType}
     */
    public UserAgentFamily getBrowserType() {
        return this.ua.getFamily(); // browserType;
    }

    /**
     * Get the browser major version.
     * <br>f.e. the major version for <i>Netscape 6.2</i> is <i>6</i>.
     *
     * @return the major version or <i>0</i> if not found
     */
    public int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Get the minor version. This is the number after the
     * dot in the version string.
     * <br>f.e. the minor version for <i>Netscape 6.01</i> is <i>0.01</i>.
     *
     * @return the minor version if found, <i>0</i> otherwise
     */
    public double getMinorVersion() {
        return minorVersion;
    }

    /**
     * Get additional information about browser version.
     * <br>f.e. the release for <i>MSIE 6.1b</i> is <i>b</i>.
     *
     * @return the release or <i>null</i>, if not available.
     */
    public String getRelease() {
        return release;
    }

    /**
     * Get the operating system string provided by the browser. {@link OSType}
     *
     * @return the os browserName or <i>null</i>, if not available.
     */
    public String getOs() {
        return this.ua.getOperatingSystem().getName();
    }

    /**
     * Get the operating system version.
     *
     * @return the os version or <i>null</i>, if not available.
     */
    public VersionNumber getOsVersion() {
        return this.ua.getOperatingSystem().getVersionNumber();
    }

    /**
     * Get the operating system type.
     *
     * @return A valid {@link OSType}
     */
    public OperatingSystemFamily getOsType() {
        return this.ua.getOperatingSystem().getFamily();
    }

    /**
     * Get the browser/client locale.
     *
     * @return the found locale or the default server locale
     *         specified by {@link Locale#getDefault} if not found.
     */
    public Locale getClientLocale() {
        return browserLocale;
    }

    /**
     * Get a full human readable representation of the browser.
     */
    public String toString() {
        return getBrowserName() + " v" + (majorVersion + minorVersion) + (release == null ? "" : "-" + release) +
                " browsertype:[" + getBrowserType() + "], locale:" + browserLocale + ", ostype:" + getOsType() + ": os:" + getOs() + " osv:" + getOsVersion();
    }


}
