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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.ie.business.dao.ShardedCounterDao;
import com.google.ie.business.dao.VoteDao;
import com.google.ie.business.dao.impl.ShardedCounterDaoImpl;
import com.google.ie.business.dao.impl.VoteDaoImpl;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.ShardedCounter;
import com.google.ie.business.domain.User;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.test.ServiceTest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test case for ShardedCounterServiceImpl class
 * 
 * @author gmaurya
 * 
 */
public class ShardedCounterServiceImplTest extends ServiceTest {
    private IdeaServiceImpl ideaService;
    private IdeaVoteServiceImpl ideaVoteService;
    private VoteDao voteDao = mock(VoteDaoImpl.class);
    private ShardedCounterServiceImpl shardedCounterService;
    private ShardedCounterDao shardedCounterDao = mock(ShardedCounterDaoImpl.class);;
    private AuditManager mockAuditManager = mock(AuditManager.class);
    private ShardedCounter counter1;
    private ShardedCounter counter2;
    private Idea idea;
    private User user;
    private List<ShardedCounter> shardListTotal;
    private List<ShardedCounter> shardListByShardNum;

    @Before
    public void setUp() {
        super.setUp();
        ideaService = mock(IdeaServiceImpl.class);

        shardedCounterService = new ShardedCounterServiceImpl();
        ideaService.setAuditManager(mockAuditManager);
        shardedCounterService.setShardedCounterDao(shardedCounterDao);
        shardedCounterService.setIdeaService(ideaService);
        ideaVoteService = new IdeaVoteServiceImpl();
        ideaVoteService.setAuditManager(mockAuditManager);
        ideaVoteService.setIdeaService(ideaService);
        ideaVoteService.setShardedCounterService(shardedCounterService);
        ideaVoteService.setVoteDao(voteDao);
        user = new User();
        user.setUserKey("userKey");
        // when(userService.addOrUpdateUser(user)).thenReturn(user);

        idea = new Idea();
        idea.setTitle("ideaTitle");
        idea.setKey("ideaKey");
        idea.setDescription("Idea Description");
        idea.setCreatorKey(user.getUserKey());
        when(ideaService.getIdeaByKey(idea.getKey())).thenReturn(idea);

        counter1 = new ShardedCounter(idea.getKey());
        counter1.setPositivePoint(0);
        counter1.setTotalPoint(15);
        counter1.setShardNumber(3);
        counter2 = new ShardedCounter(idea.getKey());
        counter2.setPositivePoint(0);
        counter2.setTotalPoint(15);
        counter2.setShardNumber(2);
        shardListTotal = new ArrayList<ShardedCounter>();
        shardListTotal.add(counter1);
        shardListTotal.add(counter2);

        shardListByShardNum = new ArrayList<ShardedCounter>();
        shardListByShardNum.add(counter2);
        when(shardedCounterDao.getShardsByParentKey(idea.getKey())).thenReturn(shardListTotal);
        when(shardedCounterDao.getShardsByParentKeyAndShardNum(idea.getKey(), 2)).thenReturn(
                        shardListByShardNum);
        when(shardedCounterDao.createOrUpdateShardedCounter(counter1)).thenReturn(
                        counter1);
    }

    @Test
    public void getMergedCounter() {

        ShardedCounter mergedCounter = shardedCounterService.getMergedShardedCounter(idea.getKey());
        assertEquals(counter1.getPositivePoint() + counter2.getPositivePoint(), mergedCounter
                        .getPositivePoint());
        assertEquals(counter1.getNegativePoint() + counter2.getNegativePoint(), mergedCounter
                        .getNegativePoint());
        assertEquals(counter1.getTotalPoint() + counter2.getTotalPoint(), mergedCounter
                        .getTotalPoint());
    }

    @Test
    public void getNegativePoint() {

        int negative = shardedCounterService.getNegativePoint(idea.getKey());
        assertEquals(counter1.getNegativePoint() + counter2.getNegativePoint(), negative);
    }

    @Test
    public void getPositivePoint() {

        int positivePoint = shardedCounterService.getNegativePoint(idea.getKey());
        assertEquals(counter1.getPositivePoint() + counter2.getPositivePoint(), positivePoint);
    }

    @Test
    public void incrementPositivePoints() {

        shardedCounterService.incrementPositivePoints(idea.getKey());
    }

    @Test
    public void incrementNegativePoints() {

        shardedCounterService.incrementNegativePoints(idea.getKey());
    }

    @Test
    public void updateTotalPoints() {

        shardedCounterService.updateTotalPoints(idea.getKey(), 89);
    }

}

