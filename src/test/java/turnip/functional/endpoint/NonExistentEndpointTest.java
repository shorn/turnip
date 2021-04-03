package turnip.functional.endpoint;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import turnip.functional.FunctionalTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NonExistentEndpointTest extends FunctionalTest {

  @Test
  public void nonExistentEndpoint() {
    try {
      get(token.getUser(), "/api/doesn-not-exist", String.class);
    }
    catch( HttpClientErrorException e ){
      assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    }

  }

}
