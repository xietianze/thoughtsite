// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.ie.business.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.ie.business.dao.IdeaDao;
import com.google.ie.business.dao.impl.IdeaDaoImpl;
import com.google.ie.business.domain.EntityIndex;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Tag;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.EntityIndexService;
import com.google.ie.business.service.ShardedCounterService;
import com.google.ie.business.service.UserService;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.common.exception.SystemException;
import com.google.ie.common.taskqueue.IndexQueueUpdater;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.ServiceTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test case for IdeaServiceImpl class
 * 
 * @author Charanjeet singh
 * 
 */
public class IdeaServiceImplTest extends ServiceTest {
    private IdeaServiceImpl ideaService;
    private IdeaDao mockIdeaDao = mock(IdeaDaoImpl.class);
    private AuditManager mockAuditManager = mock(AuditManager.class);
    private TagServiceImpl tagService = mock(TagServiceImpl.class);
    private UserService userService = mock(UserServiceImpl.class);
    private ShardedCounterService shardedCounterService = mock(ShardedCounterServiceImpl.class);
    private EntityIndexService entityIndexService = mock(EntityIndexServiceImpl.class);
    private IndexQueueUpdater indexQueueUpdater = mock(IndexQueueUpdater.class);

    @Before
    public void setUp() {
        super.setUp();
        ideaService = new IdeaServiceImpl();
        ideaService.setIdeaDao(mockIdeaDao);
        ideaService.setAuditManager(mockAuditManager);
        ideaService.setTagService(tagService);
        ideaService.setUserService(userService);
        ideaService.setEntityIndexService(entityIndexService);
        ideaService.setIndexQueueUpdater(indexQueueUpdater);
        ideaService.setShardedCounterService(shardedCounterService);
    }

    @Test
    public void addSummary() {

        Idea idea = new Idea();
        idea.setKey("key");
        idea.setCreatorKey("userKey");
        idea.setStatus("Published");
        idea.setIdeaSummary("Test Summary");

        Idea ideaTmp = new Idea();
        ideaTmp.setKey("key");
        ideaTmp.setCreatorKey("userKey");
        ideaTmp.setStatus("Published");

        Idea updatredIdea = new Idea();
        updatredIdea.setKey("key");
        updatredIdea.setCreatorKey("userKey");
        updatredIdea.setStatus("Published");
        updatredIdea.setIdeaSummary("Test Summary");

        User user = new User();
        user.setUserKey("userKey");
        when(mockIdeaDao.findEntityByPrimaryKey(Idea.class, idea.getKey()))
                        .thenReturn(ideaTmp);
        when(mockIdeaDao.saveIdea(idea)).thenReturn(updatredIdea);
        Assert.assertTrue(ideaService.addSummary(idea.getKey(), idea.getIdeaSummary(), user));
    }

    @Test(expected = SystemException.class)
    public void addSummary_withIdeaStatusNotPublish() {
        Idea idea = new Idea();
        idea.setKey("key");
        idea.setCreatorKey("userKey");
        idea.setStatus("Saved");
        idea.setIdeaSummary("Test Summary");
        User user = new User();
        user.setUserKey("userKey");

        when(mockIdeaDao.saveIdea(idea)).thenReturn(idea);
        ideaService.addSummary(idea.getKey(), idea.getIdeaSummary(), user);
    }

    @Test(expected = SystemException.class)
    public void addSummary_withWrongIdeaCreator() {
        Idea idea = new Idea();
        idea.setKey("key");
        idea.setCreatorKey("userKey");
        idea.setStatus("Published");
        idea.setIdeaSummary("Test Summary");

        User user = new User();
        user.setUserKey("userKey1");

        when(mockIdeaDao.saveIdea(idea)).thenReturn(idea);
        ideaService.addSummary(idea.getKey(), idea.getIdeaSummary(), user);
    }

    @Test
    public void saveNewIdea() {
        Idea idea = new Idea();
        idea.setKey(null);
        idea.setTitle("ideaTitle");

        User user = new User();
        user.setUserKey("key");
        user.setId("id");

        Idea savedIdea = new Idea();
        savedIdea.setKey("MyKey");
        savedIdea.setTitle("ideaTitle");
        savedIdea.setCreatorKey(user.getUserKey());
        savedIdea.setStatus(Idea.STATUS_SAVED);
        savedIdea.setLastUpdated(new Date(System.currentTimeMillis()));

        when(mockIdeaDao.saveIdea(idea)).thenReturn(savedIdea);
        when(userService.saveUser(user)).thenReturn(user);
        when(userService.getUserById(user.getId())).thenReturn(user);
        assertNotNull(ideaService.saveIdea(idea, user));
        assertEquals(idea.getTitle(), ideaService.saveIdea(idea, user).getTitle());
    }

    @Test
    public void saveNullIdea() {
        User user = new User();
        assertNull(ideaService.saveIdea(null, user));
    }

    @Test
    public void formatTagString() {
        String tagStr = "TaG1,TAg2  Tag3 \n tag4";
        assertEquals("tag1,tag2,tag3,tag4", ideaService.formatTagString(tagStr));
    }

    @Test
    public void getIdeaDetails() {
        Idea actualIdea = new Idea();
        actualIdea.setTitle("Test_Idea");
        actualIdea.setKey("TestKey");
        when(mockIdeaDao.getIdea(actualIdea)).thenReturn(actualIdea);
        actualIdea = ideaService.getIdeaDetails(actualIdea);
        assertEquals("Test_Idea", actualIdea.getTitle());
    }

    @Test
    public void getIdeaDetails_withNullKeys() {
        Idea actualIdea = new Idea();
        actualIdea.setTitle("Test_Idea");
        actualIdea.setKey(null);
        when(mockIdeaDao.getIdea(actualIdea)).thenReturn(actualIdea);
        ideaService.getIdeaDetails(actualIdea);
        assertNull(ideaService.getIdeaDetails(actualIdea));
    }

    @Test
    public void publishIdea() {
        ideaService = mock(IdeaServiceImpl.class);
        int points = 5;
        User user = new User();
        user.setUserKey("userKey");

        Idea idea = new Idea();
        idea.setKey(null);
        idea.setTitle("ideaTitle");

        Idea savedIdea = new Idea();
        savedIdea.setKey("key");
        savedIdea.setTitle("ideaTitle");
        savedIdea.setCreatorKey(user.getUserKey());
        savedIdea.setStatus(Idea.STATUS_PUBLISHED);
        savedIdea.setLastUpdated(new Date(System.currentTimeMillis()));
        savedIdea.setPublishDate(new Date(System.currentTimeMillis()));

        EntityIndex index = new EntityIndex();
        index.setKey(KeyFactory.createKey("index", "idea"));
        index.setIndexed(0);

        // when(ideaService.getEntityIndexService().createEntityIndex(
        // savedIdea.getKey()))
        // .thenReturn(index);
        //
        // doNothing().when(ideaService.getIndexQueueUpdater()).indexEntity(
        // index.getKey());
        //
        // doNothing().when(ideaService.getShardedCounterService()).updateTotalPoints(
        // user.getUserKey(), points);
        // doNothing().when(ideaService.getObjectionableManager()).checkObjectionable(
        // idea.getKey());
        when(mockIdeaDao.saveIdea(idea)).thenReturn(savedIdea);
        when(ideaService.publishIdea(idea, user)).thenReturn(savedIdea);
        assertEquals("Published", savedIdea.getStatus());
    }

    @Test
    public void getIdeasForUser_forNullUser() {
        User user = null;
        RetrievalInfo retrievalInfo = null;

        // when(mockIdeaDao.getUserIdeas(user, retrievalInfo)).thenReturn(null);
        assertNull(ideaService.getIdeasForUser(user, retrievalInfo));
    }

    @Test
    public void getIdeasByTagName() {
        String tagKey = "testTagKey1234";
        Tag tag = new Tag();
        tag.setTitle("testTag");
        tag.setKey(tagKey);

        Idea expectedIdea = new Idea();
        expectedIdea.setTitle("Test Idea");
        expectedIdea.setStatus(Idea.STATUS_PUBLISHED);

        Set<String> tagKeys = new HashSet<String>();
        tagKeys.add(tagKey);
        expectedIdea.setTagKeys(tagKeys);

        List<Idea> expectedIdeasList = new ArrayList<Idea>();
        expectedIdeasList.add(expectedIdea);
        RetrievalInfo info = new RetrievalInfo();

        when(tagService.getTagByName("testTag")).thenReturn(tag);

        Set<String> setOfStatus = new HashSet<String>();
        setOfStatus.add(Idea.STATUS_PUBLISHED);
        setOfStatus.add(Idea.STATUS_DUPLICATE);

        when(mockIdeaDao.getIdeasByTagKey(tagKey, setOfStatus, info)).thenReturn(
                        expectedIdeasList);

        ideaService.getIdeasByTagName(tag.getTitle(), info);

        assertEquals(expectedIdea.getTitle(), expectedIdeasList.get(0).getTitle());
    }
}
