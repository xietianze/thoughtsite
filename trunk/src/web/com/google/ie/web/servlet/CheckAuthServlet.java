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

import com.google.ie.business.domain.User;
import com.google.ie.common.constants.OpenIdConstants;
import com.google.ie.web.controller.WebConstants;
import com.google.inject.Inject;
import com.google.step2.AuthResponseHelper;
import com.google.step2.ConsumerHelper;
import com.google.step2.Step2;
import com.google.step2.Step2OAuthClient;
import com.google.step2.VerificationException;
import com.google.step2.AuthResponseHelper.ResultType;
import com.google.step2.consumer.OAuthProviderInfoStore;
import com.google.step2.consumer.ProviderInfoNotFoundException;
import com.google.step2.servlet.InjectableServlet;

import org.apache.log4j.Logger;
import org.openid4java.association.AssociationException;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchResponse;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Example Servlet to handle and check the response from IDP authentication
 * 
 * @author Dirk Balfanz (dirk.balfanz@gmail.com)
 * @author Breno de Medeiros (breno.demedeiros@gmail.com)
 */
public class CheckAuthServlet extends InjectableServlet {
    private static final String EMAIL_TOKEN = "@";
    private ConsumerHelper helper;
    private OAuthProviderInfoStore providerStore;
    private Step2OAuthClient oauthClient;
    private static final String NO_TOKEN = "None";
    private static final String UNKNOWN = "Unknown";
    private Logger log = Logger.getLogger(CheckAuthServlet.class);
    private static final String INVALID_PROVIDER = "Not a valid Provider";
    private static final String INVALID_USER = "Please enter valid username/ password";
    private static final String INVALID_USER_INFORMATION = "Not able to fetch user details";

    private static final String INVALID_ENDPOINT = "Could not discover OpenID endpoint.";

    private static final List<Step2.AxSchema> SUPPORTED_AX_SCHEMAS =
                    Arrays.asList(Step2.AxSchema.values());
    private static final String EMAIL_ID_REQUIRED = "Email Id from open id provider is not available.Please contact your provider.";

    @Inject
    public void setConsumerHelper(ConsumerHelper helper) {
        this.helper = helper;
    }

    @Inject
    public void setProviderInfoStore(OAuthProviderInfoStore store) {
        this.providerStore = store;
    }

    @Inject
    void setOAuthHttpClient(Step2OAuthClient client) {
        this.oauthClient = client;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
        doGet(req, resp);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {

        HttpSession session = req.getSession();
        ParameterList openidResp = Step2.getParameterList(req);
        String receivingUrl = Step2.getUrlWithQueryString(req);
        DiscoveryInformation discovered =
                        (DiscoveryInformation) session.getAttribute(OpenIdConstants.DISCOVERED);

        String requestToken = NO_TOKEN;

        // Try to get the OpenId, AX, and OAuth values from the auth response
        try {
            AuthResponseHelper authResponse =
                            helper.verify(receivingUrl, openidResp, discovered);
            // clean up stale session
            cleanupStaleSession(session);

            // Get Claimed Identifier
            Identifier claimedId = authResponse.getClaimedId();

            session.setAttribute(OpenIdConstants.USER_IDENTIFIER,
                            (claimedId == null) ? UNKNOWN : claimedId.getIdentifier());

            if (authResponse.getAuthResultType() == ResultType.SETUP_NEEDED) {
                createErrorMessage(req, resp, INVALID_PROVIDER);
            }

            if (authResponse.getAuthResultType() == ResultType.AUTH_FAILURE) {
                createErrorMessage(req, resp, INVALID_USER);
            }

            if (authResponse.getAuthResultType() == ResultType.AUTH_SUCCESS) {

                User user = new User();
                String lastName = "";
                String firstName = "";
                String emailId = "";
                Class<? extends AxMessage> axExtensionType =
                                authResponse.getAxExtensionType();
                if (axExtensionType != null) {
                    if (axExtensionType.equals(FetchResponse.class)) {
                        FetchResponse fetchResponse = authResponse.getAxFetchResponse();
                        List<String> aliases = fetchResponse.getAttributeAliases();

                        for (String alias : aliases) {
                            String typeUri = fetchResponse.getAttributeTypeUri(alias);
                            String value = fetchResponse.getAttributeValueByTypeUri(typeUri);
                            if (alias.equalsIgnoreCase(OpenIdConstants.EMAIL)) {
                                emailId = value;
                            }
                            if (alias.equalsIgnoreCase(OpenIdConstants.FIRST_NAME)) {
                                firstName = value;
                            }
                            if (alias.equalsIgnoreCase(OpenIdConstants.LATS_NAME)) {
                                lastName = value;
                            }

                            // check if it's a known type
                            Step2.AxSchema schema = Step2.AxSchema.ofTypeUri(typeUri);
                            if (null != schema) {
                                session.setAttribute(schema.getShortName(), value);
                            } else {
                                session.setAttribute(alias + " (" + typeUri + ")", value);
                            }
                        }
                    }

                    user = getUserObject(user, lastName, firstName, emailId);
                    if (isValidUser(user)) {
                        session.setAttribute(WebConstants.USER, user);

                        // creating cookies for user information
                        createCookies(req, resp, claimedId, user);
                    } else {
                        createErrorMessage(req, resp, EMAIL_ID_REQUIRED);
                    }

                } else {
                    createErrorMessage(req, resp, INVALID_USER_INFORMATION);
                }
                if (authResponse.hasHybridOauthExtension()) {
                    requestToken = authResponse.getHybridOauthResponse().getRequestToken();
                    session.setAttribute(OpenIdConstants.REQUEST_TOKEN, "yes (" + requestToken
                                    + ")");
                }

            }

        } catch (MessageException e) {
            createErrorMessage(req, resp, INVALID_ENDPOINT);
            log.error(e);
        } catch (DiscoveryException e) {
            createErrorMessage(req, resp, INVALID_ENDPOINT);
            log.error(e);
        } catch (AssociationException e) {
            createErrorMessage(req, resp, INVALID_ENDPOINT);
            log.error(e);
        } catch (VerificationException e) {
            createErrorMessage(req, resp, INVALID_ENDPOINT);
            log.error(e);
        }
        getAccessTokenFromRequest(session, requestToken);
        /*
         * Following code block is for handling the lost referer while a project
         * is joined by the non registered user.
         */
        /* Start of specific code block. */

        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (null != name && name.equals(WebConstants.REFERER)) {
                String referer = cookie.getValue();
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
                resp.sendRedirect(referer);
                break;
            }
        }
        /* End of specific code block. */
        resp.sendRedirect("/");
    }

    /**
     * Check for valid user.
     * 
     * @param user
     * @return boolean
     */
    private boolean isValidUser(User user) {
        if (user == null)
            return false;
        String email = user.getEmailId();
        // We are expecting openid should be unique but in some cases gmail is
        // responding more than one open id for same user
        // and that's the reason we are checking for email id because open id
        // from open id provider seems not unique
        if ((email == null) || email.trim().isEmpty())
            return false;

        return true;
    }

    /**
     * Get user object by information retrieved from open id.
     * 
     * @param claimedId
     * @param user
     * @param lastName
     * @param firstName
     * @param emailId
     */
    private User getUserObject(User user, String lastName, String firstName,
                    String emailId) {
        user.setEmailId(emailId);
        String displayName = firstName + " " + lastName;
        if (displayName.trim().isEmpty()) {
            if (emailId.indexOf(EMAIL_TOKEN) != -1)
                displayName = emailId.substring(0, emailId.indexOf(EMAIL_TOKEN));
        }
        user.setDisplayName(displayName);
        user.setId(emailId);
        return user;
    }

    /**
     * @param req
     * @param resp
     * @throws IOException
     */
    private void createErrorMessage(HttpServletRequest req, HttpServletResponse resp, String message)
                    throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("loginError", message);
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (null != name && name.equals(WebConstants.REFERER)) {
                String referer = cookie.getValue();
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
                resp.sendRedirect(referer);
                break;
            }
        }
        if (req.getHeader(WebConstants.REFERER) != null)
            resp.sendRedirect(req.getHeader(WebConstants.REFERER));
        else {
            resp.sendRedirect("/");
        }
    }

    /**
     * Creating cookies required for application.
     * 
     * @param req HttpServletRequest
     * @param resp HttpServletResponse
     * @param claimedId Identifier
     * @param user User
     */
    private void createCookies(HttpServletRequest req, HttpServletResponse resp,
                    Identifier claimedId, User user) {
        Cookie aCookie = new Cookie(OpenIdConstants.VIEWER_DISPLAY_NAME, user
                        .getDisplayName()
                        );
        aCookie.setDomain(req.getContextPath()); // to my domain
        resp.addCookie(aCookie);

        Cookie aCookie1 = new Cookie(OpenIdConstants.USER_IDENTIFIER, claimedId
                        .getIdentifier());
        aCookie.setDomain(req.getContextPath()); // to my domain
        resp.addCookie(aCookie1);
    }

    /**
     * Cleanup Stale Session
     * 
     * @param session
     */
    private void cleanupStaleSession(HttpSession session) {
        // Clean up stale session state if any
        for (Step2.AxSchema schema : SUPPORTED_AX_SCHEMAS) {
            session.removeAttribute(schema.getShortName());
        }
        session.removeAttribute(OpenIdConstants.REQUEST_TOKEN);
        session.removeAttribute(OpenIdConstants.ACCESS_TOKEN);
        session.removeAttribute(OpenIdConstants.ACCESS_TOKEN_SECRET);
        session.removeAttribute(OpenIdConstants.ACCESSOR);
        session.removeAttribute(WebConstants.USER);
    }

    /**
     * Getting an acess token from this request token.
     * 
     * @param session
     * @param requestToken
     * @throws IOException
     */
    private void getAccessTokenFromRequest(HttpSession session, String requestToken)
                    throws IOException {
        String accessToken = NO_TOKEN;
        String accessTokenSecret = NO_TOKEN;
        if (!NO_TOKEN.equals(requestToken)) {
            // Try getting an acess token from this request token.
            try {
                OAuthAccessor accessor = providerStore.getOAuthAccessor(OpenIdConstants.GOOGLE);

                OAuthMessage response = oauthClient.invoke(accessor,
                                accessor.consumer.serviceProvider.accessTokenURL,
                                OAuth.newList(OAuth.OAUTH_TOKEN, requestToken));

                if (response != null) {
                    accessToken = response.getParameter(OAuth.OAUTH_TOKEN);
                    accessTokenSecret = response.getParameter(OAuth.OAUTH_TOKEN_SECRET);

                    session.setAttribute(OpenIdConstants.ACCESS_TOKEN, "yes (" + accessToken + ")");
                    session.setAttribute(OpenIdConstants.ACCESS_TOKEN_SECRET,
                                    "yes (" + accessTokenSecret + ")");

                    // store the whole OAuth accessor in the session
                    accessor.accessToken = accessToken;
                    accessor.tokenSecret = accessTokenSecret;
                    session.setAttribute(OpenIdConstants.ACCESSOR, accessor);

                }
            } catch (ProviderInfoNotFoundException e) {
                e.printStackTrace();
            } catch (OAuthException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

}

