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
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.IdeaCategory;
import com.google.ie.business.domain.Tag;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.BadWordService;
import com.google.ie.business.service.IdeaCategoryService;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.TagService;
import com.google.ie.business.service.UserService;
import com.google.ie.common.util.SearchUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Uploads dummy data for testing
 * 
 * @author asirohi
 * 
 */
@Controller
public class DataUploadController {

    @Autowired
    private UserService userService;

    @Autowired
    private IdeaService ideaService;

    @Autowired
    private IdeaCategoryService ideaCategoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private BadWordService badWordService;

    /**
     * upload ideas.
     */
    @RequestMapping("/ideas/upload")
    public void uploadData() {
        String category = addCategories();
        User user = addUser();
        List<Tag> tags = addTags();
        addIdeas(user, tags, category);

    }

    /**
     * upload categories.
     */
    @RequestMapping("/categories/upload")
    public void uploadCategories() {
        addCategories();
    }

    /**
     * upload tags
     * 
     */
    @RequestMapping("/tags/upload")
    public void uploadTags() {
        addTags();
    }

    /**
     * Upload bad words.
     */
    @RequestMapping("/upload/badWords")
    public void uploadBadWords() {

        BadWord badWord2 = new BadWord();
        badWord2.setWord("bastard");
        badWordService.saveBadWord(badWord2);
        SearchUtility.indexEntity(badWord2);

        BadWord badWord3 = new BadWord();
        badWord3.setWord("mutt");
        badWordService.saveBadWord(badWord3);
        SearchUtility.indexEntity(badWord3);

        BadWord badWord4 = new BadWord();
        badWord4.setWord("cur");
        badWordService.saveBadWord(badWord4);
        SearchUtility.indexEntity(badWord4);

        // BadWord badWord5 = new BadWord();
        // badWord5.setWord("ass");
        // badWordService.saveBadWord(badWord5);
        // SearchUtility.indexEntity(badWord5);

    }

    /**
     * Adds a few tags into the datastore
     */
    private List<Tag> addTags() {

        String[] arr = { "abstract", "agriculture", "business", "bargain", "computers", "cars",
                "dairy", "robot", "science", "some", "other" };
        Tag tag;
        List<Tag> tags = new ArrayList<Tag>();
        for (String string : arr) {
            tag = new Tag();
            tag.setTitle(string);
            tag.setUpdatedOn(new Date());
            tag = tagService.saveTag(tag);
            // SearchUtility.indexEntity(tag);
            tags.add(tag);
        }
        return tags;
    }

    /**
     * Add a user into the datastore
     */
    private User addUser() {
        User user = new User();
        user.setId("08937673673258650684");
        user.setReputationPoints(20);
        user.setRoleName(User.ROLE_USER);
        user.setDisplayName("Abhi");
        user.setCreatedOn(new Date());
        return userService.saveUser(user);

    }

    /**
     * Add a user into the datastore
     */
    @SuppressWarnings("unused")
    @RequestMapping("/addAdmin")
    private User addAdmin() {
        User user = new User();
        user
                        .setId("incubatorboss@gmail.com");
        user.setEmailId("incubatorboss@gmail.com");
        user.setRoleName(User.ROLE_ADMIN);
        user.setDisplayName("Admin");
        user.setCreatedOn(new Date());
        user.setStatus(User.STATUS_ACTIVE);
        return userService.saveUser(user);
    }

    /**
     * Adds dummy categories into the datastore
     * 
     * @return
     */
    private String addCategories() {
        String[] array = { "Finance", "Education", "Medicine", "Sports", "Music", "Technology" };
        IdeaCategory category = null;
        for (String name : array) {
            category = new IdeaCategory();
            category.setName(name);
            category.setDescription("description:" + name);
            ideaCategoryService.addIdeaCategory(category);
        }
        return category.getKey();
    }

    /**
     * Adds dummy ideas into the datastore
     * 
     * @param user
     * @param category
     */
    private void addIdeas(User user, List<Tag> tags, String category) {

        addSavedIdea(user, tags, category);
        addObjectionableIdea(user, tags, category);
        addPublishedIdea(user, tags, category);
        addPublishedIdea2(user, tags, category);
    }

    /**
     * @param category
     * @param user2
     * 
     */
    private void addObjectionableIdea(User user, List<Tag> tags, String category) {
        Idea idea = new Idea();
        idea.setTitle("GreenORG (GreenORG)");

        String description = "A Client-Server application which is used to optimize the power " +
                        "usage of an organization (read LAN). A small client will sit on each" +
                        " machine in the organization which will report the vital statistics.";
        idea.setDescription(description);
        idea.setIdeaCategoryKey(category);
        idea.setCompetition("Competition");
        idea.setLastUpdated(new Date());
        idea.setPublishDate(new Date());
        idea.setStatus(Idea.STATUS_SAVED);
        idea.setIdeaRightsGivenUp(true);
        idea.setIpGivenUp(true);
        idea.setMonetization("Monetization opportunities are available");
        idea.setTargetAudience("Web administrators, Hosting companies");
        idea.setTags(tags.get(2).getTitle() + " ," + tags.get(3).getTitle()
                        + " , " + tags.get(4).getTitle());
        Set<String> setOfTagKeys = new HashSet<String>();
        setOfTagKeys.add(tags.get(2).getKey());
        setOfTagKeys.add(tags.get(3).getKey());
        setOfTagKeys.add(tags.get(4).getKey());
        idea.setTagKeys(setOfTagKeys);

        ideaService.saveIdea(idea, user);
    }

    /**
     * Save idea.
     * 
     * @param user
     * @param category
     * 
     */
    private void addSavedIdea(User user, List<Tag> tags, String category) {

        Idea idea = new Idea();
        idea.setTitle("Idea Exchange Website");

        String description = "A medium for people to propose potential web/mobile applications" +
                        " and business ideas tailored to the Indian milieu.It's a place for" +
                        " potential developers/entrepreneurs to find inspiration to build " +
                        "web/mobile applications and businesses...";
        idea.setDescription(description);

        idea.setIdeaCategoryKey(category);
        idea.setCompetition("Competition");
        idea.setLastUpdated(new Date());
        idea.setIdeaRightsGivenUp(true);
        idea.setIpGivenUp(true);
        idea.setMonetization("monetization");
        idea.setTargetAudience("targetAudience");
        idea.setPublishDate(new Date());
        idea.setStatus(Idea.STATUS_SAVED);

        idea.setTags(tags.get(1).getTitle() + " ," + tags.get(2).getTitle()
                        + " , " + tags.get(3).getTitle());
        Set<String> setOfTagKeys = new HashSet<String>();
        setOfTagKeys.add(tags.get(1).getKey());
        setOfTagKeys.add(tags.get(2).getKey());
        setOfTagKeys.add(tags.get(3).getKey());
        idea.setTagKeys(setOfTagKeys);
        ideaService.saveIdea(idea, user);
    }

    /**
     * add Published Idea
     * 
     * @param user
     * @param category
     * 
     */
    private void addPublishedIdea2(User user, List<Tag> tags, String category) {

        Idea idea = new Idea();
        idea.setTitle("Idea Exchange Website");

        String description = "A medium for people to propose potential web/mobile applications" +
                        " and business ideas tailored to the Indian milieu.It's a place for" +
                        " potential developers/entrepreneurs to find inspiration to build " +
                        "web/mobile applications and businesses...";
        idea.setDescription(description);

        idea.setCompetition("Competition");
        idea.setLastUpdated(new Date());
        idea.setIdeaRightsGivenUp(true);
        idea.setIpGivenUp(true);
        idea.setMonetization("Monetization opportunities are available");
        idea.setTargetAudience("Web administrators, Hosting companies");
        idea.setIdeaCategoryKey(category);
        idea.setTags(tags.get(0).getTitle() + " ," + tags.get(1).getTitle()
                        + " , " + tags.get(2).getTitle());
        Set<String> setOfTagKeys = new HashSet<String>();
        setOfTagKeys.add(tags.get(0).getKey());
        setOfTagKeys.add(tags.get(1).getKey());
        setOfTagKeys.add(tags.get(2).getKey());
        idea.setTagKeys(setOfTagKeys);

        ideaService.publishIdea(idea, user);
    }

    /**
     * Add Published Idea
     * 
     * @param user
     * @param category
     * 
     */
    private void addPublishedIdea(User user, List<Tag> tags, String category) {

        Idea idea = new Idea();
        idea.setTitle("CloudB:One Stop Database Backup Solution");

        String description = "The project was aimed at creating a one stop solution for all "
                        +
                        "backup and recovery related needs for any DB deployed on Amazon / Eucalyptus "
                        +
                        "Cloud. This can also be used for the database servers which are not deployed";
        idea.setDescription(description);

        idea.setCompetition("Competition");
        idea.setLastUpdated(new Date());
        idea.setIdeaRightsGivenUp(true);
        idea.setIpGivenUp(true);
        idea.setMonetization("Monetization opportunities are available");
        idea.setTargetAudience("Web administrators, Hosting companies");
        idea.setIdeaCategoryKey(category);
        idea.setTags(tags.get(0).getTitle() + " ," + tags.get(1).getTitle()
                        + " , " + tags.get(2).getTitle());
        Set<String> setOfTagKeys = new HashSet<String>();
        setOfTagKeys.add(tags.get(0).getKey());
        setOfTagKeys.add(tags.get(1).getKey());
        setOfTagKeys.add(tags.get(2).getKey());
        idea.setTagKeys(setOfTagKeys);

        ideaService.publishIdea(idea, user);

    }

    public void setIdeaCategoryService(IdeaCategoryService ideaCategoryService) {
        this.ideaCategoryService = ideaCategoryService;
    }

    public IdeaCategoryService getIdeaCategoryService() {
        return ideaCategoryService;
    }
}

