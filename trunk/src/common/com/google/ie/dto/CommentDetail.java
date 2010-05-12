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

import com.google.ie.business.domain.Comment;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.User;

import java.io.Serializable;

/**
 * A data transfer object representing the comment information.
 * 
 * @author Charanjeet singh
 */
public class CommentDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Comment object.
     */
    private Comment comment;
    private User user;
    private Idea idea;

    /**
     * @param comment the comment to set
     */
    public void setComment(Comment comment) {
        this.comment = comment;
    }

    /**
     * @return the comment
     */
    public Comment getComment() {
        return comment;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
    }

    public Idea getIdea() {
        return idea;
    }

}

