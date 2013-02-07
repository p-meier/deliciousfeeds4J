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

Here is the API documentation from delicious: [https://delicious.com/developers](https://delicious.com/developers)

##License
If you find deliciousfeeds4J useful or have issues please drop me a line, I would love to hear how you're using it.

deliciousfeeds4J is licensed under the Apache 2.0 license, see the LICENSE file for more details.

##A small example of usage
Simple and easy to use - here an example of finding the 10 most recent bookmarks posted on delicious (from all users):

    //Create an instance
    final DeliciousFeeds deliciousFeeds = new DeliciousFeeds();

    //Expand Urls (defaults to false) - Some Urls are shortened like this 'http://icio.us/+a7f570d6d6842'
    //and get expanded to with that option to this: 'http://www.competitionline.com/de/wettbewerbe/116699'
    deliciousFeeds.setExpandUrls(true);

    //Set a proper userAgent-String (recommended by delicious - but did not cause any trouble during my
    //tests without it (default is: 'deliciousfeeds4j Java/1.7'))
    deliciousFeeds.setUserAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:16.0.1)");

    //Find the 10 most recent bookmarks
    final List<Bookmark> bookmarks = deliciousFeeds.findBookmarks();

##Javadoc
Here is the Javadoc of the project: [http://p-meier.github.com/deliciousfeeds4J/](http://p-meier.github.com/deliciousfeeds4J/)

##Wiki
More documentation and exact usage can be found on the Github-Wiki.