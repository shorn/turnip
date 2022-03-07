Example project for running a Jetty server with JDK 17 and Spring.


## Building / Running

Best to set the JDK you run the project with in your IDE to 17, though it 
should work withanything back to Java 8 (Gradle will download the correct 
JDK version based on the toolchain specification in build.gradle).


## Setup for Auth0

The prod code and the functional tests integrate against Auth0.

## Must have a "Tenant"
* I created a tentant named "rabbit-turnip", which maps to 
`rabbit-turnip.us.auth0.com`
* this is the domain used when talking to the Auth0 APIs
* Functional tests: configured via property `funcTestAuth0TenantDomain`

## Must have an "API"
* I created an API with name and ID of "turnip-functional-test-api"
* the "Identifier" value is what goes in the `audience` field of the token 
* Functional tests: `audience` is read form property `funcTestAuth0Audience`  

## Must have an "Application" 
* I created an Application named "turnip-functional-test-app", enabled for
"turnip-functional-test-api".
* The Auth0 console shows values for "Client ID" and "Client Secret", 
which map to `client_id` and `client_secret` token fields.
* Functional tests: `client_id` is read from `funcTestAuth0ClientId`, 
`client_secret` is read from `funcTestAuth0ClientSecret`.

## Must define an "Auth Pipeline rule" to populate the custom token fields
Create a rule with the following defintion (name doesn't matter):
```
function (user, context, callback) {
  const namespace = 'http://turnip_';
  context.accessToken[namespace + 'email'] = user.email;
  context.accessToken[namespace + 'email_verified'] = user.email_verified;
  callback(null, user, context);
}
```

The rule isn't strictly necessary for using Auth0, but it helps me enapsulate 
dependencies on Auth0 (instead of calling Auth0 API for getting the email).

If I want to replace Auth0, I only need to make sure the new ID service 
populates these non-standard claims (along with standard claims like `audience`
and `issuer` and publishing KWKS properly), and my back-end security code could 
theoretically be completely Auth0 agnostic.

Note that currently Turnip is NOT Auth0-agnostic.  It uses use the Auth0
Spring integration library, which takes care of the fiddly JWKS stuff and token 
validation, etc.  But that's a fairly small bit of technical implementation,
compared to tying the app into how Auth0 authorization works. 


## Setup for functional tests

I store my credentials in my Keepass (/Rabbit/Auth0/Turnip Auth0).

You must set the various properties to talk to Auth0 before the functional
tests will run.   

You must create the users in Auth0 by hand, setting the same password for all
of them.

Example confif file: `~/.config/turnip/functest.properties`:
```
funcTestAuth0TenantDomain=rabbit-turnip.us.auth0.com
funcTestAuth0Audience=turnip-functional-test-api
funcTestAuth0ClientId=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
funcTestAuth0ClientSecret=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX-XXXXXXXXXXXXXX_XX_XX
funcTestSharedPassword=SuperSecretPasswordOfUnbreakableness
```

Functional tests exists as a separate Gradle source-set from unit tests.
The functonal tests can be run with the `funcTest` class (after Auth0 config).

## Auth0 usage limits

There are strict usage limits for Auth0, especially free accounts - 
eventually will run into them if running too many tests and will start getting
TooManyRequests errors.  
Will have to slow down the tests or something eventually.
Can also get TooManyRequests if messed up the user passwords and accounts get
blocked (can unblock in Auth0 console).

If there's an error with too many requests or similar, the Auth0 API will
return details of the issue (and reset time) in the response headers of the 
request.  The test client logs these headers, but also remember to look in 
the logs on the Auth0 console.


## Jetty 10 vs 11

Jetty 11 is identical to Jetty 10 except that the javax.* packages now conform
to the new jakarta.* namespace. Jetty 10 and 11 will remain in lockstep with
each other for releases, meaning all new features or bug fixes in one version
will be available in the other.


