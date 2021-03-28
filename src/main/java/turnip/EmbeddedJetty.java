package turnip;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;

public class EmbeddedJetty {

  private Logger log = LoggerFactory.getLogger(getClass());
  
  private Server server = new Server();

  public void addServletContainerInitializer(ServletContainerInitializer init) {
    ServletContextHandler contextHandler = new ServletContextHandler();
    server.setHandler(contextHandler);
    contextHandler.addServletContainerInitializer(init);
  }

  public void configureHttpConnector(int port) {
    HttpConnectionFactory connectionFactory = new HttpConnectionFactory();

    // don't return the server version header
    connectionFactory.getHttpConfiguration().setSendServerVersion(true);

    ServerConnector connector = new ServerConnector(server, connectionFactory);
    connector.setPort(port);

    server.setConnectors(new Connector[]{connector});
  }

  public void startJoin() throws Exception {
    server.start();
    server.join();
  }

  public void shutdown() {
    if( !server.isStarted() ){
      // e.g. could not bind listen address
      log.info("Turnip shutdown requested, but Jetty not yet started");
      return;
    }

    log.info("Turnip shutdown requested, stopping Jetty server");
    server.setStopAtShutdown(true);
    try {
      server.stop();
    }
    catch( Exception e ){
      log.error("Error while stopping jetty server: " + e.getMessage(), e);
    }
  }
  
}
