// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.business.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.ie.business.domain.Project;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.DatastoreTest;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test cases for ProjectDaoImpl class
 * 
 * @author Charanjeet singh
 * 
 */
public class ProjectDaoImplTest extends DatastoreTest {
    private ProjectDaoImpl projectDaoImpl;

    @Before
    public void setUp() {
        super.setUp();
        if (projectDaoImpl == null)
            projectDaoImpl = new ProjectDaoImpl();
        projectDaoImpl.setPersistenceManagerFactory(pmf);
    }

    @Test
    public final void saveProject() {
        Project project = new Project();
        project.setName("ProjectName");
        Project expectedProject = projectDaoImpl.saveProject(project);
        assertNotNull(expectedProject);
        assertEquals(expectedProject.getName(), project.getName());
    }

    @Test
    public final void getProjects() {
        Set<String> statusOfProject = new HashSet<String>();
        statusOfProject.add(Project.STATUS_CREATED);

        Project project1 = new Project();
        project1.setName("ProjectName1");
        project1.setStatus(Project.STATUS_CREATED);

        Project project2 = new Project();
        project2.setName("ProjectName2");
        project2.setStatus(Project.STATUS_CREATED);

        projectDaoImpl.saveProject(project1);
        projectDaoImpl.saveProject(project2);

        RetrievalInfo retrievalInfo = new RetrievalInfo();
        retrievalInfo.setStartIndex(ServiceConstants.ZERO);
        retrievalInfo.setNoOfRecords(ServiceConstants.PROJECT_LIST_DEFAULT_SIZE);
        retrievalInfo.setOrderType(ServiceConstants.PROJECT_DEFAULT_ORDERING_TYPE);
        retrievalInfo.setOrderBy(ServiceConstants.DEFAULT_PROJECT_ORDERING_FIELD);

        List<Project> projects = projectDaoImpl.getProjects(retrievalInfo, statusOfProject);
        assertNotNull(projects);
        assertEquals(2, projects.size());
    }

    @Test
    public final void getProject() {
        Project project1 = new Project();
        project1.setName("ProjectName1");
        project1.setStatus(Project.STATUS_CREATED);

        project1 = projectDaoImpl.saveProject(project1);
        assertNotNull(projectDaoImpl.getProject(project1.getKey()));
    }
}
