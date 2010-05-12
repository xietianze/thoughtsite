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
import static org.junit.Assert.assertNotNull;

import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.DatastoreTest;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for UserDaoImpl class
 * 
 * @author ssbains
 * 
 */
public class UserDaoImplTest extends DatastoreTest {
    private UserDaoImpl userDaoImpl;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        super.setUp();
        userDaoImpl = new UserDaoImpl();
        userDaoImpl.setPersistenceManagerFactory(pmf);
    }

    /**
     * Test method for
     * {@link com.google.ie.business.dao.impl.UserDaoImpl#saveUser(com.google.ie.business.domain.User)}
     * .
     */
    @Test
    public void testSaveUser() {
        User userToBeSaved = new User();
        userToBeSaved.setDisplayName("test user");
        User savedUser = userDaoImpl.saveUser(userToBeSaved);
        assertNotNull(savedUser);
        assertEquals(userToBeSaved.getDisplayName(), savedUser.getDisplayName());
    }

    /**
     * Test method for
     * {@link com.google.ie.business.dao.impl.UserDaoImpl#getUserById(java.lang.String)}
     * .
     */
    @Test
    public void getUserById() {
        User userToBeSaved = new User();
        userToBeSaved.setDisplayName("test user");
        userToBeSaved.setId("IDString");
        userDaoImpl.saveUser(userToBeSaved);
        User fetchedUser = userDaoImpl.getUserById(userToBeSaved.getId());
        assertNotNull(fetchedUser);
        assertEquals(userToBeSaved.getUserKey(), fetchedUser.getUserKey());
    }

    /**
     * 
     */
    @Test
    public void getUsers() {
        User userToBeSaved = new User();
        userToBeSaved.setDisplayName("test user");
        userToBeSaved.setRoleName("admin");
        userDaoImpl.saveUser(userToBeSaved);

        User userToBeSaved2 = new User();
        userToBeSaved2.setDisplayName("test user");
        userToBeSaved2.setRoleName("user");
        userDaoImpl.saveUser(userToBeSaved2);

        RetrievalInfo retrievalInfo = createDummyRetrievalParam(0, 10, "createdOn", "asc");
        assertEquals(1, userDaoImpl.getUsers(retrievalInfo, "user", null).size());

    }
}

