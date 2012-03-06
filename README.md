# Rest Client Builder Grails Plugin

## Installation

Edit `BuildConfig.groovy` and add the following dependency:

    compile ":rest-client-builder:1.0"
    
## Basic Usage

Main entry point is the `grails.plugins.rest.client.RestBuilder` class. Construct and use one of the REST "verbs".

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

