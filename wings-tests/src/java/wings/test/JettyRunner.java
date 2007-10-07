package wings.test;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Convenience servlet runner to show the demos.
 */
public class JettyRunner {
	public static void main(String argv[]) throws Exception {
		if (argv.length != 2) { 
			System.err.println("usage: WingsetRunner <warfile|directory> <port>");
			System.exit(1);
		}
		
		final String warFile = argv[0];
		final int port = Integer.parseInt(argv[1]);
		       
		Server server = new Server();
        Connector connector = new SelectChannelConnector();
        connector.setPort(port);
        connector.setHost("127.0.0.1");
        server.addConnector(connector);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setWar(warFile);
        server.setHandler(context);
        server.setStopAtShutdown(true);

        server.start();

        System.out.println("\n\n[===> Point your browser to "
		           + " http://localhost:" + port + "/  <===]\n");
	}
}
