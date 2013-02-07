/*
 * Copyright (c) 2013 by Patrick Meier.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.delicious.deliciousfeeds4J;

import com.delicious.deliciousfeeds4J.beans.*;
import com.delicious.deliciousfeeds4J.exceptions.DeliciousFeedsException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.delicious.deliciousfeeds4J.DeliciousUtil.*;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * DeliciousFeeds is a class to access the Feeds-API from <a href="http://delicious.com">Delicious</a>. It uses the current
 * version 2 of it: <a href="http://delicious.com/developers">Feeds-API Documentation v2</a>
 *
 * @author Patrick Meier
 */
public class DeliciousFeeds {

    private static final Logger logger = LoggerFactory.getLogger(DeliciousFeeds.class);

    //---------------------------------------------------------------------------
    // Constants
    //---------------------------------------------------------------------------

    //Some default values
    public static final int DEFAULT_COUNT = 10;

    public static final String DEFAULT_USER_AGENT = "deliciousfeeds4j Java/1.6";

    public static final boolean DEFAULT_EXPAND_URLS = false;

    public static final boolean DEFAULT_CONTRAIN_API_LIMIT = false;

    //The API Endpoint
    private static final String API_ENDPOINT = "http://feeds.delicious.com/v2/json/";

    //All Feeds available
    private static final String RECENT_BOOKMARKS = "recent";

    private static final String RECENT_BOOKMARKS_BY_TAG = "tag";

    private static final String PUBLIC_USER_SUMMARY = "userinfo";

    private static final String PUBLIC_TAGS = "tags";

    private static final String PRIVATE_USER_INBOX = "inbox";

    private static final String USER_NETWORK = "network";

    private static final String USER_NETWORK_MEMBERS = "networkmembers";

    private static final String URL_BOOKMARKS = "url";

    private static final String URL_INFO = "urlinfo";

    private static final String POPULAR_BOOKMARKS = "popular";

    //---------------------------------------------------------------------------
    // Instance fields
    //---------------------------------------------------------------------------

    private String userAgent = DEFAULT_USER_AGENT;

    private boolean expandUrls = DEFAULT_EXPAND_URLS;

    private boolean constainAPILimit = DEFAULT_CONTRAIN_API_LIMIT;

    //---------------------------------------------------------------------------
    // Methods
    //---------------------------------------------------------------------------

    private List<Bookmark> expandUrlsFromBookmarks(List<Bookmark> bookmarks) throws IOException {

        //Maybe expand the shortened urls
        if (expandUrls && bookmarks != null && bookmarks.size() > 0) {
            for (Bookmark bookmark : bookmarks)
                bookmark.setUrl(expandShortenedUrl(bookmark.getUrl(), userAgent));

            logger.info("Successfully expanded all shortened urls!");
        }

        return bookmarks;
    }

    /**
     * Find the most recent bookmarks on delicious from all users. Here the default count
     * of 10 entries is used.
     *
     * @return a list of the most recent bookmarks, null if nothing found!
     * @throws DeliciousFeedsException if something goes wrong
     */
    public List<Bookmark> findBookmarks() throws DeliciousFeedsException {
        return findBookmarks(DEFAULT_COUNT);
    }

    /**
     * Find the most recent bookmarks on delicious from all users.
     *
     * @param count how many entries should be returned - from 1 to 100
     * @return a list of the most recent bookmarks, null if nothing found!
     * @throws IllegalArgumentException if count does not match borders
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findBookmarks(int count) throws DeliciousFeedsException {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count has to be a value from minimum 1 to maximum 100!");

        logger.info("Trying to find the last {} recent bookmarks from all users...", count);

        final String json = doGetRequest(API_ENDPOINT + RECENT_BOOKMARKS + "?count=" + count, userAgent, constainAPILimit);

        try {
            List<Bookmark> bookmarks = deserializeBookmarksFromJson(json);

            //Expand Urls
            bookmarks = expandUrlsFromBookmarks(bookmarks);

            return bookmarks;
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding recent bookmarks",
                    buildPair("count", Integer.toString(count)));
        }
    }

    /**
     * Find the most popular bookmarks on delicious from all users. Here the default count
     * of 10 entries is used.
     *
     * @return a list of the most popular bookmarks, null if nothing found!
     * @throws DeliciousFeedsException if something goes wrong
     */
    public List<Bookmark> findPopularBookmarks() throws DeliciousFeedsException {
        return findBookmarks(DEFAULT_COUNT);
    }

    /**
     * Find the most popular bookmarks on delicious from all users.
     *
     * @param count how many entries should be returned - from 1 to 100
     * @return a list of the most popular bookmarks, null if nothing found!
     * @throws IllegalArgumentException if count does not match borders
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findPopularBookmarks(int count) throws DeliciousFeedsException {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count has to be a value from minimum 1 to maximum 100!");

        logger.info("Trying to find the last {} popular bookmarks from all users...", count);

        final String json = doGetRequest(API_ENDPOINT + POPULAR_BOOKMARKS + "?count=" + count, userAgent, constainAPILimit);

        try {
            List<Bookmark> bookmarks = deserializeBookmarksFromJson(json);

            //Expand Urls
            bookmarks = expandUrlsFromBookmarks(bookmarks);

            return bookmarks;
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding popular bookmarks",
                    buildPair("count", Integer.toString(count)));
        }
    }

    /**
     * Find the most recent bookmarks on delicious from all users by the given tags. Here the default count
     * of 10 entries is used.
     *
     * @param tags the tags to use (minimum 1!)
     * @return a list of the most recent bookmarks, null if nothing found!
     * @throws IllegalArgumentException tags are null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findBookmarksByTags(String... tags) throws DeliciousFeedsException {
        return findBookmarksByTags(DEFAULT_COUNT, tags);
    }

    /**
     * Find the most recent bookmarks on delicious from all users by the given tags.
     *
     * @param count how many entries should be returned - from 1 to 100
     * @param tags  the tags to use (minimum 1!)
     * @return a list of the most recent bookmarks, null if nothing found!
     * @throws IllegalArgumentException if count does not match borders or tags are null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findBookmarksByTags(int count, String... tags) throws DeliciousFeedsException {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count has to be a value from minimum 1 to maximum 100!");

        if (tags == null || tags.length == 0)
            throw new IllegalArgumentException("Tags must not be null or empty!");

        logger.info("Trying to find the last {} recent bookmarks from all users with this tags: {}", count,
                StringUtils.join(tags, ", "));

        final String json = doGetRequest(API_ENDPOINT + RECENT_BOOKMARKS_BY_TAG + "/" + StringUtils.join(tags, "+")
                + "?count=" + count, userAgent, constainAPILimit);

        try {
            List<Bookmark> bookmarks = deserializeBookmarksFromJson(json);

            //Expand Urls
            bookmarks = expandUrlsFromBookmarks(bookmarks);

            return bookmarks;
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding recent bookmarks by tags",
                    buildPair("count", Integer.toString(count)), buildPair("tags", StringUtils.join(tags, ", ")));
        }
    }

    /**
     * Find the most popular bookmarks on delicious from all users by the given tags. Here the default count
     * of 10 entries is used.
     *
     * @param tags the tags to use (minimum 1!)
     * @return a list of the most popular bookmarks, null if nothing found!
     * @throws IllegalArgumentException tags are null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findPopularBookmarksByTags(String... tags) throws DeliciousFeedsException {
        return findPopularBookmarksByTags(DEFAULT_COUNT, tags);
    }

    /**
     * Find the most popular bookmarks on delicious from all users by the given tags.
     *
     * @param count how many entries should be returned - from 1 to 100
     * @param tags  the tags to use (minimum 1!)
     * @return a list of the most popular bookmarks, null if nothing found!
     * @throws IllegalArgumentException if count does not match borders or tags are null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findPopularBookmarksByTags(int count, String... tags) throws DeliciousFeedsException {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count has to be a value from minimum 1 to maximum 100!");

        if (tags == null || tags.length == 0)
            throw new IllegalArgumentException("Tags must not be null or empty!");

        logger.info("Trying to find the last {} popular bookmarks from all users with this tags: {}", count,
                StringUtils.join(tags, ", "));

        final String json = doGetRequest(API_ENDPOINT + POPULAR_BOOKMARKS + "/" + StringUtils.join(tags, "+")
                + "?count=" + count, userAgent, constainAPILimit);

        try {
            List<Bookmark> bookmarks = deserializeBookmarksFromJson(json);

            //Expand Urls
            bookmarks = expandUrlsFromBookmarks(bookmarks);

            return bookmarks;
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding popular bookmarks by tags",
                    buildPair("count", Integer.toString(count)), buildPair("tags", StringUtils.join(tags, ", ")));
        }
    }

    /**
     * Find the most recent bookmarks on delicious from a specific user. Here the default count
     * of 10 entries is used.
     *
     * @param username the username
     * @return a list of the most recent bookmarks from this user, null if nothing found!
     * @throws IllegalArgumentException if username is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findBookmarksByUser(String username) throws DeliciousFeedsException {
        return findBookmarksByUser(DEFAULT_COUNT, username);
    }

    /**
     * Find the most recent bookmarks on delicious from a specific user.
     *
     * @param count    how many entries should be returned - from 1 to 100
     * @param username the username
     * @return a list of the most recent bookmarks from this user, null if nothing found!
     * @throws IllegalArgumentException if count does not match borders or username is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findBookmarksByUser(int count, String username) throws DeliciousFeedsException {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count has to be a value from minimum 1 to maximum 100!");

        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username must not be null or empty!");

        logger.info("Trying to find the last {} recent bookmarks for user '{}'...", count, username);

        final String json = doGetRequest(API_ENDPOINT + username + "?count=" + count, userAgent, constainAPILimit);

        try {
            List<Bookmark> bookmarks = deserializeBookmarksFromJson(json);

            //Expand Urls
            bookmarks = expandUrlsFromBookmarks(bookmarks);

            return bookmarks;
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding recent bookmarks for user",
                    buildPair("count", Integer.toString(count)), buildPair("username", username));
        }
    }

    /**
     * Find the most recent bookmarks on delicious from a specific user. Here the default count
     * of 10 entries is used.
     *
     * @param username the username
     * @param key      the key to retrieve private bookmarks (can be obtained on delicious.com)
     * @return a list of the most recent bookmarks from this user, null if nothing found!
     * @throws IllegalArgumentException if username is null or empty or key is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findPrivateBookmarksByUser(String username, String key) throws DeliciousFeedsException {
        return findPrivateBookmarksByUser(DEFAULT_COUNT, username, key);
    }

    /**
     * Find the most recent bookmarks on delicious from a specific user.
     *
     * @param count    how many entries should be returned - from 1 to 100
     * @param username the username
     * @param key      the key to retrieve private bookmarks (can be obtained on delicious.com)
     * @return a list of the most recent bookmarks from this user, null if nothing found!
     * @throws IllegalArgumentException if count does not match borders, username is null or empty or key is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findPrivateBookmarksByUser(int count, String username, String key) throws DeliciousFeedsException {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count has to be a value from minimum 1 to maximum 100!");

        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username must not be null or empty!");

        if (key == null | key.isEmpty())
            throw new IllegalArgumentException("Key must not be null or empty!");

        logger.info("Trying to find the last {} recent private bookmarks for user '{}'...", count, username);

        final String json = doGetRequest(API_ENDPOINT + username + "?private=" + key + "&count=" + count, userAgent, constainAPILimit);

        try {
            List<Bookmark> bookmarks = deserializeBookmarksFromJson(json);

            //Expand Urls
            bookmarks = expandUrlsFromBookmarks(bookmarks);

            return bookmarks;
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding recent private bookmarks for user",
                    buildPair("count", Integer.toString(count)), buildPair("username", username),
                    buildPair("key", key));
        }
    }

    /**
     * Find the most recent bookmarks on delicious from a specific user with the given tags. Here the default count
     * of 10 entries is used.
     *
     * @param username the username
     * @param tags     the tags to use (minimum 1!)
     * @return a list of the most recent bookmarks from this user, null if nothing found!
     * @throws IllegalArgumentException username is null or empty or tags are null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findBookmarksByUserAndTags(String username, String... tags) throws DeliciousFeedsException {
        return findBookmarksByUserAndTags(DEFAULT_COUNT, username, tags);
    }

    /**
     * Find the most recent bookmarks on delicious from a specific user with the given tags.
     *
     * @param count    how many entries should be returned - from 1 to 100
     * @param username the username
     * @param tags     the tags to use (minimum 1!)
     * @return a list of the most recent bookmarks from this user, null if nothing found!
     * @throws IllegalArgumentException if count does not match borders, username is null or empty or tags are null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findBookmarksByUserAndTags(int count, String username, String... tags) throws DeliciousFeedsException {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count has to be a value from minimum 1 to maximum 100!");

        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username must not be null or empty!");

        if (tags == null || tags.length == 0)
            throw new IllegalArgumentException("Tags must not be null or empty!");

        logger.info("Trying to find the last {} recent bookmarks for user '{}' with this tags: {}", count, username,
                StringUtils.join(tags, ", "));

        final String json = doGetRequest(API_ENDPOINT + username + "/" + StringUtils.join(tags, "+") + "?count=" + count, userAgent, constainAPILimit);

        try {
            List<Bookmark> bookmarks = deserializeBookmarksFromJson(json);

            //Expand Urls
            bookmarks = expandUrlsFromBookmarks(bookmarks);

            return bookmarks;
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding recent bookmarks for user by tags",
                    buildPair("count", Integer.toString(count)), buildPair("username", username),
                    buildPair("tags", StringUtils.join(tags, ", ")));
        }
    }

    /**
     * Find the most recent bookmarks on delicious from a specific user with the given tags. Here the default count
     * of 10 entries is used.
     *
     * @param username the username
     * @param key      the key to retrieve private bookmarks (can be obtained on delicious.com)
     * @param tags     the tags to use (minimum 1!)
     * @return a list of the most recent bookmarks from this user, null if nothing found!
     * @throws IllegalArgumentException username is null or empty, key is null or empty, or tags are null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findPrivateBookmarksByUserAndTags(String username, String key, String... tags) throws DeliciousFeedsException {
        return findPrivateBookmarksByUserAndTags(DEFAULT_COUNT, username, key, tags);
    }

    /**
     * Find the most recent bookmarks on delicious from a specific user with the given tags.
     *
     * @param count    how many entries should be returned - from 1 to 100
     * @param username the username
     * @param key      the key to retrieve private bookmarks (can be obtained on delicious.com)
     * @param tags     the tags to use (minimum 1!)
     * @return a list of the most recent bookmarks from this user, null if nothing found!
     * @throws IllegalArgumentException if count does not match borders, username is null or empty, key is null or empty, or tags are null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findPrivateBookmarksByUserAndTags(int count, String username, String key, String... tags) throws DeliciousFeedsException {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count has to be a value from minimum 1 to maximum 100!");

        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username must not be null or empty!");

        if (key == null | key.isEmpty())
            throw new IllegalArgumentException("Key must not be null or empty!");

        if (tags == null || tags.length == 0)
            throw new IllegalArgumentException("Tags must not be null or empty!");

        logger.info("Trying to find the last {} recent private bookmarks for user '{}' with this tags: {}", count, username, StringUtils.join(tags, ", "));

        final String json = doGetRequest(API_ENDPOINT + username + "/" + StringUtils.join(tags, "+") + "?private=" + key + "&count=" + count, userAgent, constainAPILimit);

        try {
            List<Bookmark> bookmarks = deserializeBookmarksFromJson(json);

            //Expand Urls
            bookmarks = expandUrlsFromBookmarks(bookmarks);

            return bookmarks;
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding recent private bookmarks for user by tags",
                    buildPair("count", Integer.toString(count)), buildPair("username", username),
                    buildPair("key", key), buildPair("tags", StringUtils.join(tags, ", ")));
        }
    }

    /**
     * Find a public information summary about the given user. This includes the number of fallowers,
     * number of bookmarks and so on.
     *
     * @param username the username
     * @return a summary of public information about the given user, null if nothing found
     * @throws IllegalArgumentException if username is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public UserInfo findPublicUserSummary(String username) throws DeliciousFeedsException {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username must not be null or empty!");

        logger.info("Trying to find a public information summary for user '{}' ...", username);

        final String json = doGetRequest(API_ENDPOINT + PUBLIC_USER_SUMMARY + "/" + username, userAgent, constainAPILimit);

        try {
            return deserializePublicUserInformationFromJson(username, json);
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding public information summary for user",
                    buildPair("username", username));
        }
    }

    /**
     * Find a list of all public tags from the given user. The result is in ascending lexical order.
     *
     * @param username the username
     * @return a list of all public tags from the given user, null if nothing found
     * @throws IllegalArgumentException if username is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public Set<Tag> findPublicTagsByUser(String username) throws DeliciousFeedsException {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username must not be null or empty!");

        logger.info("Trying to find all public tags for user '{}' ...", username);

        final String json = doGetRequest(API_ENDPOINT + PUBLIC_TAGS + "/" + username, userAgent, constainAPILimit);

        try {
            return deserializeTagsFromJson(json);
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding all public tags for user", buildPair("username", username));
        }
    }

    /**
     * Find a list of all related public tags from the given user and tag combination. The result is in ascending lexical order.
     *
     * @param username the username
     * @param tags     the tags to use (minimum 1!)
     * @return a list of all public tags from the given user, null if nothing found
     * @throws IllegalArgumentException if username is null or empty or tags are null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public Set<Tag> findRelatedPublicTagsByUserAndTags(String username, String... tags) throws DeliciousFeedsException {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username must not be null or empty!");

        if (tags == null || tags.length == 0)
            throw new IllegalArgumentException("Tags must not be null or empty!");

        logger.info("Trying to find all related public tags for user '{}' with this tags: {}", username, StringUtils.join(tags, ", "));

        final String json = doGetRequest(API_ENDPOINT + PUBLIC_TAGS + "/" + username + "/" + StringUtils.join(tags, "+"), userAgent, constainAPILimit);

        try {
            return deserializeTagsFromJson(json);
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding related public tags for user",
                    buildPair("username", username), buildPair("tags", StringUtils.join(tags, ", ")));
        }
    }

    /**
     * Find the most recent private inbox bookmarks on delicious from a specific user. Here the default count
     * of 10 entries is used.
     *
     * @param username the username
     * @param key      the key to retrieve private bookmarks (can be obtained on delicious.com)
     * @return a list of the most recent bookmarks from this user, null if nothing found!
     * @throws IllegalArgumentException if username is null or empty or key is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findPrivateInboxBookmarksByUser(String username, String key) throws DeliciousFeedsException {
        return findPrivateInboxBookmarksByUser(DEFAULT_COUNT, username, key);
    }

    /**
     * Find the most recent private inbox bookmarks on delicious from a specific user.
     *
     * @param count    how many entries should be returned - from 1 to 100
     * @param username the username
     * @param key      the key to retrieve private bookmarks (can be obtained on delicious.com)
     * @return a list of the most recent bookmarks from this user, null if nothing found!
     * @throws IllegalArgumentException if count does not match borders, username is null or empty, key is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findPrivateInboxBookmarksByUser(int count, String username, String key) throws DeliciousFeedsException {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count has to be a value from minimum 1 to maximum 100!");

        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username must not be null or empty!");

        if (key == null | key.isEmpty())
            throw new IllegalArgumentException("Key must not be null or empty!");

        logger.info("Trying to find the last {} recent private inbox bookmarks for user '{}'...", count, username);

        final String json = doGetRequest(API_ENDPOINT + PRIVATE_USER_INBOX + "/" + username + "?private=" + key + "&count=" + count, userAgent, constainAPILimit);

        try {
            List<Bookmark> bookmarks = deserializeBookmarksFromJson(json);

            //Expand Urls
            bookmarks = expandUrlsFromBookmarks(bookmarks);

            return bookmarks;
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding recent private inbox bookmarks for user",
                    buildPair("count", Integer.toString(count)), buildPair("username", username),
                    buildPair("key", key));
        }
    }

    /**
     * Find the most recent bookmarks on delicious from a specific user's network. Here the default count
     * of 10 entries is used.
     *
     * @param username the username
     * @return a list of the most recent bookmarks from this user's network, null if nothing found!
     * @throws IllegalArgumentException if username is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findNetworkBookmarksByUser(String username) throws DeliciousFeedsException {
        return findBookmarksByUser(DEFAULT_COUNT, username);
    }

    /**
     * Find the most recent bookmarks on delicious from a specific user's network.
     *
     * @param count    how many entries should be returned - from 1 to 100
     * @param username the username
     * @return a list of the most recent bookmarks from this user's network, null if nothing found!
     * @throws IllegalArgumentException if count does not match borders or username is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findNetworkBookmarksByUser(int count, String username) throws DeliciousFeedsException {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count has to be a value from minimum 1 to maximum 100!");

        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username must not be null or empty!");

        logger.info("Trying to find the last {} recent bookmarks from the user's network for user '{}'...", count, username);

        final String json = doGetRequest(API_ENDPOINT + USER_NETWORK + "/" + username + "?count=" + count, userAgent, constainAPILimit);

        try {
            List<Bookmark> bookmarks = deserializeBookmarksFromJson(json);

            //Expand Urls
            bookmarks = expandUrlsFromBookmarks(bookmarks);

            return bookmarks;
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding recent bookmarks from user's network",
                    buildPair("count", Integer.toString(count)), buildPair("username", username));
        }
    }

    /**
     * Find the most recent bookmarks on delicious from a specific user's network by the given tags. Here the default count
     * of 10 entries is used.
     *
     * @param username the username
     * @param tags     the tags to use (minimum 1!)
     * @return a list of the most recent bookmarks from this user's network, null if nothing found!
     * @throws IllegalArgumentException username is null or empty or tags are null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findNetworkBookmarksByUserAndTags(String username, String... tags) throws DeliciousFeedsException {
        return findBookmarksByUserAndTags(DEFAULT_COUNT, username, tags);
    }

    /**
     * Find the most recent bookmarks on delicious from a specific user's network by the given tags.
     *
     * @param count    how many entries should be returned - from 1 to 100
     * @param username the username
     * @param tags     the tags to use (minimum 1!)
     * @return a list of the most recent bookmarks from this user's network, null if nothing found!
     * @throws IllegalArgumentException if count does not match borders, username is null or empty or tags are null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findNetworkBookmarksByUserAndTags(int count, String username, String... tags) throws DeliciousFeedsException {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count has to be a value from minimum 1 to maximum 100!");

        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username must not be null or empty!");

        if (tags == null || tags.length == 0)
            throw new IllegalArgumentException("Tags must not be null or empty!");

        logger.info("Trying to find the last {} recent bookmarks from the user's network for user '{}' with this tags: {}",
                count, username, StringUtils.join(tags, ", "));

        final String json = doGetRequest(API_ENDPOINT + USER_NETWORK + "/" + username + "/" + StringUtils.join(tags, "+") + "?count=" + count, userAgent, constainAPILimit);

        try {
            List<Bookmark> bookmarks = deserializeBookmarksFromJson(json);

            //Expand Urls
            bookmarks = expandUrlsFromBookmarks(bookmarks);

            return bookmarks;
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding recent bookmarks from user's network",
                    buildPair("count", Integer.toString(count)), buildPair("username", username),
                    buildPair("tags", StringUtils.join(tags, ", ")));
        }
    }

    /**
     * Find all users who the given user follows.
     *
     * @param username the username
     * @return a list of all users the given user follows, null if nothing found
     * @throws IllegalArgumentException if username is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public Set<User> findNetworkMembersByUser(String username) throws DeliciousFeedsException {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("Username must not be null or empty!");

        logger.info("Trying to find all network members for user '{}' ...", username);

        final String json = doGetRequest(API_ENDPOINT + USER_NETWORK_MEMBERS + "/" + username, userAgent, constainAPILimit);

        try {
            return deserializeUsersFromJson(json);
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding all network members for user", buildPair("username", username));
        }
    }

    /**
     * Find bookmarks on delicious for a specific url. Here the default count
     * of 10 entries is used.
     *
     * @param url the url
     * @return a list of bookmarks for this url, null if nothing found!
     * @throws IllegalArgumentException if url is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findBookmarksByUrl(String url) throws DeliciousFeedsException {
        return findBookmarksByUrl(DEFAULT_COUNT / 10, url);
    }

    /**
     * Find bookmarks on delicious for a specific url.
     * <p/>
     * <b>Important! Delicious seems to multiply the given count by 10! So a given count of 1 actually
     * yields 10 bookmarks!</b>
     *
     * @param count how many entries should be returned (value is multiplied by 10!) - from 1 to 100
     * @param url   the url
     * @return a list of bookmarks for this url, null if nothing found!
     * @throws IllegalArgumentException if count does not match borders or url is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public List<Bookmark> findBookmarksByUrl(int count, String url) throws DeliciousFeedsException {
        if (count <= 0 || count > 100)
            throw new IllegalArgumentException("Count has to be a value from minimum 1 to maximum 100!");

        if (url == null || url.isEmpty())
            throw new IllegalArgumentException("Url must not be null or empty!");

        logger.info("Trying to find the last {} bookmarks for this url: {}", count * 10, url);

        final String json = doGetRequest(API_ENDPOINT + URL_BOOKMARKS + "/" + md5Hex(url) + "?count=" + count,
                userAgent, constainAPILimit);

        try {
            List<Bookmark> bookmarks = deserializeBookmarksFromJson(json);

            //Expand Urls
            bookmarks = expandUrlsFromBookmarks(bookmarks);

            return bookmarks;
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding bookmarks for url",
                    buildPair("count", Integer.toString(count)), buildPair("url", url));
        }
    }

    /**
     * Find bookmarks on delicious for a specific url. Here the default count
     * of 10 entries is used.
     *
     * @param url the url
     * @return an info-object about the url, null if nothing found!
     * @throws IllegalArgumentException if username is null or empty
     * @throws DeliciousFeedsException  if something goes wrong
     */
    public UrlInfo findUrlInfoByUrl(String url) throws DeliciousFeedsException {

        if (url == null || url.isEmpty())
            throw new IllegalArgumentException("Url must not be null or empty!");

        logger.info("Trying to find the urlinfo for this url: {}", url);

        final String json = doGetRequest(API_ENDPOINT + URL_INFO + "/" + md5Hex(url), userAgent, constainAPILimit);

        try {
            return deserializeUrlInfoFromJson(json);
        } catch (Exception ex) {
            throw buildException(ex, json, "Error while finding urlinfo for url", buildPair("url", url));
        }
    }

    //---------------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------------

    /**
     * Gets the current UserAgent used for requests.
     *
     * @return userAgent current UserAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Sets the UserAgent for the requests. Takes effect immediately on the next request taken.
     * <p/>
     * <p>It is highly
     * recommended to set this value to a real UserAgent (e.g. "Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:16.0.1)"),
     * because otherwise you may get blocked by yahoo (as written in the api-doc!)</p>
     *
     * @param userAgent the UserAgent to use
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Gets whether the shortened delicious urls should be expanded. By default this is false.
     * <p/>
     * <b>This maybe time consuming to use. For every bookmark another request is made!</b>
     *
     * @return expandUrls if shortened urls should be expanded
     */
    public boolean isExpandUrls() {
        return expandUrls;
    }

    /**
     * Sets whether the shortened delicious urls should be expanded. By default this is false.
     * <p/>
     * <b>This maybe time consuming to use. For every bookmark another request is made!</b>
     *
     * @param expandUrls if shortened urls should be expanded
     */
    public void setExpandUrls(boolean expandUrls) {
        this.expandUrls = expandUrls;
    }

    /**
     * Sets if you want to constrain the API limit. If so there is a pause between each request
     * of 1 second. By default this is true.
     * <p/>
     * <b>Important! If you set this to false you risk to get banned!</b>
     *
     * @return if you want to contrain the API limit
     */
    public boolean isConstainAPILimit() {
        return constainAPILimit;
    }

    /**
     * Sets if you want to constrain the API limit. If so there is a pause between each request
     * of 1 second. By default this is true.
     * <p/>
     * <b>Important! If you set this to false you risk to get banned!</b>
     *
     * @param constainAPILimit if you want to contrain the API limit
     */
    public void setConstainAPILimit(boolean constainAPILimit) {
        this.constainAPILimit = constainAPILimit;
    }
}
