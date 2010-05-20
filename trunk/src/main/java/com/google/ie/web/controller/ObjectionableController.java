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

import com.google.ie.business.domain.BadWord;
import com.google.ie.business.domain.EntityIndex;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.service.IdeaService;
import com.google.ie.common.objectionable.ObjectionableManager;
import com.google.ie.common.taskqueue.TagWeightUpdationManager;
import com.google.ie.common.util.ClassUtility;
import com.google.ie.common.util.SearchUtility;
import com.google.ie.dto.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.compass.annotations.SearchableProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * A controller that handles request for checking objectionable content.
 * 
 * @author gmaurya
 * 
 */
@RequestMapping("/objectionable")
@Controller
public class ObjectionableController {
    private static final Logger LOG = Logger.getLogger(ObjectionableController.class);
    @Autowired
    private IdeaService ideaService;
    @Autowired
    private TagWeightUpdationManager weightUpdationManager;

    /**
     * Handles the request for checking objectionable content.
     * This request is initiated by TaskQueue for checking objectionable content
     * of ideas and it's started worker TaskQueue for checking objectionable
     * content of different idea attribute.
     * 
     * @param key the key of the {@link EntityIndex} entity to be indexed
     * @return the name of the resource to which the request should be forwarded
     */
    @RequestMapping("/check/{key}")
    public String checkObjectionable(@PathVariable String key) {
        LOG.debug("Checking Objectionable content for idea having key: " + key);
        if (key != null && key.trim().length() > WebConstants.ZERO) {
            Field[] fields = Idea.class.getDeclaredFields();
            for (Field field : fields) {
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == SearchableProperty.class) {
                        ObjectionableManager.startCheckObjectionableWorker(key,
                                        field.getName());
                        break;
                    }
                }
            }

        }
        return "queue/queue";
    }

    /**
     * Handles the request for checking objectionable content.
     * This is a worker TaskQueue for checking objectionable
     * content of different idea attribute.
     * 
     * @param key the key of {@link Idea} object to checked
     * @param fieldName the name of the field of the {@link Idea} object to
     *        checked
     * @return the name of the resource to which the request should be forwarded
     */
    @RequestMapping("/worker/{key}/{fieldName}")
    public String objectionableWoker(@PathVariable String key, @PathVariable String fieldName) {
        LOG.debug("Checking Objectionable content for idea attribute having key: " + key
                        + " and for attribute: " + fieldName);
        Idea idea = new Idea();
        idea.setKey(key);
        try {
            idea = getIdeaService().getIdeaDetails(idea);
            if (!isIdeaObjectionable(idea)) {
                String query = getContent(idea, fieldName);
                if (query != null) {
                    SearchResult searchResult = SearchUtility.search(query, true, true, "word",
                                    WebConstants.ZERO,
                                    WebConstants.ONE,
                                    BadWord.class.getSimpleName());
                    if (searchResult.getTotalCount() > WebConstants.ZERO) {
                        LOG.debug("Marking Idea as objectionable having key: " + key);
                        idea.setStatus(Idea.STATUS_OBJECTIONABLE);
                        ideaService.updateStatus(idea);
                        SearchUtility.deleteEntityIndex(idea);
                        /*
                         * Remove this idea from popular,recently picked and
                         * recent ideas lists in cache
                         */
                        ideaService.removeIdeaFromAllListsInCache(idea.getKey());
                        /* Decrement Tags weights asynchronously. */
                        if (!StringUtils.isBlank(idea.getTags())) {
                            getWeightUpdationManager().decrementWeight(idea.getTags());
                        }
                    }
                }
            } else {
                LOG.debug("Idea is already mark as objectionable having key: " + key);
            }
        } catch (Exception e) {
            LOG.error(
                            "Error occure during checking objectionable content for idea having : "
                            + key,
                            e);

        }
        return "queue/queue";

    }

    /**
     * Check for idea that it's already marked as objectionable.
     * 
     * @param idea the {@link Idea} object to checked for objectionable
     * @return boolean whether the {@link Idea} object is already marked
     *         objectionable
     */
    private boolean isIdeaObjectionable(Idea idea) {
        boolean objectionable = false;
        if (idea == null)
            return true;

        String status = idea.getStatus();
        if (status != null && status.equalsIgnoreCase(Idea.STATUS_OBJECTIONABLE)) {
            objectionable = true;
        }
        return objectionable;

    }

    /**
     * This method fetch the value of different attribute of Idea
     * object on the basis for fieldType parameter.
     * 
     * @param idea the {@link Idea} object
     * @param fieldType fieldType
     * @return String attribute content based on field type.
     */
    private String getContent(Idea idea, String fieldName) {
        String content = null;
        try {
            content = (String) ClassUtility.getObject(idea, fieldName);
        } catch (Exception e) {
            LOG.error("Error occure during getting value from idea for field : " + fieldName);
        }

        return content;
    }

    public TagWeightUpdationManager getWeightUpdationManager() {
        return weightUpdationManager;
    }

    public void setWeightUpdationManager(TagWeightUpdationManager weightUpdationManager) {
        this.weightUpdationManager = weightUpdationManager;
    }

    /**
     * 
     * @return IdeaService
     */
    public IdeaService getIdeaService() {
        return ideaService;
    }

    /**
     * 
     * @param ideaService IdeaService
     */
    public void setIdeaService(IdeaService ideaService) {
        this.ideaService = ideaService;
    }
}

