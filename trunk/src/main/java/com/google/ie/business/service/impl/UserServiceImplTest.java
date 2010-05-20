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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.ie.business.dao.UserDao;
import com.google.ie.business.dao.impl.UserDaoImpl;
import com.google.ie.business.domain.User;
import com.google.ie.common.util.SearchUtility;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.ServiceTest;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for UserServiceImpl class
 * 
 * @author Surabhi Bhatnagar
 * 
 */
public class UserServiceImplTest extends ServiceTest {
    private UserServiceImpl userService;
    private UserDao mockUserDao = mock(UserDaoImpl.class);

    @Before
    public void setUp() {
        super.setUp();
        userService = new UserServiceImpl();
        userService.setUserDao(mockUserDao);
    }

    @Test
    public void saveUser() {
        User user = new User();
        user.setUserKey("key");
        user.setUserKey("Display Name");
        user.setId("id");
        user.setStatus("active");
        when(userService.saveUser(user)).thenReturn(user);
        assertNotNull(userService.saveUser(user));
    }

    @Test
    public void addOrUpdateUser() {
        User user = new User();
        user.setUserKey("key");
        user.setDisplayName("Display Name");
        user.setId("id");
        user.setThumbnailUrl("thumbnailUrl");
        user.setEmailId("emailId@google.com");
        when(userService.saveUser(user)).thenReturn(user);
        when(userService.getUserById("id")).thenReturn(user);
        assertNotNull(userService.addOrUpdateUser(user));
    }

    @Test
    public void banUser() {
        User user = new User();
        user.setUserKey("key");
        user.setDisplayName("Display Name");
        user.setId("id");
        user.setThumbnailUrl("thumbnailUrl");
        user.setEmailId("emailId@google.com");
        user.setStatus("active");

        User banUser = new User();
        banUser.setUserKey("key");
        banUser.setDisplayName("Display Name");
        banUser.setId("id");
        banUser.setThumbnailUrl("thumbnailUrl");
        banUser.setEmailId("emailId@google.com");
        banUser.setStatus("banned");

        when(userService.getUserDao().findEntityByPrimaryKey(User.class, user.getUserKey()))
                        .thenReturn(user);
        when(userService.saveUser(user)).thenReturn(banUser);
        SearchUtility.indexEntity(user);

        userService.banUser(banUser);
        assertEquals("banned", banUser.getStatus());
    }

    @Test
    public void activate() {
        User user = new User();
        user.setUserKey("key");
        user.setDisplayName("Display Name");
        user.setId("id");
        user.setThumbnailUrl("thumbnailUrl");
        user.setEmailId("emailId@google.com");
        user.setStatus("banned");

        User banUser = new User();
        banUser.setUserKey("key");
        banUser.setDisplayName("Display Name");
        banUser.setId("id");
        banUser.setThumbnailUrl("thumbnailUrl");
        banUser.setEmailId("emailId@google.com");
        banUser.setStatus("active");

        when(userService.getUserDao().findEntityByPrimaryKey(User.class, user.getUserKey()))
                        .thenReturn(user);
        when(userService.saveUser(user)).thenReturn(banUser);
        userService.activate(banUser);
        assertEquals("active", banUser.getStatus());
    }

    @Test
    public void getUsers() {
        RetrievalInfo retrievalInfo = new RetrievalInfo();
        String role = "user";
        assertNotNull(userService.getUserDao().getUsers(retrievalInfo, role, null));
    }

}

