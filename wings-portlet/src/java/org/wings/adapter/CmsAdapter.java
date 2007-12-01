package org.wings.adapter;

import org.wings.session.ResourceMapper;
import org.wings.SFrame;
import org.wings.conf.CmsDetail;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.OutputDocument;

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

    void setConfiguration(CmsDetail cfg);

    void parseTitle(Source source);

    void parseAnchors(Source source, OutputDocument output);

    void parseImages(Source source, OutputDocument output);

    void parseLinks(Source source);

    void parseScripts(Source source);
}
