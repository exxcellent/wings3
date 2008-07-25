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
package org.wingx.animation;

import org.wings.SComponent;

/**
 * <code>Motion</code>.
 * 
 * <pre>
 * Date: Apr 1, 2008
 * Time: 5:31:59 PM
 * </pre>
 *
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public class Motion extends AbstractAnimation {

	// Translates the affected component to the x-position.
	protected int x;
	
	// Translates the affected component to the y-position.
	protected int y;
	
	/**
	 * @param affectedComponent The component to be affected by this animation.
	 * @param duration The duration time in seconds of this animation.
	 * @param x Translates the affected component to the x-position.
	 * @param y Translates the affected component to the y-position.
	 */
	public Motion(SComponent affectedComponent, int duration, int x, int y) {
		super(affectedComponent, duration);
		this.x = x;
		this.y = y;
	}
	
	/* (non-Javadoc)
	 * @see org.wingx.animation.AbstractAnimation#getAnimationClass()
	 */
	@Override
	public String getAnimationClass() {
		return "YAHOO.util.Motion";
	}

	/* (non-Javadoc)
	 * @see org.wingx.animation.AbstractAnimation#getAttributes()
	 */
	@Override
	public String getAttributes() {
		
		StringBuilder attributes = new StringBuilder();
		
		attributes
			.append("points:{")
			.append("to:[").append(x).append(",").append(y).append("]")
			.append("}");
		
		return attributes.toString();
	}

}
