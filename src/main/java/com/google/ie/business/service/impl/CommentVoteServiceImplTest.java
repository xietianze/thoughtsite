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

package com.google.ie.business.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.ie.business.dao.impl.VoteDaoImpl;
import com.google.ie.business.domain.CommentVote;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.User;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.common.exception.IdeasExchangeException;
import com.google.ie.test.ServiceTest;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

import java.util.Date;

/**
 * Test case of {@link CommentVoteServiceImpl}
 * 
 * @author asirohi
 * 
 */
public class CommentVoteServiceImplTest extends ServiceTest {

    private CommentVoteServiceImpl commentVoteService;

    @Before
    public void setUp() {

        if (commentVoteService == null) {
            commentVoteService = new CommentVoteServiceImpl();
            commentVoteService.setAuditManager(mock(AuditManager.class));
            commentVoteService.setCommentService(mock(IdeaCommentServiceImpl.class));
            commentVoteService.setShardedCounterService(mock(ShardedCounterServiceImpl.class));
            commentVoteService.setVoteDao(mock(VoteDaoImpl.class));
        }
    }

    /**
     * Test method for
     * {@link com.google.ie.business.service.impl.CommentVoteServiceImpl#addVote(java.lang.String, com.google.ie.business.domain.Vote, com.google.ie.business.domain.User)}
     * .
     */
    @Test
    public void testAddVote() {

        User user = new User();
        user.setDisplayName("Test User");
        user.setRoleName(User.ROLE_USER);
        user.setUserKey("userKey");

        IdeaComment ideaComment = new IdeaComment();
        ideaComment.setKey("comment key");

        CommentVote commentVote = new CommentVote();
        commentVote.setPositiveVote(true);
        commentVote.setVotePoints(10);
        commentVote.setVotingDate(new Date());
        commentVote.setCreatorKey("creatorKey");
        commentVote.setCommentKey("comment key");

        when(commentVoteService.getCommentService().getCommentById(commentVote.getCommentKey()))
                        .thenReturn(ideaComment);
        when(commentVoteService.getVoteDao().saveVote(commentVote)).thenReturn(commentVote);

        try {
            assertNotNull(commentVoteService.addVote(commentVote, user));
        } catch (IdeasExchangeException e) {
            fail(e.getMessage());
        }

        try {
            assertEquals(commentVote, commentVoteService.addVote(commentVote, user));
        } catch (IdeasExchangeException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void isUserAllowedToVote() {
        String userKey = "userKey";

        IdeaComment ideaComment = new IdeaComment();
        ideaComment.setKey("comment key");
        ideaComment.setCreatorKey("anyUserKey");

        IdeaComment ownComment = new IdeaComment();
        ownComment.setKey("comment key");
        ownComment.setCreatorKey(userKey);

        /* Checking if voting is successful when user is allowed to vote */

        when(commentVoteService.getVoteDao().isCommentAlreadyVotedByUser(userKey,
                        ideaComment.getKey())).thenReturn(false);

        try {
            Assert.assertEquals(true, commentVoteService.isUserAllowedToVote(userKey, ideaComment));
        } catch (IdeasExchangeException e) {
            fail(e.getMessage());
        }

        /*
         * Checking if voting is unsuccessful when user is voting on his own
         * Idea
         */
        try {
            commentVoteService.isUserAllowedToVote(userKey, ownComment);
            fail("IdeasExchangeException expected : User should not be allowed on his own idea");
        } catch (IdeasExchangeException e) {
            assert (true);
        }

        /*
         * Checking if voting is unsuccessful when user has already voted on
         * this Idea
         */
        when(commentVoteService.getVoteDao().isCommentAlreadyVotedByUser(userKey,
                        ideaComment.getKey())).thenReturn(true);

        try {
            Assert.assertEquals(false, commentVoteService.isUserAllowedToVote(userKey,
                            ideaComment));
            fail("IdeasExchangeException expected : User should not be allowed to vote on " +
                            "idea which he/she has already voted");
        } catch (IdeasExchangeException e) {
            assert (true);
        }

    }
}

