package com.google.ie.business.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.ie.business.domain.EntityIndex;
import com.google.ie.business.domain.Idea;
import com.google.ie.test.DatastoreTest;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for EntityIndexDaoImpl class
 * 
 * @author Ashish K. Dahiya
 */
public class EntityIndexDaoImplTest extends DatastoreTest {

    private IdeaDaoImpl ideaDao;
    private EntityIndexDaoImpl entityIndexDao;

    @Before
    public void setUp() {
        super.setUp();
        if (ideaDao == null) {
            ideaDao = new IdeaDaoImpl();
            ideaDao.setPersistenceManagerFactory(pmf);
            entityIndexDao = new EntityIndexDaoImpl();
            entityIndexDao.setPersistenceManagerFactory(pmf);
        }
    }

    @Test
    // @Transactional
    public void getUnIndexedIdea() {
        Idea idea = new Idea();
        idea.setTitle("Title");
        ideaDao.saveIdea(idea);

        entityIndexDao.createEntityIndex(idea.getKey());

        EntityIndex entityIndex = entityIndexDao.getUnIndexedEntity();

        assertNotNull(entityIndex);
    }

    @Test
    public void updateIdeaIndex() {
        Idea idea = new Idea();
        idea.setTitle("Title");
        ideaDao.saveIdea(idea);

        entityIndexDao.createEntityIndex(idea.getKey());

        EntityIndex entityIndex = entityIndexDao.getUnIndexedEntity();
        entityIndex.setIndexed(1);

        EntityIndex entityIndex2 = entityIndexDao.updateEntityIndex(entityIndex);

        assertEquals(1, entityIndex2.getIndexed());
    }

    @SuppressWarnings("cast")
    @Test
    public void getEntity() {
        Idea idea = new Idea();
        idea.setTitle("Title");
        idea = ideaDao.saveIdea(idea);

        Idea idea2 = (Idea) entityIndexDao.findEntityByPrimaryKey(Idea.class, idea.getKey());

        assertEquals(idea.getKey(), idea2.getKey());
    }

}
