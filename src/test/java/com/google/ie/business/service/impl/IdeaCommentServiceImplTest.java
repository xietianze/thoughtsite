// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.business.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.ie.business.dao.AdminRequestDao;
import com.google.ie.business.dao.CommentDao;
import com.google.ie.business.dao.impl.AdminRequestDaoImpl;
import com.google.ie.business.dao.impl.CommentDaoImpl;
import com.google.ie.business.dao.impl.DaoConstants;
import com.google.ie.business.domain.AdminRequest;
import com.google.ie.business.domain.Comment;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.ServiceTest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Test case for IdeaCommentServiceImpl class
 * 
 * @author Charanjeet singh
 */
public class IdeaCommentServiceImplTest extends ServiceTest {
    private IdeaCommentServiceImpl ideaCommentService;
    private CommentDao commentDao = mock(CommentDaoImpl.class);
    private AuditManager auditmanager = mock(AuditManager.class);
    private AdminRequestDao mockAdminRequestDao = mock(AdminRequestDaoImpl.class);

    @Before
    public void setUp() {
        super.setUp();
        if (ideaCommentService == null) {
            ideaCommentService = new IdeaCommentServiceImpl();
        }
        ideaCommentService.setCommentDao(commentDao);
        ideaCommentService.setAuditManager(auditmanager);
        ideaCommentService.setAdminRequestDao(mockAdminRequestDao);
    }

    @Test
    public void addComment() {
        User user = new User();
        user.setUserKey("key");

        IdeaComment comment = new IdeaComment();
        comment.setText("TestComment");
        comment.setIdeaKey("Ideakey");

        Comment comment1 = new IdeaComment();
        comment1.setText("TestComment");
        comment1.setKey("testKey");

        when(commentDao.saveComment(comment)).thenReturn(comment);
        assertNotNull(ideaCommentService.addComment(comment, user));
        assertNotNull(Comment.STATUS_SAVED, ideaCommentService.addComment(comment, user)
                        .getStatus());
    }

    @Test
    public void getComments() {
        String ideaKey = "testKey";
        // Idea idea = new Idea();
        // idea.setKey("testKey");
        List<Comment> comments = new ArrayList<Comment>();
        IdeaComment ideaComment = new IdeaComment();
        ideaComment.setKey("testCommentKey");
        comments.add(ideaComment);
        RetrievalInfo retrievalInfo = prepareRetrievalInfo(null);
        when(commentDao.getComments(ideaKey, retrievalInfo, "ideaKey")).thenReturn(comments);
        assertNotNull(ideaCommentService.getComments(ideaKey, retrievalInfo));
        assertEquals("testCommentKey", ideaCommentService.getComments(ideaKey, retrievalInfo)
                        .get(0).getKey());
    }

    @Test
    public void flagComment() {
        IdeaComment ideaComment = new IdeaComment();
        ideaComment.setKey("testCommentKey");
        ideaComment.setText("He this is an awesome idea.Keep it up and do " +
                        "let us know about the latest advancements in the idea");
        ideaComment.setStatus("Saved");

        IdeaComment saveIdeaComment = new IdeaComment();
        saveIdeaComment.setKey("testCommentKey");
        saveIdeaComment.setText("He this is an awesome idea.Keep it up and do " +
                        "let us know about the latest advancements in the idea");
        saveIdeaComment.setStatus("Flagged");

        User user = new User();
        user.setUserKey("key");
        user.setEmailId("anujsiroh@gmail.com");

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setEntityKey("ideaCommentKey");
        adminRequest.setEntityType(IdeaComment.class.getSimpleName());
        adminRequest.setRequesterkey("UserKey");
        adminRequest.setRequestType(AdminRequest.REQUEST_OBJECTIONABLE);
        adminRequest.setCreatedOn(new Date());
        adminRequest.setStatus(AdminRequest.STATUS_PENDING);
        adminRequest.setEntityTitle(ideaCommentService.getTrimmedComment(ideaComment.getText()));

        when(ideaCommentService.getCommentById(ideaComment.getKey())).thenReturn(ideaComment);
        when(ideaCommentService.getCommentDao().saveComment(ideaComment)).thenReturn(
                        saveIdeaComment);
        when(mockAdminRequestDao.saveRequest(adminRequest)).thenReturn(true);

        ideaCommentService.flagComment(ideaComment.getKey(), user);
        assertEquals("Flagged", saveIdeaComment.getStatus());
    }

    @Test
    public void getTrimmedComment() {
        /* checking if comment text is greater than 40 */
        String commentText = "Hey this is an awesome idea.Keep it up and do " +
                        "let us know about the latest advancements in the idea";
        String trimmedComment = ideaCommentService
                        .getTrimmedComment(commentText);
        assertEquals(42, trimmedComment.length());

        /* checking if comment text is smaller than 40 */
        commentText = "Hey this is an awesome idea";
        trimmedComment = ideaCommentService.getTrimmedComment(commentText);
        assertEquals(commentText.length(), trimmedComment.length());
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
}
