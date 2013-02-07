#deliciousfeeds4J - Wrapper for delicious feeds-API v2

##Introduction

I started this project in order to build a recommender to find new and interesting links and websites by using delicious. They have an API for posting links and managing your account - a wrapper for this can be found here: [https://github.com/czarneckid/delicious-java](https://github.com/czarneckid/delicious-java).

But they also have another API called "delicious feeds" where you can retrieve information in JSON- or RSS-format. This library wraps this feeds API in an comfortable way to work with.

A few examples of what you can get:
- Recent bookmarks posted on delicious
- Public bookmarks for a specific user
- Public summary information about a user
- Recent bookmarks for a URL
- Summary information about a URL
- ...

##A small example of usage
Simple and easy to use - here an example of finding the 10 most recent bookmarks posted on delicious (from all users):

```java
//Create an instance and set expandUrls to true
final DeliciousFeeds deliciousFeeds = new DeliciousFeeds();
deliciousFeeds.setExpandUrls(true);

//Find the 10 most recent bookmarks
final List<Bookmark> bookmarks = deliciousFeeds.findBookmarks();

...
```

##How to install and setup this library
Be sure you have installed [git](http://git-scm.com/) and [Apache Maven](http://maven.apache.org/) correctly. Then you can run the following commands to install this library in your local repository:

```
git clone git://github.com/p-meier/deliciousfeeds4J.git
cd deliciousfeeds4J/
mvn install

#Optional delete the sources
rm -rf deliciousfeeds4J/
```

After this you have the current version of the library in your local repository and have to add this to your 
`pom.xml`:
```xml
<dependency>
    <groupId>com.delicious</groupId>
    <artifactId>deliciousfeeds4J</artifactId>
    <version>0.9.0</version>
</dependency>
```


##Options explained
###expandUrls (defaults to `false`)
Some URLs are shortened when they are returned by delicious (e.g. http://icio.us/+a7f570d6d6842). With this option this URLs get expanded (for the previous example to: http://www.competitionline.com/de/wettbewerbe/116699). The only downside on this is it takes time because another request must be made to get the expanded version - that's why it defaults to `false`.

###constainAPILimit (defaults to `false`)
The delicious API has an constraint to wait 1 second between requests. So far as I can tell this does not apply to the Feeds-API (this is why it defaults to `false`). But if you encounter errors, you can try to set this to `true`.

###userAgent (defaults to `"deliciousfeeds4j Java/1.6"`)
Set another userAgent-String which is used for requests. For the normal delicious API this should be 
_"something identifiable"_. So far as I can tell this does not apply to the Feeds-API. But if you encounter unexpected errors, maybe you set the userAgent to something more suitable or real (e.g. `"Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:16.0.1)"`).


##Logging
###Uses SLF4J
This library uses [SLF4J](http://www.slf4j.org/) for logging. But you need an implementation of this API - for example [logback](http://logback.qos.ch/).

###Configure Logback
First you need to include the logback-library in your classpath. If you use Maven add this to your `pom.xml`:
```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.0.9</version>
</dependency>
```

Then add a file named `logback.xml` with the following content to your classpath:
```xml
<configuration scan="true" scanPeriod="15 seconds">
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>%d{HH:mm:ss.SSS} [%thread] %.-1level %logger{36} - %msg%n</Pattern>
        </layout>
    </appender>

    <root level="TRACE">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

##License
If you find deliciousfeeds4J useful or have issues please drop me a line, I would love to hear how you're using it.

deliciousfeeds4J is licensed under the Apache 2.0 license, see the LICENSE file for more details.

##Delicious-Feeds API and Javadoc
You can find the official documentation from Delicious here: [[https://delicious.com/developers]]

The Javadoc can be found here: [[http://p-meier.github.com/deliciousfeeds4J/]]
