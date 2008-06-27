/**
 * 
 */
package org.wingx;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.wings.LowLevelEventListener;
import org.wings.SComponent;
import org.wings.SDialog;
import org.wings.SDimension;
import org.wings.SImageIcon;
import org.wings.SLabel;

/**
 * <code>XZoomableImage</code>.
 * 
 * <pre>
 * Date: Jun 13, 2008
 * Time: 9:18:24 AM
 * </pre>
 * 
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public class XZoomableImage extends SComponent implements LowLevelEventListener {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 9188294901460582145L;
	
	private SImageIcon detailImage;
	private SDimension detailImageMaxDimension;
	private SImageIcon previewImage;
	private SDimension previewImageMaxDimension;

	private String detailViewTitle;
	private boolean modal;

	/**
	 * 
	 */
	public XZoomableImage() {
		// empty
	}

	/**
	 * @param detailImage
	 * @throws IOException
	 */
	public XZoomableImage(String detailImage) throws IOException {
		this(new URL(detailImage));
	}

	/**
	 * @param detailImage
	 * @param previewImage
	 * @throws IOException
	 */
	public XZoomableImage(String detailImage, String previewImage) throws IOException {
		this(new URL(detailImage), new URL(previewImage));
	}

	/**
	 * @param detailImage
	 * @throws IOException
	 */
	public XZoomableImage(URL detailImage) throws IOException {
		InputStream input = detailImage.openStream();
		BufferedImage image = ImageIO.read(input);

		prepareImages(image, null);
	}

	/**
	 * @param detailImage
	 * @param previewImage
	 * @throws IOException
	 */
	public XZoomableImage(URL detailImage, URL previewImage) throws IOException {
		InputStream input = detailImage.openStream();
		BufferedImage dImage = ImageIO.read(input);

		input = previewImage.openStream();
		BufferedImage pImage = ImageIO.read(input);

		prepareImages(dImage, pImage);
	}

	/**
	 * @param detailImage
	 */
	public XZoomableImage(Image detailImage) {
		this(detailImage, detailImage);
	}

	/**
	 * @param detailImage
	 * @param previewImage
	 */
	public XZoomableImage(Image detailImage, Image previewImage) {
		prepareImages(detailImage, previewImage);
	}

	/**
	 * @param detailImage
	 * @param previewImage
	 */
	private void prepareImages(Image detailImage, Image previewImage) {
		this.detailImage = prepareDetailImage(detailImage);
		this.previewImage = preparePreviewImage(previewImage != null ? previewImage : detailImage);
	}
	
	/**
	 * @param detailImage
	 * @return
	 */
	private SImageIcon prepareDetailImage(Image detailImage) {
		if (detailImageMaxDimension != null) {
			int width = detailImageMaxDimension.getWidthInt();
			int height = detailImageMaxDimension.getWidthInt();

			detailImage = scaleImageTo(detailImage, width, height);
		}

		return new SImageIcon(detailImage);
	}

	/**
	 * @param previewImage
	 * @return
	 */
	private SImageIcon preparePreviewImage(Image previewImage) {
		if (previewImageMaxDimension != null) {
			int width = previewImageMaxDimension.getWidthInt();
			int height = previewImageMaxDimension.getWidthInt();

			previewImage = scaleImageTo(previewImage, width, height);
		}

		return new SImageIcon(previewImage);
	}

	/**
	 * @return the detailViewTitle
	 */
	public String getDetailViewTitle() {
		return detailViewTitle;
	}

	/**
	 * @param detailViewTitle
	 *            the detailViewTitle to set
	 */
	public void setDetailViewTitle(String detailViewTitle) {
		this.detailViewTitle = detailViewTitle;
	}

	/**
	 * @return the modal
	 */
	public boolean isModal() {
		return modal;
	}

	/**
	 * @param modal
	 *            the modal to set
	 */
	public void setModal(boolean modal) {
		this.modal = modal;
	}

	/**
	 * @param detailImage
	 *            the detailImage to set
	 */
	public void setDetailImage(String detailImage) throws IOException {
		setDetailImage(new URL(detailImage));
	}

	/**
	 * @param detailImage
	 *            the detailImage to set
	 */
	public void setDetailImage(URL detailImage) throws IOException {
		InputStream input = detailImage.openStream();
		setDetailImage(ImageIO.read(input));
	}

	/**
	 * @param detailImage
	 *            the detailImage to set
	 */
	public void setDetailImage(Image detailImage) {
		this.detailImage = prepareDetailImage(detailImage);		
		reload();
	}

	/**
	 * @param previewImage
	 *            the previewImage to set
	 */
	public void setPreviewImage(String previewImage) throws IOException {
		setPreviewImage(new URL(previewImage));
	}

	/**
	 * @param previewImage
	 *            the previewImage to set
	 */
	public void setPreviewImage(URL previewImage) throws IOException {
		InputStream input = previewImage.openStream();
		setPreviewImage(ImageIO.read(input));
	}

	/**
	 * @param previewImage
	 *            the previewImage to set
	 */
	public void setPreviewImage(Image previewImage) {
		this.previewImage = preparePreviewImage(previewImage);
		reload();
	}
	
	public void setDetailImageMaxDimension(SDimension detailImageMaxDimension) {
		this.detailImageMaxDimension = detailImageMaxDimension;

		if (detailImage != null) {
			detailImage = prepareDetailImage(detailImage.getImage());
			reload();
		}
	}

	public void setPreviewImageMaxDimension(SDimension previewImageMaxDimension) {
		this.previewImageMaxDimension = previewImageMaxDimension;

		if (previewImage != null) {
			previewImage = preparePreviewImage(previewImage.getImage());
			reload();
		}
	}

	public String getPreviewImagePath() {
		if (previewImage == null)  {
			previewImage = preparePreviewImage(detailImage.getImage());
		}
		return previewImage.getURL().toString();
	}

	/**
	 * Scales an image with pixel ratio awareness.
	 * 
	 * @param image
	 *            The source image that will be used to scale the image.
	 * @param width
	 *            The maximum width of the scaled image.
	 * @param height
	 *            The maximum height of the scaled image.
	 * @return Either the scaled image if its bigger than the source's width AND
	 *         height or the source image.
	 */
	private Image scaleImageTo(Image image, int width, int height) {

		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);

		double scaleX = (double) width / imageWidth;
		double scaleY = (double) height / imageHeight;

		double scale = Math.min(scaleX, scaleY);
		
		// Scale image only if its size is bigger than requested.
		if (scale < 1) {
			int scaledWidth = (int) (imageWidth * scale);
			int scaledHeight = (int) (imageHeight * scale);
			
			AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
			at.scale(scale, scale);
			BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = scaledImage.createGraphics();
			g2d.drawImage(image, at, null);
			g2d.dispose();
			return scaledImage;
		}
		return image;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wings.LowLevelEventListener#fireIntermediateEvents()
	 */
	public void fireIntermediateEvents() {
		// empty
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wings.LowLevelEventListener#isEpochCheckEnabled()
	 */
	public boolean isEpochCheckEnabled() {
		return true;
	}

	// LowLevelEventListener interface. Handle own events.
	public void processLowLevelEvent(String action, String[] values) {

		if (action.indexOf("_") > -1) {
			String[] actions = action.split("_");
			if ("zoomIn".equals(actions[1])) {
				SDialog zoomedImage = new SDialog(null, getDetailViewTitle() != null ? getDetailViewTitle() : "",
						isModal());
				zoomedImage.add(new SLabel(detailImage));

				// TODO: If zoomed image should be animated do it here :)
				// AbstractAnimation animation = new Motion(zoomedImage, 2, 0,
				// 0);

				zoomedImage.setVisible(true);

				// animation.start();

				return;
			}
		}
	}
}
