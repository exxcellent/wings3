package org.wings.session;

import org.wings.Resource;

/**
 * Maps an URL to a resource.
 */
public interface ResourceMapper
{
    /**
     * @param url the URL to be mapped to a resource 
     * @return the corresponding resource or null, if the resource mapper cannot resolve the url
     */
    Resource mapResource(String url);
}
