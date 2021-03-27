package turnip;

import org.eclipse.jetty.server.Connector;
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
    ServerConnector connector = new ServerConnector(server);
    connector.setPort(8080);
    server.setConnectors(new Connector[] {connector});

    ServletContextHandler contextHandler = new ServletContextHandler();
    server.setHandler(contextHandler);
    contextHandler.addServletContainerInitializer(new SpringAppConfig());
    
    addRuntimeShutdownHook(server);
    
    server.start();
    server.join();
  }

  private static String createTempDir(String prefix) {
    try{
      File tempDir = File.createTempFile(prefix +".", "." + PORT);
      tempDir.delete();
      tempDir.mkdir();
      tempDir.deleteOnExit();
      return tempDir.getAbsolutePath();
    }
    catch( IOException ex ){
      throw new RuntimeException(
        "Unable to create tempDir. java.io.tmpdir is set to " +
          System.getProperty("java.io.tmpdir"),
        ex );
    }
  }

  private static void addRuntimeShutdownHook(final Server server) {
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        if (server.isStarted()) {
          server.setStopAtShutdown(true);
          try {
            server.stop();
          } catch (Exception e) {
            System.out.println("Error while stopping jetty server: " + e.getMessage());
            log.error("Error while stopping jetty server: " + 
                e.getMessage(), e);
          }
        }
      }
    }));
  }

}

 
