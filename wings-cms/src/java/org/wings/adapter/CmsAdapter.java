package org.wings.adapter;

import org.wings.SFrame;
import org.wings.conf.Cms;
import org.wings.session.ResourceMapper;

import au.id.jericho.lib.html.OutputDocument;
import au.id.jericho.lib.html.Source;

/**
 * <code>JoomlaIntegration<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 08:28:10
 *
 * @author rrd
 * @version $Id
 */
public interface CmsAdapter extends ResourceMapper {

    void setFrame(SFrame frame);

    void setCms(Cms cms);

    Source resolveIncludes(Source source);
    
    void parseTitle(Source source);

    void parseAnchors(Source source, OutputDocument output);

    void parseImages(Source source, OutputDocument output);

    void parseLinks(Source source);

    void parseScripts(Source source);
}