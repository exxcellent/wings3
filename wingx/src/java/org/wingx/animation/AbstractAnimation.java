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
 * Base class for custom animations.
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @author leon
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

    /**
     * {@inheritDoc}
     */
	public String getScript() {
		String name = getAffectedComponentId(affectedComponent);

		StringBuilder sb = new StringBuilder();

		// Resolve affected HTML Element.
		sb.append("var ").append(name).append(" = document.getElementById('")
				.append(name).append("');\n");

		sb.append("var ").append(name).append("_animation = new ").append(
				getAnimationClass()).append("(").append(name).append(", {")
				.append(getAttributes()).append("},").append(duration).append(
						");\n");
        String preAnimationScript = getPreAnimationScript("_" + name);
        if (preAnimationScript != null && preAnimationScript.trim().length() > 0) {
            sb.append("var preAnimationScript = function() {\n");
            sb.append("var _" + name + " = document.getElementById('" + name + "');\n");
            sb.append(preAnimationScript);
            sb.append("\n};\n");
            sb.append(name).append("_animation.onStart.subscribe(preAnimationScript);\n");
        }
        String postAnimationScript = getPostAnimationScript("_" + name);
        if (postAnimationScript != null && postAnimationScript.trim().length() > 0) {
            sb.append("var postAnimationScript = function() {\n");
            sb.append("var _" + name + " = document.getElementById('" + name + "');\n");
            sb.append(postAnimationScript);
            sb.append("\n};\n");
            sb.append(name).append("_animation.onComplete.subscribe(postAnimationScript);\n");
        }
		sb.append(name).append("_animation.animate();\n");
		return sb.toString();
	}

    /**
     * Returns the script to be executed before the animation starts, or null if no script should be executed.
     *
     * @param variableName the name of the javascript variable under which the dom element
     * for the given component can be accessed.
     * @return the script to be executed before the animation starts, or null if no script should be executed.
     */
    protected abstract String getPreAnimationScript(String variableName);

    /**
     * Returns the script to be executed after the animation ends, or null if no script should be executed.
     *
     * @param variableName the name of the javascript variable under which the dom element
     * for the given component can be accessed.
     * @return the script to be executed after the animation ends, or null if no script should be executed.
     */
    protected abstract String getPostAnimationScript(String variableName);
	
	/**
	 * Starts this animation by adding this instance to the responsible
	 * <code>ScriptManager</code>.
	 */
	public void start() {
		ScriptManager.getInstance().addScriptListener(this);
	}

    /**
     * {@inheritDoc}
     */
	public String getCode() {
		return null;
	}

    /**
     * {@inheritDoc}
     */
	public String getEvent() {
		return null;
	}

    /**
     * {@inheritDoc}
     */
	public int getPriority() {
		return Integer.MIN_VALUE;
	}

    protected String getAffectedComponentId(SComponent component) {
        return component.getName();
    }
}
