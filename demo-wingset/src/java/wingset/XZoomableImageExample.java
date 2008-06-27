package wingset;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.wings.SBorderLayout;
import org.wings.SButton;
import org.wings.SComponent;
import org.wings.SDimension;
import org.wings.SGridLayout;
import org.wings.SOptionPane;
import org.wings.SPanel;
import org.wings.STextField;
import org.wingx.XZoomableImage;

/**
 * <code>XEditorExample</code>.
 * 
 * <pre>
 * Date: Apr 1, 2008
 * Time: 7:56:29 PM
 * </pre>
 * 
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public class XZoomableImageExample extends WingSetPane {

	protected SComponent createControls() {
		return null;
	}

	protected SComponent createExample() {

		SPanel panel = new SPanel(new SGridLayout(2, 1, 5, 5));

		SPanel inputPanel = new SPanel(new SGridLayout(1, 2, 5, 5));
		panel.add(inputPanel);

		final SPanel imagePanel = new SPanel(new SBorderLayout());
		panel.add(imagePanel);

		final STextField urlInput = new STextField("http://wingsframework.org/wingset/icons/wings-logo.png");
		SButton button = new SButton("Open");
		button.addActionListener(new ActionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				BufferedImage image = null;
				try {
					URL url = new URL(urlInput.getText());

					InputStream in = url.openStream();
					image = ImageIO.read(in);
				} catch (MalformedURLException ex) {
					ex.printStackTrace();
					SOptionPane.showMessageDialog(null, ex.getMessage());
				} catch (IOException ex) {
					ex.printStackTrace();
					SOptionPane.showMessageDialog(null, ex.getMessage());
				}

				if (image != null) {
					XZoomableImage zoomableImage = new XZoomableImage(image);
					zoomableImage.setPreviewImageMaxDimension(new SDimension(160, 120));
					zoomableImage.setDetailImageMaxDimension(new SDimension(400, 300));
					imagePanel.removeAll();
					imagePanel.add(zoomableImage, BorderLayout.CENTER);
				}
			}
		});
		inputPanel.add(urlInput);
		inputPanel.add(button);

		// BufferedImage image = null;
		// try {
		// URL url = new
		// URL("http://www.marjonsplaza.com/funny%20cat%20shark.jpg");
		//			
		// InputStream in = url.openStream();
		// image = ImageIO.read(in);
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//    	
		// XZoomableImage zoomableImage = new XZoomableImage(image);
		// // zoomableImage.setAttribute(CSSProperty.WIDTH, "200px");
		// // zoomableImage.setAttribute(CSSProperty.HEIGHT, "160px");
		// zoomableImage.setPreviewImageMaxDimension(new SDimension(160, 120));
		// zoomableImage.setDetailImageMaxDimension(new SDimension(400, 300));
		//        
		// image = null;
		// try {
		// URL url = new
		// URL("http://uath.org/images/funny/2006-04/pope_funny.jpg");
		//			
		// InputStream in = url.openStream();
		// image = ImageIO.read(in);
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//    	
		// XZoomableImage zoomableImage2 = new XZoomableImage(image);
		// // zoomableImage2.setAttribute(CSSProperty.WIDTH, "100px");
		// // zoomableImage2.setAttribute(CSSProperty.HEIGHT, "70px");
		// zoomableImage2.setDetailViewTitle("The funny pope with many hope
		// :)");
		// zoomableImage2.setModal(true);
		// zoomableImage2.setPreviewImageMaxDimension(new SDimension(160, 120));
		// zoomableImage2.setDetailImageMaxDimension(new SDimension(400, 300));
		//        
		//        
		// panel.add(zoomableImage);
		// panel.add(zoomableImage2);

		return panel;
	}
}
