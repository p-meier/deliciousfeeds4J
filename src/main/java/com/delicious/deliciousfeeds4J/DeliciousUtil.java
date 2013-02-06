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
import com.delicious.deliciousfeeds4J.exceptions.YouGotBannedException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple util class. Mainly for handling JSON deserialization and doing the actual
 * HTTP requests. Also contains some other handy methods.
 *
 * @author Patrick Meier
 */
final class DeliciousUtil {

    private static final Logger logger = LoggerFactory.getLogger(DeliciousUtil.class);

    //Pattern for getting the original Url
    private static final Pattern URL_PARAMETER_PATTERN = Pattern.compile("(?<=url=).*?(?=&|$)");

    //Snippet to detect if url is shortened
    private static final String URL_SHORTENED_SNIPPET = "icio.us/+";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final DefaultHttpClient HTTP_CLIENT;

    static {
        final PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
        HTTP_CLIENT = new DefaultHttpClient(connectionManager);
        HTTP_CLIENT.setRedirectStrategy(new DefaultRedirectStrategy());

        objectMapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    //---------------------------------------------------------------------------
    // Methods
    //---------------------------------------------------------------------------

    public static UrlInfo deserializeUrlInfoFromJson(String json) throws Exception {

        logger.debug("Trying to deserialize JSON to UrlInfos...");
        logger.trace("Deserializing JSON: " + json);

        //Check if empty or null
        if (json == null || json.isEmpty()) {
            logger.debug("Nothing to deserialize. JSON-string was empty!");
            return null;
        }

        //Actually deserialize
        final Set<UrlInfo> urlInfos = ((Set<UrlInfo>) objectMapper.readValue(json, new TypeReference<Set<UrlInfo>>() {
        }));

        if (urlInfos == null || urlInfos.isEmpty()) {
            logger.debug("No UrlInfos found. Collection was empty.");
            return null;
        }

        return urlInfos.iterator().next();
    }

    public static Set<User> deserializeUsersFromJson(String json) throws Exception {

        logger.debug("Trying to deserialize JSON to Users...");
        logger.trace("Deserializing JSON: " + json);

        //Check if empty or null
        if (json == null || json.isEmpty()) {
            logger.debug("Nothing to deserialize. JSON-string was empty!");
            return null;
        }

        //Actually deserialize
        final Set<User> users = ((Set<User>) objectMapper.readValue(json, new TypeReference<Set<User>>() {
        }));

        if (users == null || users.isEmpty()) {
            logger.debug("No users found. Collection was empty.");
            return null;
        }

        logger.info("Successfully deserialized {} users!", users.size());

        return users;
    }

    public static String buildPair(String name, String value) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name must not be null or empty!");

        return name + " -> " + value;
    }

    public static DeliciousFeedsException buildException(Exception cause, String json, String message, String... args) {

        final StringBuilder messageBuffer = new StringBuilder();

        boolean showCause = true;

        messageBuffer.append(message);

        if (args != null && args.length > 0) {
            messageBuffer.append(": ");

            messageBuffer.append("[");
            messageBuffer.append(StringUtils.join(args, ", "));

            //Check if it is maybe an error-result...
            final ErrorResult errorResult = deserializeErrorResult(json);

            if (errorResult != null) {
                messageBuffer.append(", ");
                messageBuffer.append(buildPair("message", errorResult.getMessage()));
                messageBuffer.append(", ");
                messageBuffer.append(buildPair("code", Integer.toString(errorResult.getCode())));
                showCause = false;
            }

            messageBuffer.append("]");

        } else {
            messageBuffer.append("!");
        }

        if (showCause)
            return new DeliciousFeedsException(messageBuffer.toString(), cause);
        else
            return new DeliciousFeedsException(messageBuffer.toString());
    }

    public static ErrorResult deserializeErrorResult(String json) {

        logger.debug("Trying to deserialize JSON to ErrorResult...");
        logger.trace("Deserializing JSON: " + json);

        //Check if empty or null
        if (json == null || json.isEmpty()) {
            logger.debug("Nothing to deserialize. JSON-string was empty!");
            return null;
        }

        try {
            //Actually deserialize
            final JsonNode root = objectMapper.readTree(json);

            if (root.isArray() == false || root.size() != 1) {
                logger.debug("Error-Result is not an array or has more or less than one entry. Got {} entries.", root.size());
                return null;
            }

            final JsonNode errorResultNode = root.iterator().next();

            if (errorResultNode.has("result") == false) {
                logger.debug("Error-Response does not have a 'result' child.");
                return null;
            }

            final String error = errorResultNode.get("result").toString();

            return objectMapper.readValue(error, ErrorResult.class);
        } catch (Exception ex) {
            logger.debug("Error while deserializing error-result response!", ex);
            return null;
        }
    }

    public static Set<Tag> deserializeTagsFromJson(String json) throws Exception {

        logger.debug("Trying to deserialize JSON to Tags...");
        logger.trace("Deserializing JSON: " + json);

        //Check if empty or null
        if (json == null || json.isEmpty()) {
            logger.debug("Nothing to deserialize. JSON-string was empty!");
            return null;
        }

        //Actually deserialize
        final Map<String, Integer> tagMap = ((Map<String, Integer>) objectMapper.readValue(json,
                new TypeReference<Map<String, Integer>>() {
                }));

        if (tagMap == null || tagMap.isEmpty()) {
            logger.debug("No tags found. Collection was empty.");
            return null;
        }

        logger.info("Successfully deserialized {} tagMap!", tagMap.size());

        //Build the set
        final Set<Tag> tags = new TreeSet<Tag>();

        for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
            final Tag tag = new Tag();
            tag.setName(entry.getKey());
            tag.setCount(entry.getValue());

            tags.add(tag);
        }

        return tags;
    }

    public static UserInfo deserializePublicUserInformationFromJson(String username, String json) throws Exception {

        logger.debug("Trying to deserialize JSON to UserInfo...");
        logger.trace("Deserializing JSON: " + json);

        //Check if empty or null
        if (json == null || json.isEmpty()) {
            logger.debug("Nothing to deserialize. JSON-string was empty!");
            return null;
        }

        //Actually deserialize
        final Set<UserInfoDetail> userInfoDetails = ((Set<UserInfoDetail>) objectMapper.readValue(json,
                new TypeReference<Set<UserInfoDetail>>() {
                }));

        if (userInfoDetails == null || userInfoDetails.isEmpty()) {
            logger.debug("No userInfoDetails found. Collection was empty.");
            return null;
        }

        logger.info("Successfully deserialized {} userInfoDetails!", userInfoDetails.size());

        //Now build a UserInfo-object
        final UserInfo userInfo = new UserInfo();
        userInfo.setUser(username);

        for (UserInfoDetail userInfoDetail : userInfoDetails) {

            if (userInfoDetail.getId().equals(UserInfoDetail.ITEMS_ID))
                userInfo.setItems(userInfoDetail.getCount());
            else if (userInfoDetail.getId().equals(UserInfoDetail.FOLLOWERS_ID))
                userInfo.setFollowers(userInfoDetail.getCount());
            else if (userInfoDetail.getId().equals(UserInfoDetail.FOLLOWING_ID))
                userInfo.setFollowing(userInfoDetail.getCount());

            userInfo.getUserInfoDetailSet().add(userInfoDetail);
        }

        return userInfo;
    }

    public static List<Bookmark> deserializeBookmarksFromJson(String json) throws Exception {

        logger.debug("Trying to deserialize JSON to Bookmarks...");
        logger.trace("Deserializing JSON: " + json);

        //Check if empty or null
        if (json == null || json.isEmpty()) {
            logger.debug("Nothing to deserialize. JSON-string was empty!");
            return null;
        }

        //Actually deserialize
        final List<Bookmark> bookmarks = ((List<Bookmark>) objectMapper.readValue(json, new TypeReference<List<Bookmark>>() {
        }));

        if (bookmarks == null || bookmarks.isEmpty()) {
            logger.debug("No bookmarks found. Collection was empty.");
            return null;
        }

        logger.info("Successfully deserialized {} bookmarks!", bookmarks.size());

        return bookmarks;
    }

    public static String doGetRequest(String url, String userAgent, boolean constainAPILimit) throws DeliciousFeedsException {

        logger.info("Executing GET-Request to url: " + url);

        final HttpGet getRequest = new HttpGet(url);

        final HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.USER_AGENT, userAgent);
        getRequest.setParams(params);

        final ResponseHandler<String> responseHandler = new BasicResponseHandler();

        try {
            if (constainAPILimit) {
                logger.info("Waiting for 1 second to not reach the API limit and get banned!");
                Thread.sleep(1000);
            }

            return HTTP_CLIENT.execute(getRequest, responseHandler);
        } catch (Exception ex) {

            //Check if you maybe got banned...
            if (ex instanceof HttpResponseException)
                if (((HttpResponseException) ex).getStatusCode() == 503)
                    throw new YouGotBannedException(ex);

            throw new DeliciousFeedsException("Error occured while executing GET-Request to url: " + url, ex);
        }
    }

    public static String expandShortenedUrl(String shortenedUrl, String userAgent) throws IOException {

        if (shortenedUrl == null || shortenedUrl.isEmpty())
            return shortenedUrl;

        if (userAgent == null || userAgent.isEmpty())
            throw new IllegalArgumentException("UserAgent must not be null or empty!");

        if (shortenedUrl.contains(URL_SHORTENED_SNIPPET) == false)
            return shortenedUrl;

        logger.debug("Trying to expand shortened url: " + shortenedUrl);

        final URL url = new URL(shortenedUrl);

        final HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);

        try {
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("User-Agent", userAgent);
            connection.connect();
            final String expandedDeliciousUrl = connection.getHeaderField("Location");

            if (expandedDeliciousUrl.contains("url=")) {

                final Matcher matcher = URL_PARAMETER_PATTERN.matcher(expandedDeliciousUrl);

                if (matcher.find()) {
                    final String expanded = matcher.group();

                    logger.trace("Successfully expanded: " + shortenedUrl + " -> " + expandedDeliciousUrl + " -> " + expanded);

                    return expanded;
                }
            }
        } catch (Exception ex) {
            logger.debug("Error while trying to expand shortened url: " + shortenedUrl, ex);
        } finally {
            connection.getInputStream().close();
        }

        return shortenedUrl;
    }
}
