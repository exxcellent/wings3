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

import java.awt.Color;

import org.wings.SComponent;

/**
 * <code>ColorAnimation</code>.
 * 
 * <pre>
 * Date: Apr 1, 2008
 * Time: 3:54:22 PM
 * </pre>
 * 
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public class ColorAnimation extends AbstractAnimation {

	private Color fromColor;
	private Color toColor;
	
	public ColorAnimation(SComponent affectedComponent, Color fromColor, Color toColor) {
		this(affectedComponent, 1, fromColor, toColor);
	}

	public ColorAnimation(SComponent affectedComponent, int duration, Color fromColor,
			Color toColor) {
		super(affectedComponent, duration);
		this.fromColor = fromColor;
		this.toColor = toColor;
	}

	/* (non-Javadoc)
	 * @see org.wingx.animation.AbstractAnimation#getAnimationClass()
	 */
	@Override
	public String getAnimationClass() {
		return "YAHOO.util.ColorAnim";
	}

	/* (non-Javadoc)
	 * @see org.wingx.animation.AbstractAnimation#getAttributes()
	 */
	@Override
	public String getAttributes() {
		
		// Convert color to hex representation and cut off 0x
		String from = Integer.toHexString(this.fromColor.getRGB());
		from = from.substring(2, from.length());
		
		// Convert color to hex representation and cut off 0x
		String to = Integer.toHexString(this.toColor.getRGB());
		to = to.substring(2, to.length());
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("backgroundColor:{")
		  .append("from:'#").append(from).append("',")
		  .append("to:'#").append(to).append("'")
		  .append("}");
		
		return sb.toString();
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
