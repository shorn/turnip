package turnip.functional.endpoint;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import turnip.endpoint.MiscAdmin;
import turnip.functional.FunctionalTest;
import turnip.service.UserSvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class AuthzEndpointTest extends FunctionalTest {
  
  @Test
  public void userInfoIsCallable(){
    var userInfo = get(token.getUser(), "/api/user-info", UserSvc.UserInfo.class);
    assertEquals(props.userEmail, userInfo.email());

    try {
      get(token.getUser(), "/api/list-users", MiscAdmin.ListUsersResult.class);
      fail("should have failed because of user not authz");
    }
    catch( HttpClientErrorException e ){
      assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
    }

    get(token.getAdmin(), "/api/list-users", MiscAdmin.ListUsersResult.class);

  }

}

