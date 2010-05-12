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

package com.google.ie.business.dao;

import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;

import java.util.List;

/**
 * A service specification for the User entity
 * 
 * @author Charanjeet singh
 * 
 */
public interface UserDao extends BaseDao {
    /**
     * Saves a user in to data store.
     * 
     * @param user User object.
     * 
     * @return Returns the saved user.
     */
    User saveUser(User user);

    /**
     * Retrieves the user corresponding to the id.
     * 
     * @param id String object representing the user's id
     * @return Returns the user object or null;
     */
    User getUserById(String id);

    /**
     * Get list of registered users with the given role and status.
     * 
     * @param retrievalInfo information for accessing users.
     * @param role role of user
     * @param status status of user
     * @return list of users
     */
    List<User> getUsers(RetrievalInfo retrievalInfo, String role, String status);
}

