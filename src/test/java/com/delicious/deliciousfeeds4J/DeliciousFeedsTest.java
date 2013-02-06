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
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

//TODO replace real online tests with mocks!
public class DeliciousFeedsTest {

    private final DeliciousFeeds deliciousFeeds = new DeliciousFeeds();

    private static final String USER = "hubert64297";

    private static final String PRIVATE_KEY = "BgDyhjOROkw5RJ75XbshU5CvqjRfXDZT1Mj0uFFtE_s=";

    private static final String URL = "http://namechk.com/";

    @Test
    public void testFindBookmarksExpanded() throws Exception {

        deliciousFeeds.setExpandUrls(true);

        final List<Bookmark> bookmarks = deliciousFeeds.findBookmarks(10);

        assertNotNull(bookmarks);
        assertEquals(10, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        checkBookmark(first);
        checkExpanded(bookmarks);
    }

    @Test
    public void testFindBookmarksShortened() throws Exception {

        deliciousFeeds.setExpandUrls(false);

        final List<Bookmark> bookmarks = deliciousFeeds.findBookmarks(5);

        assertNotNull(bookmarks);
        assertEquals(5, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        checkBookmark(first);
    }

    @Test
    public void testFindPopularBookmarksShortened() throws Exception {

        deliciousFeeds.setExpandUrls(false);

        final List<Bookmark> bookmarks = deliciousFeeds.findPopularBookmarks(5);

        assertNotNull(bookmarks);
        assertEquals(5, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        checkBookmark(first);
    }

    @Test
    public void testFindBookmarksByTagsExpanded() throws Exception {

        deliciousFeeds.setExpandUrls(true);

        final List<Bookmark> bookmarks = deliciousFeeds.findBookmarksByTags(10, "shopping");

        assertNotNull(bookmarks);
        assertEquals(10, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        checkBookmark(first);
        checkExpanded(bookmarks);
        assertTrue(first.getTags().contains("shopping"));
    }

    @Test
    public void testFindPopularBookmarksByTagsExpanded() throws Exception {

        deliciousFeeds.setExpandUrls(true);

        final List<Bookmark> bookmarks = deliciousFeeds.findPopularBookmarksByTags(10, "shopping");

        assertNotNull(bookmarks);
        assertEquals(10, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        checkBookmark(first);
        checkExpanded(bookmarks);
        assertTrue(first.getTags().contains("shopping"));
    }

    @Test
    public void testFindBookmarksByTagsShortened() throws Exception {

        deliciousFeeds.setExpandUrls(false);

        final List<Bookmark> bookmarks = deliciousFeeds.findBookmarksByTags(5, "shopping", "hardware");

        assertNotNull(bookmarks);
        assertEquals(5, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        checkBookmark(first);
        assertTrue(first.getTags().contains("shopping"));
        assertTrue(first.getTags().contains("hardware"));
    }

    @Test
    public void testFindBookmarksByUser() throws Exception {

        deliciousFeeds.setExpandUrls(true);

        final List<Bookmark> bookmarks = deliciousFeeds.findBookmarksByUser(10, USER);

        assertNotNull(bookmarks);
        assertEquals(10, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        assertEquals(USER, first.getUser());
        checkBookmark(first);
        checkExpanded(bookmarks);
    }

    @Test
    public void testFindPrivateBookmarksByUser() throws Exception {

        deliciousFeeds.setExpandUrls(true);

        final List<Bookmark> bookmarks = deliciousFeeds.findPrivateBookmarksByUser(10, USER, PRIVATE_KEY);

        assertNotNull(bookmarks);
        assertEquals(10, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        assertEquals(USER, first.getUser());
        checkBookmark(first);
        checkExpanded(bookmarks);
    }

    @Test
    public void testFindBookmarksByUserAndTags() throws Exception {

        deliciousFeeds.setExpandUrls(true);

        final List<Bookmark> bookmarks = deliciousFeeds.findBookmarksByUserAndTags(10, USER, "java", "programming");

        assertNotNull(bookmarks);
        assertEquals(10, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        assertEquals(USER, first.getUser());
        assertTrue(first.getTags().contains("java"));
        assertTrue(first.getTags().contains("programming"));
        checkBookmark(first);
        checkExpanded(bookmarks);
    }

    @Test
    public void testFindPrivateBookmarksByUserAndTags() throws Exception {

        deliciousFeeds.setExpandUrls(true);

        final List<Bookmark> bookmarks = deliciousFeeds.findPrivateBookmarksByUserAndTags(10, USER, PRIVATE_KEY, "java", "programming");

        assertNotNull(bookmarks);
        assertEquals(10, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        assertEquals(USER, first.getUser());
        assertTrue(first.getTags().contains("java"));
        assertTrue(first.getTags().contains("programming"));
        checkBookmark(first);
        checkExpanded(bookmarks);
    }

    @Test
    public void testFindPrivateInboxBookmarksByUser() throws Exception {

        deliciousFeeds.setExpandUrls(true);

        final List<Bookmark> bookmarks = deliciousFeeds.findPrivateInboxBookmarksByUser(10, USER, PRIVATE_KEY);

        assertNotNull(bookmarks);
        assertEquals(1, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        assertEquals(USER, first.getUser());
        checkBookmark(first);
        checkExpanded(bookmarks);
    }

    @Test
    public void testFindNetworkBookmarksByUser() throws Exception {

        deliciousFeeds.setExpandUrls(true);

        final List<Bookmark> bookmarks = deliciousFeeds.findNetworkBookmarksByUser(4, USER);

        assertNotNull(bookmarks);
        assertEquals(4, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        assertNotSame(USER, first.getUser());
        checkBookmark(first);
        checkExpanded(bookmarks);
    }

    @Test
    public void testFindNetworkBookmarksByUserAndTags() throws Exception {

        deliciousFeeds.setExpandUrls(true);

        final List<Bookmark> bookmarks = deliciousFeeds.findNetworkBookmarksByUserAndTags(2, USER, "java");

        assertNotNull(bookmarks);
        assertEquals(2, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        assertNotSame(USER, first.getUser());
        checkBookmark(first);
        checkExpanded(bookmarks);
    }

    @Test
    public void testFindBookmarksByUrl() throws Exception {

        deliciousFeeds.setExpandUrls(true);

        final List<Bookmark> bookmarks = deliciousFeeds.findBookmarksByUrl(URL);

        assertNotNull(bookmarks);
        assertEquals(10, bookmarks.size());

        final Bookmark first = bookmarks.get(0);

        assertEquals(URL, first.getUrl());
        checkBookmark(first);
        checkExpanded(bookmarks);
    }

    @Test
    public void testFindPublicUserSummary() throws Exception {

        final UserInfo userInfo = deliciousFeeds.findPublicUserSummary(USER);

        assertNotNull(userInfo);
        assertEquals(USER, userInfo.getUser());
        assertEquals(3, userInfo.getUserInfoDetailSet().size());
        assertTrue(userInfo.getItems() > 10);
        assertEquals(1, userInfo.getFollowers());
        assertEquals(2, userInfo.getFollowing());

        final UserInfoDetail userInfoDetail = userInfo.getUserInfoDetailSet().iterator().next();

        assertTrue(userInfoDetail.getId().length() > 0);
        assertTrue(userInfoDetail.getDescription().length() > 0);
    }

    @Test
    public void testFindPublicTagsByUser() throws Exception {

        final Set<Tag> tags = deliciousFeeds.findPublicTagsByUser(USER);

        assertNotNull(tags);
        assertTrue(tags.size() > 10);

        final Tag tag = tags.iterator().next();

        assertTrue(tag.getName().length() > 0);
        assertTrue(tag.getCount() >= 1);
    }

    @Test
    public void testFindRelatedPublicTagsByUserAndTags() throws Exception {

        final Set<Tag> tags = deliciousFeeds.findRelatedPublicTagsByUserAndTags(USER, "java");

        assertNotNull(tags);
        assertTrue(tags.size() > 10);

        final Tag tag = tags.iterator().next();

        assertTrue(tag.getName().length() > 0);
        assertTrue(tag.getCount() >= 1);
    }

    @Test
    public void testFindNetworkMembersByUser() throws Exception {

        final Set<User> users = deliciousFeeds.findNetworkMembersByUser(USER);

        assertNotNull(users);
        assertTrue(users.size() > 0);

        for (User user : users)
            checkUser(user);
    }

    @Test
    public void testFindUrlInfoByUrl() throws Exception {

        final UrlInfo urlInfo = deliciousFeeds.findUrlInfoByUrl(URL);

        assertNotNull(urlInfo);
        assertTrue(urlInfo.getTotalPosts() > 1);
        assertNotNull(urlInfo.getTopTags());
        assertFalse(urlInfo.getTopTags().isEmpty());
        assertEquals(URL, urlInfo.getUrl());
        assertFalse(urlInfo.getHash().isEmpty());
        assertFalse(urlInfo.getTitle().isEmpty());
    }

    private void checkExpanded(List<Bookmark> bookmarks) {
        for (Bookmark bookmark : bookmarks)
            assertFalse(bookmark.getUrl().contains("icio.us/+"));
    }

    private void checkBookmark(Bookmark bookmark) {
        assertNotNull(bookmark.getTitle());
        assertTrue(bookmark.getTitle().length() > 0);
        assertNotNull(bookmark.getUrl());
        assertTrue(bookmark.getUrl().length() > 0);
    }

    private void checkUser(User user) {
        assertNotNull(user.getAddedDate());
        assertFalse(user.getUser().isEmpty());
    }
}
