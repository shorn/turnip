package turnip;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.File;
import java.io.IOException;

/* Code taken from 
 https://auth0.com/blog/spring-5-embedded-tomcat-8-gradle-tutorial/ 
 */
public class App {
  private static final int PORT = 8080;

  public static void main(String... args) throws Exception {
    System.out.println("main() called");

    Server server = new Server();
    ServerConnector connector = new ServerConnector(server);
    connector.setPort(8080);
    server.setConnectors(new Connector[] {connector});

    ServletContextHandler contextHandler = new ServletContextHandler();
    server.setHandler(contextHandler);
    contextHandler.addServletContainerInitializer(new SpringAppConfig());
    
    server.start();
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

}

 
