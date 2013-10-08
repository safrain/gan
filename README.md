# GrooveAdmiN
GrooveAdmiN is a light weight debugging/management tool embedded in Java web applications.

You can upload local script file and run it on your server.

Through GrooveAdmiN, you can do your management work easily with the power of dynamic language.

## Features

- Upload local script file and run it on server
- Lightweight, minimum dependencies, easy to embed into your project
- Bash client, curl to install, one command get everything done
- Running task management
- Easy to extend and customize

## User Guide

### Embbed GrooveAdmiN in you project

1.Add following jars into you classpath

- [GrooveAdmiN](http://search.maven.org/remotecontent?filepath=com/github/safrain/gan/0.1/gan-0.1.jar)
- [Groovy Runtime](http://groovy.codehaus.org/Download) Any version greater than 1.8.6 is OK 

For maven projects, add below content into you pom

	<dependency>
		<groupId>com.github.safrain</groupId>
		<artifactId>gan</artifactId>
		<version>0.1</version>
	</dependency>

	<dependency>
		<groupId>org.codehaus.groovy</groupId>
		<artifactId>groovy-all</artifactId>
		<version>1.8.9</version>
	</dependency>
            
2.Add GANFilter configuration into your *web.xml*

**Attention:Exposing this filter may cause serious security problems**

	<filter>
		<filter-name>GAN</filter-name>
		<filter-class>com.github.safrain.gan.GANFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>GAN</filter-name>
		<url-pattern>/gan</url-pattern>
	</filter-mapping>

**Filter init params**

*charset* Request and Response character encoding, 'utf-8' as default.

### Using GrooveAdmiN

#### Show help screen
You could get some help by using an http GET on GANFilter

    curl -s http://localhost/gan

####Upload a script 
Use an http POST on GANFilter with a file, the file will be executed on server

    curl -X POST http://localhost/gan -T foo.groovy

or

    curl -X POST http://localhost/gan -d "println 'Hello'"

####Output to servlet response
Use println to directly output to servlet response, *stdout* and *stderr* had been redirected to servlet response
    
    println 'Hello GrooveAdmiN'

####Get request parameters

Access built-in variables, and use them just like in Java

    println _request.getParameter('foo')

####Access beans in spring context (Spring extension required)
    
    def someBean = beans.beanName
    def anotherBean = beans['beanName']
    
####List beans defined in spring context (Spring extension required)

    println beans
    
####Access spring context

    println beans.context

**Built-in variables**

*_request* Just the request

*_response* Just the response

*\_servlet\_context* Just the servletContext

*_this* GANFilter itself

*_engine* the javax.script.ScriptEngine running current script

*beans* Spring extension interface

### Using bash client

Assume that you configured GANFilter in you application at http://localhost/, '/gan' as url pattern:

#### Install

	curl -s http://localhost/gan?install | bash

GrooveAdmiN bash client 'gan' will be downloaded to current folder, current server address will be used as default server address of the client(in ~/.gan_host).

#### Upload script and run

You can upload a local script file and execute it on server.

	./gan foo.groovy

#### Specify server address and save it as default server address
Script will be executed at 'www.server-address.com/gan', and default server address of the client will be changed to 'www.server-address.com/gan'

    ./gan -h www.server-address.com/gan foo.groovy

####List running scripts
    
    ./gan -l
    
####Kill running script

    ./gan -k uuid

### Extending

#### Script extensions
*Script Extensions* will be executed before the script you uploaded,
In the extension script, you can put some variables or function into the script context to add support to various frameworks
Or you can simply put some of your favorite utilities in it.

Here are some default extensions

**Task management** (com/github/safrain/gan/task.groovy)

List all running scripts

    _gan_running()

Kill running script

    _gan_kill('uuid')

**Spring support** (com/github/safrain/gan/spring.groovy)

Access spring application context

    println beans.context

List all bean definitions in current application context

    println beans
    
Access bean by name

    beans.beanName

*_context* Just the spring ApplicationContext in your ServletContext

*beans* Shortcut to access your beans, use 'beans.beanName' to access your bean

###Customizing

Here are some methods in com.github.safrain.gan.GANFilter, they are designed to be overriden for sake of easy customizing

* **createScriptEngine** Return your script engine to support some other language, like Javascript, Scala, etc.
* **getScriptBeforeEvaluation** Return your customized extension script list
* **getWelcomeScreen** Return a welcome message while doing http GET on GANFilter
* **getInstallScript** Return bash script using to install the client
* **getClient** Return the customized bash client
* **getReplacements** Return k/v pairs for replacing, like server address

## Try it now

You need [gradle](http://www.gradle.org/) to build/run the example project
    
Clone the project

    git clone git@github.com:safrain/gan.git

Run the example project in jetty

    gradle jettyRunWar
    
Then show the welcome screen, and follow its instructions
   
    curl localhost:8080/gan

## TODOs

* Auth filter
* Log filter