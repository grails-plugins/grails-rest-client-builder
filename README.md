[![Build Status](https://travis-ci.org/grails-plugins/grails-rest-client-builder.svg)](https://travis-ci.org/grails-plugins/grails-rest-client-builder)


# Rest Client Builder Grails Plugin

## Notice

The code for this plugin has moved to a [subproject of the Grails Data](https://github.com/grails/grails-data-mapping/blob/master/grails-datastore-rest-client/). Please submit any pull requests there.


## Installation

Edit `BuildConfig.groovy` and add the following dependency:

    compile ":rest-client-builder:2.0.0"
    
For Grails 3.x this plugin *is no longer necessary* and you should instead just declare a dependency on the core Grails Data library:

     compile 'org.grails:grails-datastore-rest-client'
    

## Basic Usage

For API-style documentation refer to the [Groovydocs](http://springsource.github.io/grails-data-mapping/rest-client/api/index.html)

The main entry point is the [grails.plugins.rest.client.RestBuilder](http://springsource.github.io/grails-data-mapping/rest-client/api/grails/plugins/rest/client/RestBuilder.html) class. Construct and use one of the REST "verbs".

A `GET` request:

     def resp = rest.get("http://grails.org/api/v1.0/plugin/acegi/")

The response is a Spring [ResponseEntity](http://static.springsource.org/spring/docs/3.0.x/javadoc-api/org/springframework/http/ResponseEntity.html).

There are convenience methods for obtaining JSON:

      resp.json instanceof JSONObject
      resp.json.name == 'acegi'

And XML:

      resp.xml instanceof GPathResult
      resp.xml.name == 'acegi'

## POST and PUT requests

`POST` and `PUT` requests can be issued with the `post` and `put` methods respectively:

            def resp = rest.put("http://repo.grails.org/grails/api/security/groups/test-group"){
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
                contentType "application/vnd.org.jfrog.artifactory.security.Group+json"
                json {
                    name = "test-group"
                    description = "A temporary test group"
                }
            }

In the example above the `auth` method performs HTTP basic auth, the `contentType` method sets the content type, and the `json` method constructs a JSON body.

Another example for posting a JSON with variable keys

            def map = [key_1: value_1, key_2: value_2, ....,  key_n:value_n]
            def resp = rest.put("http://repo.grails.org/grails/api/security/groups/test-group"){
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
                json { map }
            }
                       

### A  `POST` with URL parameters

            def resp = rest.post('http://someServer/foo/bar?username={username}&password={password}') {
                urlVariables [username:"someDude", password:"abc#123"]
            }

Please note the above url is not a GString with String interpolation but a String that uses the format/convention specified in the RestTemplate javadoc.

http://docs.spring.io/spring/docs/3.2.x/javadoc-api/org/springframework/web/client/RestTemplate.html


## Exchanging JSON and XML content

As demonstrated in the previous example you can send JSON data using the json method which accepts the same syntax as Grails' JSONBuilder. Alternatively you can provide a String, or any other valid Java object that is convertable to JSON. The following are also valid examples:

            def resp = rest.put("http://repo.grails.org/grails/api/security/groups/test-group"){
                ...
                contentType "application/vnd.org.jfrog.artifactory.security.Group+json"
                json name: "test-group", description: "A temporary test group"
            }

            def resp = rest.put("http://repo.grails.org/grails/api/security/groups/test-group"){
                ...
                contentType "application/vnd.org.jfrog.artifactory.security.Group+json"
                json '{ name: "test-group", description: "A temporary test group" }'
            }

If you don't explicitly set the contentType like is done above then the default content type for the `json` method is "application/json".

XML is very similar, there is an `xml` method that takes a closure to build XML, and object which is convertable to XML or a String of XML.

The response object has `xml` or `json` properties for reading the response of the request as demonstratd in the "Basic Usage" section.

## Sending custom headers

You can include in your request any additional header:

    def resp = rest.put("http://example.org/api/login") {
        header 'X-Auth-Token', '1a2b3c4d5e6f7g8h'
    }

## Testing

Testing can be done with Spring's RestTemplate mocking APIs. See the tests for RestBuilder itself for [an example](https://github.com/grails-plugins/grails-rest-client-builder/blob/master/test/unit/grails/plugins/rest/client/RestBuilderSpec.groovy#L57).

## Multipart Requests

Multipart requests are possible by setting properties of the request body to `File`, `URL`, `byte[]` or `InputStream` instances:

        def resp = rest.post(url) {
            contentType "multipart/form-data"
            zip = new File(pluginPackage)
            pom = new File(pomFile)
            xml = new File(pluginXmlFile)
        }

## Connection/Proxy Configuration

Connection and proxy configuration can be specified in the constructor to `RestBuilder`:

    def rest = new RestBuilder(connectTimeout:1000, readTimeout:20000, proxy:['localhost':8888])

The proxy setting can either be map containing the key for the host name and a value for the port or an instance of `java.net.Proxy`.
