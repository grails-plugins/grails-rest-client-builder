package grails.plugins.rest.client

import grails.converters.JSON
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import grails.web.JSONBuilder
import groovy.util.slurpersupport.GPathResult

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Shared

@TestMixin(ControllerUnitTestMixin)
class RestBuilderSpec extends Specification {

    @Shared RestBuilder rest = new RestBuilder()

    def "Test proxy configuration"() {
        when:"RestBuilder is configured with proxy settings"
            def restProxy = new RestBuilder(proxy:['localhost':8888])
            def proxyAddress = restProxy.restTemplate.requestFactory?.@proxy?.address()

        then:"The proxy settings are correct"
            proxyAddress != null
            proxyAddress.hostName == "localhost"
            proxyAddress.port == 8888
    }

    def "Test that a basic GET request returns a JSON result of the response type is JSON"(){
        when:"A get request is issued for a response that returns XML"
            def resp = rest.get("http://grails.org/api/v1.0/plugin/acegi/")

        then:"The response is a gpath result"
            resp != null
            resp.json instanceof JSONObject
            resp.json.name == 'acegi'
    }

    def "Test that obtaining a 404 response doesn't throw an exception but instead returns the response object for inspection"() {
        when:"A get request is issued to a URL that returns a 404"
            def resp = rest.get("http://grails.org/api/v1.0/plugin/nonsense") {
                accept "application/xml"
            }

        then:"Check the status"
            resp.status == 404
            resp.text instanceof String
            resp.body instanceof byte[]
    }

    def "Test that a basic GET request returns a JSON result of the response type is JSON with custom settings"(){
        given:"A rest client instance"
            def restConfig = new RestBuilder(connectTimeout:1000, readTimeout:20000)

        when:"A get request is issued for a response that returns XML"
            def resp = restConfig.get("http://grails.org/api/v1.0/plugin/acegi/")

        then:"The response is a gpath result"
            resp != null
            resp.json instanceof JSONObject
            resp.json.name == 'acegi'
    }

    def "Test that a basic GET request returns a XML result of the response type is XML"(){
        when:"A get request is issued for a response that returns XML"
            def resp = rest.get("http://grails.org/api/v1.0/plugin/acegi/") {
                accept 'application/xml'
            }

        then:"The response is a gpath result"
            resp != null
            resp.xml instanceof GPathResult
            resp.xml.name == 'acegi'
    }

    @Ignore
    def "Test basic authentication with GET request"() {
        when:"A get request is issued for a response that returns XML"
            def resp = rest.get("http://repo.grails.org/grails/api/security/users"){
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
            }

        then:"The response is a gpath result"
            resp != null
            resp.json instanceof JSONArray
    }

    @Ignore
    def "Test basic authentication with PUT request"() {
        when:"A get request is issued for a response that returns XML"
            def resp = rest.put("http://repo.grails.org/grails/api/security/groups/test-group"){
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
                contentType "application/vnd.org.jfrog.artifactory.security.Group+json"
                json {
                    name = "test-group"
                    description = "A temporary test group"
                }
            }
        then:"The response is a gpath result"
            resp != null
            resp.status == 201
            resp.text == "Created"

        when:"The resource contents are requested"
            resp = rest.get("http://repo.grails.org/grails/api/security/groups/test-group") {
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
            }

        then:"The contents are valid"
            resp != null
            resp.json.name == 'test-group'

        when:"The resource is deleted"
            resp = rest.delete("http://repo.grails.org/grails/api/security/groups/test-group") {
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
            }

        then:"The resource is gone"
            resp != null
            resp.status == 200
            resp.text == "Group 'test-group' has been removed successfully."
    }

    @Ignore
    def "Test basic authentication with PUT request and JSON body"() {
        when:"A get request is issued for a response that returns XML"
            def builder = new JSONBuilder()
            JSON j = builder.build {
                name = "test-group"
                description = "A temporary test group"
            }
            def resp = rest.put("http://repo.grails.org/grails/api/security/groups/test-group"){
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
                contentType "application/vnd.org.jfrog.artifactory.security.Group+json"
                body j
            }
        then:"The response is a gpath result"
            resp != null
            resp.status == 201
            resp.text == "Created"

        when:"The resource contents are requested"
            resp = rest.get("http://repo.grails.org/grails/api/security/groups/test-group") {
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
            }

        then:"The contents are valid"
            resp != null
            resp.json.name == 'test-group'

        when:"The resource is deleted"
            resp = rest.delete("http://repo.grails.org/grails/api/security/groups/test-group") {
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
            }

        then:"The resource is gone"
            resp != null
            resp.status == 200
            resp.text == "Group 'test-group' has been removed successfully."
    }

    @Ignore
    def "Test basic authentication with PUT request and JSON as map"() {
        when:"A get request is issued for a response that returns XML"
            def builder = new JSONBuilder()
            def j = [
                name : "test-group",
                description : "A temporary test group"
            ]
            def resp = rest.put("http://repo.grails.org/grails/api/security/groups/test-group"){
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
                contentType "application/vnd.org.jfrog.artifactory.security.Group+json"
                json j
            }
        then:"The response is a gpath result"
            resp != null
            resp.status == 201
            resp.text == "Created"

        when:"The resource contents are requested"
            resp = rest.get("http://repo.grails.org/grails/api/security/groups/test-group") {
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
            }

        then:"The contents are valid"
            resp != null
            resp.json.name == 'test-group'

        when:"The resource is deleted"
            resp = rest.delete("http://repo.grails.org/grails/api/security/groups/test-group") {
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
            }

        then:"The resource is gone"
            resp != null
            resp.status == 200
            resp.text == "Group 'test-group' has been removed successfully."
    }

    @Ignore
    def "Test PUT request passing binary content in the body"() {
        setup:
            def restBinary = new RestBuilder(connectTimeout: 1000, readTimeout:10000)
            restBinary.delete("http://repo.grails.org/grails/libs-snapshots-local/org/mycompany/1.0/foo-1.0.jar") {
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
            }

        when:"A put request is issued that contains binary content"
            def resp = restBinary.put("http://repo.grails.org/grails/libs-snapshots-local/org/mycompany/1.0/foo-1.0.jar") {
                auth System.getProperty("artifactory.user"), System.getProperty("artifactory.pass")
                body "foo".bytes
            }

        then:"The response JSON is correct"
            resp.json != null
            resp.json.uri == "http://repo.grails.org/grails/libs-snapshots-local/org/mycompany/1.0/foo-1.0.jar"
            new URL( "http://repo.grails.org/grails/libs-snapshots-local/org/mycompany/1.0/foo-1.0.jar").text == "foo"
    }

	// Note that this test uses JSON query parameters, but they are not actually validated due to the call
	//	not using them. If a call that processes JSON URL parameters if used, this test would mean much more.
	@Issue("https://github.com/grails-plugins/grails-rest-client-builder/issues/3")
	def "Test URL variables for JSON URL paremeters"() {
		when:"A get request with URL parameters defined as an implicit map"
			def resp = rest.get("http://grails.org/api/v1.0/plugin/acegi/?query={query}&filter={filter}") {
				urlVariables query:'{"query":true}', filter:'{"filter":true}'
			}

        then:"The response is a gpath result"
            resp != null
            resp.json instanceof JSONObject
            resp.json.name == 'acegi'

		when:"A get request with URL parameters defined as an explicit map"
			resp = rest.get("http://grails.org/api/v1.0/plugin/acegi/?query={query}&filter={filter}") {
				urlVariables([query:'{"query":true}', filter:'{"filter":true}'])
			}

		then:"The response is a gpath result"
			resp != null
			resp.json instanceof JSONObject
			resp.json.name == 'acegi'
	}

   def "Test using the fake ssl socket factory"() {
      when:"make a https call to a url that does not have a full cert"
        def restFake = new RestBuilder(RestBuilder.getFakeSSLClient())
        def resp = restFake.get("https://uklo1.symphonycloud.savvis.com/api/apidefinitions") {
          accept "application/*+xml;version=5.1"
        }

      then:"The response XML is correct"
        resp.status >= 400
    }

   def "Test getting header values"() {
      when:"make a call to google"
        def resp = rest.get("https://www.google.com")

        def header = resp.getHeader('server')

      then:"The server header is gws"
        resp.status == 200
        header != null
        header == 'gws'
    }
}
