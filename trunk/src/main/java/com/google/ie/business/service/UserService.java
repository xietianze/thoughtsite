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

package com.google.ie.business.service;

import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;

import java.util.List;

/**
 * A service specification for User entity.
 * 
 * @author Charanjeet singh
 */
public interface UserService {
    /**
     * Saves a user in to data store.
     * 
     * @param user User to be saved.
     * @return Returns the saved User.
     */
    User saveUser(User user);

    /**
     * Retrieves a user object corresponding to a id in user object.
     * 
     * @param id id of the user to be fetched.
     * @return Returns the User object.
     */
    User getUserById(String id);

    /**
     * Retrieves the user object from datastore corresponding to a specific user
     * id.If the user does not exist in the datastore then saves the object
     * received as parameter.
     * 
     * @param user the user object containing the details fetched from friend
     *        connect.
     * @return the {@link User} object corresponding to the specific id.
     */
    User addOrUpdateUser(User user);

    /**
     * Retrieves a user object corresponding to a primary key
     * 
     * @param key primary key of the user to be fetched.
     * @return Returns the User object.
     */
    User getUserByPrimaryKey(String key);

    /**
     * 
     * Retrieves the user object from datastore corresponding to a specific user
     * id.If the user does not exist in the datastore then saves the object
     * received as parameter.
     * 
     * @param user the user to be banned
     * @return the updated {@link User} object
     */
    User banUser(User user);

    /**
     * 
     * Retrieves the user object from datastore corresponding to a specific user
     * id.If the user does not exist in the datastore then saves the object
     * received as parameter.
     * 
     * @param user user to be activated
     * @return the updated {@link User} object
     */
    User activate(User user);

    /**
     * Get all users based on the given role.
     * 
     * @param retrievalInfo the {@link RetrievalInfo} object containing
     *        parameters for the query
     * @param role the role name
     * @return list of users with the specific role
     */
    List<User> getUsers(RetrievalInfo retrievalInfo, String role);

}

