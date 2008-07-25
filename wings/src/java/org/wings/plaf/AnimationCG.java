package org.wings.plaf;

import org.wings.animation.SAnimation;
import org.wings.io.Device;

public interface AnimationCG<A extends SAnimation> extends ComponentCG<A> {
	public void write(Device device, A animation);
}
