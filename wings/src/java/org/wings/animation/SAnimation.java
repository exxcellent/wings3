package org.wings.animation;

import java.io.IOException;
import java.io.Serializable;

import org.wings.Renderable;
import org.wings.SComponent;
import org.wings.SConstants;
import org.wings.io.Device;
import org.wings.plaf.AnimationCG;
import org.wings.resource.ResourceNotFoundException;
import org.wings.session.SessionManager;

public class SAnimation extends SComponent implements SConstants, Renderable, Serializable{

	/**
     * The code generation delegate, which is responsible for
     * the visual representation of an animation.
     */
    protected transient AnimationCG cg;
	
	public SAnimation() {
		
	}
	
	protected void setCG(AnimationCG newCG) {
        cg = newCG;
    }
	
	/**
     * Return the look and feel delegate.
     *
     * @return the componet's cg
     */
    public AnimationCG getCG() {
        return cg;
    }
    
    /**
     * Notification from the CGFactory that the L&F
     * has changed.
     *
     * @see SComponent#updateCG
     */
    public void updateCG() {
        setCG((AnimationCG) SessionManager.getSession().getCGManager().getCG(this));
    }
	
	public void write(Device d) throws IOException, ResourceNotFoundException {
		cg.write(d, this);
	}
}
