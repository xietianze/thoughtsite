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

import static org.junit.Assert.assertEquals;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.ie.business.domain.IdeaVote;
import com.google.ie.test.DatastoreTest;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for VoteDaoImpl class.
 * 
 * @author gmaurya
 */
public class VoteDaoImplTest extends DatastoreTest {

    private VoteDaoImpl voteDao;

    @Before
    public void setUp() {
        super.setUp();
        if (voteDao == null) {
            voteDao = new VoteDaoImpl();
            voteDao.setPersistenceManagerFactory(pmf);
        }
    }

    @Test
    public void addVote() {

        IdeaVote vote = new IdeaVote();
        vote.setCreatorKey("creatorKey");
        vote.setIdeaKey("ideaKey");
        vote.setVotePoints(15);
        vote.setPositiveVote(true);

        voteDao.saveVote(vote);
        Query query = new Query(IdeaVote.class.getSimpleName());
        assertEquals(1, DatastoreServiceFactory.getDatastoreService().prepare(query)
                        .countEntities());

    }

    @Test
    public void isIdeaAlreadyVotedByUser() {

        IdeaVote vote = new IdeaVote();
        vote.setCreatorKey("userKey");
        vote.setIdeaKey("ideaKey");
        vote.setVotePoints(15);
        vote.setPositiveVote(true);
        voteDao.saveVote(vote);

        assertEquals(true, voteDao.isIdeaAlreadyVotedByUser("userKey", "ideaKey"));
    }

    @Test
    public void ideaNotVotedByUser() {

        assertEquals(false, voteDao.isIdeaAlreadyVotedByUser("userKey", "ideaKey"));
    }
}

