package turnip.spring.config;

import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import turnip.util.Guard;
import turnip.util.Log;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static turnip.util.Log.to;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  public static final String API = "/api";
  public static final String PUBLIC = "/public";

  private static Log log = to(WebSecurityConfig.class);

  /** The "audience of the JWT" - i.e. the target system (i.e. this 
   server) the JWT is passed to in order verify that the bearer of the JWT is 
   authentic. */
  private String audience;
  /** The "issuer of the JWT" - i.e. the system that issued the JWT to the JWT
   to the bearer. */
  private String issuer;
  
  public WebSecurityConfig(
    @Value("${auth0.audience:https://localhost:8080}") String audience, 
    @Value("${auth0.issuer:https://rabbit-turnip.us.auth0.com/}") String issuer
  ) {
    Guard.hasValue("auth0.audience must be set", audience);
    Guard.hasValue("auth0.issuer must be set", issuer);
    log.info("init audience=%s, issuer=%s", audience, issuer);
    this.audience = audience;
    this.issuer = issuer;
  }


  @Override
  protected void configure(HttpSecurity http) throws Exception {
    JwtWebSecurityConfigurer.
      forRS256(audience, issuer).
      configure(http).authorizeRequests().
        mvcMatchers(API + "/**").fullyAuthenticated().
        mvcMatchers(PUBLIC + "/**").permitAll().
        anyRequest().denyAll().
      and().
        sessionManagement().sessionCreationPolicy(STATELESS);  
  }
}
