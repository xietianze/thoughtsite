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

import com.google.ie.business.dao.AdminRequestDao;
import com.google.ie.business.dao.CommentDao;
import com.google.ie.business.dao.impl.DaoConstants;
import com.google.ie.business.domain.AdminRequest;
import com.google.ie.business.domain.Comment;
import com.google.ie.business.domain.EntityIndex;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.CommentService;
import com.google.ie.business.service.EntityIndexService;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.business.service.ShardedCounterService;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.constants.IdeaExchangeErrorCodes;
import com.google.ie.common.exception.SystemException;
import com.google.ie.common.taskqueue.IndexQueueUpdater;
import com.google.ie.dto.RetrievalInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * A service implementation of the CommentService
 * 
 * @author Sachneet
 * 
 */
@Service
public class IdeaCommentServiceImpl implements CommentService {
    private static Logger logger = Logger.getLogger(IdeaCommentServiceImpl.class);

    private static final String IDEA_KEY = "ideaKey";

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private AuditManager auditManager;

    @Autowired
    private EntityIndexService entityIndexService;

    @Autowired
    private ShardedCounterService shardedCounterService;

    @Autowired
    private AdminRequestDao adminRequestDao;

    public IdeaCommentServiceImpl() {
    }

    @Autowired
    private IndexQueueUpdater indexQueueUpdater;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Comment addComment(Comment comment, User user) {
        if (comment != null && comment.getText() != null && user != null
                        && user.getUserKey() != null) {
            comment.setCreatorKey(user.getUserKey());
            comment.setCreatedOn(new Date(System.currentTimeMillis()));
            comment.setStatus(Comment.STATUS_SAVED);
            comment = saveComment(comment);
            if (comment != null && comment.getKey() != null) {
                // add user points
                user = addPointsToUserOnPostComment(user, comment);
                getAuditManager().audit(user.getUserKey(), comment.getKey(),
                                comment.getClass().getName(),
                                ServiceConstants.AUDIT_ACTION_TYPE_SAVE_COMMENT);
                logger.info("Comment saved successfully.");
            }
            return comment;
        }
        return null;
    }

    /**
     * Saved a comment and mark it for indexing.
     * 
     * @param comment Comment object to be saved.
     * @return Saved comment.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    private Comment saveComment(Comment comment) {
        comment = getCommentDao().saveComment(comment);
        /*
         * Index the entity.Create an EntityIndex object for the entity to be
         * indexed and then queue the job to task queue
         */
        if (comment.getKey() != null) {
            EntityIndex entityIndex = entityIndexService.createEntityIndex(comment.getKey());
            getIndexQueueUpdater().indexEntity(entityIndex.getKey());
        }
        return comment;
    }

    @Override
    public String flagComment(String ideaCommentKey, User user) {
        String status = IdeaExchangeConstants.FAIL;
        /* Get description of the Comment */
        Comment comment = this.getCommentById(ideaCommentKey);
        if (comment != null && comment.getStatus().equals(IdeaComment.STATUS_FLAGGED)) {
            status = IdeaExchangeConstants.IDEA_COMMENT_ALLREADY_FLAGED;
            return status;
        }
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setEntityKey(ideaCommentKey);
        adminRequest.setEntityType(IdeaComment.class.getSimpleName());
        adminRequest.setRequesterkey(user.getUserKey());
        adminRequest.setRequestType(AdminRequest.REQUEST_OBJECTIONABLE);
        adminRequest.setCreatedOn(new Date());
        adminRequest.setStatus(AdminRequest.STATUS_PENDING);
        if (!StringUtils.isBlank(user.getEmailId())) {
            adminRequest.setRequesterEmail(user.getEmailId());
        }
        if (comment != null && !StringUtils.isBlank(comment.getKey())) {
            adminRequest.setEntityTitle(getTrimmedComment(comment.getText()));
            comment.setStatus(Comment.STATUS_FLAGGED);
            commentDao.saveComment(comment);
        } else {
            throw new SystemException(
                            IdeaExchangeErrorCodes.COMMENT_NULL_EXCEPTION,
                            "No Comment is associated with the given key");
        }
        if (adminRequestDao.saveRequest(adminRequest)) {
            status = IdeaExchangeConstants.SUCCESS;
        }
        return status;
    }

    /**
     * Trim the comment text upto 40 characters.
     * 
     * @param commentText
     * @return the trimmed text.
     */
    protected String getTrimmedComment(String commentText) {
        StringBuilder trimcomment = new StringBuilder();
        if (StringUtils.length(commentText) > 40) {
            trimcomment.append(commentText.substring(0, 40));
            trimcomment.append("..");
        } else {
            trimcomment.append(commentText);
        }
        return trimcomment.toString();
    }

    @Override
    public List<Comment> getComments(String ideaKey, RetrievalInfo retrievalInfo) {
        List<Comment> commentList;
        if (!StringUtils.isBlank(ideaKey)) {
            retrievalInfo = prepareRetrievalInfo(retrievalInfo);
            commentList = getCommentDao().getComments(ideaKey, retrievalInfo, IDEA_KEY);
            if (commentList != null && commentList.size() > ServiceConstants.ZERO) {
                return commentList;
            }
            logger.info("Comment List is null : There is no comment on the given Idea.");
        }
        logger.warn("Idea key is null or has whitespace only");
        return null;
    }

    /**
     * Prepares the {@link RetrievalInfo} object with values to be used as query
     * parameters.
     * Checks the received RetrievalInfo object attributes for valid
     * data.Updates the attributes if they contain garbage values.If the
     * received {@link RetrievalInfo} object is null,sets it to a new instance
     * with its attributes set to default values.
     * 
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        values to be used as query parameters
     * @return the {@link RetrievalInfo} object containing the query parameters
     */

    private RetrievalInfo prepareRetrievalInfo(RetrievalInfo retrievalInfo) {
        if (retrievalInfo == null) {
            retrievalInfo = new RetrievalInfo();
            retrievalInfo.setStartIndex(ServiceConstants.ZERO);
            retrievalInfo.setNoOfRecords(ServiceConstants.IDEA_COMMENT_LIST_DEFAULT_SIZE);
            retrievalInfo.setOrderType(ServiceConstants.DEFAULT_IDEA_COMMENT_ORDERING_TYPE);
            retrievalInfo.setOrderBy(ServiceConstants.DEFAULT_IDEA_COMMENT_ORDERING_FIELD);
        } else {
            // Handle garbage values if any.
            String orderOn = retrievalInfo.getOrderBy();
            String orderByParam = retrievalInfo.getOrderType();
            if (retrievalInfo.getStartIndex() < ServiceConstants.ZERO)
                retrievalInfo.setStartIndex(ServiceConstants.ZERO);
            if (retrievalInfo.getNoOfRecords() <= ServiceConstants.ZERO)
                retrievalInfo.setNoOfRecords(ServiceConstants.IDEA_COMMENT_LIST_DEFAULT_SIZE);
            if (orderByParam == null || !((orderByParam.equals(DaoConstants.ORDERING_ASCENDING)
                            || orderByParam.equals(DaoConstants.ORDERING_DESCENDING))))
                retrievalInfo.setOrderType(ServiceConstants.DEFAULT_IDEA_COMMENT_ORDERING_TYPE);
            if (orderOn == null || !(orderOn.equals(ServiceConstants.
                            IDEA_COMMENT_ORDERING_FIELD_CREATED_ON))) {
                retrievalInfo.setOrderBy(ServiceConstants.DEFAULT_IDEA_COMMENT_ORDERING_FIELD);
            }
        }
        return retrievalInfo;
    }

    /**
     * Update sharded counters for the users reputation point calculation.
     * 
     * @param user the User who posted the comment on idea
     * @param idea The Idea object.
     * @return the User who posted the comment on idea.
     */
    private User addPointsToUserOnPostComment(User user, Comment comment) {
        int points = 0;
        if (comment.getKey() != null) {
            points = IdeaExchangeConstants.REPUTATION_POINTS_COMMENT_POST;
        }
        shardedCounterService.updateTotalPoints(user.getUserKey(), points);
        return user;
    }

    @Override
    public void updateComment(Comment comment) {
        commentDao.saveComment(comment);
    }

    /**
     * @param auditManager the auditManager to set
     */
    public void setAuditManager(AuditManager auditManager) {
        this.auditManager = auditManager;
    }

    /**
     * @return the auditManager
     */
    public AuditManager getAuditManager() {
        return auditManager;
    }

    /**
     * @param commentDao the commentDao to set
     */
    public void setCommentDao(CommentDao commentDao) {
        this.commentDao = commentDao;
    }

    /**
     * @return the commentDao
     */
    public CommentDao getCommentDao() {
        return commentDao;
    }

    @Override
    public Comment getCommentById(String entityKey) {
        return commentDao.findEntityByPrimaryKey(IdeaComment.class, entityKey);
    }

    /**
     * @return the entityIndexService
     */
    public EntityIndexService getEntityIndexService() {
        return entityIndexService;
    }

    /**
     * @param entityIndexService the entityIndexService to set
     */
    public void setEntityIndexService(EntityIndexService entityIndexService) {
        this.entityIndexService = entityIndexService;
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

    /**
     * @return the adminRequestDao
     */
    public AdminRequestDao getAdminRequestDao() {
        return adminRequestDao;
    }

    /**
     * @param adminRequestDao the adminRequestDao to set
     */
    public void setAdminRequestDao(AdminRequestDao adminRequestDao) {
        this.adminRequestDao = adminRequestDao;
    }

    public IndexQueueUpdater getIndexQueueUpdater() {
        return indexQueueUpdater;
    }

    public void setIndexQueueUpdater(IndexQueueUpdater indexQueueUpdater) {
        this.indexQueueUpdater = indexQueueUpdater;
    }
}

