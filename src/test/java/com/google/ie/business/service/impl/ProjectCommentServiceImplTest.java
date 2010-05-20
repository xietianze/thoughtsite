// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.business.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.ie.business.dao.AdminRequestDao;
import com.google.ie.business.dao.CommentDao;
import com.google.ie.business.dao.impl.AdminRequestDaoImpl;
import com.google.ie.business.dao.impl.CommentDaoImpl;
import com.google.ie.business.dao.impl.DaoConstants;
import com.google.ie.business.domain.AdminRequest;
import com.google.ie.business.domain.Comment;
import com.google.ie.business.domain.EntityIndex;
import com.google.ie.business.domain.ProjectComment;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.EntityIndexService;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.common.taskqueue.IndexQueueUpdater;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.ServiceTest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author asirohi
 * 
 */
public class ProjectCommentServiceImplTest extends ServiceTest {

    ProjectCommentServiceImpl projectCommentServiceImpl;
    private CommentDao commentDao = mock(CommentDaoImpl.class);
    private AuditManager auditmanager = mock(AuditManager.class);
    private AdminRequestDao mockAdminRequestDao = mock(AdminRequestDaoImpl.class);

    private EntityIndexService entityIndexService = mock(EntityIndexServiceImpl.class);
    private IndexQueueUpdater indexQueueUpdater = mock(IndexQueueUpdater.class);

    /**
     */
    @Before
    public void setUp() {
        if (projectCommentServiceImpl == null) {
            projectCommentServiceImpl = new ProjectCommentServiceImpl();
        }

        projectCommentServiceImpl.setCommentDao(commentDao);
        projectCommentServiceImpl.setAuditManager(auditmanager);
        projectCommentServiceImpl.setAdminRequestDao(mockAdminRequestDao);
        projectCommentServiceImpl.setEntityIndexService(entityIndexService);
        projectCommentServiceImpl.setIndexQueueUpdater(indexQueueUpdater);
    }

    /**
     * Test method for
     * {@link com.google.ie.business.service.impl.ProjectCommentServiceImpl#addComment(com.google.ie.business.domain.Comment, com.google.ie.business.domain.User)}
     * .
     */
    @Test
    public void addComment() {
        ProjectComment projectComment = new ProjectComment();
        projectComment.setText("Nice project");
        projectComment.setProjectKey("projectKey");
        projectComment.setKey("key");

        User user = new User();
        user.setUserKey("userKey");

        EntityIndex index = new EntityIndex();
        // index.setKey(KeyFactory.createKey("projectComment", 12345));
        // index.setIndexed(0);

        when(projectCommentServiceImpl.getCommentDao().saveComment(projectComment))
                        .thenReturn(projectComment);

        when(projectCommentServiceImpl.getEntityIndexService().createEntityIndex(
                        projectComment.getKey()))
                        .thenReturn(index);

        doNothing().when(projectCommentServiceImpl.getIndexQueueUpdater()).indexEntity(
                        index.getKey());
        assertNotNull(projectCommentServiceImpl.addComment(projectComment, user));
        assertEquals(projectComment,
                        projectCommentServiceImpl.addComment(projectComment,
                        user));

    }

    @Test
    public void getComments() {
        String projKey = "testKey";
        List<Comment> comments = new ArrayList<Comment>();

        ProjectComment projComment = new ProjectComment();
        projComment.setKey("testCommentKey");
        comments.add(projComment);

        RetrievalInfo retrievalInfo = prepareRetrievalInfo(null);
        when(commentDao.getComments(projKey, retrievalInfo,
                        "projectKey")).thenReturn(comments);
        assertNotNull(projectCommentServiceImpl.getComments(projKey, retrievalInfo));
        assertEquals("testCommentKey", projectCommentServiceImpl.getComments(projKey,
                        retrievalInfo)
                        .get(0).getKey());
    }

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
            if (orderOn == null
                            || !(orderOn
                            .equals(ServiceConstants.IDEA_COMMENT_ORDERING_FIELD_CREATED_ON)
                            )) {
                retrievalInfo.setOrderBy(ServiceConstants.DEFAULT_IDEA_COMMENT_ORDERING_FIELD);
            }
        }
        return retrievalInfo;
    }

    @Test
    public void flagComment() {
        ProjectComment ProjectComment = new ProjectComment();
        ProjectComment.setKey("testCommentKey");
        ProjectComment.setText("He this is an awesome project. Keep it up and do " +
                        "let us know about the latest advancements in the project");
        ProjectComment.setStatus("Saved");

        ProjectComment saveProjectComment = new ProjectComment();
        saveProjectComment.setKey("testCommentKey");
        saveProjectComment.setText("He this is an awesome project. Keep it up and do " +
                        "let us know about the latest advancements in the project");
        saveProjectComment.setStatus("Flagged");

        User user = new User();
        user.setUserKey("key");
        user.setEmailId("surabhi@gmail.com");

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setEntityKey("projectCommentKey");
        adminRequest.setEntityType(ProjectComment.class.getSimpleName());
        adminRequest.setRequesterkey("UserKey");
        adminRequest.setRequestType(AdminRequest.REQUEST_OBJECTIONABLE);
        adminRequest.setCreatedOn(new Date());
        adminRequest.setStatus(AdminRequest.STATUS_PENDING);
        adminRequest.setEntityTitle(projectCommentServiceImpl.getTrimmedComment(ProjectComment
                        .getText()));

        when(projectCommentServiceImpl.getCommentById(ProjectComment.getKey()))
                        .thenReturn(ProjectComment);
        when(projectCommentServiceImpl.getCommentDao().saveComment(ProjectComment)).thenReturn(
                        saveProjectComment);
        when(mockAdminRequestDao.saveRequest(adminRequest)).thenReturn(true);

        projectCommentServiceImpl.flagComment(ProjectComment.getKey(), user);
        assertEquals("Flagged", saveProjectComment.getStatus());
    }

    @Test
    public void getTrimmedComment() {
        /* checking if comment text is greater than 40 */
        String commentText = "Hey this is an awesome project. Keep it up and do " +
                        "let us know about the latest advancements in the project";
        String trimmedComment = projectCommentServiceImpl
                        .getTrimmedComment(commentText);
        assertEquals(42, trimmedComment.length());

        /* checking if comment text is smaller than 40 */
        commentText = "Hey this is an awesome project";
        trimmedComment =
                        projectCommentServiceImpl.getTrimmedComment(commentText);
        assertEquals(commentText.length(), trimmedComment.length());
    }
}
