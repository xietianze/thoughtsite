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
import com.google.ie.business.domain.AdminRequest;
import com.google.ie.business.domain.Comment;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.ProjectComment;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.AdminService;
import com.google.ie.business.service.CommentService;
import com.google.ie.business.service.EntityIndexService;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.ProjectService;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.business.service.ShardedCounterService;
import com.google.ie.business.service.TagService;
import com.google.ie.business.service.UserService;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.common.constants.IdeaExchangeErrorCodes;
import com.google.ie.common.email.EmailManager;
import com.google.ie.common.exception.SystemException;
import com.google.ie.common.taskqueue.TagWeightUpdationManager;
import com.google.ie.common.util.SearchUtility;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.web.controller.WebConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A service implementation of the AdminService
 * 
 * @author Surabhi
 */
@Service
public class AdminServiceImpl implements AdminService {
    private static Logger logger = Logger.getLogger(AdminServiceImpl.class);
    private static boolean isDebug = logger.isDebugEnabled();

    @Autowired
    private EntityIndexService entityIndexService;
    @Autowired
    private IdeaService ideaService;
    @Autowired
    private AuditManager auditManager;
    @Autowired
    private TagService tagService;
    @Autowired
    private TagWeightUpdationManager weightUpdationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private ShardedCounterService shardedCounterService;
    @Autowired
    @Qualifier("ideaCommentServiceImpl")
    private CommentService ideaCommentService;
    @Autowired
    @Qualifier("projectCommentServiceImpl")
    private CommentService projectCommentService;
    @Autowired
    private AdminRequestDao adminRequestDao;
    @Autowired
    private ProjectService projectService;

    private static final String MAIL_OWNER_APPROVE_FLAGGED = "mail.owner.approve.flagged";
    private static final String MAIL_OWNER_APPROVE_DUPL_FLAGGED = "mail.owner.approve.dupl.flagged";
    private static final String MAIL_REQUESTER_APPROVE_FLAGGED = "mail.requester.approve.flagged";
    private static final String MAIL_REQUESTER_DENY_FLAGGED = "mail.requester.deny.flagged";
    private static final String MAIL_OWNER_DELETE_IDEA = "mail.owner.delete.idea";
    private static final String MAIL_BAN_USER = "mail.ban.user";
    private static final String MAIL_OWNER_DELETE_PROJECT = "mail.owner.delete.project";
    private static final String MAIL_SINGLE = "singleMail";
    private static final String BANNED = "banned";
    private static final String ACTIVATED = "activated";

    public AdminServiceImpl() {
    }

    @Override
    public void deleteIdea(String ideaKey, User user, String adminReason) {

        Idea idea = ideaService.getIdeaByKey(ideaKey);
        if (null != idea) {
            idea.setStatus(Idea.STATUS_DELETED);
            /* Persist idea object. */
            idea = ideaService.updateIdea(idea);

            /* Remove index of the entity */
            SearchUtility.deleteEntityIndex(idea);
            /* Decrement Tags weights asynchronously. */
            if (!StringUtils.isBlank(idea.getTags())) {
                getWeightUpdationManager().decrementWeight(idea.getTags());
            }
            /*
             * Remove this idea from popular,recently picked and
             * recent ideas lists in cache
             */
            ideaService.removeIdeaFromAllListsInCache(idea.getKey());
            /* create approved request of idea deletion */
            AdminRequest adminRequestObj = createRequestForIdeaDeletion(idea, user, adminReason);
            /* Send Mail to Owner */
            callEmailManager(idea.getCreatorKey(), adminRequestObj, MAIL_OWNER_DELETE_IDEA);
        }

    }

    /**
     * create approved request of idea deletion
     * 
     * @param user The admin {@link User} object
     * @param idea The {@link Idea} object
     * @param adminReason Reason to approve deletion of the idea.
     * 
     */
    private AdminRequest createRequestForIdeaDeletion(Idea idea, User user, String adminReason) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setAdminUserKey(user.getUserKey());
        adminRequest.setEntityKey(idea.getKey());
        adminRequest.setEntityType(Idea.class.getSimpleName());
        adminRequest.setEntityTitle(idea.getTitle());
        adminRequest.setRequestType(AdminRequest.REQUEST_DELETED);
        adminRequest.setStatus(AdminRequest.STATUS_APPROVED);
        adminRequest.setCreatedOn(new Date());
        adminRequest.setAdminReason(adminReason);

        adminRequestDao.saveRequest(adminRequest);

        return adminRequest;
    }

    /**
     * create approved request of project deletion
     * 
     * @param user The admin {@link User} object
     * @param idea The {@link Idea} object
     * @param adminReason Reason to approve deletion of the project.
     * 
     */
    private AdminRequest createRequestForProjectDeletion(Project project, User user,
                    String adminReason) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setAdminUserKey(user.getUserKey());
        adminRequest.setEntityKey(project.getKey());
        adminRequest.setEntityType(Project.class.getSimpleName());
        adminRequest.setEntityTitle(project.getName());
        adminRequest.setRequestType(AdminRequest.REQUEST_DELETED);
        adminRequest.setStatus(AdminRequest.STATUS_APPROVED);
        adminRequest.setCreatedOn(new Date());
        adminRequest.setAdminReason(adminReason);

        adminRequestDao.saveRequest(adminRequest);

        return adminRequest;
    }

    @Override
    public void approveRequests(List<AdminRequest> requestObjectList, User user) {
        Iterator<AdminRequest> iterator = requestObjectList.iterator();
        /* Iterate all the admin request. */
        while (iterator.hasNext()) {
            AdminRequest adminRequest = iterator.next();
            /* Process approve request */
            processApproveRequest(adminRequest.getEntityType(), adminRequest.getEntityKey(),
                            adminRequest
                            .getRequestType());
            adminRequest.setStatus(AdminRequest.STATUS_APPROVED);
            adminRequest.setAdminUserKey(user.getUserKey());
            /* Persist the admin approved request. */
            updateAdminRequest(adminRequest);
        }
    }

    /**
     * 
     * @param adminRequest
     */
    protected void updateAdminRequest(AdminRequest adminRequest) {
        adminRequestDao.saveRequest(adminRequest);
    }

    @Override
    public void approveAdminRequest(AdminRequest adminRequest, User user) {
        if (adminRequest != null && !StringUtils.isBlank(adminRequest.getKey())) {
            if (isDebug) {
                logger.debug("Retrieving adminRequest details for the adminRequest with key="
                                + adminRequest.getKey());
            }
            AdminRequest adminRequestObj = adminRequestDao.findEntityByPrimaryKey(
                            AdminRequest.class, adminRequest.getKey());
            String ownerKey = processApproveRequest(adminRequestObj.getEntityType(),
                            adminRequestObj.getEntityKey(),
                            adminRequestObj.getRequestType());
            adminRequestObj.setStatus(AdminRequest.STATUS_APPROVED);
            adminRequestObj.setAdminUserKey(user.getUserKey());
            adminRequestObj.setAdminReason(adminRequest.getAdminReason());
            updateAdminRequest(adminRequestObj);
            /* Send mail to Owner */
            if (adminRequestObj.getRequestType().equals(AdminRequest.REQUEST_DUPLICATE))
                callEmailManager(ownerKey, adminRequestObj, MAIL_OWNER_APPROVE_DUPL_FLAGGED);
            else
                callEmailManager(ownerKey, adminRequestObj, MAIL_OWNER_APPROVE_FLAGGED);
            /* Send mail to Requester */
            callEmailManager(adminRequestObj.getRequesterkey(), adminRequestObj,
                            MAIL_REQUESTER_APPROVE_FLAGGED);

        }
    }

    /**
     * Process approve request by calling appropriate service based on the given
     * entity
     * type.
     * 
     * @param entityType type of entity like Idea,IdeaComment or ProjectComment.
     * @param entityKey key of the entity.
     * @param requestType type of request like Objectionable,Duplicate etc.
     * @return creator key of the entity whose status is changed.
     */
    protected String processApproveRequest(String entityType, String entityKey, String requestType) {
        if (entityType.equalsIgnoreCase(Idea.class.getSimpleName())) {
            /* Processing the approval requests for Idea */
            return processApproveRequestForIdea(entityKey, requestType);
        } else if (entityType.equalsIgnoreCase(IdeaComment.class.getSimpleName())) {
            /* Processing the approval requests for IdeaComment */
            return processApproveRequestForIdeaComment(entityKey, requestType);
        } else if (entityType.equalsIgnoreCase(ProjectComment.class.getSimpleName())) {
            /* Processing the approval requests for ProjectComment */
            return processApproveRequestForProjectComment(entityKey, requestType);
        }
        return null;
    }

    /**
     * Processes the approval requests for {@link ProjectComment}
     * 
     * @param entityKey Key of Idea object.
     * @param requestType Request type like Objectionable
     * @return project comment creator's key
     */
    private String processApproveRequestForProjectComment(String entityKey, String requestType) {
        Comment projectComment = projectCommentService.getCommentById(entityKey);
        projectComment.setStatus(requestType);
        projectCommentService.updateComment(projectComment);
        /* Remove index of the entity */
        SearchUtility.deleteEntityIndex(projectComment);
        return projectComment.getCreatorKey();
    }

    /**
     * Processes the admin approval request for {@link IdeaComment}
     * 
     * @param entityKey Key of Idea object.
     * @param requestType Request type like Objectionable
     * @return idea comment creator's key
     */
    private String processApproveRequestForIdeaComment(String entityKey, String requestType) {
        Comment ideaComment = ideaCommentService.getCommentById(entityKey);
        ideaComment.setStatus(requestType);
        ideaCommentService.updateComment(ideaComment);
        /* Remove index of the entity */
        SearchUtility.deleteEntityIndex(ideaComment);
        return ideaComment.getCreatorKey();
    }

    /**
     * Processes the admin approval request for {@link Idea}
     * 
     * @param entityKey Key of Idea object
     * @param requestType Request type like Duplicate, Objectionable etc
     * @return idea creator's key
     */
    private String processApproveRequestForIdea(String entityKey, String requestType) {
        Idea idea = ideaService.getIdeaByKey(entityKey);
        idea.setStatus(requestType);
        ideaService.updateIdea(idea);
        /* Remove index of the entity - do not remove the duplicate idea */
        if (!requestType.equals(AdminRequest.REQUEST_DUPLICATE)) {
            SearchUtility.deleteEntityIndex(idea);
            /*
             * Remove this idea from popular,recently picked and
             * recent ideas lists in cache
             */
            ideaService.removeIdeaFromAllListsInCache(idea.getKey());
            /* Decrement Tags weights asynchronously. */
            if (!StringUtils.isBlank(idea.getTags())) {
                getWeightUpdationManager().decrementWeight(idea.getTags());
            }

        }
        return idea.getCreatorKey();
    }

    /**
     * Call EmailManager to send mail to the user with the given user key and
     * message based on the given message key.
     * 
     * @param userKey userKey of the recipient user.
     * @param adminRequestObj an object of type {@link AdminRequest}
     * @param messageKey key of the message source
     */
    private void callEmailManager(String userKey, AdminRequest adminRequestObj, String messageKey) {
        User user = userService.getUserByPrimaryKey(userKey);
        List<String> emailIdList = new ArrayList<String>();
        emailIdList.add(user.getEmailId());
        List<String> otherInfoList = new ArrayList<String>();
        if (adminRequestObj.getEntityType().equalsIgnoreCase(Idea.class.getSimpleName())) {
            otherInfoList.add(ServiceConstants.IDEA);
        } else if (adminRequestObj.getEntityType().equalsIgnoreCase(Project.class.getSimpleName())) {
            otherInfoList.add(ServiceConstants.PROJECT);
        } else {
            otherInfoList.add(ServiceConstants.COMMENT);
        }

        otherInfoList.add(adminRequestObj.getEntityTitle());
        otherInfoList.add(adminRequestObj.getRequestType());
        otherInfoList.add(adminRequestObj.getAdminReason());
        otherInfoList.add(messageKey);
        EmailManager.sendMail(MAIL_SINGLE, emailIdList, otherInfoList);

    }

    @Override
    public void denyRequests(List<AdminRequest> requestObjectList, User user) {
        Iterator<AdminRequest> iterator = requestObjectList.iterator();
        while (iterator.hasNext()) {
            AdminRequest adminRequest = iterator.next();
            processDenyRequest(adminRequest.getEntityType(), adminRequest.getEntityKey(),
                            adminRequest
                            .getRequestType());
            adminRequest.setStatus(AdminRequest.STATUS_REJECTED);
            adminRequest.setAdminUserKey(user.getUserKey());
            updateAdminRequest(adminRequest);
        }
    }

    @Override
    public void denyAdminRequest(AdminRequest adminRequest, User user) {
        if (adminRequest != null && !StringUtils.isBlank(adminRequest.getKey())) {
            if (isDebug) {
                logger.debug("Retrieving adminRequest details for the adminRequest with key="
                                + adminRequest.getKey());
            }
            AdminRequest adminRequestObj = adminRequestDao.findEntityByPrimaryKey(
                            AdminRequest.class, adminRequest.getKey());
            processDenyRequest(adminRequestObj.getEntityType(), adminRequestObj.getEntityKey(),
                            adminRequestObj
                            .getRequestType());
            adminRequestObj.setStatus(AdminRequest.STATUS_REJECTED);
            adminRequestObj.setAdminUserKey(user.getUserKey());
            adminRequestObj.setAdminReason(adminRequest.getAdminReason());
            updateAdminRequest(adminRequestObj);
            /* Send mail to Requester */
            callEmailManager(adminRequestObj.getRequesterkey(), adminRequestObj,
                            MAIL_REQUESTER_DENY_FLAGGED);
        }
    }

    /**
     * Process rejected request by calling appropriate service based on the
     * given entity type.
     * 
     * @param entityType Type of entity.
     * @param entityKey Key of Idea object
     * @param requestType Request type like Duplicate, Objectionable etc
     */
    protected void processDenyRequest(String entityType, String entityKey, String requestType) {
        if (entityType.equalsIgnoreCase(Idea.class.getSimpleName())) {
            Idea idea = ideaService.getIdeaByKey(entityKey);
            Set<String> flagTypeSet = idea.getFlagType();
            flagTypeSet.remove(requestType);
            idea.setOriginalIdeaKey("");
            ideaService.updateIdea(idea);
        } else if (entityType.equalsIgnoreCase(IdeaComment.class.getSimpleName())) {
            Comment ideaComment = ideaCommentService.getCommentById(entityKey);
            ideaComment.setStatus(Comment.STATUS_SAVED);
            ideaCommentService.updateComment(ideaComment);
        } else if (entityType.equalsIgnoreCase(ProjectComment.class.getSimpleName())) {
            Comment projectComment = projectCommentService.getCommentById(entityKey);
            projectComment.setStatus(Comment.STATUS_SAVED);
            projectCommentService.updateComment(projectComment);
        }
    }

    @Override
    public List<AdminRequest> getAdminRequests(RetrievalInfo retrievalInfo, String requestType) {
        /* Fetch one more record than what is required */
        retrievalInfo.setNoOfRecords(retrievalInfo.getNoOfRecords() + WebConstants.ONE);
        if (requestType == null || requestType.equalsIgnoreCase(ServiceConstants.ALL)) {
            return adminRequestDao.getAllAdminRequests(retrievalInfo);
        }
        return null;
    }

    @Override
    public User blacklistUser(User user, String adminUserkey, String adminReason) {
        /* Ban User for the site */
        user = userService.banUser(user);
        /* Create approved Admin request for the banned request */
        AdminRequest adminRequest = createRequestForBanUser(user, adminUserkey, adminReason, false);
        /* Send mail to User */
        List<String> emailIdList = new ArrayList<String>();
        emailIdList.add(user.getEmailId());

        List<String> otherInfoList = new ArrayList<String>();
        otherInfoList.add(User.class.getSimpleName());
        otherInfoList.add(user.getDisplayName());
        otherInfoList.add(BANNED);
        otherInfoList.add(adminRequest.getAdminReason());
        otherInfoList.add(MAIL_BAN_USER);

        EmailManager.sendMail(MAIL_SINGLE, emailIdList, otherInfoList);
        return user;
    }

    @Override
    public User activateUser(User user, String adminUserkey, String adminReason) {
        /* Ban User for the site */
        user = userService.activate(user);
        /* Create approved Admin request for the ativate request */
        AdminRequest adminRequest = createRequestForBanUser(user, adminUserkey, adminReason, true);
        /* Send mail to User */
        List<String> emailIdList = new ArrayList<String>();
        emailIdList.add(user.getEmailId());

        List<String> otherInfoList = new ArrayList<String>();
        otherInfoList.add(User.class.getSimpleName());
        otherInfoList.add(user.getDisplayName());
        otherInfoList.add(ACTIVATED);
        otherInfoList.add(adminRequest.getAdminReason());
        otherInfoList.add(MAIL_BAN_USER);

        EmailManager.sendMail(MAIL_SINGLE, emailIdList, otherInfoList);
        return user;
    }

    /**
     * Creates the request for banning the user.
     * 
     * @param user The admin {@link User} object
     * @param adminReason Reason to approve deletion of the project.
     * @param adminUserkey key of admin user
     */
    private AdminRequest createRequestForBanUser(User user, String adminUserKey,
                    String adminReason,
                    boolean activate) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setAdminUserKey(adminUserKey);
        adminRequest.setEntityKey(user.getUserKey());
        adminRequest.setEntityType(User.class.getSimpleName());
        adminRequest.setEntityTitle(user.getDisplayName());
        if (activate)
            adminRequest.setRequestType(AdminRequest.REQUEST_ACTIVATE);
        else
            adminRequest.setRequestType(AdminRequest.REQUEST_BANNED);
        adminRequest.setStatus(AdminRequest.STATUS_APPROVED);
        adminRequest.setCreatedOn(new Date());
        adminRequest.setAdminReason(adminReason);
        adminRequestDao.saveRequest(adminRequest);
        return adminRequest;
    }

    /**
     * Sets the AuditManager.
     * 
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

    public TagService getTagService() {
        return tagService;
    }

    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * @param weightUpdationManager the weightUpdationManager to set
     */
    public void setWeightUpdationManager(TagWeightUpdationManager weightUpdationManager) {
        this.weightUpdationManager = weightUpdationManager;
    }

    /**
     * @return the weightUpdationManager
     */
    public TagWeightUpdationManager getWeightUpdationManager() {
        return weightUpdationManager;
    }

    /**
     * @param userService the userService to set
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * @return the userService
     */
    public UserService getUserService() {
        return userService;
    }

    public EntityIndexService getEntityIndexService() {
        return entityIndexService;
    }

    public void setEntityIndexService(EntityIndexService entityIndexService) {
        this.entityIndexService = entityIndexService;
    }

    /**
     * @param shardedCounterService the shardedCounterService to set
     */
    public void setShardedCounterService(ShardedCounterService shardedCounterService) {
        this.shardedCounterService = shardedCounterService;
    }

    /**
     * @return the shardedCounterService
     */
    public ShardedCounterService getShardedCounterService() {
        return shardedCounterService;
    }

    /**
     * @return the ideaService
     */
    public IdeaService getIdeaService() {
        return ideaService;
    }

    /**
     * @param ideaService the ideaService to set
     */
    public void setIdeaService(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    /**
     * @return the ideaCommentService
     */
    public CommentService getIdeaCommentService() {
        return ideaCommentService;
    }

    /**
     * @param ideaCommentService the ideaCommentService to set
     */
    public void setIdeaCommentService(CommentService ideaCommentService) {
        this.ideaCommentService = ideaCommentService;
    }

    /**
     * @return the projectCommentService
     */
    public CommentService getProjectCommentService() {
        return projectCommentService;
    }

    /**
     * @param projectCommentService the projectCommentService to set
     */
    public void setProjectCommentService(CommentService projectCommentService) {
        this.projectCommentService = projectCommentService;
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

    @Override
    public Idea getIdeaByCommentKey(String key) {
        IdeaComment ideaComment = (IdeaComment) ideaCommentService.getCommentById(key);
        Idea idea = null;
        if (ideaComment != null && !StringUtils.isBlank(ideaComment.getKey())) {
            idea = ideaService.getIdeaByKey(ideaComment.getIdeaKey());
        } else {
            throw new SystemException(IdeaExchangeErrorCodes.COMMENT_NULL_EXCEPTION,
                            "There is no Idea Comment associated with the given key");
        }
        if (idea != null && !StringUtils.isBlank(idea.getKey())) {
            return idea;
        }
        throw new SystemException(IdeaExchangeErrorCodes.IDEA_NULL_EXCEPTION,
                        "There is no Idea associated with the given key");
    }

    @Override
    public Project getProjectByCommentKey(String commentKey) {
        ProjectComment projectComment = (ProjectComment) projectCommentService
                        .getCommentById(commentKey);
        Project project = null;
        if (projectComment != null && !StringUtils.isBlank(projectComment.getKey())) {
            project = projectService.getProjectById(projectComment.getProjectKey());
        } else {
            throw new SystemException(IdeaExchangeErrorCodes.COMMENT_NULL_EXCEPTION,
                            "There is no Project Comment associated with the given key");
        }
        if (project != null && !StringUtils.isBlank(project.getKey())) {
            return project;
        }
        throw new SystemException(IdeaExchangeErrorCodes.PROJECT_NULL_EXCEPTION,
                        "There is no project associated with the given key");
    }

    /**
     * @return the projectService
     */
    public ProjectService getProjectService() {
        return projectService;
    }

    /**
     * @param projectService the projectService to set
     */
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public void deleteProject(String projectKey, User user, String adminReason) {
        Project project = projectService.getProjectById(projectKey);
        if (project != null) {
            project.setStatus(Project.STATUS_DELETED);
        }
        /* Persist idea object. */
        project = projectService.updateProject(project);

        /* Remove index of the entity */
        SearchUtility.deleteEntityIndex(project);
        /* create approved request of idea deletion */
        AdminRequest adminRequestObj = createRequestForProjectDeletion(project, user, adminReason);

        /* Send Mail to Owner */
        callEmailManager(project.getCreatorKey(), adminRequestObj, MAIL_OWNER_DELETE_PROJECT);

    }
}

