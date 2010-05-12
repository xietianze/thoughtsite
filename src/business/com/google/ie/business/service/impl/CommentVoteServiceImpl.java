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
import com.google.ie.business.domain.Comment;
import com.google.ie.business.domain.CommentVote;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.IdeaVote;
import com.google.ie.business.domain.User;
import com.google.ie.business.domain.Vote;
import com.google.ie.business.service.CommentService;
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
 * A Service implementation of {@link VoteService}
 * 
 * @author asirohi
 * 
 */
@Service
public class CommentVoteServiceImpl implements VoteService {
    private static Logger log = Logger.getLogger(CommentVoteServiceImpl.class);
    @Autowired
    private VoteDao voteDao;
    @Autowired
    private AuditManager auditManager;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ShardedCounterService shardedCounterService;

    @Override
    public Vote addVote(Vote vote, User user) throws IdeasExchangeException {
        if (vote != null && user != null) {
            CommentVote commentVote = (CommentVote) vote;
            log.info("Adding vote to a comment with key : " + commentVote.getCommentKey());
            /**
             * Called comment service to get Comment object containing the
             * provided key
             */
            IdeaComment comment = (IdeaComment) commentService.getCommentById(commentVote
                            .getCommentKey());

            if (comment != null) {
                if (isUserAllowedToVote(user.getUserKey(), comment)) {
                    commentVote = (CommentVote) voteDao.saveVote(commentVote);
                    if (commentVote != null) {
                        /** Audit user action of adding vote */
                        auditManager.audit(user.getUserKey(), commentVote.getKey(),
                                        IdeaVote.class.getSimpleName(), ServiceConstants.SAVE);
                        updateComment(commentVote, comment);
                        shardPoints(commentVote, comment);
                        log.info("Vote is successfully added for Comment");
                    }
                    return commentVote;
                }
            }
        }
        log.warn("Voting is failed for the Comment");
        return null;

    }

    /**
     * Update the comment object
     * 
     * @param commentVote the {@link CommentVote} object to be updated
     * @param comment the {@link IdeaComment} object
     */
    private void updateComment(CommentVote commentVote, IdeaComment comment) {
        long newVotes;
        long totalVotes = comment.getTotalVotes();
        if (commentVote.isPositiveVote()) {
            newVotes = comment.getTotalPositiveVotes() + 1;
            comment.setTotalPositiveVotes(newVotes);
        } else {
            newVotes = comment.getTotalNegativeVotes() + 1;
            comment.setTotalNegativeVotes(newVotes);
        }
        comment.setTotalVotes(totalVotes + 1);

        commentService.updateComment(comment);
    }

    /**
     * Use Shard Counter to increment/decrement total
     * positive/negative votes of Comment entity and to
     * update total points of User entity.
     * 
     * @param commentVote the {@link CommentVote} object for which the points
     *        are to be sharded
     * @param commentOwnerKey the key of the owner of the comment
     */
    protected void shardPoints(CommentVote commentVote, IdeaComment comment) {
        if (commentVote.isPositiveVote()) {
            shardedCounterService.incrementPositivePoints(comment.getIdeaKey());
            shardedCounterService.incrementPositivePoints(commentVote
                            .getCommentKey());
            shardedCounterService.updateTotalPoints(comment.getCreatorKey(),
                            commentVote.getVotePoints());
        } else {
            shardedCounterService.incrementNegativePoints(comment.getIdeaKey());
            shardedCounterService.incrementNegativePoints(commentVote
                            .getCommentKey());
            shardedCounterService.updateTotalPoints(comment.getCreatorKey(),
                            ServiceConstants.MINUSONE * commentVote.getVotePoints());
        }
    }

    /**
     * Check whether a user has permissions to vote
     * 
     * @param userKey the key of the user
     * @param comment the comment on which the user wants to vote
     * @return boolean specifying the permission
     * @throws IdeasExchangeException
     */
    protected boolean isUserAllowedToVote(String userKey, Comment comment)
                    throws IdeasExchangeException {
        if (!userKey.equals(comment.getCreatorKey())) {
            if (!voteDao.isCommentAlreadyVotedByUser(userKey, comment.getKey())) {
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

    /**
     * @return the voteDao
     */
    public VoteDao getVoteDao() {
        return voteDao;
    }

    /**
     * @param voteDao the voteDao to set
     */
    public void setVoteDao(VoteDao voteDao) {
        this.voteDao = voteDao;
    }

    /**
     * @return the auditManager
     */
    public AuditManager getAuditManager() {
        return auditManager;
    }

    /**
     * @param auditManager the auditManager to set
     */
    public void setAuditManager(AuditManager auditManager) {
        this.auditManager = auditManager;
    }

    /**
     * @return the commentService
     */
    public CommentService getCommentService() {
        return commentService;
    }

    /**
     * @param commentService the commentService to set
     */
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * @return the shardedCounterService
     */
    public ShardedCounterService getShardedCounterService() {
        return shardedCounterService;
    }

    /**
     * @param shardedCounterService the shardedCounterService to set
     */
    public void setShardedCounterService(ShardedCounterService shardedCounterService) {
        this.shardedCounterService = shardedCounterService;
    }

}

