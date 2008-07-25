package org.wings.animation;

import java.awt.Color;

import org.wings.SComponent;
import org.wings.plaf.ColorAnimationCG;

public class SColorAnimation extends SAnimation {

	protected SComponent component;
	protected Color fromColor;
	protected Color toColor;
	
	public SColorAnimation(SComponent component, Color fromColor, Color toColor) {
		this.component = component;
		this.fromColor = fromColor;
		this.toColor = toColor;
	}
	
	public SComponent getComponent() {
		return component;
	}
	
	public Color getFromColor() {
		return fromColor;
	}
	
	public Color getToColor() {
		return toColor;
	}
	
	protected void setCG(ColorAnimationCG cg) {
		super.setCG(cg);
	}
}
