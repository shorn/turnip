package turnip;

import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;

/* Code taken from 
 https://auth0.com/blog/spring-5-embedded-tomcat-8-gradle-tutorial/ 
 */
public class App {
  private static final int PORT = 8080;

  public static void main(String... args) throws Exception {
    System.out.println("main() called");

    Tomcat tomcat = configTomcat();

    tomcat.start();
    tomcat.getServer().await();
  }

  public static Tomcat configTomcat() throws Exception {
    String appBase = ".";
    Tomcat tomcat = new Tomcat();
    tomcat.setBaseDir(createTempDir());
    tomcat.setPort(PORT);
    tomcat.getHost().setAppBase(appBase);
    tomcat.addWebapp("", appBase);

    return tomcat;
  }

  // based on AbstractEmbeddedServletContainerFactory
  private static String createTempDir() {
    try{
      File tempDir = File.createTempFile("tomcat.", "." + PORT);
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

 
