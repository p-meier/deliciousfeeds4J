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

package com.delicious.deliciousfeeds4J.beans;

import com.delicious.deliciousfeeds4J.json.JacksonTagDeserializer;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.io.Serializable;
import java.util.Set;

/**
 * A simple bean class for holding the informations about a url.
 *
 * @author Patrick Meier
 */
public class UrlInfo implements Serializable {

    private String title;

    private String url;

    @JsonProperty(value = "total_posts")
    private long totalPosts;

    private String hash;

    @JsonProperty(value = "top_tags")
    @JsonDeserialize(using = JacksonTagDeserializer.class)
    private Set<Tag> topTags;

    //---------------------------------------------------------------------------
    // Methods
    //---------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlInfo urlInfo = (UrlInfo) o;

        if (totalPosts != urlInfo.totalPosts) return false;
        if (hash != null ? !hash.equals(urlInfo.hash) : urlInfo.hash != null) return false;
        if (title != null ? !title.equals(urlInfo.title) : urlInfo.title != null) return false;
        if (topTags != null ? !topTags.equals(urlInfo.topTags) : urlInfo.topTags != null) return false;
        if (url != null ? !url.equals(urlInfo.url) : urlInfo.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (int) (totalPosts ^ (totalPosts >>> 32));
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        result = 31 * result + (topTags != null ? topTags.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UrlInfo{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", totalPosts=" + totalPosts +
                ", hash='" + hash + '\'' +
                ", topTags=" + topTags +
                '}';
    }

    //---------------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(long totalPosts) {
        this.totalPosts = totalPosts;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Set<Tag> getTopTags() {
        return topTags;
    }

    public void setTopTags(Set<Tag> topTags) {
        this.topTags = topTags;
    }
}
