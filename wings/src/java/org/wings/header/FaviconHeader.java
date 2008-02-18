package org.wings.header;

import org.wings.URLResource;
import org.wings.resource.DefaultURLResource;

/**
 * Use this to add a favicon (header) to a frame.
 * <code>
 *   frame.addHeader(new FaviconHeader("../images/favicon.ico"));
 * </code>
 */
public class FaviconHeader extends Link {


    public FaviconHeader(String url) {
        this(new DefaultURLResource(url));
    }

    public FaviconHeader(URLResource resource) {
        super("shortcut icon", null, "image/vnd.microsoft.icon", null, resource);
    }

}