package hugetestportlet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.wings.SButton;
import org.wings.SForm;
import org.wings.SFrame;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.SPortletAnchor;
import org.wings.STextField;
import org.wings.event.SRenderEvent;
import org.wings.event.SRenderListener;
import org.wings.portlet.PortletParameterEvent;
import org.wings.portlet.PortletParameterListener;
import org.wings.portlet.PortletParameterProvider;

public class HugeTestPortletWingS {

	public HugeTestPortletWingS() {

		SGridLayout gridLayout = new SGridLayout(1);
		SForm panel = new SForm(gridLayout);
		SButton submitButton = new SButton("Submit");
		gridLayout.setVgap(10);

		final SLabel message = new SLabel();
		final STextField textField = new STextField();

		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				message.setText("Hallo " + textField.getText() + " !");
			}
		});

		panel.add(new SLabel("Please insert name:"));
		panel.add(textField);
		panel.add(submitButton);
		panel.add(message);

		SPortletAnchor editMode = new SPortletAnchor(SPortletAnchor.ACTION_URL,
				SPortletAnchor.EDIT_MODE);
		editMode.add(new SLabel("go to edit mode"));

		panel.add(editMode);

		final SLabel paramLabel = new SLabel();

		PortletParameterProvider ppp = PortletParameterProvider.getInstance();
		ppp.addPortletParameterListener(new PortletParameterListener() {

			public void newPortletParameters(PortletParameterEvent e) {

				String[] param = e.getParameterValues("testprop");
				if (param != null) {
					paramLabel.setText("param: " + param[0]);
				} else {
					paramLabel.setText("param is null");
				}

			}

		});

		panel.add(paramLabel);

		SFrame rootFrame = new SFrame();
		rootFrame.getContentPane().add(panel);
		rootFrame.setVisible(true);

	}

}
