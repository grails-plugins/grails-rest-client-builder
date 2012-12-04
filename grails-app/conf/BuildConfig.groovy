grails.project.work.dir = 'target'
grails.project.source.level = 1.6

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
	}

	plugins {
		test ":spock:0.6", {
			excludes 'xml-apis'
			export = false
		}
	}
}
