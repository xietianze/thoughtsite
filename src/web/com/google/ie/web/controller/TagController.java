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

import com.google.ie.business.domain.Tag;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.TagService;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.util.SearchUtility;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.dto.SearchResult;
import com.google.ie.dto.ViewStatus;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * A controller that handles request for idea tags.
 * 
 * @author Sachneet
 * 
 */
@Controller
@RequestMapping("/tags")
@SessionAttributes("user")
public class TagController {
    private static Logger logger = Logger.getLogger(TagController.class);
    @Autowired
    private TagService tagService;

    @RequestMapping("/list")
    public String showTags() {
        // List<Tag> tags = tagService.getTagsForAutoSuggestion(initial,
        // retrievalInfo);
        return "tags/list";
    }

    /**
     * Get tags for auto-suggestion
     * 
     * @param initial of the tag
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        query parameters
     * @param model the map object
     */
    @RequestMapping("/suggest/{initial}")
    public void showSimilarTags(@PathVariable String initial,
                    @ModelAttribute RetrievalInfo retrievalInfo,
                    Map<String, Object> model) {
        List<Tag> tags = tagService.getTagsForAutoSuggestion(initial,
                        retrievalInfo);
        ViewStatus viewStatus = ViewStatus
                        .createTheViewStatus(tags, WebConstants.TAGS, null);
        /* Remove the RetrievalInfo object from model */
        if (model.containsKey("retrievalInfo")) {
            model.remove("retrievalInfo");
        }
        /* Put the ViewStatus object containing the tag data into the model */
        model.put(WebConstants.VIEW_STATUS, viewStatus);
    }

    /**
     * Get tags starting with a specific string
     * 
     * @param startString
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        query parameters
     * @param model the map object
     */
    @RequestMapping("/list/{startString}")
    public void showTagsStaringWithSpecificString(@PathVariable String startString,
                    @ModelAttribute RetrievalInfo retrievalInfo,
                    Map<String, Object> model) {
        List<Tag> tags = tagService.getTagsWithSpecificStartString(startString.toLowerCase(),
                        retrievalInfo);
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (tags != null && tags.size() > WebConstants.ZERO) {
            parameters.put(WebConstants.WEIGHTS, getTheMaxAndMinWeights(tags));
        }
        ViewStatus viewStatus = ViewStatus.createTheViewStatus(tags, WebConstants.TAGS,
                        parameters);
        /* Remove the RetrievalInfo object from model */
        if (model.containsKey("retrievalInfo")) {
            model.remove("retrievalInfo");
        }
        /* Put the ViewStatus object containing the tag data into the model */
        model.put(WebConstants.VIEW_STATUS, viewStatus);
    }

    /**
     * Displays tag cloud based on the retrieval parameters.
     * 
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        query parameters
     * 
     * @param model the map object
     */
    @RequestMapping("/tagcloud")
    public void showTagCloud(@ModelAttribute RetrievalInfo retrievalInfo,
                    Map<String, Object> model) {
        List<Tag> tags = tagService.getTagsForTagCloud(retrievalInfo);
        /*
         * The second argument is true so that a map containing max/min weight
         * is also added as data
         */
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (tags != null && tags.size() > WebConstants.ZERO) {
            parameters.put(WebConstants.WEIGHTS, getTheMaxAndMinWeights(tags));
        }
        ViewStatus viewStatus = ViewStatus.createTheViewStatus(tags, WebConstants.TAGS,
                        parameters);
        /* Remove the RetrievalInfo object from model */
        if (model.containsKey("retrievalInfo")) {
            model.remove("retrievalInfo");
        }
        /* Put the ViewStatus object containing the tag data into the model */
        model.put(WebConstants.VIEW_STATUS, viewStatus);
    }

    /**
     * Displays the tag cloud for the specified user
     * 
     * @param user the {@link User} whose cloud to be displayed
     * @param model the map object
     */
    @RequestMapping("/my.json")
    public void showMyTagCloud(HttpSession session, Map<String, Object> model) {
        User user = (User) session.getAttribute(WebConstants.USER);
        List<Tag> myTags = tagService.getMyTagCloud(user);
        Map<String, Object> parameters = null;
        if (myTags != null && myTags.size() > WebConstants.ZERO) {
            parameters = new HashMap<String, Object>();
            parameters.put(WebConstants.WEIGHTS, getTheMaxAndMinWeights(myTags));
        }
        ViewStatus viewStatus = ViewStatus.createTheViewStatus(myTags, WebConstants.TAGS,
                        parameters);
        model.put(WebConstants.VIEW_STATUS, viewStatus);
        if (model.containsKey(WebConstants.USER)) {
            model.remove(WebConstants.USER);
        }
    }

    @RequestMapping("/incrementweight")
    public String incrementWeights(@RequestParam String tagString) {
        if (tagString == null || tagString.equals("")) {
            logger.warn("No tag found in query string to increment the weight.");
        } else {
            List<Tag> tagList = tagService.incrementWeights(tagString);
            if (tagList == null || tagList.size() == 0) {
                logger.warn("No tag weight increment occured.");
            }
        }
        return "queue/queue";
    }

    /**
     * Decrement the weight for the tag.
     * 
     * @param tagString for which weight to be decremented
     * @return
     */
    @RequestMapping("/decrementweight")
    public String decrementWeights(@RequestParam String tagString) {
        if (tagString == null || tagString.equals("")) {
            logger.warn("No tag found in query string to decrement the weight.");
        } else {
            List<Tag> tagList = tagService.decrementWeights(tagString);
            if (tagList == null || tagList.size() == 0) {
                logger.warn("No tag weight decrement occured.");
            }
        }
        return "queue/queue";
    }

    /**
     * Searches for a tag.
     * 
     * @param offset of the search result
     * @param keyword for which tag to be searched
     * @param model the map object
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/search")
    public void searchTag(@RequestParam(required = false) String offset,
                    @RequestParam(required = false) String keyword,
                    Map<String, Object> model) {
        int offsetToSearch = 0;
        if (offset != null && offset.trim().length() > 0) {
            try {
                offsetToSearch = Integer.parseInt(offset);
            } catch (Exception e) {
                // no need to throw number format exception.
            }
        }
        keyword = WebConstants.ASTERISK + keyword + WebConstants.ASTERISK;
        ViewStatus viewStatus = new ViewStatus();
        SearchResult searchResult = SearchUtility.search(keyword, true, "title", offsetToSearch,
                        IdeaExchangeConstants.DEFAULT_PAGE_SIZE, Tag.class.getSimpleName());
        List<Tag> tags = null;
        if (searchResult != null && searchResult.getTotalCount() > 0) {
            tags = (List<Tag>) searchResult.getData();
            tags = tagService.removeTagWithZeroWeight(tags);
        }
        if (tags != null && tags.size() > WebConstants.ZERO) {
            viewStatus.setStatus(WebConstants.SUCCESS);
            viewStatus.addData(WebConstants.TAGS, tags);
        } else {
            viewStatus.setStatus(WebConstants.ERROR);
            viewStatus.addMessage(WebConstants.ERROR, WebConstants.NO_TAGS_FOUND);
        }

        model.put(WebConstants.VIEW_STATUS, viewStatus);
    }

    public TagService getTagService() {
        return tagService;
    }

    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Return a map containing the maximum and minimum weights of the tags in
     * the tag cloud data received as list
     * 
     * @param tags the list of tags to be searched for max and min weights
     * @return a map containing the maximum and minimum weights
     */
    private Map<String, Long> getTheMaxAndMinWeights(List<Tag> tags) {
        long min = Collections.min(tags).getWeightage();
        long max = Collections.max(tags).getWeightage();
        Map<String, Long> mapOfMinMax = new HashMap<String, Long>();
        mapOfMinMax.put(WebConstants.MAX_WEIGHT, max);
        mapOfMinMax.put(WebConstants.MIN_WEIGHT, min);

        return mapOfMinMax;
    }
}

