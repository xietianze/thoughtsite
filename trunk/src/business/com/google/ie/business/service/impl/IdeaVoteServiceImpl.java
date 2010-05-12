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

import com.google.ie.business.dao.VoteDao;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.IdeaVote;
import com.google.ie.business.domain.User;
import com.google.ie.business.domain.Vote;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.business.service.ShardedCounterService;
import com.google.ie.business.service.VoteService;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.constants.IdeaExchangeErrorCodes;
import com.google.ie.common.exception.IdeasExchangeException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service implementation of the VoteService.
 * 
 * @author asirohi
 * 
 */
@Service
public class IdeaVoteServiceImpl implements VoteService {
    /**
     * Logger for idea vote service class
     */
    private static Logger log = Logger.getLogger(IdeaServiceImpl.class);

    @Autowired
    private IdeaService ideaService;

    @Autowired
    private ShardedCounterService shardedCounterService;

    @Autowired
    private AuditManager auditManager;

    @Autowired
    private VoteDao voteDao;

    @Override
    public Vote addVote(Vote vote, User user) throws IdeasExchangeException {

        if (vote != null && user != null) {
            IdeaVote ideaVote = (IdeaVote) vote;
            log.debug("Adding vote to an Idea with key  :" + ideaVote.getIdeaKey());

            /** Get IdeaService to get the Idea object from Data Store */
            Idea idea = ideaService.getIdeaByKey(ideaVote.getIdeaKey());

            if (idea != null) {
                if (isUserAllowedToVote(user.getUserKey(), idea)) {
                    ideaVote = (IdeaVote) voteDao.saveVote(ideaVote);
                    if (ideaVote != null) {
                        shardPoints(ideaVote, idea.getCreatorKey());
                        updateIdea(ideaVote, idea);
                        /** Audit user action of adding vote */
                        auditManager.audit(user.getUserKey(), ideaVote.getKey(),
                                        IdeaVote.class.getSimpleName(), ServiceConstants.SAVE);
                        log.info("Vote is successfully added for Idea");
                    }
                    return ideaVote;
                }
            }
        }
        log.warn("Voting is failed for the Idea");
        return null;

    }

    /**
     * Adds the vote to the idea. It increments the total positive or
     * total negative votes
     * 
     * @param ideaVote the {@link IdeaVote} object
     * @param idea the idea to be voted
     */
    private void updateIdea(IdeaVote ideaVote, Idea idea) {
        long newVotes;
        long totalVotes = idea.getTotalVotes();
        /* Add vote to totalPositiveVotes and totalVotes */
        if (ideaVote.isPositiveVote()) {
            newVotes = idea.getTotalPositiveVotes() + 1;
            idea.setTotalPositiveVotes(newVotes);
            idea.setTotalVotes(totalVotes + 1);
        } else {
            /* Add vote to totalNegativeVotes and decrement totalVotes by one */
            newVotes = idea.getTotalNegativeVotes() + 1;
            idea.setTotalNegativeVotes(newVotes);
            idea.setTotalVotes(totalVotes - 1);
        }
        ideaService.updateIdeaVotes(idea);
    }

    /**
     * Use ShardedCounter to increment/decrement total
     * positive/negative votes of Idea entity and owner total points
     * 
     * @param ideaVote the {@link IdeaVote} object
     * @param ideaOwnerKey the key of the owner of the idea
     */
    protected void shardPoints(IdeaVote ideaVote, String ideaOwnerKey) {

        if (ideaVote.isPositiveVote()) {
            shardedCounterService.incrementPositivePoints(ideaVote.getIdeaKey());
            shardedCounterService.updateTotalPoints(ideaOwnerKey, ideaVote
                            .getVotePoints());
            log.debug("Added positive points and votes for comment");
        } else {
            shardedCounterService.incrementNegativePoints(ideaVote.getIdeaKey());
            shardedCounterService.updateTotalPoints(ideaOwnerKey,
                            ServiceConstants.MINUSONE * ideaVote.getVotePoints());
            log.debug("Added negative points and votes for comment");
        }
    }

    /**
     * Check whether user is allowed to vote on the idea
     * 
     * @param userKey the key of the user who wants to vote
     * @param idea the idea to be voted
     * @return boolean specifying the permission
     * @throws IdeasExchangeException
     */
    protected boolean isUserAllowedToVote(String userKey, Idea idea) throws IdeasExchangeException {
        if (!userKey.equals(idea.getCreatorKey())) {
            if (!voteDao.isIdeaAlreadyVotedByUser(userKey, idea.getKey())) {
                return true;
            }
            throw new IdeasExchangeException(IdeaExchangeErrorCodes.REPEAT_VOTE_EXCEPTION,
                            IdeaExchangeConstants.Messages.REPEAT_VOTE_MESSAGE);

        }
        log.warn("User is not allowed to vote on his own idea or on Idea" +
                        " for which he/she has already voted");

        throw new IdeasExchangeException(IdeaExchangeErrorCodes.OWNER_VOTE_EXCEPTION,
                        IdeaExchangeConstants.Messages.OWNER_VOTE_MESSAGE);

    }

    public IdeaService getIdeaService() {
        return ideaService;
    }

    public void setIdeaService(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    public ShardedCounterService getShardedCounterService() {
        return shardedCounterService;
    }

    public void setShardedCounterService(ShardedCounterService shardedCounterService) {
        this.shardedCounterService = shardedCounterService;
    }

    public AuditManager getAuditManager() {
        return auditManager;
    }

    public void setAuditManager(AuditManager auditManager) {
        this.auditManager = auditManager;
    }

    public VoteDao getVoteDao() {
        return voteDao;
    }

    public void setVoteDao(VoteDao voteDao) {
        this.voteDao = voteDao;
    }
}

