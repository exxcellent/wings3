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
package org.wings;

import org.wings.plaf.AnchorCG;

import java.net.URL;

/**
 * Container used to force a HTML Link.
 * <p/>
 * Creates a 'normal'
 * &lt;a href=&quot;http://whatever/&quot;&gt;...&lt;/a&gt;
 * HTML link around some components that are stored in the container.
 *
 * @author <a href="mailto:H.Zeller@acm.org">Henner Zeller</a>
 */
public class SAnchor extends SContainer {

    /**
     * the URL to link to.
     */
    protected SimpleURL url;

    /**
     * the target frame/window.
     */
    protected String target;

    /**
     * creates an anchor with emtpy URL and target.
     */
    public SAnchor() {
        this(new SimpleURL("#"), null);
    }

    /**
     * create an anchor that points to the URL url.
     *
     * @param url the url to point to.
     */
    public SAnchor(String url) {
        this(url, null);
    }

    /**
     * creates an anchor that points to the URL and is openend
     * in the frame or window named target.
     *
     * @param url    the url to link to.
     * @param target the target window or frame.
     */
    public SAnchor(String url, String target) {
        setURL(url);
        setTarget(target);
    }

    /**
     * creates an anchor that points to the URL and is openend
     * in the frame or window named target.
     *
     * @param url    the url to link to.
     * @param target the target window or frame.
     */
    public SAnchor(SimpleURL url, String target) {
        setURL(url);
        setTarget(target);
    }

    /**
     * set the url this anchor points to.
     *
     * @param ref the url.
     */
    public void setURL(URL ref) {
        if (ref != null) {
            setURL(ref.toString());
        } else {
            setURL((SimpleURL) null);
        }
    }

    /**
     * set the url this anchor points to.
     *
     * @param r the url.
     */
    public void setURL(SimpleURL r) {
        SimpleURL oldURL = url;
        url = r;
        if (url == null && oldURL != null
                || (url != null && !url.equals(oldURL))) {
            reload();
        }
        propertyChangeSupport.firePropertyChange("URL", oldURL, this.url);
    }

    /**
     * set the url this anchor points to.
     *
     * @param url the url.
     */
    public void setURL(String url) {
        setURL(new SimpleURL(url));
    }

    /**
     * set the name of the target frame/window.
     */
    public void setTarget(String t) {
        String oldVal = this.target;
        target = t;
        propertyChangeSupport.firePropertyChange("target", oldVal, this.target);
    }

    /**
     * get the name of the target frame/window.
     */
    public String getTarget() {
        return target;
    }

    /**
     * get the url the anchor points to.
     */
    public SimpleURL getURL() {
        return url;
    }

    public void setCG(AnchorCG cg) {
        super.setCG(cg);
    }
}
