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

import com.google.ie.business.domain.Developer;
import com.google.ie.business.domain.Idea;
import com.google.ie.dto.RetrievalInfo;

import java.util.List;

/**
 * A data access object specification for Developer entity.
 * 
 * @author gmaurya
 */
public interface DeveloperDao extends BaseDao {

    /**
     * Saves a {@link Developer} which is being created from a published
     * {@link Idea}, into the data store.
     * 
     * @param Developer {@link Developer} object
     * @return Returns the saved Developer.
     */
    Developer saveDeveloper(Developer developer);

    /**
     * Retrieves the list of developers associated with a project.
     * 
     * @param projectKey primary key of entity project
     * @return List list of {@link Developer} objects
     */
    List<Developer> getDevelopersByProjectKey(String projectKey);

    /**
     * Retrieves the list of developers associated with a user.
     * 
     * @param userKey primary key of entity User
     * @param retrievalInfo retrieval information detail
     * @return List list of {@link Developer} objects
     */
    List<Developer> getDevelopersByUserKey(String userKey, RetrievalInfo retrievalInfo);
}

