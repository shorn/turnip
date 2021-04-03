package turnip.service;

import org.springframework.stereotype.Component;
import turnip.spring.security.Role;
import turnip.util.Log;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static turnip.util.Log.to;

@Component
public class UserSvc {
  private static Log log = to(UserSvc.class);

  private Map<String, List<Role>> userDb = Map.of(
    "turnip-test-user@example.com", AuthzSvc.USER_ROLE,
    "turnip-test-admin@example.com", AuthzSvc.ADMIN_ROLE
  );

  public Optional<UserInfo> findUser(String email){
    List<Role> roles = userDb.get(email);
    if( roles == null ){
      return Optional.empty();
    }
    return Optional.of(new UserInfo(email, roles));
  }
  
  public List<String> listUserEmails(){
    return userDb.keySet().stream().toList();
  }

  public static record UserInfo(String email, List<Role> roles){}
}
