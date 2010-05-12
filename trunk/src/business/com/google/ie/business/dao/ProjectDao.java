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

import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;

import java.util.List;
import java.util.Set;

/**
 * A data access object specification for Project entity.
 * 
 * @author Charanjeet singh
 */
public interface ProjectDao extends BaseDao {

    /**
     * Saves a {@link Project} which is being created from a published
     * {@link Idea}, into the data store.
     * 
     * @param project {@link Project} object
     * @return Returns the saved project.
     */
    Project saveProject(Project project);

    /**
     * Retrieves the list of projects.
     * 
     * All retrieval specific information can be passed using RetrievalInfo
     * object as a parameter.
     * 
     * @param retrievalInfo {@link RetrievalInfo} object containing information
     *        of startIndex and total number of records
     * @param statusOfProject the set of status to which the Project status
     *        should be matched
     * @return Returns the Project list.
     */
    List<Project> getProjects(RetrievalInfo retrievalInfo, Set<String> statusOfProject);

    /**
     * Retrieves the list of projects associated with a User.
     * 
     * All retrieval specific information can be passed using RetrievalInfo
     * object as a parameter.
     * 
     * @param user The User object.
     * @param retrievalInfo {@link RetrievalInfo} object having information of
     *        startIndex and total number of records
     * @param statusOfProject the set of status to which the Project status
     *        should be matched
     * @return Returns the Project list.
     */
    List<Project> getProjects(User user, RetrievalInfo retrievalInfo, Set<String> statusOfProject);

    /**
     * Retrieves the details of a project
     * 
     * @param projectKey String object holding the key of the project object to
     *        be retrieved.
     * @return Project object.
     */
    Project getProject(String projectKey);

    /**
     * Get project by idea key.
     * 
     * @param ideaKey primary key of entity Idea
     * @return List<Project> Returns the list of Projects.
     */
    List<Project> getProjectsByIdeaKey(String ideaKey);

    /**
     * Retrieves the projects by project keys.
     * 
     * @param projectKeys Set of project keys.
     * @param retrievalInfo RetrievalInfo object having information of
     *        startIndex and total number of records
     * @return Returns the list of Projects.
     */
    List<Project> getProjectsByKeys(Set<String> projectKeys, RetrievalInfo retrievalInfo);
}

