/*
 * Copyright (c) 2013 by Patrick Meier.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.delicious.deliciousfeeds4J;

import com.delicious.deliciousfeeds4J.beans.*;
import com.delicious.deliciousfeeds4J.exceptions.DeliciousFeedsException;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class DeliciousUtilTest {

    @Test
    public void testBuildPair() throws Exception {
        assertEquals("test -> value", DeliciousUtil.buildPair("test", "value"));
        assertEquals("test2 -> null", DeliciousUtil.buildPair("test2", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildPairWithoutName() throws Exception {
        DeliciousUtil.buildPair(null, "test");
    }

    @Test
    public void testDoGetRequest() throws Exception {
        final String result = DeliciousUtil.doGetRequest("http://feeds.delicious.com/v2/json/tags/hubert64297", DeliciousFeeds.DEFAULT_USER_AGENT, false);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.startsWith("{"));
        assertTrue(result.endsWith("}"));
    }

    @Test
    public void testExpandShortenedUrl() throws Exception {
        final String expanded = DeliciousUtil.expandShortenedUrl("http://icio.us/+a7f570d6d6842", DeliciousFeeds.DEFAULT_USER_AGENT);
        assertEquals("http://www.competitionline.com/de/wettbewerbe/116699", expanded);
    }

    @Test
    public void testExpandShortenedUrlWithExpandedUrl() throws Exception {
        final String expanded = DeliciousUtil.expandShortenedUrl("http://www.competitionline.com/de/wettbewerbe/116699",
                DeliciousFeeds.DEFAULT_USER_AGENT);
        assertEquals("http://www.competitionline.com/de/wettbewerbe/116699", expanded);
    }

    @Test
    public void testExpandShortenedUrlWithoutUrl() throws Exception {
        assertEquals(null, DeliciousUtil.expandShortenedUrl(null, DeliciousFeeds.DEFAULT_USER_AGENT));
        assertEquals("", DeliciousUtil.expandShortenedUrl("", DeliciousFeeds.DEFAULT_USER_AGENT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExpandShortenedUrlWithoutUserAgent() throws Exception {
        DeliciousUtil.expandShortenedUrl("http://www.competitionline.com/de/wettbewerbe/116699", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExpandShortenedUrlWithUserAgentNull() throws Exception {
        DeliciousUtil.expandShortenedUrl("http://www.competitionline.com/de/wettbewerbe/116699", null);
    }

    @Test
    public void testBuildException() throws Exception {
        final DeliciousFeedsException exception = DeliciousUtil.buildException(new RuntimeException(), "", "Test");

        assertNotNull(exception);
        assertEquals("Test!", exception.getMessage());
    }

    @Test
    public void testBuildExceptionWithParam() throws Exception {
        final DeliciousFeedsException exception = DeliciousUtil.buildException(new RuntimeException(), "", "Test", DeliciousUtil.buildPair("test", "value"));

        assertNotNull(exception);
        assertEquals("Test: [test -> value]", exception.getMessage());
    }

    @Test
    public void testBuildExceptionWithManyParams() throws Exception {
        final DeliciousFeedsException exception = DeliciousUtil.buildException(new RuntimeException(), "", "Test",
                DeliciousUtil.buildPair("test", "value"), DeliciousUtil.buildPair("test2", "value2"));

        assertNotNull(exception);
        assertEquals("Test: [test -> value, test2 -> value2]", exception.getMessage());
    }

    @Test
    public void testBuildExceptionWithErrorResult() throws Exception {
        final DeliciousFeedsException exception = DeliciousUtil.buildException(new RuntimeException(),
                "[{\"result\": {\"message\": \"something went wrong\", \"code\": 1000}}]", "Test",
                DeliciousUtil.buildPair("test", "value"));

        assertNotNull(exception);
        assertEquals("Test: [test -> value, message -> something went wrong, code -> 1000]", exception.getMessage());
    }


    @Test
    public void testDeserializeUsersFromJson() throws Exception {

        final String SAMPLE_DATA = "[{\"dt\": \"2012-10-29T12:42:29Z\", \"user\": \"testuser\"}]";

        final Set<User> users = DeliciousUtil.deserializeUsersFromJson(SAMPLE_DATA);

        assertNotNull(users);
        assertFalse(users.isEmpty());

        final User user = users.iterator().next();

        assertNotNull(user);
        assertEquals("testuser", user.getUser());

        final Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        calendar.set(Calendar.MONTH, Calendar.OCTOBER);
        calendar.set(Calendar.YEAR, 2012);

        assertTrue(DateUtils.isSameDay(calendar.getTime(), user.getAddedDate()));
    }

    @Test
    public void testDeserializeErrorResult() throws Exception {

        final String SAMPLE_DATA = "[{\"result\": {\"message\": \"something went wrong\", \"code\": 1000}}]";

        final ErrorResult errorResult = DeliciousUtil.deserializeErrorResult(SAMPLE_DATA);

        assertNotNull(errorResult);
        assertEquals("something went wrong", errorResult.getMessage());
        assertEquals(1000, errorResult.getCode());
    }

    @Test
    public void testDeserializeErrorResultWithWrongValues() throws Exception {

        final String SAMPLE_DATA_1 = "{\"result\": {\"message\": \"something went wrong\", \"code\": 1000}}";
        final String SAMPLE_DATA_2 = "{\"hurz\": {\"message\": \"something went wrong\", \"code\": 1000}}";
        final String SAMPLE_DATA_3 = "[{\"result\": 2}]";

        assertNull(DeliciousUtil.deserializeErrorResult(SAMPLE_DATA_1));
        assertNull(DeliciousUtil.deserializeErrorResult(SAMPLE_DATA_2));
        assertNull(DeliciousUtil.deserializeErrorResult(SAMPLE_DATA_3));
    }

    @Test
    public void testDeserializeTagsFromJson() throws Exception {

        final String SAMPLE_DATA = "{\"science\": 1, \"nlp\": 2}";

        final Set<Tag> tags = DeliciousUtil.deserializeTagsFromJson(SAMPLE_DATA);

        assertNotNull(tags);
        assertEquals(2, tags.size());

        final Tag science = new Tag();
        science.setCount(1);
        science.setName("science");

        final Tag nlp = new Tag();
        nlp.setCount(2);
        nlp.setName("nlp");

        assertTrue(tags.contains(science));
        assertTrue(tags.contains(nlp));
    }

    @Test
    public void testDeserializePublicUserInformationFromJson() throws Exception {

        final String SAMPLE_DATA = "[{\"n\": 196, \"d\": \"Items\", \"id\": \"items\"}]";
        final String User = "testuser";

        final UserInfo userInfo = DeliciousUtil.deserializePublicUserInformationFromJson(User, SAMPLE_DATA);

        assertNotNull(userInfo);
        assertNotNull(userInfo.getUserInfoDetailSet());
        assertFalse(userInfo.getUserInfoDetailSet().isEmpty());
        assertEquals(1, userInfo.getUserInfoDetailSet().size());
        assertEquals(User, userInfo.getUser());

        final UserInfoDetail userInfoDetail = userInfo.getUserInfoDetailSet().iterator().next();

        assertNotNull(userInfoDetail);
        assertEquals("items", userInfoDetail.getId());
        assertEquals("Items", userInfoDetail.getDescription());
        assertEquals(196, userInfoDetail.getCount());
    }

    @Test
    public void testDeserializeBookmarksFromJson() throws Exception {

        final String SAMPLE_DATA = "[{\"a\": \"test123\", \"d\": \"Writing Reactive Apps with ReactiveMongo and Play\", " +
                "\"n\": \"\", \"u\": \"http://stephane.godbillon.com/2012/10/18/writing-a-simple-app-with-reactivemongo-and-play-framework-pt-1.html\"," +
                " \"t\": [\"web\", \"programming\", \"mongodb\", \"scala\"], \"dt\": \"2012-10-22T13:40:31Z\", \"md5\": \"4967ef979fca2b4629c3d5ad70f83c01\"}]";

        final List<Bookmark> bookmarks = DeliciousUtil.deserializeBookmarksFromJson(SAMPLE_DATA);

        assertNotNull(bookmarks);
        assertFalse(bookmarks.isEmpty());
        assertEquals(1, bookmarks.size());

        final Bookmark bookmark = bookmarks.iterator().next();

        assertEquals("test123", bookmark.getUser());
        assertEquals("Writing Reactive Apps with ReactiveMongo and Play", bookmark.getTitle());
        assertEquals("", bookmark.getDescription());
        assertEquals("http://stephane.godbillon.com/2012/10/18/writing-a-simple-app-with-reactivemongo-and-play-framework-pt-1.html", bookmark.getUrl());
        assertNotNull(bookmark.getTags());
        assertTrue(bookmark.getTags().contains("web"));

        final Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 22);
        calendar.set(Calendar.MONTH, Calendar.OCTOBER);
        calendar.set(Calendar.YEAR, 2012);

        assertTrue(DateUtils.isSameDay(calendar.getTime(), bookmark.getLastUpdatedDate()));
    }

    @Test
    public void testDeserializeUrlInfosFromJson() throws Exception {

        final String SAMPLE_DATA = "[{\"url\": \"http://namechk.com/\", \"total_posts\": 5849, \"top_tags\": {\"username\": 1, " +
                "\"web2.0\": 1, \"search\": 1, \"management\": 1, \"web\": 1, \"socialmedia\": 1, \"socialnetworking\": 1, " +
                "\"social\": 1, \"tools\": 1, \"check\": 1}, \"hash\": \"80e661f28a8f9fb62b4003af90fad6ed\", \"title\": " +
                "\"Check Username Availability at Multiple Social Networking Sites\"}]";

        final UrlInfo urlInfo = DeliciousUtil.deserializeUrlInfoFromJson(SAMPLE_DATA);

        assertNotNull(urlInfo);
        assertEquals("http://namechk.com/", urlInfo.getUrl());
        assertEquals("80e661f28a8f9fb62b4003af90fad6ed", urlInfo.getHash());
        assertEquals("Check Username Availability at Multiple Social Networking Sites", urlInfo.getTitle());
        assertEquals(5849, urlInfo.getTotalPosts());
        assertNotNull(urlInfo.getTopTags());
        assertEquals(10, urlInfo.getTopTags().size());

        final Tag science = new Tag();
        science.setCount(1);
        science.setName("socialnetworking");

        final Tag nlp = new Tag();
        nlp.setCount(1);
        nlp.setName("check");

        assertTrue(urlInfo.getTopTags().contains(science));
        assertTrue(urlInfo.getTopTags().contains(nlp));
    }
}
