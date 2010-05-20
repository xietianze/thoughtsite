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

package com.google.ie.web.controller;

import static com.google.ie.web.controller.WebConstants.ERROR;
import static com.google.ie.web.controller.WebConstants.SUCCESS;
import static com.google.ie.web.controller.WebConstants.VIEW_STATUS;

import com.google.ie.business.domain.AdminRequest;
import com.google.ie.business.domain.Comment;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.AdminService;
import com.google.ie.business.service.CommentService;
import com.google.ie.business.service.ProjectService;
import com.google.ie.business.service.UserService;
import com.google.ie.common.builder.IdeaBuilder;
import com.google.ie.common.builder.ProjectBuilder;
import com.google.ie.common.exception.IdeasExchangeException;
import com.google.ie.dto.IdeaDetail;
import com.google.ie.dto.ProjectDetail;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.dto.ViewStatus;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A controller that handles requests for admin user for delete idea,approve and
 * deny request for duplicate and objectionable for ideas and comments.
 * 
 * @author Surabhi
 */
@Controller
@RequestMapping("/admin")
@SessionAttributes("user")
public class AdminController {
    /* Logger for logging information. */
    private static Logger log = Logger.getLogger(AdminController.class);

    /* Services autowired for invoking service methods */
    @Autowired
    @Qualifier("ideaCommentServiceImpl")
    private CommentService ideaCommentService;
    @Autowired
    @Qualifier("projectCommentServiceImpl")
    private CommentService projectCommentService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    /* Builder for building object of idea and project */
    @Autowired
    private ProjectBuilder projectBuilder;
    @Autowired
    private IdeaBuilder ideaBuilder;

    /**
     * Get View Status For Invalid User.
     * 
     * @return ViewStatus
     */
    private ViewStatus getViewStatusForInvalidUser() {
        ViewStatus viewStatus = new ViewStatus();
        viewStatus.setStatus(ERROR);
        viewStatus.addMessage(ERROR, WebConstants.INVALID_USER);
        return viewStatus;

    }

    /**
     * 
     * Handles request for view Action inbox items request
     * 
     * @param retrievalInfo RetrievalInfo provide detail for fetching data
     * @param requestType String
     * @param model Map request map
     * @param req HttpServletRequest
     * @return String resource to which request get forwarded.
     */
    @RequestMapping("/action")
    public String actionItems(@ModelAttribute RetrievalInfo retrievalInfo,
                    @RequestParam(required = false) String requestType, Map<String, Object> model,
                    HttpServletRequest req) {
        if (!isUserAdmin(req)) {
            model.put(VIEW_STATUS, getViewStatusForInvalidUser());
            return "admin/inbox-items";
        }
        /* Fetch the range parameters as sent in the request */
        long startIndex = retrievalInfo.getStartIndex();
        retrievalInfo.setNoOfRecords(WebConstants.PAGE_LIMIT);
        long noOfRecordsRequested = retrievalInfo.getNoOfRecords();
        List<AdminRequest> inboxItems = adminService.getAdminRequests(retrievalInfo, requestType);
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        /* Map containing the previous and next index values */
        HashMap<String, Long> pagingMap = new HashMap<String, Long>();
        /*
         * If the size of the list is greater than the no. of records requested
         * ,set the parameter 'next' to be used as start index for the next
         * page retrieval.
         */
        if (inboxItems != null && inboxItems.size() > noOfRecordsRequested) {
            pagingMap.put(WebConstants.NEXT, startIndex + noOfRecordsRequested);
        } else {
            /*
             * If the list size is not greater than the number requested set
             * the 'next' parameter to minus one
             */
            pagingMap.put(WebConstants.NEXT, (long) WebConstants.MINUS_ONE);
        }
        /*
         * Set the parameter 'previous' to be used as the start index for the
         * previous page retrieval
         */
        pagingMap.put(WebConstants.PREVIOUS, startIndex - noOfRecordsRequested);
        /* Add the map containing the paging values to the map of parameters */
        parameters.put(WebConstants.PAGING, pagingMap);
        ViewStatus viewStatus = ViewStatus.createTheViewStatus(inboxItems,
                        WebConstants.ADMIN_REQUESTS, parameters);
        model.put(VIEW_STATUS, viewStatus);
        return "admin/inbox-items";
    }

    /**
     * Handles request to Approve list of entities on user request
     * 
     * @param key Primary key of entity(idea or comment) whose details are to be
     *        shown
     * @return String resource to which request get forwarded.
     */
    @RequestMapping("/approveList/{key}")
    public String approveList(@PathVariable String key) {
        log.info("Into Admin Controller");
        // adminService.approveIdea(key);
        return "admin/show";
    }

    /**
     * Handles request to Approve user request
     * 
     * @param key Primary key of entity(AdminRequest) which need to be approve.
     * @param user User Logged in user detail
     * @param retrievalInfo RetrievalInfo provide detail for fetching data
     * @param requestType String
     * @param adminReason String reason for request approval
     * @param model Map request
     * @param req HttpServletRequest
     * @return String resource to which request get forwarded.
     */
    @RequestMapping("/approve/{key}")
    public String approve(@PathVariable String key, HttpSession session,
                    @ModelAttribute RetrievalInfo retrievalInfo,
                    @RequestParam(required = false) String requestType,
                    @RequestParam(required = true) String adminReason, Map<String, Object> model,
                    HttpServletRequest req) {
        if (!isUserAdmin(req)) {
            model.put(VIEW_STATUS, getViewStatusForInvalidUser());
            return "admin/inbox-items";
        }
        log.info("Into Admin Controller to approve the request");
        User user = (User) session.getAttribute(WebConstants.USER);
        AdminRequest adminReq = new AdminRequest();
        adminReq.setKey(key);
        adminReq.setAdminReason(adminReason);
        adminService.approveAdminRequest(adminReq, user);
        List<AdminRequest> inboxItems = adminService.getAdminRequests(retrievalInfo, requestType);
        ViewStatus viewStatus = ViewStatus.createTheViewStatus(inboxItems,
                        WebConstants.ADMIN_REQUESTS, null);
        model.put(VIEW_STATUS, viewStatus);
        return "admin/inbox-items";
    }

    /**
     * Handles request to deny user request
     * 
     * @param key Primary key of entity(AdminRequest) which need to be deny.
     * @param user User detail of logged-in user.
     * @param retrievalInfo RetrievalInfo provide detail for fetching data
     * @param requestType String
     * @param adminReason String reason for denying the request
     * @param model request map
     * @param req HttpServletRequest
     * @return String resource to which request get forwarded.
     */
    @RequestMapping("/deny/{key}")
    public String deny(@PathVariable String key, HttpSession session,
                    @ModelAttribute RetrievalInfo retrievalInfo,
                    @RequestParam(required = false) String requestType,
                    @RequestParam(required = true) String adminReason,
                    Map<String, Object> model, HttpServletRequest req) {
        if (!isUserAdmin(req)) {
            model.put(VIEW_STATUS, getViewStatusForInvalidUser());
            return "admin/inbox-items";
        }
        log.info("Into Admin Controller to deny the request");
        User user = (User) session.getAttribute(WebConstants.USER);
        AdminRequest adminReq = new AdminRequest();
        adminReq.setKey(key);
        adminReq.setAdminReason(adminReason);
        adminService.denyAdminRequest(adminReq, user);
        List<AdminRequest> inboxItems = adminService.getAdminRequests(retrievalInfo, requestType);
        ViewStatus viewStatus = ViewStatus.createTheViewStatus(inboxItems,
                        WebConstants.ADMIN_REQUESTS, null);
        model.put(VIEW_STATUS, viewStatus);
        return "admin/inbox-items";
    }

    /**
     * Handles request to delete idea/comment
     * 
     * @param key Primary key of entity(idea or comment) which need to be
     *        deleted.
     * @param user User
     * @param retrievalInfo RetrievalInfo provide detail for fetching data
     * @param requestType String
     * @param adminReason String reason for deleting idea or comment
     * @param model request map
     * @param req HttpServletRequest
     * @return String resource to which request get forwarded.
     */
    @RequestMapping("/delete/{key}")
    public String deleteIdea(@PathVariable String key, @ModelAttribute RetrievalInfo retrievalInfo,
                    @RequestParam String adminReason,
                    Map<String, Object> model, HttpSession session, HttpServletRequest req) {
        if (!isUserAdmin(req)) {
            model.put(VIEW_STATUS, getViewStatusForInvalidUser());
            return "admin/ideas";
        }
        log.info("Into Admin Controller");
        User user = (User) session.getAttribute(WebConstants.USER);
        adminService.deleteIdea(key, user, adminReason);
        listIdea(retrievalInfo, model);
        return "admin/ideas";
    }

    /**
     * Handles request to delete project
     * 
     * @param key Primary key of entity(idea or comment) which need to be
     *        deleted.
     * @param user User detail of logged in user
     * @param retrievalInfo RetrievalInfo provide detail for fetching data
     * @param requestType String
     * @param adminReason String reason for deleting project
     * @param model request map
     * @param req HttpServletRequest
     * @return String resource to which request get forwarded.
     */
    @RequestMapping("/deleteProject/{key}")
    public String deleteProject(@PathVariable String key,
                    @ModelAttribute RetrievalInfo retrievalInfo,
                    @RequestParam String adminReason,
                    Map<String, Object> model, HttpSession session, HttpServletRequest req) {
        if (!isUserAdmin(req)) {
            model.put(VIEW_STATUS, getViewStatusForInvalidUser());
            return "admin/projects";
        }
        log.info("Into Admin Controller to delete the project.");
        User user = (User) session.getAttribute(WebConstants.USER);
        adminService.deleteProject(key, user, adminReason);
        listProjects(retrievalInfo, model, req);
        return "admin/projects";
    }

    /**
     * 
     * @param key primary key of Entity(Idea)
     * @param model request map
     * @return String resource to which request get forwarded.
     */
    @RequestMapping("/viewIdea/{key}")
    public String getIdeaByIdeaCommentKey(@PathVariable String key, Map<String, Object> model) {
        Comment comment = ideaCommentService.getCommentById(key);
        Idea idea = adminService.getIdeaByCommentKey(key);
        ViewStatus viewStatus = new ViewStatus();
        IdeaDetail ideaDetail = null;
        try {
            ideaDetail = ideaBuilder.getIdeaDetail(idea.getKey(), true);
            if (null != comment && null != ideaDetail && null != ideaDetail.getIdea()) {
                viewStatus.addData(WebConstants.IDEA_DETAIL, ideaDetail);
                viewStatus.addData(WebConstants.IDEA_COMMENT, comment);
                viewStatus.setStatus(SUCCESS);
            } else {
                viewStatus.setStatus(ERROR);
                viewStatus.addMessage(ERROR, WebConstants.RECORD_NOT_FOUND);
            }

        } catch (IdeasExchangeException e) {
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(ERROR, e.getMessage());
        }
        model.put(VIEW_STATUS, viewStatus);
        return "admin/ideaComment";
    }

    /**
     * Handles request to delete idea request
     * 
     * @param key Primary key of entity(idea or comment) whose details are to be
     *        shown
     * @return String resource to which request get forwarded.
     */
    @RequestMapping("/viewProject/{key}")
    public String getProjectByProjectCommentKey(@PathVariable String key, Map<String, Object> model) {
        Comment comment = projectCommentService.getCommentById(key);
        Project proj = adminService.getProjectByCommentKey(key);
        ViewStatus viewStatus = new ViewStatus();
        ProjectDetail projectDetail = null;
        projectDetail = projectBuilder.getProjectDetail(proj.getKey());
        if (null != comment && null != projectDetail && null != projectDetail.getProject()) {
            viewStatus.addData(WebConstants.PROJECT_DETAIL, projectDetail);
            viewStatus.addData(WebConstants.PROJECT_COMMENT, comment);
            viewStatus.setStatus(SUCCESS);
        } else {
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(ERROR, WebConstants.RECORD_NOT_FOUND);
        }

        model.put(VIEW_STATUS, viewStatus);
        return "admin/projectComment";
    }

    /**
     * Handle request for listing idea.
     * 
     * @param retrievalInfo RetrievalInfo provide detail for fetching data
     * @param model request map
     */
    public void listIdea(@ModelAttribute RetrievalInfo retrievalInfo, Map<String, Object> model) {
        /* Fetch the range parameters as sent in the request */
        long startIndex = retrievalInfo.getStartIndex();
        long noOfRecordsRequested = retrievalInfo.getNoOfRecords();
        /* Get the idea list */
        List<IdeaDetail> ideas = ideaBuilder.getIdeasForListing(retrievalInfo);
        /* Map of data to be inserted into the view status object */
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        /* Map containing the previous and next index values */
        HashMap<String, Long> pagingMap = new HashMap<String, Long>();
        /*
         * If the size of the list is greater than the no. of records requested
         * ,set the parameter 'next' to be used as start index for the next
         * page retrieval.
         */
        if (ideas != null && ideas.size() > noOfRecordsRequested) {
            pagingMap.put(WebConstants.NEXT, startIndex + noOfRecordsRequested);
        } else {
            /*
             * If the list size is not greater than the number requested set
             * the 'next' parameter to minus one
             */
            pagingMap.put(WebConstants.NEXT, (long) WebConstants.MINUS_ONE);
        }
        /*
         * Set the parameter 'previous' to be used as the start index for the
         * previous page retrieval
         */
        pagingMap.put(WebConstants.PREVIOUS, startIndex - noOfRecordsRequested);
        /* Add the map containing the paging values to the map of parameters */
        parameters.put(WebConstants.PAGING, pagingMap);
        ViewStatus viewStatus = ViewStatus
                        .createTheViewStatus(ideas, WebConstants.IDEAS, parameters);

        model.put(VIEW_STATUS, viewStatus);
    }

    /**
     * Handle request for getting list of ideas.
     * 
     * @param RetrievalInfo provide detail for fetching data
     * @param model request map
     * @param req HttpServletRequest
     * @return String resource on which response get forwarded.
     */
    @RequestMapping(value = "/ideas")
    public String listIdeas(@ModelAttribute RetrievalInfo retrievalInfo, Map<String, Object> model,
                    HttpServletRequest req) {
        if (!isUserAdmin(req)) {
            model.put(VIEW_STATUS, getViewStatusForInvalidUser());
            return "admin/ideas";
        }
        /* Fetch the range parameters as sent in the request */
        long startIndex = retrievalInfo.getStartIndex();
        retrievalInfo.setNoOfRecords(WebConstants.PAGE_LIMIT);// set the
        // number of records to 50 for
        // Admin view
        long noOfRecordsRequested = retrievalInfo.getNoOfRecords();
        /* Get the idea list */
        List<IdeaDetail> ideas = ideaBuilder.getIdeasForListing(retrievalInfo);
        /* Map of data to be inserted into the view status object */
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        /* Map containing the previous and next index values */
        HashMap<String, Long> pagingMap = new HashMap<String, Long>();
        /*
         * If the size of the list is greater than the no. of records requested
         * ,set the parameter 'next' to be used as start index for the next
         * page retrieval.
         */
        if (ideas != null && ideas.size() > noOfRecordsRequested) {
            pagingMap.put(WebConstants.NEXT, startIndex + noOfRecordsRequested);
        } else {
            /*
             * If the list size is not greater than the number requested set
             * the 'next' parameter to minus one
             */
            pagingMap.put(WebConstants.NEXT, (long) WebConstants.MINUS_ONE);
        }
        /*
         * Set the parameter 'previous' to be used as the start index for the
         * previous page retrieval
         */
        pagingMap.put(WebConstants.PREVIOUS, startIndex - noOfRecordsRequested);
        /* Add the map containing the paging values to the map of parameters */
        parameters.put(WebConstants.PAGING, pagingMap);
        ViewStatus viewStatus = ViewStatus
                        .createTheViewStatus(ideas, WebConstants.IDEAS, parameters);

        model.put(VIEW_STATUS, viewStatus);
        return "admin/ideas";
    }

    /**
     * Handle request for getting list of projects.
     * 
     * @param RetrievalInfo provide detail for fetching data
     * @param model request map
     * @param req HttpServletRequest
     * @return String resource on which response get forwarded.
     */
    @RequestMapping(value = "/projects")
    public String listProjects(@ModelAttribute RetrievalInfo retrievalInfo,
                    Map<String, Object> model, HttpServletRequest req) {
        if (!isUserAdmin(req)) {
            model.put(VIEW_STATUS, getViewStatusForInvalidUser());
            return "admin/projects";
        }
        /* Fetch the range parameters as sent in the request */
        long startIndex = retrievalInfo.getStartIndex();
        retrievalInfo.setNoOfRecords(WebConstants.PAGE_LIMIT);
        long noOfRecordsRequested = retrievalInfo.getNoOfRecords();
        List<Project> projects = projectService.listProjects(retrievalInfo);
        /* Map of data to be inserted into the view status object */
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        /* Map containing the previous and next index values */
        HashMap<String, Long> pagingMap = new HashMap<String, Long>();
        /*
         * If the size of the list is greater than the no. of records requested
         * ,set the parameter 'next' to be used as start index for the next
         * page retrieval.
         */
        if (projects != null && projects.size() > noOfRecordsRequested) {
            pagingMap.put(WebConstants.NEXT, startIndex + noOfRecordsRequested);
        } else {
            /*
             * If the list size is not greater than the number requested set
             * the 'next' parameter to minus one
             */
            pagingMap.put(WebConstants.NEXT, (long) WebConstants.MINUS_ONE);
        }
        /*
         * Set the parameter 'previous' to be used as the start index for the
         * previous page retrieval
         */
        pagingMap.put(WebConstants.PREVIOUS, startIndex - noOfRecordsRequested);
        /* Add the map containing the paging values to the map of parameters */
        parameters.put(WebConstants.PAGING, pagingMap);
        // Create viewStatus
        ViewStatus viewStatus = ViewStatus
                        .createTheViewStatus(projects, WebConstants.PROJECTS, parameters);
        model.put(VIEW_STATUS, viewStatus);
        return "admin/projects";
    }

    /**
     * Handle request for getting list of users.
     * 
     * @param RetrievalInfo provide detail for fetching data
     * @param model request map
     * @param req HttpServletRequest
     * @return String resource on which response get forwarded.
     */
    @RequestMapping(value = "/users")
    public String listUsers(@ModelAttribute RetrievalInfo retrievalInfo,
                    Map<String, Object> model, HttpServletRequest req) {
        if (!isUserAdmin(req)) {
            model.put(VIEW_STATUS, getViewStatusForInvalidUser());
            return "admin/projects";
        }
        /* Fetch the range parameters as sent in the request */
        long startIndex = retrievalInfo.getStartIndex();
        long noOfRecordsRequested = retrievalInfo.getNoOfRecords();
        List<User> users = userService.getUsers(retrievalInfo, null);
        /* Map of data to be inserted into the view status object */
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        /* Map containing the previous and next index values */
        HashMap<String, Long> pagingMap = new HashMap<String, Long>();
        /*
         * If the size of the list is greater than the no. of records requested
         * ,set the parameter 'next' to be used as start index for the next
         * page retrieval.
         */
        if (users != null && users.size() > noOfRecordsRequested) {
            pagingMap.put(WebConstants.NEXT, startIndex + noOfRecordsRequested);
        } else {
            /*
             * If the list size is not greater than the number requested set
             * the 'next' parameter to minus one
             */
            pagingMap.put(WebConstants.NEXT, (long) WebConstants.MINUS_ONE);
        }
        /*
         * Set the parameter 'previous' to be used as the start index for the
         * previous page retrieval
         */
        pagingMap.put(WebConstants.PREVIOUS, startIndex - noOfRecordsRequested);
        /* Add the map containing the paging values to the map of parameters */
        parameters.put(WebConstants.PAGING, pagingMap);
        // Create viewStatus
        ViewStatus viewStatus = ViewStatus
                        .createTheViewStatus(users, WebConstants.USERS, parameters);
        model.put(VIEW_STATUS, viewStatus);
        return "admin/users";
    }

    /**
     * Handles request to ban user request
     * 
     * @param key Primary key of entity(User) which need to be banned.
     * @param user User detail of logged-in user.
     * @param adminReason String reason for banning user
     * @param model request map
     * @param req HttpServletRequest
     * @return String resource to which request get forwarded.
     */
    @RequestMapping(value = "/banUser/{key}")
    public String banUser(@PathVariable String key, HttpSession session,
                    @RequestParam String adminReason,
                    Map<String, Object> model, HttpServletRequest req) {
        if (!isUserAdmin(req)) {
            model.put(VIEW_STATUS, getViewStatusForInvalidUser());
            return "admin/users";
        }
        ViewStatus viewStatus = new ViewStatus();
        User userToBan = new User();
        userToBan.setUserKey(key);
        User user = (User) session.getAttribute(WebConstants.USER);
        userToBan = adminService.blacklistUser(userToBan, user.getUserKey(), adminReason);
        if (userToBan.getStatus() == User.STATUS_BANNED) {
            viewStatus.addData(WebConstants.USER, userToBan);
            viewStatus.setStatus(SUCCESS);
        }
        model.put(VIEW_STATUS, viewStatus);
        return "admin/users";

    }

    /**
     * Handles request to activate user request
     * 
     * @param key Primary key of entity(User) which need to be activated.
     * @param user User detail of logged-in user.
     * @param adminReason String reason for banning user
     * @param model request map
     * @param req HttpServletRequest
     * @return String resource to which request get forwarded.
     */

    @RequestMapping(value = "/activateUser/{key}")
    public String activateUser(@PathVariable String key, HttpSession session,
                    @RequestParam String adminReason,
                    Map<String, Object> model, HttpServletRequest req) {
        if (!isUserAdmin(req)) {
            model.put(VIEW_STATUS, getViewStatusForInvalidUser());
            return "admin/users";
        }
        ViewStatus viewStatus = new ViewStatus();
        User user = (User) session.getAttribute(WebConstants.USER);
        User userToBan = new User();
        userToBan.setUserKey(key);
        userToBan = adminService.activateUser(userToBan, user.getUserKey(), adminReason);
        if (userToBan.getStatus() == User.STATUS_ACTIVE) {
            viewStatus.addData(WebConstants.USER, userToBan);
            viewStatus.setStatus(SUCCESS);
        }
        model.put(VIEW_STATUS, viewStatus);
        return "admin/users";
    }

    /**
     * @return the ideaBuilder
     */
    public IdeaBuilder getIdeaBuilder() {
        return ideaBuilder;
    }

    /**
     * @param ideaBuilder the ideaBuilder to set
     */
    public void setIdeaBuilder(IdeaBuilder ideaBuilder) {
        this.ideaBuilder = ideaBuilder;
    }

    /**
     * Check for user logging or not and also check for admin role
     * 
     * @param req HttpServletRequest
     * @return boolean reture true if user is admin
     */
    private boolean isUserAdmin(HttpServletRequest req) {
        boolean isAdmin = false;
        if (req.getSession(true).getAttribute(WebConstants.USER) != null) {
            User user = (User) req.getSession(true).getAttribute(WebConstants.USER);
            if (User.ROLE_ADMIN.equalsIgnoreCase(user.getRoleName())) {
                isAdmin = true;
            }
        }
        return isAdmin;
    }
}

