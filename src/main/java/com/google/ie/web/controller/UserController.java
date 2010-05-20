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

import static com.google.ie.web.controller.WebConstants.VIEW_STATUS;

import com.google.ie.business.domain.User;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.ShardedCounterService;
import com.google.ie.business.service.UserService;
import com.google.ie.common.builder.IdeaBuilder;
import com.google.ie.common.builder.ProjectBuilder;
import com.google.ie.common.constants.OpenIdConstants;
import com.google.ie.common.util.GsonUtility;
import com.google.ie.dto.IdeaDetail;
import com.google.ie.dto.ProjectDetail;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.dto.ViewStatus;
import com.google.ie.web.filter.AuthenticationFilter;
import com.google.step2.Step2;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * A controller that handles requests for user services including
 * authentication.
 * 
 * @author abraina
 */
@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger LOGGER = Logger.getLogger(UserController.class);
    private static boolean isDebugEnabled = LOGGER.isDebugEnabled();
    private static final int AUTH_USER = 1;
    private static final int FIRST_TIME_USER = 0;
    private static final int UNAUTH_USER = -1;
    @Autowired
    private IdeaService ideaService;
    // Authentication cookie name read from properties file
    @Value("${fcauthCookieName}")
    private String fcauthCookieName;

    // Friend connect url read from properties file
    @Value("${friendConnectUrl}")
    private String friendConnectUrl;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectBuilder projectBuilder;
    @Autowired
    private IdeaBuilder ideaBuilder;
    @Autowired
    private ShardedCounterService shardedCounterService;

    /**
     * Authenticates the user using GFC open social API. Also, saves the user
     * information into the datastore.
     * 
     * @param user
     * @param request
     * @return
     */
    @RequestMapping("/activate")
    public String activate(HttpServletRequest request) {
        return "users/activate";
    }

    /**
     * Authenticates the user using GFC open social API. Also, saves the user
     * information into the datastore.
     * 
     * @param user
     * @param request
     * @return
     */
    @RequestMapping("/authenticate")
    public Map<String, Object> authenticate(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.debug(fcauthCookieName);
        ViewStatus viewStatus = new ViewStatus();
        // This method adds the user object into view status
        int authStatus = authenticationCheckForOpenId(request, response, viewStatus);
        if (authStatus == AUTH_USER) {
            viewStatus.setStatus(WebConstants.SUCCESS);
            viewStatus.addMessage(WebConstants.GLOBAL_MESSAGE, "User login successful");
            LOGGER.debug("Login succesful");
        } else if (authStatus == FIRST_TIME_USER) {
            viewStatus.setStatus(WebConstants.ERROR);
            viewStatus.addMessage(WebConstants.EMAIL_REQUIRED, "Please provide your email id.");
            LOGGER.debug("Login successful. Email required");
        } else {
            viewStatus.setStatus(WebConstants.ERROR);
            viewStatus.addMessage(WebConstants.GLOBAL_MESSAGE, WebConstants.UN_AUTHORIZED_USER);
            LOGGER.debug("Login failed");
        }
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(WebConstants.VIEW_STATUS, viewStatus);
        return model;
    }

    /**
     * Add users email at join time
     * 
     * @param email
     * @return
     */
    @RequestMapping("/register-mail/{email}")
    public void addMail(HttpSession session, Map<String, Object> model,
                    @PathVariable String email) {
        LOGGER.info("Add users email");
        ViewStatus viewStatus = new ViewStatus();
        User user = (User) session.getAttribute(WebConstants.USER);
        LOGGER.debug("User" + user);
        if (null == user || StringUtils.isBlank(user.getUserKey())) {
            viewStatus.setStatus(WebConstants.ERROR);
            viewStatus.addMessage(WebConstants.EMAIL_ERROR, "Invalid User.");
            model.put(WebConstants.VIEW_STATUS, viewStatus);
            return;
        }
        LOGGER.debug("Logged in user key is : " + user.getUserKey());
        LOGGER.info("Logged in user name is: " + user.getDisplayName());
        user.setEmailId(email);
        user = userService.addOrUpdateUser(user);
        /* Add the updated user object into the session */
        session.setAttribute(WebConstants.USER, user);
        viewStatus.setStatus(WebConstants.SUCCESS);
        viewStatus.addMessage(WebConstants.EMAIL_SUCCESS, "Email added successfully.");
        model.put(WebConstants.VIEW_STATUS, viewStatus);
    }

    /**
     * Authenticates the user using GFC open social API. Also, saves the user
     * information into the datastore. Adds user object to viewstatus
     * 
     * @param request the {@link HttpServletRequest} object
     * @param response {@link HttpServletResponse} object
     * @param viewStatus
     * @return a int specifying the authentication result (1 = authenticated, -1
     *         = non authenticated, 0 = email required)
     */
    public int authenticationCheckForOpenId(HttpServletRequest request,
                    HttpServletResponse response,
                    ViewStatus viewStatus) {
        LOGGER.info(fcauthCookieName);
        int retVal = UNAUTH_USER;
        User user = (User) request.getSession().getAttribute(WebConstants.USER);
        user = checkLoginWithOpenId(user);
        if (user != null && StringUtils.isBlank(user.getEmailId())) {
            retVal = FIRST_TIME_USER;
            request.getSession(false).setAttribute(WebConstants.USER,
                            user);
        } else if (user != null) {
            retVal = AUTH_USER;
            request.getSession(false).setAttribute(WebConstants.USER,
                            user);
        } else {
            /* Invalidate the session and remove cookies */
            logOffUser(request, response, request.getSession());

        }
        // Add user to viewStatus
        if (viewStatus != null && user != null) {
            viewStatus.addData(WebConstants.USER, user);
        }
        if (isDebugEnabled && retVal == UNAUTH_USER) {
            LOGGER.debug("Authentication check for the user failed");
        }
        return retVal;
    }

    /**
     * Authenticates the user using GFC open social API. Also, saves the user
     * information into the datastore. Adds user object to viewstatus
     * 
     * @param request the {@link HttpServletRequest} object
     * @param response {@link HttpServletResponse} object
     * @param viewStatus
     * @return a int specifying the authentication result (1 = authenticated, -1
     *         = non authenticated, 0 = email required)
     */
    public int authenticationCheck(HttpServletRequest request, HttpServletResponse response,
                    ViewStatus viewStatus) {
        LOGGER.info(fcauthCookieName);
        int retVal = UNAUTH_USER;
        String authToken = getAuthToken(request);
        if (authToken != null && authToken.length() > WebConstants.ZERO) {
            User user = checkLogin(authToken);
            if (user != null && StringUtils.isBlank(user.getEmailId())) {
                retVal = FIRST_TIME_USER;
                request.getSession(false).setAttribute(WebConstants.USER,
                                user);
            } else if (user != null) {
                retVal = AUTH_USER;
                request.getSession(false).setAttribute(WebConstants.USER,
                                user);
            } else {
                /* Invalidate the session and remove cookies */
                logOffUser(request, response, request.getSession());
            }
            // Add user to viewStatus
            if (viewStatus != null) {
                viewStatus.addData(WebConstants.USER, user);
            }
        }
        if (isDebugEnabled && retVal == UNAUTH_USER) {
            LOGGER.debug("Authentication check for the user failed");
        }
        return retVal;
    }

    /**
     * Log off the user.Inavidate the current session and remove the cookies
     * 
     * @param request {@link HttpServletRequest} object
     * @param response {@link HttpServletResponse} object
     */
    @RequestMapping("/logoff")
    private void logOffUser(HttpServletRequest request, HttpServletResponse response,
                    HttpSession httpSession) {
        /* Invalidate the session if exists */
        if (httpSession != null) {
            httpSession.setAttribute(WebConstants.USER,
                            null);
            LOGGER.info("Removing cookies from the browser");

            // Clean up stale session state if any
            for (Step2.AxSchema schema : Step2.AxSchema.values()) {
                httpSession.removeAttribute(schema.getShortName());
            }
            httpSession.removeAttribute(OpenIdConstants.REQUEST_TOKEN);
            httpSession.removeAttribute(OpenIdConstants.ACCESS_TOKEN);
            httpSession.removeAttribute(OpenIdConstants.ACCESS_TOKEN_SECRET);
            httpSession.removeAttribute(OpenIdConstants.ACCESSOR);

            /* Remove the cookies */
            removeCookieFromSystem(request, response);
            /* Invalidate the session */
            httpSession.invalidate();

        }
    }

    /**
     * Delete all the cookies related to the user from the system
     * 
     * @param request {@link HttpServletRequest} object
     * @param response {@link HttpServletResponse} object
     */
    private void removeCookieFromSystem(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                // Don't remove access token cookie
                if (!StringUtils.equals(cookie.getName(), AuthenticationFilter.ACCESS_TOKEN)) {
                    /* Set the max age to zero so that the cookie is deleted */
                    cookie.setMaxAge(WebConstants.ZERO);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
        if (isDebugEnabled) {
            LOGGER.debug("The age of the cookies related to the " +
                            "user has been set to zero and the cookies set into the response");
        }

    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieve the value of friend connect authorization cookie from the
     * request.
     * 
     * @param request
     * @return
     */
    private String getAuthToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookie != null && cookie.getName().equals(getFcauthCookieName())) {
                    return cookie.getValue();
                }
            }
        }
        if (isDebugEnabled) {
            LOGGER.debug("The cookie " + getFcauthCookieName() + " was not found ");
        }
        return null;
    }

    /**
     * Gets user info from GFC open social API for the given fcauth token
     * 
     * @param authToken Friend connect authorization token
     * @return User information retrieved from GFC server.
     */
    private User checkLoginWithOpenId(User user) {
        LOGGER.debug("User: " + user);
        /*
         * Check if the user object obtained above is not null and also
         * contains the user id
         */
        if (user != null && (user.getId() != null && user.getId().length() > WebConstants.ZERO)) {
            LOGGER.info("Adding or updating user");
            return userService.addOrUpdateUser(user);
        }

        if (isDebugEnabled) {
            LOGGER.debug("User does not exist and could not be added either");
        }
        return null;
    }

    /**
     * Gets user info from GFC open social API for the given fcauth token
     * 
     * @param authToken Friend connect authorization token
     * @return User information retrieved from GFC server.
     */
    private User checkLogin(String authToken) {
        String urlStr = new StringBuilder(getFriendConnectUrl())
                        .append(authToken).toString();
        try {
            URL url = new URL(urlStr.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            // Convert JSON data to user object
            User user = convertToUserObject(response.toString());
            LOGGER.debug("User from GFC: " + user);
            /*
             * Check if the user object obtained above is not null and also
             * contains the user id
             */
            if (user != null && (user.getId() != null && user.getId().length() > WebConstants.ZERO)) {
                LOGGER.info("Adding or updating user");
                return userService.addOrUpdateUser(user);
            }

        } catch (MalformedURLException mue) {
            LOGGER.error("Wrong friend connect URL: " + urlStr, mue);
        } catch (IOException ioe) {
            LOGGER.error("Exception encountered while accessing friend connect", ioe);
        }
        if (isDebugEnabled) {
            LOGGER.debug("User does not exist and could not be added either");
        }
        return null;
    }

    /**
     * Returns the {@link User} representation of the json string
     * 
     * @param jsonString the json string to be converted to {@link User} object
     * @return the {@link User} representation of the json string
     */
    private User convertToUserObject(String jsonString) {
        User user = null;
        /* Modify the string to make it compatible for parsing by GSON */
        int startIndex = jsonString.indexOf(":") + 1;
        int endIndex = jsonString.lastIndexOf("}");
        if (startIndex > WebConstants.ZERO && endIndex > WebConstants.ZERO) {
            jsonString = jsonString.substring(startIndex, endIndex);
            user = GsonUtility.convertFromJson(jsonString, User.class);
        }
        return user;
    }

    /**
     * Handles the request to get ideas for the currently logged in user if no
     * paging offset is specified.
     * 
     * @param model Carries data for the view
     * @return
     */
    @RequestMapping("/ideas")
    public String ideas(HttpSession session, @ModelAttribute RetrievalInfo retrievalInfo,
                    Map<String, Object> model) {
        User user = (User) session.getAttribute(WebConstants.USER);
        /* Fetch the parameters as sent in the request */
        long startIndex = retrievalInfo.getStartIndex();
        long noOfRecordsRequested = retrievalInfo.getNoOfRecords();
        List<IdeaDetail> ideas = ideaBuilder.getIdeasForUser(user, retrievalInfo);

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
        return "users/ideas";
    }

    /**
     * Handles the request to get ideas for the currently logged in user if no
     * paging offset is specified.
     * 
     * @param model Carries data for the view
     * @return
     */
    @RequestMapping("/projects")
    public String projects(HttpSession session, @ModelAttribute RetrievalInfo retrievalInfo,
                    Map<String, Object> model) {
        User user = (User) session.getAttribute(WebConstants.USER);
        /* Fetch the parameters as sent in the request */
        long startIndex = retrievalInfo.getStartIndex();
        long noOfRecordsRequested = retrievalInfo.getNoOfRecords();
        List<ProjectDetail> projectDtoList = projectBuilder.getProjectsForUser(user, retrievalInfo);
        if (projectDtoList != null) {
            /* Map containing the previous and next index values */
            HashMap<String, Long> pagingMap = new HashMap<String, Long>();
            /*
             * If the size of the list is greater than the no. of records
             * requested
             * ,set the parameter 'next' to be used as start index for the next
             * page retrieval.
             */
            if (projectDtoList.size() > noOfRecordsRequested) {
                pagingMap.put(WebConstants.NEXT, startIndex + noOfRecordsRequested);
            } else {
                /*
                 * If the list size is not greater than the number requested set
                 * the 'next' parameter to minus one
                 */
                pagingMap.put(WebConstants.NEXT, (long) WebConstants.MINUS_ONE);
            }
            /*
             * Set the parameter 'previous' to be used as the start index for
             * the
             * previous page retrieval
             */
            pagingMap.put(WebConstants.PREVIOUS, startIndex - noOfRecordsRequested);
            /* Map of data to be inserted into the view status object */
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            /* Add the map containing the paging values to the map of parameters */
            parameters.put(WebConstants.PAGING, pagingMap);
            ViewStatus viewStatus = ViewStatus.createTheViewStatus(projectDtoList,
                            WebConstants.PROJECTS, parameters);
            model.put(WebConstants.VIEW_STATUS, viewStatus);
        } else {
            model.put(WebConstants.VIEW_STATUS, ViewStatus.createTheViewStatus(null,
                            WebConstants.PROJECTS, null));
        }

        return "users/projects";
    }

    /**
     * Handles the request for My Profile Page
     * 
     * @param model Carries data for the view
     * @return View name.
     */
    @RequestMapping(value = "/profile/{userKey}", method = RequestMethod.GET)
    public String profile(@PathVariable String userKey, Map<String, Object> model) {
        ViewStatus viewStatus = new ViewStatus();
        User user = userService.getUserByPrimaryKey(userKey);
        // Get reputation points for user

        if (user != null) {
            long userPoints = shardedCounterService.getTotalPoint(user.getUserKey());
            user.setReputationPoints(userPoints);
            viewStatus.setStatus(WebConstants.SUCCESS);
            viewStatus.addData(WebConstants.USER, user);
        } else {/* In case the user is null or empty */
            viewStatus.setStatus(WebConstants.ERROR);
            viewStatus.addMessage(WebConstants.USER, WebConstants.RECORD_NOT_FOUND);
        }
        model.put(WebConstants.VIEW_STATUS, viewStatus);
        return "users/profile";
    }

    /**
     * Handles the request for My Profile Page
     * 
     * @param model Carries data for the view
     * @return View name.
     */
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String myProfile(HttpSession session, Map<String, Object> model) {
        ViewStatus viewStatus = new ViewStatus();
        User user = (User) session.getAttribute(WebConstants.USER);
        if (user != null) {
            viewStatus.setStatus(WebConstants.SUCCESS);
            user = userService.getUserByPrimaryKey(user.getUserKey());
            long userPoints = shardedCounterService.getTotalPoint(user.getUserKey());
            user.setReputationPoints(userPoints);
            viewStatus.addData(WebConstants.USER, user);
        } else {/* In case the user is null or empty */
            viewStatus.setStatus(WebConstants.ERROR);
            viewStatus.addMessage(WebConstants.USER, WebConstants.RECORD_NOT_FOUND);
        }
        model.put(WebConstants.VIEW_STATUS, viewStatus);
        return "users/profile";
    }

    public String getFcauthCookieName() {
        if (fcauthCookieName != null) {
            return fcauthCookieName.trim();
        }
        return fcauthCookieName;
    }

    public void setFcauthCookieName(String fcauthCookieName) {
        this.fcauthCookieName = fcauthCookieName;
    }

    public void setFriendConnectUrl(String friendConnectUrl) {
        this.friendConnectUrl = friendConnectUrl;
    }

    public String getFriendConnectUrl() {
        return friendConnectUrl;
    }

    public void setIdeaBuilder(IdeaBuilder ideaBuilder) {
        this.ideaBuilder = ideaBuilder;
    }

    /**
     * @return the projectBuilder
     */
    public ProjectBuilder getProjectBuilder() {
        return projectBuilder;
    }

    /**
     * @param projectBuilder the projectBuilder to set
     */
    public void setProjectBuilder(ProjectBuilder projectBuilder) {
        this.projectBuilder = projectBuilder;
    }

    /**
     * @return the userService
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * @return the ideaBuilder
     */
    public IdeaBuilder getIdeaBuilder() {
        return ideaBuilder;
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
}

