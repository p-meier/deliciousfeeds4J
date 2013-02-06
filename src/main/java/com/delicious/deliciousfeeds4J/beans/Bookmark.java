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

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * A simple bean class for holding the values of a bookmark.
 *
 * @author Patrick Meier
 */
public class Bookmark implements Serializable {

    @JsonProperty(value = "a")
    private String user;

    @JsonProperty(value = "d")
    private String title;

    @JsonProperty(value = "n")
    private String description;

    @JsonProperty(value = "u")
    private String url;

    @JsonProperty(value = "t")
    private Set<String> tags;

    @JsonProperty(value = "dt")
    private Date lastUpdatedDate;

    @JsonProperty(value = "md5")
    private String md5;

    //---------------------------------------------------------------------------
    // Methods
    //---------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bookmark bookmark = (Bookmark) o;

        if (description != null ? !description.equals(bookmark.description) : bookmark.description != null)
            return false;
        if (lastUpdatedDate != null ? !lastUpdatedDate.equals(bookmark.lastUpdatedDate) : bookmark.lastUpdatedDate != null)
            return false;
        if (md5 != null ? !md5.equals(bookmark.md5) : bookmark.md5 != null) return false;
        if (tags != null ? !tags.equals(bookmark.tags) : bookmark.tags != null) return false;
        if (title != null ? !title.equals(bookmark.title) : bookmark.title != null) return false;
        if (url != null ? !url.equals(bookmark.url) : bookmark.url != null) return false;
        if (user != null ? !user.equals(bookmark.user) : bookmark.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (lastUpdatedDate != null ? lastUpdatedDate.hashCode() : 0);
        result = 31 * result + (md5 != null ? md5.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "user='" + user + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", tags=" + tags +
                ", lastUpdatedDate=" + lastUpdatedDate +
                ", md5='" + md5 + '\'' +
                '}';
    }

    //---------------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------------

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
