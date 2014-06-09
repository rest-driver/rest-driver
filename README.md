#REST-driver

[![Build Status](https://secure.travis-ci.org/rest-driver/rest-driver.png?branch=master)](http://travis-ci.org/rest-driver/rest-driver)

Test Driver to test your RESTful services and clients.

There are two libraries here:

* REST server driver - for testing your RESTful service
* REST client driver - for testing your RESTful client & mocking remote services

**Latest version: 1.1.40**

[Downloads](https://github.com/rest-driver/rest-driver/wiki/Downloads) - [Server Driver](https://github.com/rest-driver/rest-driver/wiki/Server-Driver) - [Client Driver](https://github.com/rest-driver/rest-driver/wiki/Client-driver)

##Goals

Everyone knows that testing is good for your health and REST is good for your sanity.  Now it is easy to keep both in check by allowing you to write _quick_, _readable_, _fluid_ tests which are easy to refactor and give excellent error handling/reporting.  So we provide these libraries to test from both ends of the pipe.

##REST server driver

In order to do thorough testing of your RESTful service, you'll have to make actual HTTP requests and check the actual HTTP responses from your service.  REST driver makes this as easy as:

```java
Response response = get( "http://www.example.com" );

assertThat(response, hasStatusCode(200));
assertThat(response.asJson(), hasJsonPath("$.name", equalTo("jeff")));
```

More info: [Server Driver](https://github.com/rest-driver/rest-driver/wiki/Server-Driver)

##REST client driver

If you have a client for a RESTful service, it's not ideal to have an actual service running somewhere to test against.  This is difficult to keep on top of, makes for brittle/flickering tests, and tightly couples your tests to someone else's code.

We provide a mock-like interface which launches a real HTTP server and allows setup like this:

```java
clientDriver.addExpectation(onRequestTo("/").withMethod(Method.GET), 
                            giveResponse("some wonderful content", "text/plain"));
```

The server will listen for a requests and respond with the replies you set.  Any unexpected action or unmet expectation will fail your tests.

You can also use the client-driver to mock out any remote services which your RESTful service uses.

More info: [Client Driver](https://github.com/rest-driver/rest-driver/wiki/Client-driver)

## Other languages

* [rest-cljer](https://github.com/whostolebenfrog/rest-cljer), a convenient Clojure wrapper for REST client driver.

