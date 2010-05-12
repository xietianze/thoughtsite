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

