package turnip.functional.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import turnip.util.Guard;
import turnip.util.Log;

import javax.annotation.PostConstruct;

import static java.lang.String.format;
import static turnip.util.Log.to;

@Component
public class AuthnTokenSvc {
  protected Log log = to(AuthnTokenSvc.class);
  
  private String user;
  private String admin;

  @Autowired private FuncTestProps props;
  @Autowired private RestTemplate rest;

  /** Doing this eagerly, the execution time is not counted against 
   whatever test happens to run first */
  @PostConstruct
  public void setup(){
    Guard.hasValue(props.auth0ClientId, "auth0ClientId must be configured");
    Guard.hasValue(props.sharedPassword, "sharedPassword must be configured");
    log.info("load user authn token");
    user = authenticateUser(props.userEmail);

    log.info("load admin authn token");
    admin = authenticateUser(props.adminEmail);

    log.info("warm up the API server");
    get(admin, "/api/warmup", String.class);

  }

  /**
   A user with that email, using the sharedPassword is expected to already
   have been created for the test client/audience.
   Don't share this audience with production, keep them separate.
   In order for this to work, the Auth0 tenant settings must have the default
   realm set ot the connection that contains the users (i.e. 
   functional-test-realm).
   */
  public String authenticateUser(String email) {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    var map = new LinkedMultiValueMap<String, String>();
    map.add("username", email);
    map.add("password", props.sharedPassword);
    map.add("audience", props.auth0Audience);
    map.add("client_id", props.auth0ClientId);
    map.add("client_secret", props.auth0ClientSecret);
    map.add("grant_type", "password");
    map.add("scope", "openid email");

    var request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

    var response = rest.postForEntity(
      format("https://%s/oauth/token", props.auth0TenantDomain),
      request, Auth0AuthToken.class);

    return response.getBody().access_token;
  }

  private <T> T get(String authnToken, String url, Class<T> returnType){
    Guard.notNull(authnToken);
    HttpHeaders epHeaders = new HttpHeaders();
    epHeaders.set("Authorization", "bearer "+ authnToken);

    HttpEntity entity = new HttpEntity(epHeaders);

    var epResponse = rest.exchange(format("http://%s%s", props.turnipApiServer, url),
      HttpMethod.GET, entity, returnType);

    return epResponse.getBody();
  }

  public String getUser() {
    return user;
  }

  public String getAdmin() {
    return admin;
  }

  static record Auth0AuthToken(
    String token_type, String scope, String access_token
  ) {
  }

}
