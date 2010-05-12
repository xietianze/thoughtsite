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

import com.google.ie.business.dao.ProjectDao;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.Query;

/**
 * A JDO implementation for ProjectDao.
 * 
 * @author Charanjeet singh
 */

public class ProjectDaoImpl extends BaseDaoImpl implements ProjectDao {
    private static final String IDEA_KEY = "ideaKey";

    @Override
    public Project saveProject(Project project) {
        project = getJdoTemplate().makePersistent(project);
        return project;
    }

    @Override
    public List<Project> getProjects(RetrievalInfo retrievalInfo, Set<String> statusOfProjects) {
        /* Execute query and return projects */
        return executeGetProject(null, retrievalInfo, statusOfProjects);
    }

    @Override
    public List<Project> getProjects(User user, RetrievalInfo retrievalInfo,
                    Set<String> statusOfProject) {
        return executeGetProject(user, retrievalInfo, statusOfProject);
    }

    @Override
    public Project getProject(String projectKey) {
        return findEntityByPrimaryKey(Project.class, projectKey);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Project> getProjectsByIdeaKey(String ideaKey) {
        Query query = getJdoTemplate().getPersistenceManagerFactory()
                        .getPersistenceManager().newQuery(Project.class);
        Map<String, Object> mapOfFilterValues = new HashMap<String, Object>();
        query.setFilter(IDEA_KEY + " == '" + ideaKey + "'");
        mapOfFilterValues.put("ideaKey", ideaKey);
        Collection<Project> projects = getJdoTemplate()
                        .find(query.toString(), mapOfFilterValues);
        if (projects == null) {
            return null;
        }
        return new ArrayList<Project>(projects);
    }

    /**
     * Method to retrieve the list of Projects.".
     * 
     * @param user The user object.
     * @param retrievalInfo RetrievalInfo object having information of
     *        startIndex and total number of records.
     * @param statusOfProject A set of strings holding the project
     *        status.
     * 
     * @return List of Project objects saved or published by user.
     */

    @SuppressWarnings("unchecked")
    private List<Project> executeGetProject(User user, RetrievalInfo retrievalInfo,
                    Set<String> statusOfProject) {
        Query query = null;
        try {
            query = getJdoTemplate().getPersistenceManagerFactory()
                            .getPersistenceManager().newQuery(Project.class);
            query.setRange(retrievalInfo.getStartIndex(), retrievalInfo.getStartIndex()
                            + retrievalInfo.getNoOfRecords());
            query.setOrdering("" + retrievalInfo.getOrderBy() + " " + retrievalInfo.getOrderType());
            Map<String, Object> mapOfFilterValues = new HashMap<String, Object>();
            if (user != null && user.getUserKey() != null) {
                query.setFilter("status == :statusOfProject && creatorKey == :creatorKeyParam");
                mapOfFilterValues.put("creatorKeyParam", user.getUserKey());
            } else {
                query.setFilter("status == :statusOfProject");
            }
            mapOfFilterValues.put("statusOfProject", statusOfProject);
            Collection<Project> collection = getJdoTemplate()
                            .find(query.toString(), mapOfFilterValues);
            /* Detach the result from the corresponding persistence manager */
            collection = getJdoTemplate().detachCopyAll(collection);
            return new ArrayList<Project>(collection);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<Project> getProjectsByKeys(Set<String> projectKeys, RetrievalInfo retrievalInfo) {
        Query query = null;
        try {
            query = getJdoTemplate().getPersistenceManagerFactory()
                            .getPersistenceManager().newQuery(Project.class);
            query.setFilter("key == :projectKeys");
            query.setRange(retrievalInfo.getStartIndex(), retrievalInfo.getNoOfRecords()
                            + retrievalInfo.getStartIndex());
            Map<String, Object> mapOfFilterValues = new HashMap<String, Object>();
            mapOfFilterValues.put("projectKeys", projectKeys);

            Collection<Project> projects = getJdoTemplate()
                            .find(query.toString(), mapOfFilterValues);
            /* Detach the result from the corresponding persistence manager */
            projects = getJdoTemplate().detachCopyAll(projects);
            return new ArrayList<Project>(projects);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }

    }
}

