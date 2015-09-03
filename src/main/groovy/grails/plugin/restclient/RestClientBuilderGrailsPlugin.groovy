package grails.plugin.restclient

import grails.plugins.Plugin

class RestClientBuilderGrailsPlugin extends Plugin {

	def grailsVersion = '3.0.0 > *'
	def title = 'REST Client Builder Plugin'
	def description = 'Grails REST Client Builder Plugin'
	def documentation = 'https://github.com/grails-plugins/grails-rest-client-builder/'
	def license = 'APACHE'
	def organization = [name: 'Grails', url: 'http://www.grails.org/']
	def developers = [[name: 'Graeme Rocher', email: 'graeme.rocher@gmail.com']]
	def issueManagement = [url: 'http://jira.grails.org/browse/GPRESTCLIENTBUILDER']
	def scm = [url: 'https://github.com/grails-plugins/grails-rest-client-builder']
	def profiles = ['web']
}
