package org.wings.adapter;

import java.io.IOException;

import org.wings.IntegrationFrame;
import org.wings.conf.Integration;
import org.wings.session.ResourceMapper;

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
public interface IntegrationAdapter extends ResourceMapper {
    
    void initialize();

    void setFrame(IntegrationFrame frame);

    void setIntegration(Integration integration);
    
    Object getResource(String[] params) throws IOException;

    Object getResource(String type, String[] params) throws IOException;
}