package turnip.spring.config;

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

  private String audience;
  private String issuer;
  
  public WebSecurityConfig(
    @Value("${auth0.audience:}") String audience, 
    @Value("${auth0.issuer:}") String issuer
  ) {
    Guard.hasValue("auth0.audience must be set", audience);
    Guard.hasValue("auth0.issuer must be set", issuer);
    log.info("init audience=%s, issuer=%s", audience, issuer);
    this.audience = audience;
    this.issuer = issuer;
  }

  public void configure(HttpSecurity http) throws Exception {
    log.info("config http");
    http.httpBasic().disable().csrf().disable().
      authorizeRequests().
        mvcMatchers(API + "/**").fullyAuthenticated().
        mvcMatchers(PUBLIC + "/**").permitAll().
        anyRequest().denyAll().
      and().
        sessionManagement().sessionCreationPolicy(STATELESS);  
  }

}
