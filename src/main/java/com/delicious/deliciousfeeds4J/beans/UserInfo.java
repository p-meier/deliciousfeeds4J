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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple bean class for holding the values of a userinfo-detail.
 *
 * @author Patrick Meier
 */
public class UserInfo implements Serializable {

    private String user;

    private Set<UserInfoDetail> userInfoDetailSet = new HashSet<UserInfoDetail>();

    private int items;

    private int following;

    private int followers;

    //---------------------------------------------------------------------------
    // Methods
    //---------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInfo userInfo = (UserInfo) o;

        if (followers != userInfo.followers) return false;
        if (following != userInfo.following) return false;
        if (items != userInfo.items) return false;
        if (user != null ? !user.equals(userInfo.user) : userInfo.user != null) return false;
        if (userInfoDetailSet != null ? !userInfoDetailSet.equals(userInfo.userInfoDetailSet) : userInfo.userInfoDetailSet != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (userInfoDetailSet != null ? userInfoDetailSet.hashCode() : 0);
        result = 31 * result + items;
        result = 31 * result + following;
        result = 31 * result + followers;
        return result;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "user='" + user + '\'' +
                ", userInfoDetailSet=" + userInfoDetailSet +
                ", items=" + items +
                ", following=" + following +
                ", followers=" + followers +
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

    public Set<UserInfoDetail> getUserInfoDetailSet() {
        return userInfoDetailSet;
    }

    public void setUserInfoDetailSet(Set<UserInfoDetail> userInfoDetailSet) {
        this.userInfoDetailSet = userInfoDetailSet;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }
}
