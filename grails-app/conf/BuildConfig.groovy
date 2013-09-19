grails.project.work.dir = 'target'
grails.project.source.level = 1.6

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
	}

  dependencies {
    compile "org.apache.httpcomponents:httpclient:4.2.4"
  }

	plugins {
	}
}
