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

import com.google.appengine.api.datastore.Blob;
import com.google.ie.business.service.ProjectService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

/**
 * The controller that handles the request for images used in the system
 * 
 * @author Sachneet
 * 
 */
@Controller
public class ImageController {
    private static final Logger LOG = Logger.getLogger(ImageController.class);
    public static final String PATH_TO_DEFAULT_IMAGE = "/public/images/img.gif";
    private static byte[] defaultImage;
    @Autowired
    private ProjectService projectService;

    /**
     * Get the profile image of project
     * 
     * @param key the key of the project
     * @param req the {@link HttpServletRequest} object
     * @param outputStream {@link OutputStream} object
     */
    @RequestMapping(value = "/showImage/{key}", method = RequestMethod.GET)
    public void getProjectImageContent(@PathVariable("key") String key, HttpServletRequest req,
                    OutputStream outputStream) {
        byte[] imageBytes = null;
        boolean imageExist = false;
        try {
            Blob image = projectService.getImageById(key);
            if (image != null) {
                imageBytes = image.getBytes();
                if (imageBytes.length > WebConstants.ZERO) {
                    outputStream.write(imageBytes, WebConstants.ZERO, imageBytes.length);
                    imageExist = true;
                }
            }
            if (!imageExist) {
                /* if there's not image, return default image */
                byte img[] = getDefaultImage(req);
                if (img != null)
                    outputStream.write(img, WebConstants.ZERO, img.length);
            }
        } catch (IOException e) {
            LOG.error("couldn't load defaultImage ", e);
        }
    }

    /**
     * Construct the url for the default image
     * 
     * @param req the {@link HttpServletRequest} object
     * @return the url of the default image
     */
    protected String constructDefaultImageURL(HttpServletRequest req) {
        String baseURL = req.getProtocol() + "://" + req.getServerName() + PATH_TO_DEFAULT_IMAGE;
        return baseURL;
    }

    /**
     * Lazy load default image for use if SwagItem isn't created with an image
     * (ImageIO had a nice way to do it but it is blacklisted on appengine)
     * 
     * @param req {@link HttpServletRequest } object
     * @return the byte array of the image
     */
    private byte[] getDefaultImage(HttpServletRequest req) {
        if (defaultImage != null && defaultImage.length != WebConstants.ZERO) {
            return defaultImage;
        }
        String defaultImageURLString = constructDefaultImageURL(req);
        ByteArrayOutputStream bas = null;
        BufferedInputStream bis = null;
        /*
         * create defaultImage byte[] from URL.
         * Ouch this would have been easier with ImageIO!
         */
        try {
            URL defaultImageURL = new URL(defaultImageURLString);
            bis = new BufferedInputStream(defaultImageURL.openStream());
            bas = new ByteArrayOutputStream();
            int i;
            while ((i = bis.read()) != -1) {
                bas.write(i);
            }
            defaultImage = bas.toByteArray();
            return defaultImage;
        } catch (IOException e) {
            LOG.warn("couldn't load defaultImage at " + defaultImageURLString, e);
            return null;
        } finally {
            try {
                if (bas != null)
                    bas.close();
            } catch (IOException e) {
                LOG.warn(e);
            }
            try {
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
                LOG.warn(e);
            }
        }
    }
}

