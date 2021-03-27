package turnip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"turnip"})
public class SpringAppConfig implements ServletContainerInitializer {
  private static Logger log = LoggerFactory.getLogger(App.class);
  
  @Override
  public void onStartup(
    Set<Class<?>> classes,
    ServletContext ctx
  ) throws ServletException {
    log.info("onStartup() called");
    initSpring(ctx);
  }

  public static void initSpring(ServletContext ctx) {
    // Create the 'root' Spring application context
    AnnotationConfigWebApplicationContext rootContext = 
      new AnnotationConfigWebApplicationContext();
    rootContext.register(SpringAppConfig.class);

    // Manage the lifecycle of the root application context
    ctx.addListener(new ContextLoaderListener(rootContext));

    // Create the dispatcher servlet's Spring application context
    AnnotationConfigWebApplicationContext dispatcherContext = 
      new AnnotationConfigWebApplicationContext();

    // Register and map the dispatcher servlet
    ServletRegistration.Dynamic dispatcher = ctx.addServlet(
      "dispatcher", new DispatcherServlet(dispatcherContext) );
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
  }
}


