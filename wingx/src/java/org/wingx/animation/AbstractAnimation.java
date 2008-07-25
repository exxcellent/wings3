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
import org.wings.script.ScriptListener;
import org.wings.session.ScriptManager;

/**
 * <code>AbstractAnimation</code>.
 * 
 * <pre>
 * Date: Apr 1, 2008
 * Time: 3:54:45 PM
 * </pre>
 * 
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public abstract class AbstractAnimation implements ScriptListener {

	// The component to be affected by this animation.
	private SComponent affectedComponent;

	// The duration time of this animation.
	private int duration;

	/**
	 * This is the base class for every animation based on JavaScript.
	 * 
	 * @param affectedComponent The component to be affected by this animation.
	 * @param duration The duration time of this animation.
	 */
	public AbstractAnimation(SComponent affectedComponent, int duration) {
		this.affectedComponent = affectedComponent;
		this.duration = duration;
	}

	/**
	 * Returns the component to be affected by this animation.
	 * 
	 * @return The component to be affected by this animation.
	 */
	public SComponent getAffectedComponent() {
		return affectedComponent;
	}

	/**
	 * Returns the animation class (JavaScript class).
	 * 
	 * E.g.
	 * <ul>
	 *   <li>YAHOO.util.Anim</li>
	 *   <li>YAHOO.util.ColorAnim</li>
	 * </ul>
	 * 
	 * @return The animation class (JavaScript class).
	 */
	public abstract String getAnimationClass();

	/**
	 * Returns the animation attributes propagated as second parameter.
	 * 
	 * @return The animation attributes propagated as second parameter.
	 */
	public abstract String getAttributes();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wings.script.ScriptListener#getScript()
	 */
	public String getScript() {
		String name = getAffectedComponent().getName();

		StringBuilder sb = new StringBuilder();

		// Resolve affected HTML Element.
		sb.append("var ").append(name).append(" = document.getElementById('")
				.append(name).append("');\n");

		sb.append("var ").append(name).append("_animation = new ").append(
				getAnimationClass()).append("(").append(name).append(", {")
				.append(getAttributes()).append("},").append(duration).append(
						");\n");
		sb.append(name).append("_animation.animate();\n");

		return sb.toString();
	}
	
	/**
	 * Starts this animation by adding this instance to the responsible
	 * <code>ScriptManager</code>.
	 */
	public void start() {
		ScriptManager.getInstance().addScriptListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wings.script.ScriptListener#getCode()
	 */
	public String getCode() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wings.script.ScriptListener#getEvent()
	 */
	public String getEvent() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wings.script.ScriptListener#getPriority()
	 */
	public int getPriority() {
		return Integer.MIN_VALUE;
	}
}
