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

package com.google.ie.common.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Utility class to handle recaptcha verification
 * 
 * @author aksrivastava
 * 
 */
@Component
public class ReCaptchaUtility {
    /** Logger for logging information */
    private static final Logger LOG = Logger.getLogger(ReCaptchaUtility.class.getName());
    @Value("${reCaptchaVerifyUrl}")
    private String reCaptchaVerifyUrl;

    @Value("${rcGlobalPrivateKey}")
    private String rcGlobalPrivateKey;
    private static final String UTF_8_ENCODING = "UTF-8";
    private static final String TRUE = "true";

    /**
     * This method verifies the captcha input by the user.
     * 
     * @param remoteAddr the url of the recaptcha server
     * @param recaptchaChallengeField the captcha challenge value
     * @param recaptchaResponseField the captcha response value input by the
     *        user
     * @return boolean the verification result
     * @throws IOException
     */
    public boolean verifyCaptcha(String remoteAddr, String recaptchaChallengeField,
                    String recaptchaResponseField) throws IOException {

        /* create post params */
        if (null == remoteAddr || null == recaptchaChallengeField || null == recaptchaResponseField) {
            return false;
        }
        String postParameters = "privatekey="
                        + URLEncoder.encode(rcGlobalPrivateKey, UTF_8_ENCODING)
                        + "&remoteip="
                        + URLEncoder.encode(remoteAddr, UTF_8_ENCODING) +
                        "&challenge=" + URLEncoder.encode(recaptchaChallengeField, UTF_8_ENCODING)
                        + "&response="
                        + URLEncoder.encode(recaptchaResponseField, UTF_8_ENCODING);
        /* create url */
        String message = this.sendPostRequest(reCaptchaVerifyUrl, postParameters);
        if (null == message) {
            return false;
        }
        /* validate with response */
        String[] arrayOfMsg = message.split("\r?\n");
        if (arrayOfMsg.length < 1) {

            LOG.debug("No answer returned from recaptcha: " + message);
            return false;
        }
        boolean valid = TRUE.equals(arrayOfMsg[0]);
        if (!valid) {
            return false;
        }
        return true;
    }

    /**
     * 
     * This method makes a post request to captcha server for verification of
     * captcha.
     * 
     * @param postUrl the url of the server
     * @param postParameters the parameters making the post request
     * @return String the message from the server in response to the request
     *         posted
     * @throws IOException
     */
    private String sendPostRequest(String postUrl, String postParameters) throws IOException {
        OutputStream out = null;
        String message = null;
        InputStream in = null;
        try {
            URL url = new URL(postUrl);

            /* open connection with settings */
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            /* open output stream */
            out = urlConnection.getOutputStream();
            out.write(postParameters.getBytes());
            out.flush();

            /* open input stream */
            in = urlConnection.getInputStream();

            /* get output */
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int rc = in.read(buf);
                if (rc <= 0)
                    break;
                bout.write(buf, 0, rc);
            }
            message = bout.toString();
            /* close streams */
            out.close();
            in.close();
            return message;
        } catch (IOException e) {
            LOG.error("Cannot load URL: " + e.getMessage());
            out.close();
            in.close();
            return null;
        }
    }
}

