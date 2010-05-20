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

import com.google.appengine.api.datastore.Blob;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;

import java.util.List;
import java.util.Set;

/**
 * A service specification for the Project entity
 * 
 * @author Charanjeet singh
 */
public interface ProjectService {

    /**
     * Creates and saves {@link Project} into data store.<br>
     * It creates a project from a published idea, when user submits details of
     * project.
     * 
     * @param project The {@link Project} entity.
     * @param user The creator of project.
     * @return The created {@link Project}.
     */
    Project createOrUpdateProject(Project project, User user);

    /**
     * Returns {@link Project} on the basis for project key.
     * 
     * @param key key id of project entity.
     * @return {@link Project} with full details.
     */
    Project getProjectById(String key);

    /**
     * Retrieves the list of projects by set of project keys.
     * 
     * @param projectKeys Set of project keys.
     * @param retrievalInfo request parameters encapsulated in
     *        {@link RetrievalInfo}.
     * @return List of {@link Project} entities.
     */
    List<Project> getProjects(Set<String> projectKeys, RetrievalInfo retrievalInfo);

    /**
     * This method return Image on the basis for key.
     * 
     * @param key key id project entity.
     * @return object holding the bytes of image.
     */
    Blob getImageById(String key);

    /**
     * Returns the paginated list of projects, sorted by their updation date.
     * 
     * @param retrievalInfo RetrievalInfo object representing the auxiliary
     *        request information to be passed.
     * @return list of projects.
     */
    List<Project> listProjects(RetrievalInfo retrievalInfo);

    /**
     * Return the paginated list of projects created or joined by user,
     * sorted on their creation date.
     * 
     * @param user The {@link User} object.
     * @param retrievalInfo {@link RetrievalInfo} object representing the
     *        auxiliary information to be passed.
     * @return list of {@link Projects} created or joined by user.
     */
    List<Project> listProjects(User user, RetrievalInfo retrievalInfo);

    /**
     * Returns the details of project.
     * 
     * @param project Project for which details are being fetched.
     * @return project details populated in {@link Project} object.
     */
    Project getDetails(Project project);

    /**
     * Retrieves the {@link Project} associated with an {@link Idea}.
     * 
     * @param key {@link Idea} key.
     * @return List of {@link Projects}.
     */
    List<Project> getProjectsByIdeaKey(String ideaKey);

    /**
     * Retrieves the most recent projects.
     * 
     * @return list containing most recent {@link Project} objects
     */
    List<Project> getRecentProjects();

    /**
     * Update project in data store.
     * 
     * @param project {@link Project} to be updated in to data store.
     * 
     * @return the updated project.
     */
    Project updateProject(Project project);
}

