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

/**
 * A simple bean class for holding the values of a user you follow.
 *
 * @author Patrick Meier
 */
public class User implements Serializable {

    private String user;

    @JsonProperty(value = "dt")
    private Date addedDate;

    //---------------------------------------------------------------------------
    // Methods
    //---------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user1 = (User) o;

        if (addedDate != null ? !addedDate.equals(user1.addedDate) : user1.addedDate != null)
            return false;
        if (user != null ? !user.equals(user1.user) : user1.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (addedDate != null ? addedDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "user='" + user + '\'' +
                ", addedDate=" + addedDate +
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

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }
}
