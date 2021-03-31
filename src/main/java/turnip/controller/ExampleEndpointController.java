package turnip.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turnip.spring.security.Role;

import java.util.Map;

import static turnip.spring.config.WebSecurityConfig.API;
import static turnip.spring.config.WebSecurityConfig.PUBLIC;

@RestController
public class ExampleEndpointController {
  private static Logger log = 
    LoggerFactory.getLogger(ExampleEndpointController.class);

  @Role.User
  @GetMapping(API+"/endpoint1")
  public Map<String, String> endpoint1() {
    log.info("endpoint1");
    return Map.of("user stuff", "42" );
  }

  @Role.Admin
  @GetMapping(PUBLIC+"/endpoint2")
  public Map<String, String> endpoint2() {
    log.info("endpoint2");
    return Map.of("public stuff", "42" );
  }
}