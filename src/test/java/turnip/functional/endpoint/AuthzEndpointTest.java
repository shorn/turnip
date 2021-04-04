package turnip.functional.endpoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import turnip.endpoint.MiscAdmin;
import turnip.functional.FunctionalTest;
import turnip.functional.spring.bean.UserManager;
import turnip.service.AuthzSvc;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.http.HttpMethod.POST;
import static turnip.util.RestUtil.createEntityWithBearer;

public class AuthzEndpointTest extends FunctionalTest {
  public static final Map<String, String> EMPTY_POST_PARAM = Map.of(
    "requestData", "from AuthzEndpointTest");

  /** 
   The value is an object that will be passed to the POST request.
   Should be defined as a value that will allow the endpoint request to succeed 
   without throwing an internal error because of NullPonterException or 
   similar.  
   The intent is to minimise polluting the test logs with a bunch of 
   error stack traces that aren't actually a problem (note the tests still  
   still pass if the endpoint fails).  The errors are just distracting and 
   they waste the time of everybody so does the right thing and investigates 
   the errors thinking they're a potential problem.
   */
  private Map<String, Object> postParams;
  
  @Autowired private UserManager userManager;
  
  @TestFactory
  public Stream<DynamicTest> allEndpointsShouldBeAuthorized() {
    return jettyTestServer.getTurnipApiHandlerMethods().entrySet().stream().
      flatMap((i) -> createEndpointTests(i.getKey(), i.getValue()));
  }
  
  @BeforeEach
  public void configureAllowedParams(TestInfo testInfo){
    if( postParams != null ){
      return;
    }
    postParams = new HashMap<>();
    postParams.put("/api/add-user", new MiscAdmin.AddUserRequest(
      userManager.formatNewUserEmail(getTestPrefix(testInfo)),
      AuthzSvc.USER_ROLE ));
  }
  
  public Stream<DynamicTest> createEndpointTests(
    RequestMappingInfo mapping,
    HandlerMethod handler
  ){
    String path = mapping.getDirectPaths().stream().toList().get(0);
    return mapping.getMethodsCondition().getMethods().stream().map(iMethod-> {
      if( iMethod == RequestMethod.GET ){
        return dynamicTest(
          "GET " + path,
          () -> testGetEndpoint(path, handler));
      }
      
      if( iMethod == RequestMethod.POST ){
        return dynamicTest(
          "POST " + path,
          () -> testPostEndpoint(path, handler));
      }

      throw new UnsupportedOperationException(
        "don't know how to deal with method: " + iMethod);
    });
  }

  /* This likely won't work very well for dynamic path structures, but I 
  mostly only use POST requests anyway. */
  private void testGetEndpoint(String path, HandlerMethod handler) {
    HttpEntity<Object> entity =
      createEntityWithBearer(token.getNonUser());
    log.msg("testing endpoint").with("method", "GET").with("path", path).info();
    try {
      rest.exchange(turnipApiServerUrl(path), HttpMethod.GET, entity, String.class);
      fail("should not have been able to call GET endpoint: " + path);
    }
    catch( HttpClientErrorException e ){
      assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
    }
    catch( HttpServerErrorException e ){
      // if the server gets to the point of having an 500 error, then it must
      // not have validated the user
      fail("should not have been able to call GET endpoint: " + path);
    }

    // any GET endpoint should be callable by the admin user
    entity = createEntityWithBearer(token.getAdmin());
    try {
      rest.exchange(turnipApiServerUrl(path), HttpMethod.GET, entity, String.class);
      // if the test gets to here, then the server got past the security 
      // check and actually executed successfully
    }
    catch( HttpServerErrorException e ){
      // this is Ok, if it got to the point of throwing a 500, then it got
      // past any security check
    }
  }

  private void testPostEndpoint(String path, HandlerMethod handler) {
    Object body = postParams.getOrDefault(path, EMPTY_POST_PARAM);
    HttpEntity<Object> entity =
      createEntityWithBearer(token.getNonUser(), body);

    try {
      rest.exchange(turnipApiServerUrl(path), POST, entity, String.class);
      fail("should not have been able to call GET endpoint: " + path);
    }
    catch( HttpClientErrorException e ){
      assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
    }
    catch( HttpServerErrorException e ){
      // if the server gets to the point of having an 500 error, then it must
      // not have validated the user
      fail("should not have been able to call GET endpoint: " + path);
    }

    // any POST endpoint should be callable by the admin user
    entity = createEntityWithBearer(token.getAdmin(), body);
    try {
      rest.exchange(turnipApiServerUrl(path), POST, entity, String.class);
      // if the test gets to here, then the server got past the security 
      // check and actually executed successfully
    }
    catch( HttpServerErrorException e ){
      // this is Ok, if it got to the point of throwing a 500, then it got
      // past any security check
    }
  }
}

