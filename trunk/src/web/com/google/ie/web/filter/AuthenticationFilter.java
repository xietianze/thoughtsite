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

package com.google.ie.web.filter;

import com.google.ie.common.constants.IdeaExchangeConstants;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Temporary class to allow site access based on access code
 * 
 * @author adahiya
 * 
 */
public class AuthenticationFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class);
    private FilterConfig filterConfig;

    private String accessCode;
    private String exclusionUrls;

    private static String FORM = "<HTML> <HEAD>  <TITLE> Access Token </TITLE> </HEAD> <BODY>  <FORM METHOD=\"POST\" ACTION=\"Service_URL\">  <TABLE> <TR>        <TD>Enter Access Code</TD>      <TD>            <INPUT TYPE=\"password\" NAME=\"accessToken\">      </TD>   </TR>   <TR>        <TD></TD>       <TD><INPUT TYPE=\"submit\" value=\"Submit\"></TD>   </TR>   </TABLE>  </FORM> </BODY></HTML>";

    public static final String ACCESS_TOKEN = "accessToken";
    private static final String EXCLUSION_URL = "exclusionURLs";

    /**
     * Default constructor.
     */
    public AuthenticationFilter() {
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                    FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (!isAccessCookiePresent(httpServletRequest)) {

            String accessCodeFromUSer = httpServletRequest.getParameter(ACCESS_TOKEN);

            if (accessCodeFromUSer != null && accessCodeFromUSer.equalsIgnoreCase(this.accessCode)) {
                /*
                 * Create cookie and set it in response
                 */
                Cookie cookie = new Cookie(ACCESS_TOKEN, ACCESS_TOKEN);
                // Max age of 12 hours
                cookie.setMaxAge(43200);
                ((HttpServletResponse) response).addCookie(cookie);
            } else {
                if (!urlForExclusion(httpServletRequest.getRequestURI(), exclusionUrls)) {

                    PrintWriter writer = response.getWriter();
                    String updatedForm = FORM.replaceAll("Service_URL", httpServletRequest
                                    .getRequestURI());
                    writer.print(updatedForm);
                    LOGGER.debug("Access token not found");
                    return;
                }
            }

        }
        LOGGER.debug("Access token found");
        // pass the request along the filter chain
        chain.doFilter(request, response);
    }

    private boolean urlForExclusion(String requestURL, String exclusionURLs) {
        boolean flag = false;
        if (exclusionURLs != null && exclusionURLs.contains(IdeaExchangeConstants.COMMA)) {
            String[] urls = exclusionURLs.split(IdeaExchangeConstants.COMMA);
            for (int i = 0; i < urls.length; i++) {
                if (requestURL.contains(urls[i])) {
                    flag = true;
                    LOGGER.debug("Allowing URL without access tocken due to exclusion policy");
                    break;
                }
            }
        }

        return flag;
    }

    /**
     * Checks if the access token cookie is present with a non empty value
     * 
     * @param request
     * @return true if a valid access token cookie is present in the request,
     *         else false
     */
    private boolean isAccessCookiePresent(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (cookie != null && cookie.getName().equals(ACCESS_TOKEN)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
        this.filterConfig = fConfig;

        this.accessCode = filterConfig.getInitParameter(ACCESS_TOKEN);
        this.exclusionUrls = filterConfig.getInitParameter(EXCLUSION_URL);
    }

}

