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

package com.google.ie.web.interceptor;

import com.google.ie.web.controller.UserController;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An interceptor to check if user is logged in or not. User is logged in if an
 * fcauth cookie is present in the request scope
 * 
 * @author abraina
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOG = Logger.getLogger(LoginInterceptor.class);

    // Authentication cookie name
    private String fcauthCookieName;
    @Autowired
    private UserController userController;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                    Object handler) throws Exception {
        // Check auth token and return true if found
        // Check for user object in session
        if (null == request.getSession().getAttribute("user")) {
            // Forward for authentication
            int authStatus = userController.authenticationCheckForOpenId(request, response, null);
            if (authStatus == 1) {
                return true;
            }
            /** Redirect to home page */
            request.getRequestDispatcher("/").forward(request, response);
            return false;

        }
        return true;

    }

    /**
     * Checks if the fcauth cookie is present with a non empty value
     * 
     * @param request
     * @return true if a valid fcauth cookie is present in the request, else
     *         false
     */
    private boolean checkAuthToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookie != null && cookie.getName().equals(getFcauthCookieName())) {
                    // Cookie found. Check for value
                    String authToken = cookie.getValue();
                    if (authToken != null && authToken.length() > 0) {
                        LOG.info("Auth token found. Allowing request to proceed");
                        return true;
                    }
                }
            }
        }
        LOG.warn("Auth token not found. Stopping request to proceed");
        return false;
    }

    public void setFcauthCookieName(String fcauthCookieName) {
        this.fcauthCookieName = fcauthCookieName;
    }

    public String getFcauthCookieName() {
        if (fcauthCookieName != null) {
            return fcauthCookieName.trim();
        }
        return fcauthCookieName;
    }

}

