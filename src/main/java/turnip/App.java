package turnip;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/* Code taken from 
 https://auth0.com/blog/spring-5-embedded-tomcat-8-gradle-tutorial/ 
 */
public class App {
  private static Logger log = LoggerFactory.getLogger(App.class);

  private static final int PORT = 8080;

  public static void main(String... args) throws Exception {
    log.info("main() called");

    Server server = new Server();

    configureHttpConnector(server);
    configureSpring(server);
    addRuntimeShutdownHook(server);

    server.start();
    server.join();
  }

  private static void configureSpring(Server server) {
    ServletContextHandler contextHandler = new ServletContextHandler();
    server.setHandler(contextHandler);
    contextHandler.addServletContainerInitializer(new SpringAppConfig());
  }

  private static void configureHttpConnector(Server server) {
    HttpConnectionFactory connectionFactory = new HttpConnectionFactory();
    
    // don't return the server version header
    connectionFactory.getHttpConfiguration().setSendServerVersion(true);
    
    ServerConnector connector = new ServerConnector(server, connectionFactory);
    connector.setPort(8080);
    
    server.setConnectors(new Connector[]{connector});
  }

  private static String createTempDir(String prefix) {
    try {
      File tempDir = File.createTempFile(prefix + ".", "." + PORT);
      tempDir.delete();
      tempDir.mkdir();
      tempDir.deleteOnExit();
      return tempDir.getAbsolutePath();
    }
    catch( IOException ex ){
      throw new RuntimeException(
        "Unable to create tempDir. java.io.tmpdir is set to " +
          System.getProperty("java.io.tmpdir"),
        ex);
    }
  }

  private static void addRuntimeShutdownHook(final Server server) {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      if( server.isStarted() ){
        server.setStopAtShutdown(true);
        try {
          server.stop();
        }
        catch( Exception e ){
          log.error("Error while stopping jetty server: " + e.getMessage(), e);
        }
      }
    }));
  }

}

 
