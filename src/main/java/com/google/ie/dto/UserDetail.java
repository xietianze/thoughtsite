/* Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS.
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.google.ie.dto;

import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.User;

import java.io.Serializable;
import java.util.List;

/**
 * @author asirohi
 * 
 */
public class UserDetail implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4826284376243620102L;

    private User user;

    private List<Idea> ideasList;

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the ideasList
     */
    public List<Idea> getIdeasList() {
        return ideasList;
    }

    /**
     * @param ideasList the ideasList to set
     */
    public void setIdeasList(List<Idea> ideasList) {
        this.ideasList = ideasList;
    }

}

