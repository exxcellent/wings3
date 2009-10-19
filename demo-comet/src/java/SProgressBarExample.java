import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.wings.SBorderLayout;
import org.wings.SButton;
import org.wings.SContainer;
import org.wings.SFrame;
import org.wings.SGridLayout;
import org.wings.SPanel;
import org.wings.SProgressBar;
import org.wings.comet.Comet;
import org.wings.session.Session;
import org.wings.session.SessionManager;

public class SProgressBarExample {

  final Comet comet = SessionManager.getSession().getComet();

  public SProgressBarExample() {
    final SFrame frame = new SFrame( "SProgressBarExample" );
    final SContainer container = frame.getContentPane();
    container.setLayout(new SGridLayout(3, 1, 10, 10));

    container.add( createProgressBarPanel( 2000 ) );
    
    container.add( createProgressBarPanel( 3000 ) );

    SPanel buttonPanel = new SPanel();
    SButton activateButton = new SButton( "Activate" );
    buttonPanel.add( activateButton );
    activateButton.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent event ) {
        comet.activateComet();
      }
    } );

    SButton deactivateButton = new SButton( "Deactivate" );
    buttonPanel.add( deactivateButton );
    deactivateButton.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent event ) {
        comet.deactivateComet();
      }
    } );
    container.add(buttonPanel);
    
    frame.setVisible( true );
  }

  private SPanel createProgressBarPanel( final int interval ) {
    SPanel panel = new SPanel( new SBorderLayout(5, 5) );
    final SProgressBar progressBar = new SProgressBar( 0, 100 );
    panel.add( progressBar, SBorderLayout.CENTER );

    SButton startButton = new SButton( "Start Task on Server" );
    panel.add( startButton, SBorderLayout.EAST );
    startButton.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent event ) {
        final AsyncTask asyncTask = new AsyncTask( SessionManager.getSession(),
            interval, progressBar );
        final Thread thread = new Thread( asyncTask );
        thread.start();
      }
    } );
    return panel;
  }

  private class AsyncTask implements Runnable {

    private final Session session;

    private final SProgressBar progressbar;

    private final int interval;

    public AsyncTask( Session session, int interval, SProgressBar progressbar ) {
      this.session = session;
      this.interval = interval;
      this.progressbar = progressbar;
    }

    public void run() {
      for ( int i = 10; i <= 100; i += 10 ) {
        final int percent = i;
        try {
          Thread.sleep( interval );
        }
        catch ( InterruptedException e ) {
        }
        session.getDispatcher().invokeLater( new Runnable() {
          public void run() {
            progressbar.setValue( percent );
          }
        } );
      }
    }
  }
}
