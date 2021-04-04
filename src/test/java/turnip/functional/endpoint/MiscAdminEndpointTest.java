package turnip.functional.endpoint;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import turnip.endpoint.MiscAdmin.AddUserRequest;
import turnip.endpoint.MiscAdmin.ListUsersResult;
import turnip.functional.FunctionalTest;
import turnip.functional.spring.bean.UserManager;
import turnip.service.UserSvc.UserInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static turnip.service.AuthzSvc.USER_ROLE;

/**
The actual application logic doesn't really make sense because it's not a real
app.  The current endpoints just exist so I can demonstrate GET/POST requests. 
 */
public class MiscAdminEndpointTest extends FunctionalTest {

  @Autowired private UserManager userManager;
  
  @Test
  public void addUserFlowShouldWork(TestInfo testInfo){
    var userInfo = get(token.getUser(), "/api/user-info", UserInfo.class);
    assertEquals(props.userEmail, userInfo.email());

    log.debug("only admin should be able to call /list-users");
    try {
      get(token.getUser(), "/api/list-users", ListUsersResult.class);
      fail("should have failed because of user not authz");
    }
    catch( HttpClientErrorException e ){
      assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
    }

    String newUserEmail = userManager.
      formatNewUserEmail(getTestPrefix(testInfo));

    log.info("should not list new user");
    ListUsersResult listResult = 
      get(token.getAdmin(), "/api/list-users", ListUsersResult.class);
    // will start failing (possibly intermittently) once result is paginated
    assertFalse(listResult.emails().contains(newUserEmail));
    
    log.info("should add new user");
    UserInfo newUser = post(token.getAdmin(), "/api/add-user", 
      new AddUserRequest(newUserEmail, USER_ROLE), UserInfo.class);
    log.info("newUser email %s", newUser.email());
    
    log.info("should list new user");
    listResult =
      get(token.getAdmin(), "/api/list-users", ListUsersResult.class);
    // will start failing (possibly intermittently) once result is paginated
    assertTrue(listResult.emails().contains(newUserEmail));
  }


}
