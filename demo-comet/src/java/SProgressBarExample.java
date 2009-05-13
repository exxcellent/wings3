import org.wings.*;
import org.wings.comet.Comet;
import org.wings.session.Session;
import org.wings.session.SessionManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SProgressBarExample implements ActionListener {

    final SProgressBar progressBar = new SProgressBar(0, 100);
    final SButton startButton = new SButton("Start Task on Server");
    final Comet comet = SessionManager.getSession().getComet();

    public SProgressBarExample() {
        final SFrame frame = new SFrame("SProgressBarExample");
        final SContainer container = frame.getContentPane();
        container.add(progressBar, SBorderLayout.NORTH);
        container.add(startButton, SBorderLayout.SOUTH);
        startButton.addActionListener(this);
        frame.setVisible(true);
        comet.connect();
    }

    public void actionPerformed(ActionEvent event) {
        final AsyncTask asyncTask  = new AsyncTask(SessionManager.getSession());
        final Thread thread = new Thread(asyncTask);
        thread.start();
    }

    private class AsyncTask implements Runnable {

        private final Session session;

        public AsyncTask(Session session) {
            this.session = session;
        }

        public void run() {
            for (int i = 10; i <= 100; i += 10) {
                final int percent = i;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                session.getDispatcher().invokeLater(new Runnable() {
                    public void run() {
                        progressBar.setValue(percent);
                    }
                });
            }
            comet.disconnect();
        }
    }
}
