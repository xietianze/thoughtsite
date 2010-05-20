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

import com.google.ie.business.domain.Developer;
import com.google.ie.dto.RetrievalInfo;

import java.util.List;

/**
 * A service specification for the Developer entity
 * 
 * @author gmaurya
 */
public interface DeveloperService {

    /**
     * Creates and saves Developer into data store.<br>
     * It created a Developer from a published idea, when user submits details
     * of Developer.
     * 
     * @param Developer The {@link Developer} entity.
     * @return The created Developer.
     */
    Developer saveDeveloper(Developer Developer);

    /**
     * Returns Developer on the basis of developer key.
     * 
     * @param key key of developer.
     * @return {@link Developer} object corresponding to the key.
     */
    Developer getDeveloperById(String key);

    /**
     * Return the list of Developers.
     * 
     * @param projectKey Key of project for which developers are being
     *        retrieved.
     * @return list of {@link Developer}.
     */
    List<Developer> getDevelopersByProjectKey(String projectKey);

    /**
     * Updates the status of developer.<br />
     * When developer joins a project, it's status is updated to
     * "STATUS_REQUEST_ACCEPTED" from "STATUS_REQUEST_ALLREADY_SENT".
     * 
     * @param developerKey Key of developer.
     * @param status New status.
     */
    void updateStatus(String developerKey, String statusRequestAccepted);

    /**
     * Retrieves the list of developers associated with a user.
     * 
     * @param userKey Key of user object.
     * @param retrievalInfo {@link RetrievalInfo} object representing the
     *        auxiliary information like startIndex, totalRecords etc.
     * @return List of {@link Developer}.
     */
    List<Developer> getDeveloperByUserKey(String userKey, RetrievalInfo retrievalInfo);
}

