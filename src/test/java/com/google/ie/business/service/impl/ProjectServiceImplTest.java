// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.business.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.ie.business.dao.impl.ProjectDaoImpl;
import com.google.ie.business.domain.EntityIndex;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.EntityIndexService;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.common.exception.SystemException;
import com.google.ie.common.taskqueue.IndexQueueUpdater;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.ServiceTest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test case for ProjectServiceImpl class
 * 
 * @author Charanjeet singh
 * 
 */
public class ProjectServiceImplTest extends ServiceTest {
    private ProjectServiceImpl projectService;
    private ProjectDaoImpl projectDao = mock(ProjectDaoImpl.class);
    private AuditManager mockAuditManager = mock(AuditManager.class);
    private IdeaServiceImpl ideaService = mock(IdeaServiceImpl.class);
    private EntityIndexService entityIndexService = mock(EntityIndexServiceImpl.class);
    private IndexQueueUpdater indexQueueUpdater = mock(IndexQueueUpdater.class);

    @Before
    public void setUp() {
        super.setUp();
        if (projectService == null)
            projectService = new ProjectServiceImpl();
        projectService.setAuditManager(mockAuditManager);
        projectService.setProjectDao(projectDao);
        projectService.setIdeaService(ideaService);
        projectService.setEntityIndexService(entityIndexService);
        projectService.setIndexQueueUpdater(indexQueueUpdater);
    }

    @Test
    public final void createProject() {
        User user = new User();
        user.setUserKey("userKey");

        Idea idea = new Idea();
        idea.setKey("ideaKey");
        idea.setStatus(Idea.STATUS_PUBLISHED);

        Project project = new Project();
        project.setIdeaKey("ideaKey");
        project.setName("Project Name");
        project.setKey("projectkey");
        project.setStatus(Project.STATUS_CREATED);

        EntityIndex index = new EntityIndex();
        index.setKey(KeyFactory.createKey("index", "project"));
        index.setIndexed(0);

        when(projectService.getIdeaService().getIdeaByKey(project.getIdeaKey())).thenReturn(idea);
        when(projectService.getProjectDao().saveProject(project)).thenReturn(project);
        when(projectService.getEntityIndexService().createEntityIndex(project.getKey()))
                        .thenReturn(index);
        when(projectService.getProjectDao().getProject(project.getKey())).thenReturn(project);
        doNothing().when(projectService.getIndexQueueUpdater()).indexEntity(index.getKey());
        assertNotNull(projectService.createOrUpdateProject(project, user));
        assertEquals(projectService.createOrUpdateProject(project, user).getStatus(),
                        Project.STATUS_CREATED);
    }

    @Test(expected = SystemException.class)
    public final void createProject_withIdeaNotPublished() {
        User user = new User();
        user.setUserKey("userKey");

        Idea idea = new Idea();
        idea.setKey("ideaKey");
        idea.setStatus(Idea.STATUS_OBJECTIONABLE);

        Project project = new Project();
        project.setIdeaKey("ideaKey");
        project.setName("Project Name");

        when(projectService.getIdeaService().getIdeaByKey(project.getIdeaKey())).thenReturn(idea);
        when(projectService.getProjectDao().saveProject(project)).thenReturn(project);
        assertNotNull(projectService.createOrUpdateProject(project, user));
    }

    @Test
    public final void listProjects() {
        Set<String> statusOfProject = new HashSet<String>();
        RetrievalInfo retrievalInfo = new RetrievalInfo();
        statusOfProject.add(Project.STATUS_CREATED);
        List<Project> projects = new ArrayList<Project>();
        projects.add(new Project());
        when(projectDao.getProjects(null, statusOfProject)).thenReturn(projects);
        assertNotNull(projectService.listProjects(retrievalInfo));
    }

    @Test
    public final void getDetails() {
        Project project = new Project();
        project.setKey("testKey");
        project.setName("testName");
        when(projectDao.getProject(project.getKey())).thenReturn(project);
        assertNotNull(projectService.getDetails(project));
        assertEquals(project.getKey(), projectService.getDetails(project).getKey());
    }

    @Test(expected = SystemException.class)
    public final void getDetails_invalidArgument() {
        Project project = new Project();
        project.setName("testName");
        when(projectDao.getProject(project.getKey())).thenReturn(project);
        assertNotNull(projectService.getDetails(project));
        assertEquals(project.getKey(), projectService.getDetails(project).getKey());
    }
}
