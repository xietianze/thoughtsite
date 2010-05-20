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

import com.google.ie.business.domain.CommentVote;
import com.google.ie.business.domain.IdeaVote;
import com.google.ie.business.domain.Vote;

/**
 * A data access object specification for Vote entity.
 * 
 * @author asirohi
 * 
 */
public interface VoteDao extends BaseDao {

    /**
     * 
     * Saves the Vote entity to data store.
     * 
     * @param vote object to save
     * @return saved vote object.
     */
    Vote saveVote(Vote vote);

    /**
     * Get idea vote by idea key
     * 
     * @param ideaKey primary key of idea.
     * @return IdeaVote
     */
    IdeaVote getIdeaVote(String ideaKey);

    /**
     * Get comment vote on the basis of comment key.
     * 
     * @param commentKey primary key of comment key.
     * @return CommentVote
     */

    CommentVote getCommentVote(String commentKey);

    /**
     * Check for idea if idea is already voted by user.
     * 
     * @param userKey primary key of user
     * @param ideaKey primary key of idea
     * @return boolean
     */

    boolean isIdeaAlreadyVotedByUser(String userKey, String ideaKey);

    /**
     * Check for idea if comment is already voted by user.
     * 
     * @param userKey primary key of user
     * @param commentKey primary key of comment
     * @return boolean
     */

    boolean isCommentAlreadyVotedByUser(String userKey, String commentKey);

}

