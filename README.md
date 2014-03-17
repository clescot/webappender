# webappender

## Goal

webappender allow to overflow your [logback](http://logback.qos.ch) logs to your [firefox](http://www.mozilla.org/firefox/new/) or [chrome](http://www.google.com/chrome/‎) browser.

It permits to simply visualize logs of your java remote server, directly in your browser.


## test quickly the demo webapp

You can test our super simple demo webapp, to illustrate the features of the webappender library.

### prerequisites

* [git](http://git-scm.com/) client.
* [java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [maven 3](http://maven.apache.org) or
higher
* [firefox](http://www.mozilla.org/fr/firefox/new/), need to be installed.
* [firebug](https://addons.mozilla.org/fr/firefox/addon/firebug/)
* [firelogger](https://addons.mozilla.org/firefox/addon/firelogger) addon.


### install the demo
Follow these instructions in your terminal :

    git clone git@github.com:clescot/webappender.git
    cd webappender/webappender-war-example
    mvn org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run-war -Dwebappender=true



### test in your browser

Launch your firefox browser, hit the F12 touch to activate firebug, and click on the *logger* tab.
 Go to [the webapp demo url](http://127.0.0.1:8080/webappender-war-example), and inspect the logger tab.

 You will see logs of the demo webapp :

 ![a demo webapp with the firelogger tab open](webappender.png)


### additional informations

 Note the additional informations provided on the right panel! when you right-click on a row to display the DOM tab : [additional informations provided on the right](webappender2.png) 




