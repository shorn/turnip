package turnip.unit;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import turnip.util.RestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestUtilTest {
  @Test
  public void testit(){
    String testToken = "testtoken";
    HttpEntity<?> entity = RestUtil.createEntityWithBearer(testToken);
    assertEquals("bearer " + testToken,
      entity.getHeaders().get("authorization").get(0));
  }
}
