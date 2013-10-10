# GrooveAdmiN
GrooveAdmiN 是一个简单轻便的Java Web工程管理工具

你可以使用它上传本地的groovy文件并在你的服务器上执行

使用GrooveAdmiN，你可以借助强大的动态语言来简化你的工作

## 特性

- 在服务器端执行本地的脚本文件
- 轻便，几乎没有额外依赖，可以被轻松的整合到你的项目中
- 符合Restful语义
- 提供基于Bash的客户端，使用curl命令就可以安装 
- 可以管理正在运行的任务
- 易于定制和扩展

##使用说明

### 将GrooveAdmiN整合到你的项目中

1.在你的classpath下加入如下依赖

- [GrooveAdmiN](https://oss.sonatype.org/service/local/repositories/snapshots/content/com/github/safrain/gan/1.0-SNAPSHOT/gan-1.0-20131010.060527-1.jar)
- [Groovy Runtime](http://groovy.codehaus.org/Download) 任意高于1.8.6的版本皆可

对于Maven管理的项目，把如下内容加入你的pom中

	<dependency>
		<groupId>com.github.safrain</groupId>
		<artifactId>gan</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>

	<dependency>
		<groupId>org.codehaus.groovy</groupId>
		<artifactId>groovy-all</artifactId>
		<version>1.8.9</version>
	</dependency>

2.在你的 *web.xml* 加入GANFilter

**当心:直接暴露这个Filter可能会导致严重的安全问题，请考虑限制客户端IP或配合有身份验证功能的Filter使用**

	<filter>
		<filter-name>GAN</filter-name>
		<filter-class>com.github.safrain.gan.GANFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>GAN</filter-name>
		<url-pattern>/gan</url-pattern>
	</filter-mapping>

**Filter 初始化参数**

*charset* 请求和响应的编码， 默认为 'utf-8'


### 使用 GrooveAdmiN

#### 显示帮助信息
对 GANFilter 所在的路径进行HTTP GET，可以显示帮助信息

    curl -s http://localhost/gan

####上传脚本并执行 
对 GANFilter 所在的路径进行HTTP POST, 脚本会在服务器端被执行

    curl -X POST http://localhost/gan -T foo.groovy

或直接上传脚本内容

    curl -X POST http://localhost/gan -d "println 'Hello'"

####输出到Response

直接使用 println 输出到 Response， 脚本环境中的 *stdout* 和 *stderr* 都已经被重定向到 Response 中
    
    println 'Hello GrooveAdmiN'

####获得请求参数

就像在Java中一样，使用脚本环境中的内置变量即可

    println _request.getParameter('foo')
    
####访问/操作 Spring 环境

参考下面的 *扩展* 部分

**脚本环境中的内置变量**

*_request* Http请求

*_response* Http响应

*\_servlet\_context* Servlet上下文

*_this* GANFilter 本身

*_engine* 运行当前脚本的 javax.script.ScriptEngine


### 使用Bash客户端

假设你把 GANFilter 配置在了 http://localhost/ 的项目中, '/gan' 作为GANFilter的路径:

#### 安装

	curl -s http://localhost/gan?install | bash

GrooveAdmiN 的bash 客户端 'gan' 会被下载到当前目录中，当前的服务器地址会被用作默认服务器地址(存储在 ~/.gan_host中)

#### 上传脚本并执行

	./gan foo.groovy

#### 指定服务器地址并将其保存为默认地址

脚本会在 www.server-address.com/gan 上被执行, 客户端的默认地址会被修改为 www.server-address.com/gan

    ./gan -h www.server-address.com/gan foo.groovy

#### 运行中脚本列表
    
    ./gan -l
    
#### 结束运行中的脚本

    ./gan -k uuid


### 扩展

#### 脚本扩展
*脚本扩展* 会在你上传的脚本执行前被执行，在脚本扩展中，你可以在脚本环境中定义一些变量和方法，用来支持各种框架，或者放一些你常用的工具方法

下面是一些默认的扩展功能

**任务管理** (com/github/safrain/gan/task.groovy)

显示所有运行中的脚本

    _gan_running()

停止运行中的脚本

    _gan_kill('uuid')

**Spring支持** (com/github/safrain/gan/spring.groovy)

访问Spring环境中的bean
    
    def someBean = beans.beanName
    def anotherBean = beans['beanName']
    
显示所有定义在Spring环境中的bean

    println beans
    
访问Spring环境

    println beans.context

###定制

下面的 com.github.safrain.gan.GANFilter 中的若干方法, 是被设计用来被覆盖的，以实现方便的定制

* **createScriptEngine** 返回你自己的脚本引擎，用来支持其他的语言，例如Scala，Javascript等
* **getScriptBeforeEvaluation** 返回你自己的脚本扩展列表
* **getWelcomeScreen** 返回你自定义的帮助信息
* **getInstallScript** 返回客户端安装脚本
* **getClient** 返回你定制的客户端
* **getReplacements** 返回帮助信息和客户端中需要被替换的键值对，例如服务器地址

## 试一下？

你需要安装 [gradle](http://www.gradle.org/) 来构建和运行示例项目
    
从git clone项目

    git clone git@github.com:safrain/gan.git

在Jetty中运行项目

    gradle jettyRunWar
    
然后显示帮助信息，按照其中的提示做就可以了
   
    curl localhost:8080/gan

## TODOs

* 客户端支持HTTP 基本认证和 HTTP 摘要认证


# GrooveAdmiN
GrooveAdmiN is a light weight debugging/management tool embedded in Java web applications.

You can upload local groovy script file and run it on your server.

Through GrooveAdmiN, you can do your management work easily with the power of dynamic language.

## Features

- Upload local script file and run it on server
- Lightweight, minimum dependencies, easy to embed into your project
- GrooveAdmiN is Restful 
- Bash client, curl to install, one command get everything done
- Running task management
- Easy to extend and customize

## User Guide

### Embbed GrooveAdmiN in you project

1.Add following jars into you classpath

- [GrooveAdmiN](https://oss.sonatype.org/service/local/repositories/snapshots/content/com/github/safrain/gan/1.0-SNAPSHOT/gan-1.0-20131010.060527-1.jar)
- [Groovy Runtime](http://groovy.codehaus.org/Download) Any version greater than 1.8.6 is OK 

For maven projects, add below content into you pom

	<dependency>
		<groupId>com.github.safrain</groupId>
		<artifactId>gan</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>

	<dependency>
		<groupId>org.codehaus.groovy</groupId>
		<artifactId>groovy-all</artifactId>
		<version>1.8.9</version>
	</dependency>
            
2.Add GANFilter configuration into your *web.xml*

**Attention:Exposing this filter may cause serious security problems, consider restrict remote address or add an authentication filter**

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
    
####Access/Manipulate spring context

See *Extending* section below

**Built-in variables**

*_request* Just the request

*_response* Just the response

*\_servlet\_context* Just the servletContext

*_this* GANFilter itself

*_engine* the javax.script.ScriptEngine running current script

### Using bash client

Assume that you configured GANFilter in you application at http://localhost/, '/gan' as url pattern:

#### Install

	curl -s http://localhost/gan?install | bash

GrooveAdmiN bash client 'gan' will be downloaded to current folder, current server address will be used as default server address of the client(in ~/.gan_host).

#### Upload script and run

You can upload a local script file and execute it on server.

	./gan foo.groovy

#### Specify server address and save it as default server address
Script will be executed at <server address>, and default server address of the client will be changed to <server address>

    ./gan -h <server address> foo.groovy

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

Access beans in spring context
    
    def someBean = beans.beanName
    def anotherBean = beans['beanName']
    
List beans defined in spring context

    println beans
    
Access spring application context

    println beans.context

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

* Support http basic auth and digest auth on bash client
