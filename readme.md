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


### Setup for functional tests

The functional tests actually integrate against Auth0, so you must have an
Auth0 account created, with an `API` And `Application` configured.

Most Auth0 settings defaulted for the account stored in Keepass under 
/Rabbit/Auth0/Turnip Auth0, but you must set the `funcTestAuth0ClientSecret`
property (get it from Keepass or from the Application config in the Auth0 
console.

The usernames are defaulted (`funcTestUserEmail` etc.), but the password must 
be set in the `funcTestSharedPassword` property.  
You must create the users in Auth0 by hand, setting the same password for all
of them.

Example `~/.config/turnip/functest.properties`:
```
funcTestAuth0ClientSecret=XXX
funcTestSharedPassword=XXX
```

### Auth0 usage limits

There are strict usage limits for Auth0, especially free accounts - 
eventually will run into them if making too many tests.  Will have to slow down
the tests or something eventually.

If there's an error with too many requests or similar, the Auth0 API will
return details of the issue (and reset time) in the response headers of the 
request.  The test client logs these headers, but also remember to look in 
the logs on the Auth0 console.