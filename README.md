# webappender

## Goal

webappender allow to overflow to a firefox plugin, your [logback](http://logback.qos.ch) logs.

It permits to simply visualize logs of your remote server, directly in your browser.

## test quickly

### prerequisites

[java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html), [maven 3](http://maven.apache.org) or
higher, [firefox](http://www.mozilla.org/fr/firefox/new/), need to be installed.
Install the [firebug](https://addons.mozilla.org/fr/firefox/addon/firebug/) and [firelogger firefox](https://addons.mozilla.org/firefox/addon/firelogger) addons.


    git clone git@github.com:clescot/webappender.git
    cd webappender/webappender-war-example
    mvn org.apache.tomcat.maven:tomcat7-maven-plugin:2.2:run-war -Dwebappender=true

launch your firefox browser, hit the F12 touch to activate firebug, and click on the 'logger' tab.
 Go to [the webapp demo url](http://127.0.0.1:8080/webappender-war-example), and inspect the logger tab.

 You will see




