// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
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
