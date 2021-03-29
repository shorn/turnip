package turnip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.Set;

import static java.util.Collections.emptySet;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"turnip"})
/* Simplest production deployment is to just dump the uberJar and 
config in a directory and run the Java command from that directory.
It's possible to maintain multiple different configurations on the same 
machine by putting config in separate directories and executing from those 
directories. I'd use Docker to encapsulate in a real setup, but this can be
useful on dev machines to maintain multiple configurations. */
@PropertySource(name = "working_dir_environment",
  value = "./env.properties",
  ignoreResourceNotFound = true)
/* During standard development cycle, use hardcoded default XDG location for 
config files. IMPROVE: use XDG_CONFIG_HOME env variable */
@PropertySource(name = "user_config_environment",
  value = "file:///${user.home}/.config/turnip/env.properties",
  ignoreResourceNotFound = true)
public class SpringAppConfig implements ServletContainerInitializer {
  private static Logger log = LoggerFactory.getLogger(App.class);

  @Override
  public void onStartup(
    Set<Class<?>> classes,
    ServletContext ctx
  ) {
    log.info("onStartup() called");
    initSpring(ctx);
  }

  public static void initSpring(ServletContext ctx) {
    // Create the 'root' Spring application context
    AnnotationConfigWebApplicationContext rootContext =
      new AnnotationConfigWebApplicationContext();
    rootContext.register(SpringAppConfig.class);

    // probs not necessary if Spring http config is set to STATELESS 
    ctx.setSessionTrackingModes(emptySet());
    
    // Manage the lifecycle of the root application context
    ctx.addListener(new ContextLoaderListener(rootContext));

    // Create the DispatcherServlet application context
    AnnotationConfigWebApplicationContext dispatcherContext =
      new AnnotationConfigWebApplicationContext();

    // Register and map the dispatcher servlet
    ServletRegistration.Dynamic dispatcher = ctx.addServlet(
      "dispatcher", new DispatcherServlet(dispatcherContext));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
  }
}


