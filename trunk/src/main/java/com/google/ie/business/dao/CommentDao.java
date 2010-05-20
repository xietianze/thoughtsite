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

package com.google.ie.business.dao;

import com.google.ie.business.domain.Comment;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Project;
import com.google.ie.dto.RetrievalInfo;

import java.util.List;

/**
 * A data access object specification for Comment entity.
 * 
 * @author Sachneet
 * 
 */
public interface CommentDao extends BaseDao {
    /**
     * Saves a Comment object in to data store.
     * 
     * @param comment {@link Comment} object to be saved.
     * @return saved comment object.
     */

    Comment saveComment(Comment comment);

    /**
     * Retrieves the list of {@link Comment} objects associated with an Idea
     * or Project.
     * 
     * @param key the String object having key of an {@link Idea} or
     *        a {@link Project} .
     * @param retrievalInfo the retrieval information parameter.
     * @param keyType it can be either ideaKey or projectkey.
     * 
     * @return List of comment objects .It can be IdeaComment object list or
     *         ProjectComment list.
     */
    <T extends Comment> List<T> getComments(String key, RetrievalInfo retrievalInfo,
                    String keyType);

}

