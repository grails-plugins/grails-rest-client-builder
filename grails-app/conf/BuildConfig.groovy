grails.project.work.dir = 'target'
grails.project.source.level = 1.6

grails.project.dependency.resolver="maven"
grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenCentral()
	}

	dependencies {
		compile 'org.grails:grails-datastore-rest-client:1.0.0.RELEASE', {
            excludes 'grails-core', 'grails-web', 'grails-plugin-converters'
        }
	}

    plugins {
        build(":release:3.0.1") {
            excludes 'rest-client-builder'
        }
    }
}
