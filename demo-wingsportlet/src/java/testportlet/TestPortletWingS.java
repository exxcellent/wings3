package testportlet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.wings.SButton;
import org.wings.SForm;
import org.wings.SFrame;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.STextField;

public class TestPortletWingS {
	
	public TestPortletWingS() {
		
		// Erstellung der HTML-Form mit einem GridLayout
		SGridLayout gridLayout = new SGridLayout(1);
		SForm panel = new SForm(gridLayout);
		SButton submitButton = new SButton("Submit");
		gridLayout.setVgap(10);

		final SLabel message = new SLabel();
		final STextField textField = new STextField();
		
		// ActionListener für den Button
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				message.setText("Hallo "+textField.getText()+ " !"); }
		});
		
		panel.add(new SLabel("Please insert name:"));
		panel.add(textField);
		panel.add(submitButton);
		panel.add(message);
		
		SFrame rootFrame = new SFrame();
		rootFrame.getContentPane().add(panel);
		rootFrame.setVisible(true);
		
	}

}
