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

import org.wings.plaf.TemplateIntegrationFormCG;

/**
 * <code>CmsForm<code>.
 * <p/>
 * User: raedler
 * Date: 14.08.2007
 * Time: 16:58:29
 *
 * @author raedler
 * @version $Id
 */
public class TemplateIntegrationForm extends SForm {
    
    private static final long serialVersionUID = 1L;

    public TemplateIntegrationForm() {
        super();
    }

    public TemplateIntegrationForm(SLayoutManager layout) {
        super(layout);
    }

    public void setCG(TemplateIntegrationFormCG cg) {
        super.setCG(cg);
    }
}
