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

package com.google.ie.business.service;

import com.google.ie.business.domain.Comment;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;

import java.util.List;

/**
 * A service specification for the Comment entity.
 * 
 * @author ssbains
 */
public interface CommentService {
    /**
     * Adds the comment submitted by the a user.<Br>
     * <p>
     * This method will save the comment on an Idea or on a Project being
     * developed by a team.
     * </p>
     * 
     * @param comment The Comment object to be saved.
     * @param user The User object representing the comment creator.
     * @return Saved comment.
     */
    Comment addComment(Comment comment, User user);

    /**
     * Retrieves the list of comment objects corresponding to the
     * {@link RetrievalInfo}.
     * 
     * To retrieve the comments of an {@link Idea}, first parameter must be a
     * valid Idea key.
     * 
     * To retrieve the comments of a {@link Project}, first parameter must be
     * valid Project key.
     * 
     * @param key An Idea or a Project key for which comments are
     *        being fetched.
     * @param retrievalInfo The RetrievalInfo object containing the information
     *        for comments retrieval.
     * @return List of comments object.
     */
    <T extends Comment> List<T> getComments(String key, RetrievalInfo retrievalInfo);

    /**
     * Flag a comment to be objectionable etc.
     * 
     * @param commentKey key of the flagged comment.
     * @param user user object flagging the object.
     * @return true if flagged successfully else false.
     */
    String flagComment(String commentKey, User user);

    /**
     * Get Comment with the given key.
     * 
     * @param entityKey Key of comment.
     * @return the comment entity.
     */
    Comment getCommentById(String entityKey);

    /**
     * Update comment with the given comment entity.
     * 
     * @param comment the {@link Comment} entity.
     */
    void updateComment(Comment comment);
}

