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
 * <code>Animation</code>.
 * 
 * <pre>
 * Date: Apr 1, 2008
 * Time: 4:58:11 PM
 * </pre>
 * 
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public class Animation extends AbstractAnimation {

	protected int width;
	protected int height;

	/**
	 * @param affectedComponent
	 *            The component to be affected by this animation.
	 * @param duration
	 *            The duration time of this animation.
	 * @param width
	 *            Animates the affected to component to the defined width.
	 * @param height
	 *            Animates the affected to component to the defined height.
	 */
	public Animation(SComponent affectedComponent, int duration, int width,
			int height) {
		super(affectedComponent, duration);
		this.width = width;
		this.height = height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wingx.animation.AbstractAnimation#getAnimationClass()
	 */
	@Override
	public String getAnimationClass() {
		return "YAHOO.util.Anim";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wingx.animation.AbstractAnimation#getAttributes()
	 */
	@Override
	public String getAttributes() {

		StringBuilder attributes = new StringBuilder();

		attributes
			.append("width:{to:").append(width).append("},")
			.append("height:{to:").append(height).append("}");

		return attributes.toString();
	}

    @Override
    protected String getPreAnimationScript(String variableName) {
        return null;
    }

    @Override
    protected String getPostAnimationScript(String variableName) {
        return null;
    }

}
