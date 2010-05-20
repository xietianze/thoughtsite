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

package com.google.ie.business.dao.impl;

import com.google.ie.business.dao.VoteDao;
import com.google.ie.business.domain.CommentVote;
import com.google.ie.business.domain.IdeaVote;
import com.google.ie.business.domain.Vote;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * A JDO implementation object of VoteDao.
 * 
 * @author asirohi
 * 
 */
public class VoteDaoImpl extends BaseDaoImpl implements VoteDao {

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Vote saveVote(Vote vote) {
        return getJdoTemplate().makePersistent(vote);
    }

    @Override
    public CommentVote getCommentVote(String commentKey) {
        return null;
    }

    @Override
    public IdeaVote getIdeaVote(String ideaKey) {
        return null;
    }

    @Override
    public boolean isIdeaAlreadyVotedByUser(String userKey, String ideaKey) {
        Collection<IdeaVote> ideaVotesCollection = getJdoTemplate().find(IdeaVote.class,
                        "creatorKey == user && ideaKey == key", "String user,String key",
                        userKey, ideaKey);
        if (ideaVotesCollection != null && ideaVotesCollection.size() != 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isCommentAlreadyVotedByUser(String userKey, String ideaKey) {
        Collection<CommentVote> commentVotes = getJdoTemplate().find(CommentVote.class,
                        "creatorKey == user && commentKey == key", "String user,String key",
                        userKey, ideaKey);
        if (commentVotes != null && commentVotes.size() != 0) {
            return true;
        }
        return false;
    }

}

