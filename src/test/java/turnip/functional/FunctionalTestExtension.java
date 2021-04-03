package turnip.functional;

import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import turnip.App;
import turnip.jetty.EmbeddedJetty;
import turnip.spring.config.AppConfig;
import turnip.util.Log;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static turnip.spring.config.WebSecurityConfig.AUDIENCE_PROP_NAME;
import static turnip.util.ExceptionUtil.createRuntimeException;
import static turnip.util.JvmUtil.normaliseJvmDefaults;
import static turnip.util.NetUtil.isLocalhostPortAvailable;

public class FunctionalTestExtension implements BeforeAllCallback,
  ExtensionContext.Store.CloseableResource {

  private static boolean started = false;

  private static ServerConnector serverConnector;
  private static EmbeddedJetty jetty;


  @Override
  public void beforeAll(ExtensionContext context) throws Exception{
    if( !started ){
      started = true;
      initTurnip();
      context.getRoot().getStore(GLOBAL).
        put(this.getClass().getName(), this);
    }
  }

  @Override
  public void close() {
    shutdownTurnip();
  }

  public void initTurnip() throws Exception {
    Log.to(FunctionalTest.class).info("initTurnip()");
    normaliseJvmDefaults();

    jetty = new EmbeddedJetty();

    /* I often accidentally run both local dev server and func tests at same 
    time on the same DB.  Using the same http port acts as a proxy "shared 
    resource" to detect that situation. */
    if( !isLocalhostPortAvailable(App.PORT) ){
      throw createRuntimeException(
        "Port %s is in use," +
          " usually caused by a Turnip dev server still running." +
          " Stop other process so they don't step on each others DB.",
        App.PORT);
    }

    serverConnector = jetty.configureHttpConnector(App.PORT);
    jetty.addServletContainerInitializer((sci, ctx) ->
    {
      var rootContext = AppConfig.initServletContext(ctx);
      MutablePropertySources propertySources =
        rootContext.getEnvironment().getPropertySources();
      /* use a different API audience for functional tests, this way 
      the production user database is not polluted with tests users */
      propertySources.addLast(new MapPropertySource(
        "functest_source",
        Map.of(AUDIENCE_PROP_NAME, "turnip-functional-test-api")));
    });

    jetty.getServer().start();
  }

  public static void shutdownTurnip(){
    try {
      jetty.shutdown();
    }
    catch( Exception e ){
      fail("Jetty did not shutdown properly after unit tests", e);
    }
  }
  
}