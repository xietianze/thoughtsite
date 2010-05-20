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

import com.google.ie.business.domain.AdminRequest;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;

import java.util.List;

/**
 * A service specification for the Admin entity.
 * 
 * @author Surabhi
 * 
 */
public interface AdminService {

    /**
     * Soft deletion of an idea into data store. <br>
     * Changing the status of idea to 'Deleted' from 'Published'
     * 
     * @param ideaKey key of the idea to be deleted
     * @param user admin {@link User} object
     * @param adminReason reason for deletion
     */
    void deleteIdea(String ideaKey, User user, String adminReason);

    /**
     * Soft deletion of an project into data store. <br>
     * Changing the status of project to 'Deleted'.
     * 
     * @param projectKey key of the project to be deleted
     * @param user admin {@link User} object
     * @param adminReason reason for deletion
     */
    void deleteProject(String projectKey, User user, String adminReason);

    /**
     * Approve all requests provided in the list.
     * 
     * @param requestObjectList The list of {@link AdminRequest} object
     * @param user The {@link User} object
     */
    void approveRequests(List<AdminRequest> requestObjectList, User user);

    /**
     * Deny all admin request provided in the list
     * 
     * @param requestObjectList The list of {@link AdminRequest} object
     * @param user The {@link User} object
     */
    void denyRequests(List<AdminRequest> requestObjectList, User user);

    /**
     * Get admin requests based on the given type.
     * 
     * @param retrievalInfo The {@link RetrievalInfo } object
     * @param requestType Type of request Objectionable,Duplicate or ALL
     * 
     * @return List containing the {@link AdminRequest} entities
     */
    List<AdminRequest> getAdminRequests(RetrievalInfo retrievalInfo, String requestType);

    /**
     * Approve a request to administer.
     * 
     * @param adminRequest The {@link AdminRequest} object
     * @param user The {@link User} object
     */
    void approveAdminRequest(AdminRequest adminRequest, User user);

    /**
     * Deny admin request provided in the list
     * 
     * @param adminRequest he {@link AdminRequest} object
     * @param user The {@link User} object
     */
    void denyAdminRequest(AdminRequest adminRequest, User user);

    /**
     * Ban user from accessing the site.
     * 
     * @param user The {@link User} to be banned
     * @param adminUserkey The key of the admin user
     * @param adminReason The reason for deletion
     */
    User blacklistUser(User user, String adminUserkey, String adminReason);

    /**
     * Activate the user for accessing the site
     * 
     * @param user The {@link User} to activate for the site.
     * @param userKey The key for admin user who is activating the user.
     * @param string The reason to activate.
     * 
     * @return the activated {@link User}
     */
    User activateUser(User user, String adminUserkey, String adminReason);

    /**
     * Retrieves {@link Idea} having the comment with the given key.
     * 
     * @param key Key of the {@link IdeaComment}.
     * @return an object of the {@link Idea} which is having comment with the
     *         given key.
     */
    Idea getIdeaByCommentKey(String key);

    /**
     * Retrieves {@link Project} having the comment with the given key.
     * 
     * @param commentKey Key of the project comment.
     * @return An object of the {@link Project} which is having comment with the
     *         given key.
     */
    Project getProjectByCommentKey(String commentKey);

}

