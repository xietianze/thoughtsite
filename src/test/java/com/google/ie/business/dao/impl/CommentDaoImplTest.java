// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.business.dao.impl;

import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.ProjectComment;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.DatastoreTest;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * Test cases for CommentDaoImpl class
 * 
 * @author Charanjeet singh
 * 
 */
public class CommentDaoImplTest extends DatastoreTest {
    private CommentDaoImpl commentDao = new CommentDaoImpl();
    private IdeaDaoImpl ideaDao = new IdeaDaoImpl();

    @Before
    public void setUp() {
        super.setUp();
        commentDao.setPersistenceManagerFactory(pmf);
        ideaDao.setPersistenceManagerFactory(pmf);
    }

    @Test
    public void saveComment() {
        IdeaComment comment = new IdeaComment();

        comment.setText("Test_Comment");
        Assert.assertNotNull(commentDao.saveComment(comment));
        Assert.assertEquals("Test_Comment", commentDao.saveComment(comment).getText());
    }

    @Test
    public void getIdeaComments() {
        Idea idea = new Idea();
        idea.setTitle("Test Idea");
        idea = ideaDao.saveIdea(idea);
        IdeaComment comment = new IdeaComment();
        comment.setIdeaKey(idea.getKey());
        comment.setText("Test_Comment");
        comment.setStatus(IdeaComment.STATUS_SAVED);
        comment = (IdeaComment) commentDao.saveComment(comment);
        RetrievalInfo retrievalParam = createDummyRetrievalParam(0, 1,
                        "createdOn", DaoConstants.ORDERING_DESCENDING);
        Assert.assertNotNull(commentDao.getComments(comment.getIdeaKey(),
                        retrievalParam, "ideaKey"));
        Assert.assertEquals("Test_Comment", commentDao.getComments(comment.getIdeaKey(),
                        retrievalParam, "ideaKey").get(0).getText());
    }

    @Test
    public void getProjectComments() {
        ProjectComment comment = new ProjectComment();
        comment.setProjectKey("newProjectKey");
        comment.setText("Test_Comment");
        comment.setStatus(IdeaComment.STATUS_SAVED);
        commentDao.saveComment(comment);
        RetrievalInfo retrievalParam = createDummyRetrievalParam(0, 1,
                        "createdOn", DaoConstants.ORDERING_DESCENDING);
        Assert.assertEquals("Test_Comment", commentDao.getComments("newProjectKey",
                        retrievalParam, "projectKey").get(0).getText());
    }
}
