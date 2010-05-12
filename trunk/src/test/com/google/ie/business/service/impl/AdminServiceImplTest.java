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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.ie.business.dao.AdminRequestDao;
import com.google.ie.business.dao.impl.AdminRequestDaoImpl;
import com.google.ie.business.domain.AdminRequest;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.ProjectService;
import com.google.ie.business.service.UserService;
import com.google.ie.common.util.SearchUtility;
import com.google.ie.test.ServiceTest;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Test case for AdminServiceImpl class
 * 
 * @author Surabhi Bhatnagar
 * 
 */
public class AdminServiceImplTest extends ServiceTest {
    private AdminServiceImpl adminService;
    private AdminRequestDao mockAdminRequestDao = mock(AdminRequestDaoImpl.class);
    private UserService userService = mock(UserServiceImpl.class);
    private IdeaCommentServiceImpl ideaCommentService = mock(IdeaCommentServiceImpl.class);
    private IdeaService ideaService = mock(IdeaServiceImpl.class);
    private ProjectService projectService = mock(ProjectServiceImpl.class);

    @Before
    public void setUp() {
        super.setUp();
        adminService = mock(AdminServiceImpl.class);
        // adminService = new AdminServiceImpl();
        // adminService.setAdminRequestDao(mockAdminRequestDao);
        // adminService.setAuditManager(mockAuditManager);
        // adminService.setIdeaService(ideaService);
        // adminService.setProjectService(projectService);
        // adminService.setUserService(userService);
        // adminService.setIdeaCommentService(ideaCommentService);
        // adminService.setProjectCommentService(projectCommentService);

    }

    @Test
    public void deleteIdea() {

        String adminReason = "Admin Reason";

        Idea idea = new Idea();
        idea.setKey("key");
        idea.setCreatorKey("userKey");
        idea.setStatus("Published");

        Idea updtIdea = new Idea();
        updtIdea.setKey("key");
        updtIdea.setCreatorKey("userKey");
        updtIdea.setStatus("Deleted");

        User user = new User();
        user.setUserKey("userKey");
        user.setStatus("active");
        user.setEmailId("email@google.com");

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setAdminUserKey(user.getUserKey());
        adminRequest.setEntityKey("key");
        adminRequest.setEntityType("Idea");
        adminRequest.setEntityTitle("idea");
        adminRequest.setRequestType("Deleted");
        adminRequest.setStatus("appproved");
        // adminRequest.setCreatedOn(new Date());
        adminRequest.setAdminReason(adminReason);

        when(ideaService.getIdeaByKey("key")).thenReturn(idea);
        when(ideaService.updateIdea(idea)).thenReturn(updtIdea);
        when(mockAdminRequestDao.saveRequest(adminRequest)).thenReturn(true);
        when(userService.getUserByPrimaryKey("userKey")).thenReturn(user);
        /* Remove index of the entity */
        SearchUtility.indexEntity(idea);
        // doNothing().when(adminService).callEmailManager("userKey",
        // adminRequest,
        // "mail.owner.delete.idea");

        adminService.deleteIdea(idea.getKey(), user, adminReason);

        assertEquals("Deleted", updtIdea.getStatus());
    }

    @Test
    public void approveAdminRequest() {

        String adminReason = "Admin Reason";

        Idea idea = new Idea();
        idea.setKey("entityKey");
        idea.setCreatorKey("userKey");
        idea.setStatus("Published");

        Idea updtIdea = new Idea();
        updtIdea.setKey("entityKey");
        updtIdea.setCreatorKey("userKey");
        updtIdea.setStatus("Objectionable");

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setKey("key");
        adminRequest.setAdminReason(adminReason);
        adminRequest.setEntityKey("entityKey");
        adminRequest.setStatus("pending");
        adminRequest.setEntityType("Idea");
        adminRequest.setRequestType("Objectionable");

        AdminRequest approvedAdminRequest = new AdminRequest();
        approvedAdminRequest.setKey("key");
        approvedAdminRequest.setAdminReason(adminReason);
        approvedAdminRequest.setEntityKey("entityKey");
        approvedAdminRequest.setStatus("appproved");
        approvedAdminRequest.setEntityType("Idea");
        approvedAdminRequest.setRequestType("Objectionable");

        User user = new User();
        user.setUserKey("userKey");
        user.setStatus("active");
        user.setEmailId("email@google.com");

        when(ideaService.getIdeaByKey("entityKey")).thenReturn(idea);
        when(mockAdminRequestDao.findEntityByPrimaryKey(AdminRequest.class, adminRequest.getKey()))
                        .thenReturn(adminRequest);
        when(ideaService.updateIdea(idea)).thenReturn(updtIdea);
        when(mockAdminRequestDao.saveRequest(approvedAdminRequest)).thenReturn(true);
        when(userService.getUserByPrimaryKey("userKey")).thenReturn(user);
        adminService.approveAdminRequest(adminRequest, user);
        assertEquals("Objectionable", updtIdea.getStatus());
        assertEquals("appproved", approvedAdminRequest.getStatus());
    }

    @Test
    public void denyAdminRequest() {

        String adminReason = "Admin Reason";

        Idea idea = new Idea();
        idea.setKey("entityKey");
        idea.setCreatorKey("userKey");
        idea.setStatus("Published");
        // AdminRequest ideaAdminRequest = new AdminRequest();
        // ideaAdminRequest.setKey("ideaAdminRequestKey");
        // ideaAdminRequest.setAdminReason(adminReason);
        // ideaAdminRequest.setEntityKey("entityKey");
        // ideaAdminRequest.setStatus("pending");
        // ideaAdminRequest.setEntityType("Idea");
        // ideaAdminRequest.setRequestType("Objectionable");

        AdminRequest ideaCommAdminRequest = new AdminRequest();
        ideaCommAdminRequest.setKey("ideaCommAdminRequestKey");
        ideaCommAdminRequest.setAdminReason(adminReason);
        ideaCommAdminRequest.setEntityKey("entityKey");
        ideaCommAdminRequest.setStatus("pending");
        ideaCommAdminRequest.setEntityType("IdeaComment");
        ideaCommAdminRequest.setRequestType("Objectionable");

        // AdminRequest projCommAdminRequest = new AdminRequest();
        // projCommAdminRequest.setKey("projCommAdminRequestKey");
        // projCommAdminRequest.setAdminReason(adminReason);
        // projCommAdminRequest.setEntityKey("entityKey");
        // projCommAdminRequest.setStatus("pending");
        // projCommAdminRequest.setEntityType("ProjectComment");
        // projCommAdminRequest.setRequestType("Objectionable");

        AdminRequest rejectedAdminRequest = new AdminRequest();
        rejectedAdminRequest.setKey("ideaCommAdminRequestKey");
        rejectedAdminRequest.setAdminReason(adminReason);
        rejectedAdminRequest.setEntityKey("entityKey");
        rejectedAdminRequest.setStatus("rejected");
        rejectedAdminRequest.setEntityType("IdeaComment");
        rejectedAdminRequest.setRequestType("Objectionable");

        IdeaComment ideaComment = new IdeaComment();
        ideaComment.setKey("ideaCommKey");
        ideaComment.setText("Idea Comment");
        ideaComment.setStatus("Flagged");

        IdeaComment updtIdeaComment = new IdeaComment();
        updtIdeaComment.setKey("ideaCommKey");
        updtIdeaComment.setText("Idea Comment");
        updtIdeaComment.setStatus("Saved");

        // ProjectComment projComment = new ProjectComment();
        // projComment.setKey("projCommKey");
        // projComment.setText("Project Comment");
        // projComment.setStatus("Flagged");

        User user = new User();
        user.setUserKey("userKey");
        user.setStatus("active");
        user.setEmailId("email@google.com");

        when(ideaService.getIdeaByKey("entityKey")).thenReturn(idea);
        when(mockAdminRequestDao.findEntityByPrimaryKey(AdminRequest.class,
                        ideaCommAdminRequest.getKey()))
                        .thenReturn(ideaCommAdminRequest);
        when(mockAdminRequestDao.saveRequest(rejectedAdminRequest)).thenReturn(true);
        when(ideaCommentService.getCommentById("entityKey")).thenReturn(ideaComment);
        when(userService.getUserByPrimaryKey("userKey")).thenReturn(user);

        ideaCommentService.updateComment(updtIdeaComment);
        adminService.denyAdminRequest(ideaCommAdminRequest, user);
        assertEquals("rejected", rejectedAdminRequest.getStatus());
        assertEquals("Saved", updtIdeaComment.getStatus());
    }

    @Test
    public void blacklistUser() {
        String adminReason = "Admin Reason";

        User user = new User();
        user.setUserKey("userKey");
        user.setStatus("active");

        User banUser = new User();
        banUser.setUserKey("userKey");
        banUser.setStatus("banned");
        banUser.setEmailId("email@google.com");

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setAdminUserKey("adminUserKey");
        adminRequest.setEntityKey(user.getUserKey());
        adminRequest.setEntityType(User.class.getSimpleName());
        adminRequest.setEntityTitle("Display Name");
        adminRequest.setRequestType("banned");
        adminRequest.setStatus("appproved");
        adminRequest.setCreatedOn(new Date());
        adminRequest.setAdminReason(adminReason);

        when(userService.getUserByPrimaryKey("userKey")).thenReturn(user);
        when(userService.saveUser(banUser)).thenReturn(banUser);
        when(mockAdminRequestDao.saveRequest(adminRequest)).thenReturn(true);

        adminService.blacklistUser(user, "adminUserKey", adminReason);
        assertEquals("banned", banUser.getStatus());

    }

    @Test
    public void activateUser() {
        String adminReason = "Admin Reason";

        User user = new User();
        user.setUserKey("userKey");
        user.setStatus("banned");

        User activeUser = new User();
        activeUser.setUserKey("userKey");
        activeUser.setStatus("active");
        activeUser.setEmailId("email@google.com");

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setAdminUserKey("adminUserKey");
        adminRequest.setEntityKey(user.getUserKey());
        adminRequest.setEntityType(User.class.getSimpleName());
        adminRequest.setEntityTitle("Display Name");
        adminRequest.setRequestType("activate");
        adminRequest.setStatus("appproved");
        adminRequest.setCreatedOn(new Date());
        adminRequest.setAdminReason(adminReason);

        when(userService.getUserByPrimaryKey("userKey")).thenReturn(user);
        when(userService.saveUser(activeUser)).thenReturn(activeUser);
        when(mockAdminRequestDao.saveRequest(adminRequest)).thenReturn(true);

        adminService.blacklistUser(user, "adminUserKey", adminReason);
        assertEquals("active", activeUser.getStatus());

    }

    @Test
    public void deleteProject() {
        String adminReason = "Admin Reason";
        Project project = new Project();
        project.setKey("projKey");
        project.setName("New Project");
        project.setStatus(Project.STATUS_DELETED);

        User user = new User();
        user.setUserKey("userKey");
        user.setStatus("banned");

        User activeUser = new User();
        activeUser.setUserKey("userKey");
        activeUser.setStatus("active");
        activeUser.setEmailId("email@google.com");

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setAdminUserKey("adminUserKey");
        adminRequest.setEntityKey(project.getKey());
        adminRequest.setEntityType(Project.class.getSimpleName());
        adminRequest.setEntityTitle(project.getName());
        adminRequest.setRequestType("Deleted");
        adminRequest.setStatus("appproved");
        adminRequest.setCreatedOn(new Date());
        adminRequest.setAdminReason(adminReason);

        when(projectService.getProjectById("ProjKey")).thenReturn(project);
        when(mockAdminRequestDao.saveRequest(adminRequest)).thenReturn(true);

        projectService.updateProject(project);
        SearchUtility.indexEntity(project);

        assertEquals("Deleted", project.getStatus());

    }

}

