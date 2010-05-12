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
import com.google.ie.business.domain.ProjectComment;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.CommentService;
import com.google.ie.business.service.EntityIndexService;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.constants.IdeaExchangeErrorCodes;
import com.google.ie.common.exception.SystemException;
import com.google.ie.common.taskqueue.IndexQueueUpdater;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.web.controller.WebConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * A service implementation of CommentService. Class is used to provide
 * operations on comments of projects.
 * 
 * @author Anuj Sirohi
 * 
 */
@Service
public class ProjectCommentServiceImpl implements CommentService {

    private static final Logger LOGGER = Logger.getLogger(ProjectCommentServiceImpl.class);

    @Autowired
    private CommentDao commentDao;
    @Autowired
    private AuditManager auditManager;
    @Autowired
    private EntityIndexService entityIndexService;

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

    @Autowired
    private AdminRequestDao adminRequestDao;
    @Autowired
    private IndexQueueUpdater indexQueueUpdater;

    public IndexQueueUpdater getIndexQueueUpdater() {
        return indexQueueUpdater;
    }

    public void setIndexQueueUpdater(IndexQueueUpdater indexQueueUpdater) {
        this.indexQueueUpdater = indexQueueUpdater;
    }

    private static final String PROJECT_KEY = "projectKey";

    @Override
    public Comment addComment(Comment comment, User user) {
        if (comment != null && !StringUtils.isBlank(comment.getText())
                        && user != null && !StringUtils.isBlank(user.getUserKey())) {
            comment.setCreatorKey(user.getUserKey());
            comment.setCreatedOn(new Date());
            comment.setStatus(Comment.STATUS_SAVED);
            comment = commentDao.saveComment(comment);
            if (comment != null && comment.getKey() != null) {
                /*
                 * Index the entity.Create an EntityIndex object for the entity
                 * to be indexed and then queue the job to task queue
                 */
                EntityIndex entityIndex = entityIndexService.createEntityIndex(comment.getKey());
                getIndexQueueUpdater().indexEntity(entityIndex.getKey());
            }
            if (comment != null && !StringUtils.isBlank(comment.getKey())) {
                auditManager.audit(user.getUserKey(), comment.getKey(),
                                ProjectComment.class.getSimpleName(),
                                ServiceConstants.AUDIT_ACTION_TYPE_SAVE_COMMENT);
                LOGGER.info("Project comment is saved");
                return comment;
            }
            LOGGER.error("Comment is not saved : Detached Comment object is null or without key");
            throw new SystemException("save.failed.exception",
                            "Comment is not saved : Detached Comment object is null or without key");
        }
        LOGGER.error("Parameter passed to the method are illegal");
        throw new SystemException("illegal.argument.exception",
                        "Either Comment is null or is without text / User is null or without key");
    }

    @Override
    public String flagComment(String projectCommentKey, User user) {
        /* Get description of the Comment */
        String status = IdeaExchangeConstants.FAIL;
        Comment comment = this.getCommentById(projectCommentKey);
        /* check if comment is already flagged */
        if (comment != null && comment.getStatus().equals(ProjectComment.STATUS_FLAGGED)) {
            status = IdeaExchangeConstants.PROJETC_COMMENT_ALLREADY_FLAGED;
            return status;
        }
        /* Create admin request to flag a project comment */
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setEntityKey(projectCommentKey);
        adminRequest.setEntityType(ProjectComment.class.getSimpleName());
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
     * @param commentText text of the comment posted on idea.
     * @return trimmed comment text
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
    public Comment getCommentById(String entityKey) {
        return getCommentDao().findEntityByPrimaryKey(ProjectComment.class, entityKey);
    }

    @Override
    public List<Comment> getComments(String key, RetrievalInfo retrievalInfo) {
        List<Comment> commentList;
        /* Fetch one more record than what is required */
        retrievalInfo.setNoOfRecords(retrievalInfo.getNoOfRecords() + WebConstants.ONE);
        if (!StringUtils.isBlank(key)) {
            retrievalInfo = prepareRetrievalInfo(retrievalInfo);
            commentList = getCommentDao().getComments(key, retrievalInfo, PROJECT_KEY);
            if (commentList != null && commentList.size() > ServiceConstants.ZERO) {
                return commentList;
            }
            LOGGER.info("Comment List is null : There is no comment on the given Idea.");
        }
        LOGGER.warn("Project key is null or has whitespace only or no comment found on project");
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
            String orderByParam = retrievalInfo.getOrderBy();
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

    @Override
    public void updateComment(Comment comment) {
        commentDao.saveComment(comment);
    }

    /**
     * @return the commentDao
     */
    public CommentDao getCommentDao() {
        return commentDao;
    }

    /**
     * @param commentDao the commentDao to set
     */
    public void setCommentDao(CommentDao commentDao) {
        this.commentDao = commentDao;
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

}

