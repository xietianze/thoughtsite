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

package com.google.ie.web.servlet;

import com.google.ie.common.constants.OpenIdConstants;
import com.google.ie.common.openid.OAuthConsumerUtil;
import com.google.ie.web.controller.WebConstants;
import com.google.inject.Inject;
import com.google.step2.AuthRequestHelper;
import com.google.step2.ConsumerHelper;
import com.google.step2.Step2;
import com.google.step2.consumer.OAuthProviderInfoStore;
import com.google.step2.consumer.ProviderInfoNotFoundException;
import com.google.step2.discovery.IdpIdentifier;
import com.google.step2.servlet.InjectableServlet;

import org.apache.log4j.Logger;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Example Servlet that redirects to an IDP login page
 * 
 * @author Dirk Balfanz (dirk.balfanz@gmail.com)
 * @author Breno de Medeiros (breno.demedeiros@gmail.com)
 */
public class LoginServlet extends InjectableServlet {
    private static final String COLON = ":";
    private static final String COLON_BACKSLASH = "://";
    private static final String HTTPS = "https";
    private static final String HTTP = "http";

    private static final long serialVersionUID = 1586193563568283252L;
    private Logger log = Logger.getLogger(LoginServlet.class);
    private static final String TEMPLATE_FILE = "/WEB-INF/jsp/openid/login.jsp";
    private static final String REDIRECT_PATH = "/checkauth";

    private ConsumerHelper consumerHelper;
    private OAuthProviderInfoStore providerStore;
    private OAuthConsumerUtil oauthConsumerUtil;

    @Inject
    public void setConsumerHelper(ConsumerHelper helper) {
        this.consumerHelper = helper;
    }

    @Inject
    public void setProviderInfoStore(OAuthProviderInfoStore providerStore) {
        this.providerStore = providerStore;
    }

    @Inject
    public void setOAuthConsumerUtil(OAuthConsumerUtil util) {
        oauthConsumerUtil = util;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                    throws IOException, ServletException {
        RequestDispatcher d = req.getRequestDispatcher(TEMPLATE_FILE);
        d.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
        log.info("Login Servlet Post");
        StringBuffer realmBuf = baseURL(req);
        String realm = realmBuf.toString();
        // String returnToUrl = new StringBuffer(realm)
        // .append(req.getContextPath()).append(REDIRECT_PATH).toString();
        String returnToUrl = new StringBuffer(realm)
                        .append(req.getContextPath()).append(REDIRECT_PATH).toString();
        // this is magic - normally this would also fall out of the discovery:
        OAuthAccessor accessor = null;
        // Fetch an unauthorized OAuth request token to test authorizing
        try {
            accessor = providerStore.getOAuthAccessor(OpenIdConstants.GOOGLE);
            accessor = oauthConsumerUtil.getRequestToken(accessor);
            String oauthTestEndpoint =
                            (String) accessor.getProperty(OpenIdConstants.OAUTH_TEST_END_POINT);
            if (oauthTestEndpoint != null) {
                realm = oauthTestEndpoint;
                returnToUrl = oauthTestEndpoint;
            }
        } catch (ProviderInfoNotFoundException e) {
            throw new ServletException(e);
        } catch (OAuthException e) {
            throw new ServletException(e);
        } catch (URISyntaxException e) {
            throw new ServletException(e);
        }

        // we assume that the user typed an identifier for an IdP, not for a
        // user
        if (null == req.getParameter(OpenIdConstants.OPENID)) {
            return;
        }
        IdpIdentifier openId = new IdpIdentifier(req.getParameter(OpenIdConstants.OPENID));

        AuthRequestHelper helper = consumerHelper.getAuthRequestHelper(
                        openId, returnToUrl.toString());

        log.debug("Requesting OAuth scope : " +
                        (String) accessor.getProperty(OpenIdConstants.SCOPE));
        helper.requestOauthAuthorization(accessor.consumer.consumerKey,
                        (String) accessor.getProperty(OpenIdConstants.SCOPE));

        helper.requestUxIcon(true);

        log.debug("Requesting OAuth scope : " +
                        (String) accessor.getProperty(OpenIdConstants.SCOPE));
        helper.requestOauthAuthorization(accessor.consumer.consumerKey,
                        (String) accessor.getProperty(OpenIdConstants.SCOPE));

        // add attribute list required from open id provider
        addRequestAttributes(helper);

        HttpSession session = req.getSession();
        AuthRequest authReq = null;

        try {
            authReq = helper.generateRequest();
            // updateAuthRequest(authReq);
            authReq.setRealm(realm);
            session.setAttribute(OpenIdConstants.DISCOVERED, helper.getDiscoveryInformation());
        } catch (DiscoveryException e) {
            createErrorMessage(req, resp);
            log.error(e);
        } catch (MessageException e) {
            createErrorMessage(req, resp);
            log.error(e);
        } catch (ConsumerException e) {
            createErrorMessage(req, resp);
            log.error(e);
        }
        /*
         * Following code for setting the refererCookie is being used to provide
         * the referer information to the CheckAuthServlet for redirecting.
         */
        String referer = req.getHeader(WebConstants.REFERER);
        if (null != referer && !referer.equals("")) {
            Cookie refererCookie = new Cookie(WebConstants.REFERER, referer);
            resp.addCookie(refererCookie);
        }
        resp.sendRedirect(authReq.getDestinationUrl(true));
    }

    /**
     * Generate Realm URL
     * 
     * @param req
     * @return StringBuffer
     */
    private StringBuffer baseURL(HttpServletRequest req) {
        // posted means they're sending us an OpenID4
        StringBuffer realmBuf = new StringBuffer(req.getScheme())
                        .append(COLON_BACKSLASH).append(req.getServerName());

        if ((req.getScheme().equalsIgnoreCase(HTTP)
                        && req.getServerPort() != 80)
                        || (req.getScheme().equalsIgnoreCase(HTTPS)
                        && req.getServerPort() != 443)) {
            realmBuf.append(COLON).append(req.getServerPort());
        }
        return realmBuf;
    }

    /**
     * @param req
     * @param resp
     * @throws IOException
     */
    private void createErrorMessage(HttpServletRequest req, HttpServletResponse resp)
                    throws IOException {
        HttpSession session = req.getSession();
        StringBuffer errorMessage =
                        new StringBuffer("Could not discover OpenID endpoint.");
        session.setAttribute("loginError", errorMessage);
        resp.sendRedirect(req.getHeader(WebConstants.REFERER));
    }

    /**
     * This method add list of attribute which are required from provider.
     * 
     * @param helper
     */
    private void addRequestAttributes(AuthRequestHelper helper) {
        log.debug("Requesting AX email");
        helper.requestAxAttribute(Step2.AxSchema.EMAIL, true);

        log.debug("Requesting AX First Name");
        helper.requestAxAttribute(Step2.AxSchema.FIRST_NAME, true);

        log.debug("Requesting AX Last Name");
        helper.requestAxAttribute(Step2.AxSchema.LAST_NAME, true);

        log.debug("Requesting AX Country");
        helper.requestAxAttribute(Step2.AxSchema.COUNTRY, true);

        log.debug("Requesting AX Language");
        helper.requestAxAttribute(Step2.AxSchema.LANGUAGE, true);

    }
}

