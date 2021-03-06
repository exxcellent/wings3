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
package org.wings.plaf;

/**
 * <code>TemplateIntegrationLayoutCG<code>.
 * <p/>
 * User: raedler
 * Date: 08.08.2007
 * Time: 12:58:33
 *
 * @author raedler
 * @version $Id
 */
public interface TemplateIntegrationLayoutCG extends LayoutCG {

  void addTagHandler(String tagname, Class handlerClass);

}
