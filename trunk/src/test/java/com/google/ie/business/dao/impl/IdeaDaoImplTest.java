package com.google.ie.business.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Tag;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.DatastoreTest;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test cases for IdeaDaoImpl class
 * 
 * @author abraina
 */
public class IdeaDaoImplTest extends DatastoreTest {

    private IdeaDaoImpl ideaDao;
    private UserDaoImpl userDao;
    private TagDaoImpl tagDao;

    @Before
    public void setUp() {
        super.setUp();
        if (ideaDao == null) {
            ideaDao = new IdeaDaoImpl();
            ideaDao.setPersistenceManagerFactory(pmf);
            userDao = new UserDaoImpl();
            userDao.setPersistenceManagerFactory(pmf);
            tagDao = new TagDaoImpl();
            tagDao.setPersistenceManagerFactory(pmf);
        }
    }

    @Test
    public void save() {
        Idea idea = new Idea();
        idea.setTitle("Title");
        ideaDao.saveIdea(idea);
        Query query = new Query(Idea.class.getSimpleName());
        assertEquals(1,
                        DatastoreServiceFactory.getDatastoreService().prepare(query)
                        .countEntities());
    }

    @Test
    public void save_existingIdea() {
        Idea idea = new Idea();
        idea.setTitle("Title");
        idea = ideaDao.saveIdea(idea);
        idea.setTitle("Title1234");
        idea = ideaDao.saveIdea(idea);
        Idea newIdea = ideaDao.getIdea(idea);
        assertEquals("Title1234", newIdea.getTitle());
        Query query = new Query(Idea.class.getSimpleName());
        assertEquals(1,
                        DatastoreServiceFactory.getDatastoreService().prepare(query)
                        .countEntities());
    }

    @Test
    public void getUserIdeas() {
        User user = new User();
        user.setUserKey("UserTestKey");
        String key = user.getUserKey();

        Idea idea1 = new Idea();
        idea1.setTitle("title1");
        idea1.setCreatorKey(key);
        ideaDao.saveIdea(idea1);
        RetrievalInfo dummyInfo = createDummyRetrievalParam(0, 1,
                        DaoConstants.DEFAULT_IDEA_ORDERING_FIELD,
                        DaoConstants.DEFAULT_IDEA_ORDERING_TYPE);
        Set<String> statusOfIdeas = new HashSet<String>();
        statusOfIdeas.add("published");
        List<Idea> ideas = ideaDao.getUserIdeas(user, statusOfIdeas, dummyInfo);
        assertNotNull(ideas);
    }

    @Test
    public void getUserIdeas_checkNumberOfIdeasReturned() {
        User user = new User();
        user.setReputationPoints(100);
        user = userDao.saveUser(user);

        String key = user.getUserKey();
        Idea idea1 = new Idea();
        idea1.setTitle("title1");
        idea1.setCreatorKey(key);
        idea1.setStatus("Objectionable");
        ideaDao.saveIdea(idea1);

        Idea idea2 = new Idea();
        idea2.setTitle("title2");
        idea2.setCreatorKey(key);
        idea2.setStatus("Saved");
        ideaDao.saveIdea(idea2);

        RetrievalInfo dummyInfo = createDummyRetrievalParam(0, 2,
                        DaoConstants.DEFAULT_IDEA_ORDERING_FIELD,
                        DaoConstants.DEFAULT_IDEA_ORDERING_TYPE);
        Set<String> statusOfIdeas = new HashSet<String>();
        statusOfIdeas.add("Published");
        statusOfIdeas.add("Objectionable");
        statusOfIdeas.add("Saved");
        List<Idea> ideas = ideaDao.getUserIdeas(user, statusOfIdeas, dummyInfo);
        assertEquals(2, ideas.size());
    }

    @Test
    public void getUserIdeas_checkRetrievalInfoParam() {
        User user = new User();
        user.setReputationPoints(100);
        user = userDao.saveUser(user);

        RetrievalInfo retrievalParam = createDummyRetrievalParam(0, 1,
                        DaoConstants.DEFAULT_IDEA_ORDERING_FIELD,
                        DaoConstants.DEFAULT_IDEA_ORDERING_TYPE);
        String key = user.getUserKey();
        Idea idea1 = new Idea();
        idea1.setTitle("title1");
        idea1.setCreatorKey(key);
        ideaDao.saveIdea(idea1);

        Idea idea2 = new Idea();
        idea2.setTitle("title2");
        idea2.setCreatorKey(key);
        idea2.setStatus("Published");
        ideaDao.saveIdea(idea2);

        Set<String> statusOfIdeas = new HashSet<String>();
        statusOfIdeas.add("Published");
        List<Idea> ideas = ideaDao.getUserIdeas(user, statusOfIdeas,
                        retrievalParam);
        assertEquals(1, ideas.size());
    }

    @Test
    public void getIdea() {
        Idea newIdea;
        Idea ideaOld = new Idea();
        ideaOld.setTitle("TestTitle");
        ideaOld.setCreatorKey("userKey");
        ideaOld.setStatus(Idea.STATUS_PUBLISHED);
        newIdea = ideaDao.saveIdea(ideaOld);
        assertNotNull(ideaDao.getIdea(newIdea));
    }

    @Test
    public void getIdeas() {
        Idea newIdea;
        Idea ideaOld = new Idea();
        ideaOld.setTitle("TestTitle");
        ideaOld.setCreatorKey("userKey");
        ideaOld.setStatus(Idea.STATUS_PUBLISHED);
        newIdea = ideaDao.saveIdea(ideaOld);

        RetrievalInfo retrievalParam = createDummyRetrievalParam(0, 1,
                        DaoConstants.IDEA_ORDERING_FIELD_PUBLISH_DATE,
                        DaoConstants.DEFAULT_IDEA_ORDERING_TYPE);
        Set<String> statusOfIdeas = new HashSet<String>();
        statusOfIdeas.add("Published");
        assertNotNull(ideaDao.getIdeas(retrievalParam, statusOfIdeas));
        assertEquals(newIdea.getTitle(),
                        ideaDao.getIdeas(retrievalParam, statusOfIdeas).get(0).getTitle());
    }

    @Test
    public void getIdeasByTagKey() {
        Idea expectedIdea = new Idea();
        Tag tag = new Tag();
        tag.setTitle("tag1");
        tag = tagDao.saveTag(tag);
        System.out.println("Tag key ::" + tag.getKey());
        expectedIdea.setTitle("Test Idea");
        expectedIdea.setCreatorKey("userKey");
        expectedIdea.setStatus(Idea.STATUS_PUBLISHED);
        Set<String> tagKeys = new HashSet<String>();
        tagKeys.add(tag.getKey());
        expectedIdea.setTagKeys(tagKeys);
        expectedIdea = ideaDao.saveIdea(expectedIdea);
        Set<String> setOfStatus = new HashSet<String>();
        setOfStatus.add(Idea.STATUS_PUBLISHED);
        List<Idea> listOfIdeas = ideaDao.getIdeasByTagKey(tag.getKey(), setOfStatus, new
                        RetrievalInfo(0, 10, ServiceConstants.DEFAULT_IDEA_ORDERING_FIELD,
                        ServiceConstants.DEFAULT_IDEA_ORDERING_TYPE));
        assertNotNull(listOfIdeas);
        Idea actualIdea = listOfIdeas.iterator().next();
        assertEquals(expectedIdea.getTitle(), actualIdea.getTitle());
    }
}
