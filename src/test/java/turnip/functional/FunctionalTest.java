package turnip.functional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;
import turnip.functional.config.AuthnTokenSvc;
import turnip.functional.config.FuncTestProps;
import turnip.functional.config.FunctionalTestConfig;
import turnip.util.Log;

import static java.lang.String.format;
import static turnip.util.Log.to;
import static turnip.util.RestUtil.createEntityWithBearer;

@SpringJUnitConfig(FunctionalTestConfig.class)
@ExtendWith({FunctionalTestExtension.class})
public abstract class FunctionalTest {
  protected Log log = to(getClass());

  @Autowired protected RestTemplate rest;
  @Autowired protected FuncTestProps props;
  @Autowired protected AuthnTokenSvc token;

  public <T> T get(String authnToken, String url, Class<T> returnType){
    HttpEntity<T> entity = createEntityWithBearer(authnToken);

    var epResponse = rest.exchange(format("http://%s%s", props.turnipApiServer, url),
      HttpMethod.GET, entity, returnType);
    
    return epResponse.getBody();
  }

}
