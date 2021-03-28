package turnip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

/* Code taken from 
 https://auth0.com/blog/spring-5-embedded-tomcat-8-gradle-tutorial/ 
 */
public class App {
  private static final int PORT = 8080;
  
  private static Logger log = LoggerFactory.getLogger(App.class);

  public static void main(String... args) throws Exception {
    log.info("main() called, timezone={}", TimeZone.getDefault().getID());

    EmbeddedJetty jetty = new EmbeddedJetty();
    jetty.configureHttpConnector(PORT);
    jetty.addServletContainerInitializer(new SpringAppConfig());

    // Will be called when pressing ctrl-c, for example.
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      jetty.shutdown();
    }, "app-shutdown"));
    
    jetty.startJoin();
  }

}

 
