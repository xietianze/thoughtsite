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

import com.google.appengine.api.datastore.Blob;
import com.google.ie.business.dao.ProjectDao;
import com.google.ie.business.domain.EntityIndex;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.EntityIndexService;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.ProjectService;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.common.cache.CacheConstants;
import com.google.ie.common.cache.CacheHelper;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.constants.IdeaExchangeErrorCodes;
import com.google.ie.common.exception.SystemException;
import com.google.ie.common.taskqueue.IndexQueueUpdater;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.web.controller.WebConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A service implementation of the ProjectService
 * 
 * @author Charanjeet singh
 * 
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    private static final Logger LOGGER = Logger.getLogger(ProjectServiceImpl.class);
    private static final int DEFAULT_NO_OF_RECENT_PROJECTS = 3;
    @Autowired
    private IdeaService ideaService;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private AuditManager auditManager;
    @Autowired
    private EntityIndexService entityIndexService;
    @Autowired
    private IndexQueueUpdater indexQueueUpdater;

    public IndexQueueUpdater getIndexQueueUpdater() {
        return indexQueueUpdater;
    }

    public void setIndexQueueUpdater(IndexQueueUpdater indexQueueUpdater) {
        this.indexQueueUpdater = indexQueueUpdater;
    }

    @Override
    public Project createOrUpdateProject(Project project, User user) {
        /* check for user and idea key. */
        if (StringUtils.isBlank(user.getUserKey()) || StringUtils.isBlank(project.getIdeaKey())) {
            throw new SystemException(IdeaExchangeErrorCodes.PROJECT_CREATION_FAILURE_EXCEPTION,
                            IdeaExchangeConstants.Messages.PROJECT_CREATION_FAILURE_MESSAGE);
        }
        Idea idea = ideaService.getIdeaByKey(project.getIdeaKey());

        /*
         * We need to check only for saved idea as edit project is possible on
         * deleted idea as well
         */
        if (!idea.getStatus().equals(Idea.STATUS_SAVED)) {
            /* Save project details */
            long date = System.currentTimeMillis();
            /* If project already exists then update it */
            if (project.getKey() != null) {
                Project projectFromDataStore = getProjectDao().getProject(project.getKey());
                projectFromDataStore.setUpdatedOn(new Date(date));
                projectFromDataStore.setName(project.getName());
                projectFromDataStore.setDescription(project.getDescription());
                projectFromDataStore.setLogo(project.getLogo());
                project = saveProjectLocal(projectFromDataStore);
            } else {
                /* If project is newly created */
                project.setStatus(Project.STATUS_CREATED);
                project.setCreatedOn(new Date(date));
                project.setUpdatedOn(new Date(date));
                project.setCreatorKey(user.getUserKey());
                project = saveProjectLocal(project);
                afterProjectCreation(project, user, idea);
            }
            return project;
        }
        return null;
    }

    /**
     * Check validity arguments for project creation .
     * 
     * @throws SystemException
     */
    private boolean checkIfValid(Idea idea) throws SystemException {

        /* Check if idea is published. */
        if (!idea.getStatus().equals(Idea.STATUS_PUBLISHED)) {
            throw new SystemException(
                            IdeaExchangeErrorCodes.PROJECT_CREATION_FAILURE_UNPUBLISHED_IDEA_EXCEPTION,
                            IdeaExchangeConstants.Messages.PROJECT_CREATION_FAILURE_UNPUBLISHED_IDEA_MESSAGE);
        }
        return true;
    }

    /**
     * Save project and index it.
     * 
     * @param project Project to be saved
     * @return Saved project
     */
    private Project saveProjectLocal(Project project) {
        project = projectDao.saveProject(project);
        if (project != null) {
            /*
             * Index the entity.Create an EntityIndex object for the entity
             * to be indexed and then queue the job to task queue
             */
            EntityIndex entityIndex = entityIndexService.createEntityIndex(project.getKey());
            getIndexQueueUpdater().indexEntity(entityIndex.getKey());
        }
        return project;
    }

    /**
     * Performs the post project creation activity.
     * 
     * @param project Project entity.
     * @param user The User who has created the project.
     * @param idea Idea entity on which a project is being created.
     * 
     */
    private void afterProjectCreation(Project project, User user, Idea idea) {
        LOGGER.info("Project is successfully saved");
        /* log the activity into Audit log. */
        getAuditManager().audit(user.getUserKey(), project.getKey(),
                        project.getClass().getName(),
                        ServiceConstants.AUDIT_ACTION_TYPE_CREATE_PROJECT);
        /* Place the idea into recently picked ideas. */
        CacheHelper.putObject(CacheConstants.PROJECT_NAMESPACE,
                        CacheConstants.RECENTLY_PICKED, idea);
        addIdeaToRecentlyPickedIdeaListInCache(idea);
    }

    @SuppressWarnings("unchecked")
    private void addIdeaToRecentlyPickedIdeaListInCache(Idea ideaToBeAdded) {
        /* Get the list of recently picked ideas from cache */
        LinkedList<Idea> ideas = (LinkedList<Idea>) CacheHelper.getObject(
                        CacheConstants.IDEA_NAMESPACE,
                        CacheConstants.RECENTLY_PICKED_IDEAS);
        if (ideas != null) {
            Iterator<Idea> iterator = ideas.iterator();
            Idea ideaFromCache = null;
            /* Iterate to check whether the list already contains the idea */
            while (iterator.hasNext()) {
                ideaFromCache = iterator.next();
                String ideaKey = ideaFromCache.getKey();
                if (ideaKey.equalsIgnoreCase(ideaToBeAdded.getKey())) {
                    /*
                     * If idea already exists in the list , move it to the head
                     */
                    ideas.remove(ideaFromCache);
                    ideas.addFirst(ideaFromCache);
                    CacheHelper.putObject(CacheConstants.IDEA_NAMESPACE,
                                    CacheConstants.RECENTLY_PICKED_IDEAS, ideas,
                                    CacheConstants.RECENTLY_PICKED_IDEAS_EXPIRATION_DELAY);
                    return;
                }
            }
        }
        /* This is executed if the idea does not already exist in cache */
        ideaService.addIdeaToListInCache(ideaToBeAdded, CacheConstants.RECENTLY_PICKED_IDEAS,
                        DEFAULT_NO_OF_RECENT_PROJECTS,
                        CacheConstants.RECENTLY_PICKED_IDEAS_EXPIRATION_DELAY);
    }

    @Override
    public Blob getImageById(String key) {
        Project project = projectDao.getProject(key);
        Blob image = project.getLogo();
        if (image == null)
            return null;
        return image;

    }

    @Override
    public Project getDetails(Project project) {
        if (project != null && !StringUtils.isBlank(project.getKey())) {
            LOGGER.debug("Retrieving details for the project with key=" + project.getKey());
            project = projectDao.getProject(project.getKey());

            return project;
        }
        throw new SystemException(IdeaExchangeErrorCodes.PROJECT_DETAILS_EXCEPTION,
                        IdeaExchangeConstants.Messages.PROJECT_DETAILS_EXCEPTION_MESSAGE);
    }

    @Override
    public List<Project> listProjects(RetrievalInfo retrievalInfo) {
        /* Fetch one more record than what is required */
        retrievalInfo.setNoOfRecords(retrievalInfo.getNoOfRecords() + WebConstants.ONE);
        /*
         * Retrieve Project based on the retrieval information. If retrievalInfo
         * object is null then use default parameter information.
         */
        retrievalInfo = prepareRetrievalInfoForQuery(retrievalInfo);
        /* Prepare the Set of status */
        Set<String> statusOfProject = new HashSet<String>();
        statusOfProject.add(Project.STATUS_CREATED);

        return getProjectDao().getProjects(retrievalInfo, statusOfProject);
    }

    @Override
    public List<Project> listProjects(User user, RetrievalInfo retrievalInfo) {
        /*
         * Retrieve Project based on the retrieval information. If retrievalInfo
         * object is null then use default parameter information.
         */
        retrievalInfo = prepareRetrievalInfoForQuery(retrievalInfo);
        /* Prepare the Set of status */
        Set<String> statusOfProject = new HashSet<String>();
        statusOfProject.add(Project.STATUS_CREATED);
        return getProjectDao().getProjects(user, retrievalInfo, statusOfProject);
    }

    @Override
    public List<Project> getProjects(Set<String> projectKeys, RetrievalInfo retrievalInfo) {
        return getProjectDao().getProjectsByKeys(projectKeys, retrievalInfo);
    }

    /**
     * Prepares the {@link RetrievalInfo} object with values to be used as query
     * parameters.
     * Checks the received RetrievalInfo object attributes for valid
     * data.Updates the attributes if they contain garbage values.If the
     * received {@link RetrievalInfo} object is null,sets it to a new instance
     * with its attributes set to default values.
     * 
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        values to be used as query parameters
     * @return the {@link RetrievalInfo} object containing the query parameters
     */

    private RetrievalInfo prepareRetrievalInfoForQuery(RetrievalInfo retrievalInfo) {
        if (retrievalInfo == null) {
            retrievalInfo = new RetrievalInfo();
            retrievalInfo.setStartIndex(ServiceConstants.ZERO);
            retrievalInfo.setNoOfRecords(ServiceConstants.PROJECT_LIST_DEFAULT_SIZE);
            retrievalInfo.setOrderType(ServiceConstants.PROJECT_DEFAULT_ORDERING_TYPE);
            retrievalInfo.setOrderBy(ServiceConstants.DEFAULT_PROJECT_ORDERING_FIELD);
        } else {
            // Handle garbage values if any.
            String orderOn = retrievalInfo.getOrderBy();
            String orderByParam = retrievalInfo.getOrderType();
            if (retrievalInfo.getStartIndex() < ServiceConstants.ZERO)
                retrievalInfo.setStartIndex(ServiceConstants.ZERO);
            if (retrievalInfo.getNoOfRecords() <= ServiceConstants.ZERO)
                retrievalInfo.setNoOfRecords(ServiceConstants.PROJECT_LIST_DEFAULT_SIZE);
            if (orderByParam == null || !((orderByParam.equals(ServiceConstants.ORDERING_ASCENDING)
                            || orderByParam.equals(ServiceConstants.ORDERING_DESCENDING))))
                retrievalInfo.setOrderType(ServiceConstants.PROJECT_DEFAULT_ORDERING_TYPE);
            if (orderOn == null
                            || !orderOn.equals(ServiceConstants.PROJECT_ORDERING_FIELD_UPDATED_ON)) {
                retrievalInfo.setOrderBy(ServiceConstants.DEFAULT_PROJECT_ORDERING_FIELD);
            }
        }
        return retrievalInfo;
    }

    @Override
    public List<Project> getProjectsByIdeaKey(String key) {
        return getProjectDao().getProjectsByIdeaKey(key);
    }

    /**
     * @param ideaService the ideaService to set
     */
    public void setIdeaService(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    /**
     * @return the ideaService
     */
    public IdeaService getIdeaService() {
        return ideaService;
    }

    /**
     * @param projectDao the projectDao to set
     */
    public void setProjectDao(ProjectDao projectDao) {
        this.projectDao = projectDao;
    }

    /**
     * @return the projectDao
     */
    public ProjectDao getProjectDao() {
        return projectDao;
    }

    /**
     * @param auditManager the auditManager to set
     */
    public void setAuditManager(AuditManager auditManager) {
        this.auditManager = auditManager;
    }

    /**
     * @return the auditManager
     */
    public AuditManager getAuditManager() {
        return auditManager;
    }

    /**
     * @return the entityIndexService
     */
    public EntityIndexService getEntityIndexService() {
        return entityIndexService;
    }

    /**
     * @param entityIndexService the entityIndexService to set
     */
    public void setEntityIndexService(EntityIndexService entityIndexService) {
        this.entityIndexService = entityIndexService;
    }

    @Override
    public Project getProjectById(String key) {
        return projectDao.findEntityByPrimaryKey(Project.class, key);

    }

    @Override
    public List<Project> getRecentProjects() {
        RetrievalInfo retrievalInfo = new RetrievalInfo();
        retrievalInfo.setNoOfRecords(DEFAULT_NO_OF_RECENT_PROJECTS);
        retrievalInfo.setOrderBy(Project.PROJECT_FIELD_CREATED_ON);
        retrievalInfo.setOrderType(ServiceConstants.ORDERING_DESCENDING);
        /* The set of status to match for */
        Set<String> statusOfProject = new HashSet<String>();
        statusOfProject.add(Project.STATUS_CREATED);
        return projectDao.getProjects(retrievalInfo, statusOfProject);
    }

    @Override
    public Project updateProject(Project project) {
        return projectDao.saveProject(project);
    }

}

