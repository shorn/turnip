package turnip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/hello")
public class HelloWorldController {
  private static Logger log = 
    LoggerFactory.getLogger(HelloWorldController.class);

  @GetMapping
  public Map<String, String> sayHello() {
    log.info("hello'd");
    return Map.of(
      "Hello", "world", 
      "Spring", "5",
      "Jetty", "10" );
    
  }
}