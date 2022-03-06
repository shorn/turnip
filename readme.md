Example project for running a Jetty server with JDK 17 and Spring.

### Building / Running

Best to set the JDK you run the project with in your IDE to 17, though it 
should work withanything back to Java 8 (Gradle will download the correct 
JDK version based on the toolchain specification in build.gradle).


### Jetty 10 vs 11

Jetty 11 is identical to Jetty 10 except that the javax.* packages now conform
to the new jakarta.* namespace. Jetty 10 and 11 will remain in lockstep with
each other for releases, meaning all new features or bug fixes in one version
will be available in the other.