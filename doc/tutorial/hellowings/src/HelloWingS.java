import org.wings.SButton;
import org.wings.SFont;
import org.wings.SFrame;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.STextField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class HelloWingS {
    // Apache jakarta commons logger
    private static final Log LOG = LogFactory.getLog(HelloWingS.class);

    /** This is the entry point of you application as denoted in your web.xml */ 
    public HelloWingS() {
        LOG.info("HelloWingS!");
        // the root application frame
        SFrame rootFrame = new SFrame();
        // add a simple demo GUI
        SPanel panel = buildGuessingPanel();
        rootFrame.getContentPane().add(panel);
        // "show" application
        rootFrame.setVisible(true);
    }

    private SPanel buildGuessingPanel() {
        SPanel panel = new SPanel(new SGridLayout(5, 1, 10, 5));
        SLabel title = new SLabel("Hello wingS!");
        SButton doGuess = new SButton("Guess!");
        final SLabel message = new SLabel();
        final STextField textField = new STextField();

        // arrange components using a grid layout
        panel.add(title);
        panel.add(new SLabel("Congratulation! Your wingS application is up'n'running.\n\n" +
                "So now we want fun - let's play a game.\n" +
                "Try to guess my number between 1 and 10."));
        panel.add(message);
        panel.add(textField);
        panel.add(doGuess);

        // Some formatting
        title.setFont(new SFont("sans-serif", SFont.BOLD, 18));
        message.setForeground(Color.RED);

        // The game logic as simple Swing-Actionlistener.
        final int randomNr = new Random().nextInt(10) + 1;
        doGuess.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Integer.toString(randomNr).equals(textField.getText())) {
                    message.setText("Congratulations! You guessed my number!");
                } else {
                    message.setText("No - '" + textField.getText() +
                            "' is not the right number. Try again!");
                }
            }
        });
        return panel;
    }
}