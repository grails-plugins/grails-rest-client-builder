if(System.getenv('TRAVIS_BRANCH')) {
    grails.project.repos.grailsCentral.username = System.getenv("GRAILS_CENTRAL_USERNAME")
    grails.project.repos.grailsCentral.password = System.getenv("GRAILS_CENTRAL_PASSWORD")
}

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
        compile 'org.grails:grails-datastore-rest-client:3.1.4.RELEASE', {
            exclude group:'javax.servlet', name:'javax.servlet-api'
            exclude group:'commons-codec', name:'commons-codec'
            exclude group:'org.grails', name:'grails-plugin-converters'
            exclude group:'org.grails', name:'grails-core'
            exclude group:'org.grails', name:'grails-web'
        }
    }

    plugins {
        build(":release:3.1.0") {
            export = false
            excludes 'rest-client-builder'
        }
    }
}
