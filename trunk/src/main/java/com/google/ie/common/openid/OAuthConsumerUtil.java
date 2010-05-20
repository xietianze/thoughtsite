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

package com.google.ie.common.openid;

import com.google.inject.Inject;
import com.google.step2.Step2OAuthClient;

import org.apache.log4j.Logger;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Utility class for requesting OAuth tokens from accessors
 * 
 * @author sweis@google.com (Steve Weis)
 */
public class OAuthConsumerUtil {

    private Logger log = Logger.getLogger(OAuthConsumerUtil.class);
    private final Step2OAuthClient client;

    @Inject
    public OAuthConsumerUtil(Step2OAuthClient client) {
        this.client = client;
    }

    public OAuthAccessor getRequestToken(OAuthAccessor accessor)
                    throws IOException, OAuthException, URISyntaxException {
        OAuthAccessor accessorCopy = new OAuthAccessor(accessor.consumer);

        OAuthMessage response = client.invoke(accessor,
                        accessor.consumer.serviceProvider.requestTokenURL,
                        OAuth.newList("scope", accessor.getProperty("scope").toString()));
        log.info("Successfully got OAuth request token");
        accessorCopy.requestToken = response.getParameter("oauth_token");
        accessorCopy.tokenSecret = response.getParameter("oauth_token_secret");
        return accessor;
    }

    public OAuthAccessor getAccessToken(OAuthAccessor accessor)
                    throws IOException, OAuthException, URISyntaxException {
        OAuthAccessor accessorCopy = new OAuthAccessor(accessor.consumer);

        OAuthMessage response = client.invoke(accessor,
                        accessor.consumer.serviceProvider.accessTokenURL,
                        OAuth.newList("oauth_token", accessor.requestToken,
                        "scope", accessor.getProperty("scope").toString()));
        log.info("Successfully got OAuth access token");
        accessorCopy.accessToken = response.getParameter("oauth_token");
        accessorCopy.tokenSecret = response.getParameter("oauth_token_secret");
        return accessorCopy;
    }
}

