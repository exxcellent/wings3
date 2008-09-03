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

import java.io.IOException;

import org.wings.adapter.AbstractTemplateIntegrationAdapter;
import org.wings.session.SessionManager;
import org.wings.template.StringTemplateSource;

/**
 * <code>IntegrationContainer</code>.
 * 
 * <pre>
 * Date: Sep 3, 2008
 * Time: 11:08:48 PM
 * </pre>
 *
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public class IntegrationContainer extends SContainer {

	private STemplateLayout layout;
	
	private AbstractTemplateIntegrationAdapter adapter;
	
	/**
	 * 
	 */
	public IntegrationContainer(String name) {
		layout = new TemplateIntegrationLayout();
		
		setLayout(layout);
		
		adapter = (AbstractTemplateIntegrationAdapter) SessionManager.getSession().getProperty("AbstractTemplateIntegrationAdapter");
		
		try {
			setTemplate(name);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param name
	 * @throws IOException
	 */
	public void setTemplate(String name) throws IOException {
		String template = adapter.process("<include name=\"" + name + "\"></include>");
		layout.setTemplate(new StringTemplateSource(template));
		reload();
	}
}
