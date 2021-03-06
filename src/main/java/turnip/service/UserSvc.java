package turnip.service;

import org.springframework.stereotype.Component;
import turnip.spring.security.Role;
import turnip.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static turnip.util.ExceptionUtil.createIllegalArgException;
import static turnip.util.Log.to;

/**
 Quick 'm dirty user database.  Will make it use a real DB at some point.
 */
@Component
public class UserSvc {
  private static Log log = to(UserSvc.class);

  private Map<String, List<Role>> userDb = new HashMap<>(Map.of(
    "turnip-test-user@example.com", AuthzSvc.USER_ROLE,
    "turnip-test-admin@example.com", AuthzSvc.ADMIN_ROLE
  ));

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
  
  public void addUser(UserInfo userInfo){
    if( userDb.containsKey(userInfo.email) ){
      throw createIllegalArgException("duplicate email: %s", userInfo.email);
    }
    if( userInfo.roles.isEmpty() ){
      throw createIllegalArgException(
        "use rmust have a role: %s", userInfo.email);
    }
    userDb.put(userInfo.email, userInfo.roles);
  }

  public static record UserInfo(String email, List<Role> roles){}
}
