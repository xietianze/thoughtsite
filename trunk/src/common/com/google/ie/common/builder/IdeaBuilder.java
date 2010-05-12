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

package com.google.ie.common.builder;

import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.Tag;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.ProjectService;
import com.google.ie.business.service.ShardedCounterService;
import com.google.ie.business.service.TagService;
import com.google.ie.business.service.UserService;
import com.google.ie.common.exception.IdeasExchangeException;
import com.google.ie.dto.IdeaDetail;
import com.google.ie.dto.RetrievalInfo;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This will be used for converting the complex idea dto
 * for different request flows like getIdeaForUser, getFeatured idea.
 * 
 * @author Charanjeet singh
 */
@Component
public class IdeaBuilder {

    private static final int ONE = 1;
    private static final int ZERO = 0;
    @Autowired
    private IdeaService ideaService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;
    @Autowired
    private ShardedCounterService shardedCounterService;

    /**
     * Retrieves the list of published ideas along with their tags and teams by
     * tag names.
     * 
     * @param retrievalInfo the idea list retrieval information
     * @return Returns the list of IdeaDetail objects
     */
    public List<IdeaDetail> getIdeasByTagName(String tagName, RetrievalInfo retrievalInfo) {
        List<IdeaDetail> ideaDtoList = null;
        /* Fetch one more record than what is required */
        retrievalInfo.setNoOfRecords(retrievalInfo.getNoOfRecords() + ONE);
        List<Idea> ideaList = ideaService.getIdeasByTagName(tagName, retrievalInfo);
        if (ideaList != null && ideaList.size() > ZERO) {
            ideaDtoList = convertToIdeaDetailList(ideaList, true, true, false, true);
        }
        return ideaDtoList;
    }

    /**
     * Retrieves the list of published ideas along with their tags and teams.
     * 
     * @param retrievalInfo the idea list retrieval information
     * @return Returns the list of IdeaDetail objects
     */
    public List<IdeaDetail> getIdeasForListing(RetrievalInfo retrievalInfo) {
        List<IdeaDetail> ideaDtoList = null;
        /* Fetch one more record than what is required */
        retrievalInfo.setNoOfRecords(retrievalInfo.getNoOfRecords() + ONE);
        List<Idea> ideaList = ideaService.getIdeas(retrievalInfo);
        if (ideaList != null && ideaList.size() > 0) {
            ideaDtoList = convertToIdeaDetailList(ideaList, true, true, true, true);
        }
        return ideaDtoList;
    }

    /**
     * Retrieves the list of Ideas published or saved by user.
     * 
     * @param user the {@link User} object.
     * @param retrievalInfo the idea list retrieval information
     * @return Returns the list of IdeaDetail objects
     */
    public List<IdeaDetail> getIdeasForUser(User user, RetrievalInfo retrievalInfo) {
        List<IdeaDetail> ideaDtoList = null;
        /* Fetch one more record than what is required for paging purpose */
        retrievalInfo.setNoOfRecords(retrievalInfo.getNoOfRecords() + ONE);
        List<Idea> ideaList = ideaService.getIdeasForUser(user, retrievalInfo);
        if (ideaList != null && ideaList.size() > ZERO) {
            ideaDtoList = convertToIdeaDetailList(ideaList, true, true, false, true);
        }
        return ideaDtoList;
    }

    /**
     * Gets idea details for the given idea key. For non admin request, only
     * published and duplicate ideas are fetched.
     * For admin requests, ideas with all statuses are fetched.
     * 
     * @param ideaKey the key of the idea whose details are to be fetched
     * @param isAdmin boolean that indicates the request is for admin
     * @return {@link IdeaDetail} object containing the idea details
     * @throws IdeasExchangeException
     */
    public IdeaDetail getIdeaDetail(String ideaKey, boolean isAdmin) throws IdeasExchangeException {
        Idea idea = ideaService.getIdeaByKey(ideaKey);
        if (idea == null)
            return null;
        if (Idea.STATUS_PUBLISHED.equalsIgnoreCase(idea.getStatus())
                        || Idea.STATUS_DUPLICATE.equalsIgnoreCase(idea.getStatus()) || isAdmin) {
            return convertToIdeaDetail(idea, true, true, true, true);
        }
        throw new IdeasExchangeException("", "Idea is marked as objectionable or deleted");

    }

    /**
     * @param shardedCounterService the shardedCounterService to set
     */
    public void setShardedCounterService(ShardedCounterService shardedCounterService) {
        this.shardedCounterService = shardedCounterService;
    }

    /**
     * @return the shardedCounterService
     */
    public ShardedCounterService getShardedCounterService() {
        return shardedCounterService;
    }

    /**
     * Converts the the list of Idea objects into list of IdeaDetail objects.
     * 
     * @param ideaList The list of Idea objects to be transformed into
     *        IdeaDetail.
     * @return Returns the list of IdeaDetail.
     */
    public List<IdeaDetail> convertToIdeaDetailList(List<Idea> ideaList) {
        return convertToIdeaDetailList(ideaList, true, true, true, true);
    }

    /**
     * Converts the the list of Idea objects into list of IdeaDetail objects.
     * 
     * @param ideaList The list of Idea objects to be transformed into
     *        IdeaDetail.
     * @return Returns the list of IdeaDetail.
     */
    public List<IdeaDetail> convertToIdeaDetailList(List<Idea> ideaList, boolean addUser) {
        return convertToIdeaDetailList(ideaList, true, true, addUser, true);
    }

    /**
     * Converts the the list of Idea objects into list of IdeaDetail objects.
     * 
     * @param ideaList The list of Idea objects to be transformed into
     *        IdeaDetail.
     * @param addTags Flag which means that also fetch Tag objects associated
     *        with
     *        the ideas, when set true.
     * @param addTeams Flag which means that also fetch Team objects associated
     *        with the ideas, when set true.
     * @param trimLongFields
     * @return Returns the list of IdeaDetail.
     */
    public List<IdeaDetail> convertToIdeaDetailList(List<Idea> ideaList, boolean addTags,
                    boolean addTeams, boolean addUser, boolean trimLongFields) {
        List<IdeaDetail> ideaDetailList = new ArrayList<IdeaDetail>();
        IdeaDetail ideaDetail = null;
        for (Idea idea : ideaList) {
            if (trimLongFields) {
                shortenFields(idea);
            }
            ideaDetail = convertToIdeaDetail(idea, addTags, addTeams, addUser, false);
            ideaDetailList.add(ideaDetail);
        }
        return ideaDetailList;
    }

    /**
     * Shortens the length of title and description fields
     * 
     * @param idea
     */
    private void shortenFields(Idea idea) {
        if (null != idea) {
            /* 50 chars for title */
            idea.setTitle(StringUtils.abbreviate(idea.getTitle(), 50));
            /* 100 chars for description */
            idea.setDescription(StringUtils.abbreviate(idea.getDescription(), 150));
        }
    }

    /**
     * Converts the the Idea object to IdeaDetail object.
     * 
     * @param idea The Idea object to be transformed into IdeaDetail.
     * @param addTag Flag which means that also fetch Tag objects associated
     *        with
     *        the idea, when set true.
     * @param addTeam Flag which means that also fetch Team objects associated
     *        with the ideas, when set true.
     * @param addProjects Flag which means also fetch the Project objects
     *        associated with the idea
     * @return Returns the list of IdeaDetail.
     */
    public IdeaDetail convertToIdeaDetail(Idea idea, boolean addTag, boolean addTeam,
                    boolean addUser, boolean addProjects) {
        IdeaDetail ideaDetail = new IdeaDetail();
        ideaDetail.setIdea(idea);
        if (addTag) {
            /*
             * Get all tags using tag keys in idea and set them to the
             * corresponding property in ideaDto.
             */
            List<Tag> tagList = tagService.getTagsByKeys(idea.getTagKeys());
            ideaDetail.setTags(tagList);
        }
        if (addUser) {
            User user = userService.getUserByPrimaryKey(idea.getCreatorKey());
            if (user != null) {
                /* Get reputation points for user */
                long userPoints = shardedCounterService.getTotalPoint(idea.getCreatorKey());
                user.setReputationPoints(userPoints);
            }
            ideaDetail.setUser(user);
        }
        /* If true add the projects associated with the idea */
        if (addProjects) {
            List<Project> projects = projectService.getProjectsByIdeaKey(idea.getKey());
            /*
             * If the list is not empty,iterate it and create a map with key of
             * the project object as the key in the map and project name as the
             * value.Only these two attributes are required on the idea detail
             * page
             */
            if (projects != null && projects.size() > ZERO) {
                Iterator<Project> iterator = projects.iterator();
                Map<String, String> mapOfProjectIdAndName = new HashMap<String, String>();
                Project project;
                while (iterator.hasNext()) {
                    project = iterator.next();
                    mapOfProjectIdAndName.put(project.getKey(), project.getName());
                }
                ideaDetail.setMapOfProjectIdAndName(mapOfProjectIdAndName);
            }
        }
        return ideaDetail;
    }

}

