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

/**
 * A simple bean class for holding the values of a userinfo-detail.
 *
 * @author Patrick Meier
 */
public class UserInfoDetail implements Serializable {

    public static final String ITEMS_ID = "items";

    public static final String FOLLOWING_ID = "networkmembers";

    public static final String FOLLOWERS_ID = "networkfans";

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "d")
    private String description;

    @JsonProperty(value = "n")
    private int count;

    //---------------------------------------------------------------------------
    // Methods
    //---------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfoDetail that = (UserInfoDetail) o;

        if (count != that.count) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + count;
        return result;
    }

    @Override
    public String toString() {
        return "UserInfoDetail{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", count=" + count +
                '}';
    }

    //---------------------------------------------------------------------------
    // Getters & Setters
    //---------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
