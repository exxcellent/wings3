package org.wings.adapter;

import org.wings.SFrame;
import org.wings.conf.Integration;
import org.wings.session.ResourceMapper;
import org.wings.template.ResourceResolver;

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
public interface IntegrationAdapter extends ResourceMapper, ResourceResolver {

    void setFrame(SFrame frame);

    void setIntegration(Integration integration);
}