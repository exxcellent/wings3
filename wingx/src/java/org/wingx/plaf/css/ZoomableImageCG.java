/**
 * 
 */
package org.wingx.plaf.css;

import java.io.IOException;

import org.wings.SComponent;
import org.wings.SResourceIcon;
import org.wings.io.Device;
import org.wings.plaf.css.AbstractComponentCG;
import org.wings.plaf.css.Utils;
import org.wingx.XZoomableImage;

/**
 * <code>ZoomableImageCG</code>.
 * 
 * <pre>
 * Date: Jun 13, 2008
 * Time: 9:21:57 AM
 * </pre>
 * 
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public class ZoomableImageCG extends AbstractComponentCG implements org.wingx.plaf.ZoomableImageCG {

    static {
        String[] images = new String [] {
        	"org/wingx/zoomableimage/zoomable_image_cursor.gif",
            "org/wingx/zoomableimage/zoomable_image_cursor.cur"
        };

        for ( int x = 0, y = images.length ; x < y ; x++ ) {
            new SResourceIcon(images[x]).getId();
        }
    }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wings.plaf.ComponentCG#write(org.wings.io.Device,
	 *      org.wings.SComponent)
	 */
	public void writeInternal(Device device, SComponent component) throws IOException {

		XZoomableImage zoomableImage = (XZoomableImage) component;

		device.print("<img src=\"" + zoomableImage.getPreviewImagePath()
				+ "\" onclick=\"wingS.request.sendEvent(null, false, true, this.id + '_zoomIn', 1);\" ");

		Utils.writeAllAttributes(device, component);

		device.print("/>");
	}
}
