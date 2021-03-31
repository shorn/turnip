package turnip.spring.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import turnip.util.ExceptionUtil;
import turnip.util.Guard;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/** Note: Deep in the bowels of spring somwhere, there's logic that filters 
 * values without the "ROLE_" prefix. 
 * Dunno why, but it means all our roles must have that prefix.
 * (This may have become configurable in recent Spring versions, I can't be
 * bothered pfaffing about with it).
 * <p/>
 * Be careful of changing the actual string values, they match to the enum 
 * values defined in the database.  
 * Would have liked to use the literal value generated from the DB, but then
 * it won't be usable in an annotation ("attribute value must be constant").
 * There's a unit test to test equality of the values instead.
 */
public enum Role implements GrantedAuthority{
  Admin(Role.ADMINUSER),
  User(Role.USER);

  /** Can add / maintain users */
  public static final String ADMINUSER = "ROLE_ADMIN";
  /** Can user normal system funcations */
  public static final String USER = "ROLE_USER";


  public static final String HAS_ROLE_ANY_USER =
    "hasAnyRole(" +
      "'" + Role.ADMINUSER + "'," +
      "'" + Role.USER + "'" +
      ")";

  public static boolean hasAdmin(List<Role> roles){
    return roles.contains(Admin);
  }

  private String authority;

  Role(String authority) {
    Guard.hasValue(authority);
    this.authority = authority;
  }

  @Override
  public String getAuthority() {
    return authority;
  }

  @Override
  public String toString() {
    return "Role{" +
      "authority='" + authority + '\'' +
      '}';
  }

  public static Role map(String value){
    Guard.hasValue("value to map to Role cannot be empty", value);
    value = value.trim();
    if( ADMINUSER.equalsIgnoreCase(value) ){
      return Role.Admin;
    }
    else if( USER.equalsIgnoreCase(value) ){
      return Role.User;
    }
    else {
      throw ExceptionUtil.createIllegalArgException(
        "cannot convert value to Role: '%s'", value );
    }
  }


  @Target({ElementType.METHOD, ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @Inherited
  @Documented
  @PreAuthorize(
    "hasAnyRole(" +
      "'" + Role.ADMINUSER + "'" +
    ")"
  )  
  public @interface Admin{

  }

  @Target({ElementType.METHOD, ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @Inherited
  @Documented
  @PreAuthorize(HAS_ROLE_ANY_USER)
  public @interface User{

  }
  
}
