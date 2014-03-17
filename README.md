# Webappender

## Goal

webappender allow you to overflow your [JEE](http://en.wikipedia.org/wiki/Java_Platform,_Enterprise_Edition) [logback](http://logback.qos.ch) logs to your [firefox](http://www.mozilla.org/firefox/new/) or [chrome](http://www.google.com/chrome/‎) browser.

It permits to simply visualize logs of your java remote server, directly in your browser.

## Features

### Install the webappender library in your webapp

webappender support actually, [JEE](http://en.wikipedia.org/wiki/Java_Platform,_Enterprise_Edition) webapps which use the [logback](http://logback.qos.ch) library.

1. You have to add to your pom.xml this dependency :
   
	```xml
	<dependency>
	  <groupId>com.clescot</groupId>
	    <artifactId>webappender</artifactId>
	    <version>1.0</version>
	</dependency>
	```

	A ServletFilter installed via annotation is shipped with the library. Its *urlPatterns* maps to all requests (`/*`).

2. Activate the webappender

	By default, webappender is **disabled** .

	**Visualizing logs can be very risky and dangerous in production environments**.

	So, to prevent any configuration error, we disable by default the webappender.
	To activate it, you have to put this parameter on the command line launching your application server :
	`-Dwebappender=true`.


 
### What kind of logs i can visualize ?

webappender, output only logs generated by the thread of **your request**. It permits to diagnose easily some unit problems, and avoid any interwoven hell from multiple simultaneously requests.

### visualize your logback logs into your firefox browser

when the webappender is shipped in your webapp, and your application server is up, you have to install [the fireLogger plugin](https://addons.mozilla.org/en-us/firefox/addon/firelogger/).

When it's done, hit the F12 key, to visualise the firebug panel ;  you should see a new logger panel. It will highlight your logs, according to the level.

### Visualize your logback logs into your chrome browser

when the webappender is shipped in your webapp, and your application server is up, you have to [install](https://chrome.google.com/webstore/detail/chrome-logger/noaneddfkdjfnfdakjjmocngnfkfehhd) [the chrome logger plugin](http://craig.is/writing/chrome-logger).

When it's done, hit the F12 key, to visualise the **console** panel. It will highlight your logs, when you navigate on your webapp.

### Tune log output

Webappender permits to tune the log output, via some specific request headers.
All of them start with the `X-wa` prefix, to explain that this option is non standard (*X*), and specific to the webappender (*wa*). 

#### Reduce log verbosity

webappender, permits to reduce the verbosity level, to :

1. lower the response header overhead 
2. inactivate time consuming logback display options

To reduce the log verbosity across all browsers, you have to put in your request these header key and value : 

`X-wa-verbose-logs=false`.

If you use firefox, we recommend the [modify headers plugin](https://addons.mozilla.org/en-US/firefox/addon/modify-headers/)

#### Filtering logs

##### Reduce logs by a threshold filter

##### Reduce logs by a level filter

## Test quickly the demo webapp

You can test our super simple demo webapp, to illustrate the features of the webappender library.

### Prerequisites of the test

* [git](http://git-scm.com/) client.
* [java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [maven 3](http://maven.apache.org) or
higher
* [firefox](http://www.mozilla.org/fr/firefox/new/), need to be installed.
* [firebug](https://addons.mozilla.org/fr/firefox/addon/firebug/)
* [firelogger](https://addons.mozilla.org/firefox/addon/firelogger) addon.


### Install the demo
Follow these instructions in your terminal :

    git clone git@github.com:clescot/webappender.git
    cd webappender/webappender-war-example
    mvn org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run-war -Dwebappender=true



### Test in your browser

Launch your firefox browser, hit the F12 touch to activate firebug, and click on the *logger* tab.
 Go to [the webapp demo url](http://127.0.0.1:8080/webappender-war-example), and inspect the logger tab.

 You will see logs of the demo webapp :

 ![a demo webapp with the firelogger tab open](webappender.png)


### Additional informations

 Note the additional informations provided on the right panel! when you right-click on a row to display the DOM tab : 

![additional informations provided on the right](webappender2.png) 




