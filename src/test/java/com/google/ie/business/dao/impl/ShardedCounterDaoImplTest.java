package com.google.ie.business.dao.impl;

import static org.junit.Assert.assertEquals;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.ShardedCounter;
import com.google.ie.business.domain.User;
import com.google.ie.test.DatastoreTest;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Test cases for IdeaDaoImpl class
 * 
 * @author gmaurya
 */
public class ShardedCounterDaoImplTest extends DatastoreTest {

    private ShardedCounterDaoImpl shardedCounterDao;
    private UserDaoImpl userDao;
    private IdeaDaoImpl ideaDao;

    private Idea idea;
    private User user;
    private ShardedCounter counter;
    private static final int shardNumber = 2;

    @Before
    public void setUp() {
        super.setUp();
        if (shardedCounterDao == null) {
            shardedCounterDao = new ShardedCounterDaoImpl();
            shardedCounterDao.setPersistenceManagerFactory(pmf);
        }
        if (userDao == null) {
            userDao = new UserDaoImpl();
            userDao.setPersistenceManagerFactory(pmf);
        }
        if (ideaDao == null) {
            ideaDao = new IdeaDaoImpl();
            ideaDao.setPersistenceManagerFactory(pmf);
        }

        user = new User();
        user.setDisplayName("test user");
        user = userDao.saveUser(user);

        idea = new Idea();
        idea.setTitle("Title");
        idea.setCreatorKey(user.getUserKey());
        idea.setDescription("Idea Description");

        idea = ideaDao.saveIdea(idea);
    }

    @Test
    public void createOrUpdateShardedCounter() {
        counter = new ShardedCounter(idea.getKey());
        counter.setPositivePoint(0);
        counter.setTotalPoint(15);
        counter.setShardNumber(shardNumber);
        counter = shardedCounterDao.createOrUpdateShardedCounter(counter);

        Query query = new Query(ShardedCounter.class.getSimpleName());
        assertEquals(1,
                        DatastoreServiceFactory.getDatastoreService().prepare(query)
                        .countEntities());
        counter.setPositivePoint(12);
        counter.setTotalPoint(30);
        counter = shardedCounterDao.createOrUpdateShardedCounter(counter);
        counter = shardedCounterDao.findEntityByPrimaryKey(ShardedCounter.class, counter.getKey());
        assertEquals(12, counter.getPositivePoint());

    }

    @Test
    public void getShardByParentKey() {
        counter = new ShardedCounter(idea.getKey());
        counter.setPositivePoint(0);
        counter.setTotalPoint(15);
        counter.setShardNumber(shardNumber);
        counter = shardedCounterDao.createOrUpdateShardedCounter(counter);

        List<ShardedCounter> shards = shardedCounterDao.getShardsByParentKey(idea.getKey());
        assertEquals(1, shards.size());

    }

    @Test
    public void getShardByParentKeyAndShardNumber() {
        counter = new ShardedCounter(idea.getKey());
        counter.setPositivePoint(0);
        counter.setTotalPoint(15);
        counter.setShardNumber(shardNumber);
        counter = shardedCounterDao.createOrUpdateShardedCounter(counter);
        List<ShardedCounter> shards =
                        shardedCounterDao.getShardsByParentKeyAndShardNum(idea
                        .getKey(), shardNumber);
        assertEquals(1, shards.size());

    }

}
