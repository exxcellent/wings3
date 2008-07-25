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
package org.wings.plaf.css;


import java.io.IOException;

import org.wings.IntegrationFrame;
import org.wings.SFrame;
import org.wings.io.Device;

/**
 * <code>CmsFrameCG<code>.
 * <p/>
 * User: rrd
 * Date: 10.08.2007
 * Time: 16:43:09
 *
 * @author rrd
 * @version $Id
 */
public class IntegrationFrameCG extends FrameCG implements org.wings.plaf.IntegrationFrameCG {

    private static final long serialVersionUID = 1L;

    @Override
    protected void writeHeadExtension(Device out, SFrame frame) throws IOException {
        String extension = ((IntegrationFrame) frame).getHeadExtension();
        if (extension != null) {
            out.print(extension);
        }
    }
    
    @Override
    protected void wirteBodyExtension(Device out, SFrame frame) throws IOException {
        String extension = ((IntegrationFrame) frame).getBodyExtension();
        if (extension != null) {
            out.print(extension);
        }
    }

}
