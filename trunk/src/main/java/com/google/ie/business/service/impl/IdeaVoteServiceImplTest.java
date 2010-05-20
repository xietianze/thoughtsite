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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.ie.business.dao.VoteDao;
import com.google.ie.business.dao.impl.VoteDaoImpl;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.IdeaVote;
import com.google.ie.business.domain.User;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.common.exception.IdeasExchangeException;
import com.google.ie.test.ServiceTest;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * Test case for IdeaVoteServiceImpl class
 * 
 * @author gmaurya
 * 
 */
public class IdeaVoteServiceImplTest extends ServiceTest {
    private IdeaServiceImpl ideaService;
    private IdeaVoteServiceImpl ideaVoteService;
    private VoteDao voteDao = mock(VoteDaoImpl.class);
    private ShardedCounterServiceImpl shardedCounterService;
    private AuditManager mockAuditManager = mock(AuditManager.class);

    @Before
    public void setUp() {
        super.setUp();
        ideaService = mock(IdeaServiceImpl.class);

        shardedCounterService = mock(ShardedCounterServiceImpl.class);
        ideaService.setAuditManager(mockAuditManager);

        ideaVoteService = new IdeaVoteServiceImpl();
        ideaVoteService.setAuditManager(mockAuditManager);
        ideaVoteService.setIdeaService(ideaService);
        ideaVoteService.setShardedCounterService(shardedCounterService);
        ideaVoteService.setVoteDao(voteDao);
    }

    @Test
    public void addVote() {
        User user = new User();
        user.setDisplayName("Test User");
        user.setRoleName(User.ROLE_USER);
        user.setUserKey("userKey");
        // when(userService.addOrUpdateUser(user)).thenReturn(user);

        Idea idea = new Idea();
        idea.setTitle("ideaTitle");
        idea.setKey("ideaKey");
        idea.setDescription("Idea Description");
        idea.setCreatorKey("anyUserKey");
        when(ideaService.getIdeaByKey(idea.getKey())).thenReturn(idea);

        IdeaVote vote = new IdeaVote();
        vote.setKey("voteKey");
        vote.setCreatorKey(user.getUserKey());
        vote.setIdeaKey(idea.getKey());
        vote.setVotePoints(15);
        vote.setPositiveVote(true);
        when(voteDao.saveVote(vote)).thenReturn(vote);
        try {
            vote = (IdeaVote) ideaVoteService.addVote(vote, user);
        } catch (IdeasExchangeException e) {
            Assert.fail();

        }
        Assert.assertNotNull(vote.getKey());

        vote.setPositiveVote(false);
        try {
            vote = (IdeaVote) ideaVoteService.addVote(vote, user);
        } catch (IdeasExchangeException e) {
            Assert.fail();
        }
        Assert.assertNotNull(vote.getKey());

    }

    @Test
    public void isUserAllowedToVote() {
        String userKey = "userKey";

        Idea idea = new Idea();
        idea.setTitle("ideaTitle");
        idea.setKey("ideaKey");
        idea.setDescription("Idea Description");
        idea.setCreatorKey("anyUserKey");

        Idea ownIdea = new Idea();
        ownIdea.setTitle("ideaTitle");
        ownIdea.setKey("ideaKey");
        ownIdea.setDescription("Idea Description");
        ownIdea.setCreatorKey(userKey);

        /** Checking if voting is successful when user is allowed to vote */
        when(voteDao.isIdeaAlreadyVotedByUser(userKey, idea.getKey())).thenReturn(false);
        boolean actual = false;
        try {
            actual = ideaVoteService.isUserAllowedToVote(userKey, idea);
        } catch (IdeasExchangeException e) {
            actual = false;
        }
        Assert.assertEquals(true, actual);

        /**
         * Checking if voting is unsuccessful when user is voting on his own
         * Idea
         */
        try {
            actual = ideaVoteService.isUserAllowedToVote(userKey, ownIdea);
        } catch (IdeasExchangeException e) {
            actual = false;
        }
        Assert.assertEquals(false, actual);

        when(voteDao.isIdeaAlreadyVotedByUser(userKey, idea.getKey())).thenReturn(true);
        /**
         * Checking if voting is unsuccessful when user has already voted on
         * this Idea
         */
        try {
            actual = ideaVoteService.isUserAllowedToVote(userKey, idea);
        } catch (IdeasExchangeException e) {
            actual = false;
        }
        Assert.assertEquals(false, actual);

    }
}

