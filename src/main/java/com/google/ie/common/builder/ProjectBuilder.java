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

package com.google.ie.common.builder;

import com.google.ie.business.domain.Developer;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.DeveloperService;
import com.google.ie.business.service.ProjectService;
import com.google.ie.business.service.UserService;
import com.google.ie.common.comparator.ProjectCreationDateComparator;
import com.google.ie.dto.ProjectDetail;
import com.google.ie.dto.RetrievalInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This will be used for converting the complex project dto
 * for different request flows like getProjectsForUser, getProjects.
 * 
 * @author gmaurya
 */
@Component
public class ProjectBuilder {
    /**
     * logger for logging error and data.
     */
    private static final Logger LOGGER = Logger.getLogger(ProjectBuilder.class);
    private static final int ZERO = 0;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    @Autowired
    private DeveloperService developerService;

    /**
     * Retrieves the list of Projects created by a user.
     * 
     * @param user the User object.
     * @param retrievalInfo the idea list retrieval information.
     * @return Returns the list of ProjectDetail objects.
     */
    public List<ProjectDetail> getProjectsForUser(User user, RetrievalInfo retrievalInfo) {
        List<ProjectDetail> projectDtoList = null;
        Set<String> projectKeys = new HashSet<String>();
        /* Get developers having with the specific user key */
        LOGGER.debug("User key =" + user.getUserKey());
        List<Developer> developers = developerService.getDeveloperByUserKey(user.getUserKey(),
                        retrievalInfo);
        /* Get the Set of project keys associated with the developers */
        Iterator<Developer> iterator = developers.iterator();
        while (iterator.hasNext()) {
            Developer developer = iterator.next();
            if (!StringUtils.isBlank(developer.getProjectKey())) {
                Project proj = projectService.getProjectById(developer.getProjectKey());
                if (!proj.getStatus().equals(Project.STATUS_DELETED))
                    projectKeys.add(developer.getProjectKey());
            }

        }
        LOGGER.debug("Project keys =" + projectKeys.toString());
        /* Get Project list by Set of project keys. */
        List<Project> projects = projectService.getProjects(projectKeys, retrievalInfo);
        Collections.sort(projects, new ProjectCreationDateComparator());
        /* Convert them into ProjectDetails. */
        if (projects != null && projects.size() > ZERO) {
            LOGGER.debug("Project size =" + projects.size());
            projectDtoList = convertToProjectDetailList(projects, true, false, true);
        }
        return projectDtoList;
    }

    /**
     * Get the detail of a project.
     * 
     * @param projectKey the key of the project
     * @return {@link ProjectDetail} object containing details
     */
    public ProjectDetail getProjectDetail(String projectKey) {
        Project project = projectService.getProjectById(projectKey);
        return convertToProjectDetail(project, true, true);
    }

    /**
     * Converts the list of {@link Project} objects into list of
     * {@link ProjectDetail} objects.
     * 
     * @param projectList the list of Project objects to be transformed into
     *        ProjectDetail.
     * @param addUser flag that specifies whether the User object
     *        associated with the project is to be fetched or not
     * @param addDevelopers flag that specifies whether the Developer objects
     *        associated with the project are to be fetched or not
     * @return the list of ProjectDetail.
     */
    public List<ProjectDetail> convertToProjectDetailList(List<Project> projectList,
                    boolean addUser, boolean addDevelopers, boolean trimLongFields) {
        List<ProjectDetail> projectDtoList = new ArrayList<ProjectDetail>();
        ProjectDetail projectDto = null;
        for (Project project : projectList) {
            if (trimLongFields) {
                shortenFields(project);
            }
            projectDto = convertToProjectDetail(project, addUser, addDevelopers);
            projectDtoList.add(projectDto);
        }
        return projectDtoList;
    }

    /**
     * Shortens the length of title and description fields
     * 
     * @param project
     */
    private void shortenFields(Project project) {
        if (null != project) {
            /* 50 chars for title */
            project.setName(StringUtils.abbreviate(project.getName(), 50));
            /* 150 chars for description */
            project.setDescription(StringUtils.abbreviate(project.getDescription(),
                            150));
        }
    }

    /**
     * Converts the the Project object to ProjectDetail object and populate data
     * of developer,user and project.
     * 
     * @param project the Project object to be transformed into IdeaDetail.
     * @param addUser flag that specifies whether the User object
     *        associated with the project is to be fetched or not
     * @param addDeveloper flag that specifies whether the Developer objects
     *        associated with the project are to be fetched or not
     * @return the list of IdeaDetail.
     */
    public ProjectDetail convertToProjectDetail(Project project,
                    boolean addUser, boolean addDeveloper) {
        ProjectDetail projectDetail = new ProjectDetail();
        projectDetail.setProject(project);
        if (addUser && project != null) {
            User user = userService.getUserByPrimaryKey(project.getCreatorKey());
            projectDetail.setUser(user);
        }
        if (addDeveloper && project != null) {
            projectDetail
                            .setDevelopers(developerService.getDevelopersByProjectKey(project
                            .getKey()));
        }
        projectDetail.setDeveloperCount(projectDetail.getDevelopers().size());

        return projectDetail;
    }

}

