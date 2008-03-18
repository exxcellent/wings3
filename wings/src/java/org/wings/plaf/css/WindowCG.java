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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.SWindow;
import org.wings.header.Header;
import org.wings.io.StringBuilderDevice;
import org.wings.plaf.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>WindowCG</code>.
 * User: raedler
 * Date: Oct 5, 2007
 * Time: 1:30:02 AM
 *
 * @author raedler
 * @version $Id
 */
public class WindowCG extends FormCG implements org.wings.plaf.WindowCG {

    private final static Log log = LogFactory.getLog(WindowCG.class);

    protected final List<Header> headers = new ArrayList<Header>();
}
