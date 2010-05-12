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

import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.ProjectComment;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.ProjectService;
import com.google.ie.common.builder.IdeaBuilder;
import com.google.ie.common.util.SearchUtility;
import com.google.ie.dto.IdeaDetail;
import com.google.ie.dto.ProjectDetail;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.dto.SearchResult;
import com.google.ie.dto.UserDetail;
import com.google.ie.dto.ViewStatus;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A controller that handles requests for search
 * 
 * @author abraina
 * 
 */
@Controller
@RequestMapping("/search")
public class SearchController {
    private static Logger logger = Logger.getLogger(SearchController.class);
    @Autowired
    private IdeaBuilder ideaBuilder;

    @Autowired
    private IdeaService ideaService;
    @Autowired
    private ProjectService projectService;
    private static final String TITLE = "title";

    /**
     * Search the ideas.The search covers the Idea entity and the IdeaComment
     * entity
     * 
     *@param retrievalInfo the {@link RetrievalInfo} object containing the
     *        query parameters
     * @param keyword the keyword to search for
     * @param model the map object
     * @return
     */
    @RequestMapping("/ideasByTitle")
    public void searchIdeaByTitle(@ModelAttribute RetrievalInfo retrievalInfo,
                    @RequestParam(required = false) String keyword, Map<String, Object> model) {
        /* Fetch the range parameters as sent in the request */
        int startIndex = (int) retrievalInfo.getStartIndex();
        int noOfRecordsRequested = (int) retrievalInfo.getNoOfRecords();
        /*
         * Since the compass query accepts page number and not the start
         * index,calculate the page number
         */
        int pageNumber = startIndex / noOfRecordsRequested;
        /*
         * Search both Idea and IdeaComment in one query by specifying both
         * alias together as an array
         */
        SearchResult searchResultOfIdea = search(pageNumber, keyword,
                        noOfRecordsRequested,
                        TITLE, Idea.class.getSimpleName());
        /* Total number of entities fetched */
        int totalCount = searchResultOfIdea.getTotalCount();
        ViewStatus viewStatus = null;
        /*
         * If enough entities exist statisfying the requested range,create the
         * IdeaDetail list
         */
        if (totalCount > (pageNumber * noOfRecordsRequested)) {
            List<Idea> ideaList = convertToIdeaList(searchResultOfIdea);
            /*
             * the method called below creates the ViewStatus object and
             * inserts the data as well as the next and previous indexes
             */
            viewStatus = getTheViewStatusObjectForIdea(ideaList, totalCount, startIndex,
                            noOfRecordsRequested);
        } else {
            viewStatus = ViewStatus.createTheViewStatus(null, WebConstants.SEARCH, null);
        }
        model.put(WebConstants.VIEW_STATUS, viewStatus);
        // return "search/view";
    }

    /**
     * Search the ideas.The search covers the Idea entity and the IdeaComment
     * entity
     * 
     *@param retrievalInfo the {@link RetrievalInfo} object containing the
     *        query parameters
     * @param keyword the keyword to search for
     * @param model the map object
     * @return
     */
    @RequestMapping("/ideas")
    public String searchIdea(@ModelAttribute RetrievalInfo retrievalInfo,
                    @RequestParam(required = false) String keyword,
                    @RequestParam(required = false) String categoryKey, Map<String, Object> model) {
        ViewStatus viewStatus = null;
        if (StringUtils.isBlank(categoryKey)) {
            viewStatus = searchIdeaWithKeyWord(retrievalInfo, keyword);
        } else {
            viewStatus = searchIdeaWithKeyWordAndCategory(retrievalInfo, keyword, categoryKey);
        }
        model.put(WebConstants.VIEW_STATUS, viewStatus);
        return "search/view";
    }

    /**
     * Search idea on the basis of specified keyword and category.
     * 
     * @param retrievalInfo reference of {@link RetrievalInfo}
     * @param keyword for which idea to be searched
     * @param categoryKey to which idea belongs
     * @return
     */
    private ViewStatus searchIdeaWithKeyWordAndCategory(RetrievalInfo retrievalInfo,
                    String keyword, String categoryKey) {
        /* Fetch the range parameters as sent in the request */
        int startIndex = (int) retrievalInfo.getStartIndex();
        int noOfRecordsRequested = (int) retrievalInfo.getNoOfRecords();
        /*
         * Since the compass query accepts page number and not the start
         * index,calculate the page number
         */
        int pageNumber = startIndex / noOfRecordsRequested;
        /*
         * Search both Idea and IdeaComment in one query by specifying both
         * alias together as an array
         */
        SearchResult searchResultOfIdeaAndComment = null;
        List<Idea> ideas = null;
        if (keyword != null && keyword.trim().length() > 0) {
            searchResultOfIdeaAndComment = search(pageNumber, keyword,
                            noOfRecordsRequested,
                            null, new String[] { Idea.class.getSimpleName() });
        } else {
            ideas = ideaService.getIdeasByCategory(categoryKey, retrievalInfo);

        }
        ViewStatus viewStatus = null;
        List<IdeaDetail> ideaDetailList = null;
        int totalCount = 0;
        if (searchResultOfIdeaAndComment != null) {
            /* Total number of entities fetched */
            totalCount = searchResultOfIdeaAndComment.getTotalCount();

            /*
             * If enough entities exist satisfying the requested range,create
             * the
             * IdeaDetail list
             */
            if (totalCount > (pageNumber * noOfRecordsRequested)) {

                ideaDetailList = convertToIdeaDetailList(
                                searchResultOfIdeaAndComment,
                                categoryKey);

                /*
                 * the method called below creates the ViewStatus object and
                 * inserts the data as well as the next and previous indexes
                 */
                viewStatus = getTheViewStatusObject(ideaDetailList, totalCount, startIndex,
                                noOfRecordsRequested);
                return viewStatus;
            }
        } else if (ideas != null) {
            /*
             * the method called below creates the ViewStatus object and
             * inserts the data as well as the next and previous indexes
             */
            IdeaDetail ideaDetail = null;
            ideaDetailList = new ArrayList<IdeaDetail>();
            totalCount = ideas.size();
            for (Idea idea : ideas) {
                ideaDetail = new IdeaDetail();
                ideaDetail.setIdea(idea);
                ideaDetailList.add(ideaDetail);
            }
            viewStatus = getTheViewStatusObject(ideaDetailList, totalCount, startIndex,
                            noOfRecordsRequested);
            return viewStatus;
        }

        viewStatus = ViewStatus.createTheViewStatus(null, WebConstants.IDEAS, null);

        return viewStatus;
    }

    /**
     * Search idea on the basis of specified keyword.
     * 
     * @param retrievalInfo reference of {@link RetrievalInfo}
     * @param keyword for which idea to be searched
     * @return
     */
    private ViewStatus searchIdeaWithKeyWord(RetrievalInfo retrievalInfo, String keyword) {
        /* Fetch the range parameters as sent in the request */
        int startIndex = (int) retrievalInfo.getStartIndex();
        int noOfRecordsRequested = (int) retrievalInfo.getNoOfRecords();
        /*
         * Since the compass query accepts page number and not the start
         * index,calculate the page number
         */
        int pageNumber = startIndex / noOfRecordsRequested;
        /*
         * Search both Idea and IdeaComment in one query by specifying both
         * alias together as an array
         */
        SearchResult searchResultOfIdeaAndComment = search(pageNumber, keyword,
                        noOfRecordsRequested,
                        null, new String[] { Idea.class.getSimpleName(),
                IdeaComment.class.getSimpleName() });
        /* Total number of entities fetched */

        ViewStatus viewStatus = null;
        /*
         * If enough entities exist statisfying the requested range,create the
         * IdeaDetail list
         */
        if (searchResultOfIdeaAndComment != null) {
            int totalCount = searchResultOfIdeaAndComment.getTotalCount();
            if (totalCount > (pageNumber * noOfRecordsRequested)) {
                List<IdeaDetail> ideaDetailList = convertToIdeaDetailList(
                                searchResultOfIdeaAndComment,
                                null);
                /*
                 * the method called below creates the ViewStatus object and
                 * inserts the data as well as the next and previous indexes
                 */
                viewStatus = getTheViewStatusObject(ideaDetailList, totalCount, startIndex,
                                noOfRecordsRequested);
                return viewStatus;
            }
        }
        viewStatus = ViewStatus.createTheViewStatus(null, WebConstants.IDEAS, null);

        return viewStatus;
    }

    /**
     * Search users based on the search keyword.
     * 
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        query parameters
     * @param keyword the keyword to search for
     * @param model the map object
     * @return
     */
    @RequestMapping("/users")
    public String searchUser(@ModelAttribute RetrievalInfo retrievalInfo,
                    @RequestParam(required = false) String keyword, Map<String, Object> model) {
        /* Fetch the range parameters as sent in the request */
        int startIndex = (int) retrievalInfo.getStartIndex();
        int noOfRecordsRequested = (int) retrievalInfo.getNoOfRecords();
        logger.debug("startIndex = " + startIndex + " ,noOfRecordsRequested="
                        + noOfRecordsRequested);
        /*
         * Since the compass query accepts page number and not the start
         * index,calculate the page number
         */
        int pageNumber = startIndex / noOfRecordsRequested;

        SearchResult searchResultOfUser = search(pageNumber, keyword,
                        noOfRecordsRequested,
                        null, User.class.getSimpleName());
        /* Total number of entities fetched */
        int totalCount = searchResultOfUser.getTotalCount();
        logger.debug("Search result count=" + totalCount);
        ViewStatus viewStatus = null;
        /*
         * If enough entities exist satisfying the requested range,create the
         * UserDetail list
         */
        logger.debug("pageNumber * noOfRecordsRequested = " + (pageNumber * noOfRecordsRequested));
        if (totalCount > (pageNumber * noOfRecordsRequested)) {
            List<UserDetail> userDetailList = convertToUserDetailList(searchResultOfUser);
            /*
             * the method called below creates the ViewStatus object and
             * inserts the data as well as the next and previous indexes
             */
            viewStatus = getTheViewStatusObjectForUser(userDetailList, totalCount,
                            startIndex, noOfRecordsRequested);
        } else {
            viewStatus = ViewStatus.createTheViewStatus(null, WebConstants.SEARCH, null);
        }
        model.put(WebConstants.VIEW_STATUS, viewStatus);
        return "search/view";
    }

    /**
     * Get the view status object for Users list.
     * 
     * @param userDetailList
     * @param totalCount
     * @param startIndex
     * @param noOfRecordsRequested
     * @return
     */
    private ViewStatus getTheViewStatusObjectForUser(List<UserDetail> userDetailList,
                    int totalCount, int startIndex, int noOfRecordsRequested) {
        Map<String, Object> parameterMapforViewStatus = getParameterMapForViewStatus(totalCount,
                        startIndex, noOfRecordsRequested);
        ViewStatus viewStatus = ViewStatus.createTheViewStatus(userDetailList,
                        WebConstants.USERS, parameterMapforViewStatus);
        return viewStatus;
    }

    /**
     * Convert the search result to user detail list
     * 
     * @param searchResultOfUser
     * @return
     */
    private List<UserDetail> convertToUserDetailList(SearchResult searchResultOfUser) {

        List<UserDetail> userDetailList = new ArrayList<UserDetail>();

        for (Serializable serializable : searchResultOfUser.getData()) {
            User user = (User) serializable;
            UserDetail userDetail = new UserDetail();
            userDetail.setUser(user);
            /* Get ideas of the user */
            if (user.getIdeaKeys() != null && user.getIdeaKeys().size() > 0) {
                userDetail.setIdeasList(getIdeaList(user.getIdeaKeys()));
            }
            userDetailList.add(userDetail);
        }
        return userDetailList;
    }

    /**
     * Gets the list of ideas for the specified list of {@link Idea} keys.
     * 
     * @param ideaKeys
     */
    private List<Idea> getIdeaList(Set<String> ideaKeys) {
        List<Idea> ideaList = new ArrayList<Idea>();
        Iterator<String> ideaKeyIterator = ideaKeys.iterator();
        while (ideaKeyIterator.hasNext()) {
            String ideaKey = ideaKeyIterator.next();
            Idea idea = ideaService.getIdeaByKey(ideaKey);
            if (idea != null) {
                ideaList.add(idea);
            }
        }
        return ideaList;
    }

    /**
     * Search the ideas.The search covers the Idea entity and the IdeaComment
     * entity
     * 
     *@param retrievalInfo the {@link RetrievalInfo} object containing the
     *        query parameters
     * @param keyword the keyword to search for
     * @param model the map object
     * @return
     */
    @RequestMapping("/projects")
    public String searchProject(@ModelAttribute RetrievalInfo retrievalInfo,
                    @RequestParam(required = false) String keyword, Map<String, Object> model) {
        /* Fetch the range parameters as sent in the request */
        int startIndex = (int) retrievalInfo.getStartIndex();
        int noOfRecordsRequested = (int) retrievalInfo.getNoOfRecords();
        /*
         * Since the compass query accepts page number and not the start
         * index,calculate the page number
         */
        int pageNumber = startIndex / noOfRecordsRequested;
        /*
         * Search both Idea and IdeaComment in one query by specifying both
         * alias together as an array
         */
        SearchResult searchResultOfProjectAndComment = search(pageNumber, keyword,
                        noOfRecordsRequested,
                        null, new String[] { Project.class.getSimpleName(),
                ProjectComment.class.getSimpleName() });
        /* Total number of entities fetched */
        int totalCount = 0;
        if (searchResultOfProjectAndComment != null)
            totalCount = searchResultOfProjectAndComment.getTotalCount();
        ViewStatus viewStatus = null;
        /*
         * If enough entities exist statisfying the requested range,create the
         * Project list
         */
        if (totalCount > (pageNumber * noOfRecordsRequested)) {
            /*
             * the method called below creates the ViewStatus object and
             * inserts the data as well as the next and previous indexes
             */
            List<ProjectDetail> projectDetails = convertToProjectDetailList(searchResultOfProjectAndComment);
            viewStatus = getTheViewStatusProjectObject(projectDetails,
                            totalCount,
                            startIndex,
                            noOfRecordsRequested);
        } else {
            viewStatus = ViewStatus.createTheViewStatus(null, WebConstants.PROJECTS, null);
        }
        model.put(WebConstants.VIEW_STATUS, viewStatus);
        return "search/view";
    }

    /**
     * Create the {@link ViewStatus} object containing the list data and the
     * paging information as well.
     * 
     * @param listOfIdeaDetail list of {@link IdeaDetail}
     * @param totalCount the total number of records satisfying the requested
     *        search
     * @param startIndex the start index for search query
     * @param noOfRecordsRequested number of records requested
     * @return the {@link ViewStatus}object
     */
    private ViewStatus getTheViewStatusProjectObject(List<ProjectDetail> projectDetails,
                    int totalCount,
                    int startIndex, int noOfRecordsRequested) {

        Map<String, Object> parameterMapforViewStatus = getParameterMapForViewStatus(totalCount,
                        startIndex, noOfRecordsRequested);
        ViewStatus viewStatus = ViewStatus.createTheViewStatus(projectDetails,
                        WebConstants.PROJECTS, parameterMapforViewStatus);

        return viewStatus;
    }

    /**
     * Create the {@link ViewStatus} object containing the list data and the
     * paging information as well.
     * 
     * @param listOfIdeaDetail list of {@link IdeaDetail}
     * @param totalCount the total number of records satisfying the requested
     *        search
     * @param startIndex the start index for search query
     * @param noOfRecordsRequested number of records requested
     * @return the {@link ViewStatus}object
     */
    private ViewStatus getTheViewStatusObject(List<IdeaDetail> listOfIdeaDetail, int totalCount,
                    int startIndex, int noOfRecordsRequested) {
        Map<String, Object> parameterMapforViewStatus = getParameterMapForViewStatus(totalCount,
                        startIndex, noOfRecordsRequested);
        ViewStatus viewStatus = ViewStatus.createTheViewStatus(listOfIdeaDetail,
                        WebConstants.IDEAS, parameterMapforViewStatus);
        return viewStatus;
    }

    /**
     * Get parameter map for creating the view status object.
     * 
     * @param totalCount
     * @param startIndex
     * @param noOfRecordsRequested
     * @return
     */
    private Map<String, Object> getParameterMapForViewStatus(int totalCount, int startIndex,
                    int noOfRecordsRequested) {
        /* Map of data to be inserted into the view status object */
        Map<String, Object> parameterMapforViewStatus = new HashMap<String, Object>();
        parameterMapforViewStatus.put(WebConstants.TOTAL_COUNT, totalCount);
        HashMap<String, Integer> pagingMap = new HashMap<String, Integer>();
        /* Set the next and previous index values */
        if (totalCount > startIndex + noOfRecordsRequested) {
            pagingMap.put(WebConstants.NEXT, startIndex + noOfRecordsRequested);
        } else {
            pagingMap.put(WebConstants.NEXT, WebConstants.MINUS_ONE);
        }
        /* (Start index) - (number of records requested) is the previous index */
        pagingMap.put(WebConstants.PREVIOUS, startIndex - noOfRecordsRequested);
        parameterMapforViewStatus.put(WebConstants.PAGING, pagingMap);
        return parameterMapforViewStatus;
    }

    /**
     * Create the {@link ViewStatus} object containing the list data and the
     * paging information as well.
     * 
     * @param listOfIdeaDetail list of {@link IdeaDetail}
     * @param totalCount the total number of records satisfying the requested
     *        search
     * @param startIndex the start index for search query
     * @param noOfRecordsRequested number of records requested
     * @return the {@link ViewStatus}object
     */
    private ViewStatus getTheViewStatusObjectForIdea(List<Idea> listOfIdea, int totalCount,
                    int startIndex, int noOfRecordsRequested) {
        Map<String, Object> parameterMapforViewStatus = getParameterMapForViewStatus(totalCount,
                        startIndex, noOfRecordsRequested);
        ViewStatus viewStatus = ViewStatus.createTheViewStatus(listOfIdea,
                        WebConstants.IDEAS, parameterMapforViewStatus);

        return viewStatus;
    }

    /**
     * Performs search with the given search parameters
     * 
     * @param offset
     * @param keyword
     * @param numOfRecords
     * @param The class to search
     * @return
     */
    private SearchResult search(int offset, String keyword, int numOfRecords,
                    String fieldToBeSearched, String... alias) {
        SearchResult searchResult = SearchUtility.search(keyword, true, fieldToBeSearched, offset,
                        numOfRecords, alias);
        return searchResult;
    }

    /**
     * Converts the search result to list of ideas.
     * 
     * @param searchResultOfIdeaAndComment
     * @return
     */
    private List<Idea> convertToIdeaList(SearchResult searchResultOfIdea) {
        List<Idea> ideas = new ArrayList<Idea>();
        Idea idea = null;
        for (Object obj : searchResultOfIdea.getData()) {
            if (obj instanceof Idea) {
                idea = (Idea) obj;
                ideas.add(idea);
            }
        }
        return ideas;
    }

    /**
     * Create a list of {@link IdeaDetail} objects containing the search
     * result
     * 
     * @param searchResult the {@link SearchResult} object containing the
     *        results of the search
     * @return the list of {@link IdeaDetail} objects
     */
    private List<IdeaDetail> convertToIdeaDetailList(SearchResult searchResult, String categoryKey) {
        Idea ideaRelatedToComment = null;
        Map<String, IdeaDetail> ideaDetailMap = new HashMap<String, IdeaDetail>();
        IdeaDetail ideaDetail = null;
        for (Serializable result : searchResult.getData()) {
            /* If idea object is fetched */
            if (result instanceof Idea) {

                ideaDetail = new IdeaDetail();
                ideaDetail.setIdea((Idea) result);
                /*
                 * Put IdeaDetail object into a map so that all comments related
                 * to the this very idea are added to the same IdeaDetail object
                 */
                if (categoryKey != null && categoryKey.trim().length() > 0) {
                    if (categoryKey.equalsIgnoreCase(ideaDetail.getIdea().getIdeaCategoryKey())) {
                        ideaDetailMap.put(((Idea) result).getKey(), ideaDetail);
                    }
                } else {
                    ideaDetailMap.put(((Idea) result).getKey(), ideaDetail);
                }

            } else if (result instanceof IdeaComment) {
                /*
                 * If IdeaComment object is fetched,get the idea related to the
                 * comment and add it to the IdeaDetail object
                 */
                String ideaKey = ((IdeaComment) result).getIdeaKey();
                if (ideaDetailMap.containsKey(ideaKey)) {
                    ideaDetail = ideaDetailMap.get(ideaKey);
                    ideaDetail.addComments((IdeaComment) result);
                } else {
                    /* get the idea related to the comment */
                    ideaRelatedToComment = ideaService.getIdeaByKey(((IdeaComment) result)
                                    .getIdeaKey());
                    ideaDetail = new IdeaDetail();
                    ideaDetail.setIdea(ideaRelatedToComment);
                    ideaDetail.addComments((IdeaComment) result);
                    ideaDetailMap.put(ideaRelatedToComment.getKey(), ideaDetail);
                }
            }
        }
        /* Get the list of the objects contained in the map */

        List<IdeaDetail> ideaDetailList = convertFromMapToListOfIdeaDetail(ideaDetailMap);

        return ideaDetailList;
    }

    /**
     * Create a list of {@link IdeaDetail} objects containing the search
     * result
     * 
     * @param searchResult the {@link SearchResult} object containing the
     *        results of the search
     * @return the list of {@link IdeaDetail} objects
     */
    private List<ProjectDetail> convertToProjectDetailList(SearchResult searchResult) {
        Map<String, ProjectDetail> projectDetailMap = new HashMap<String, ProjectDetail>();
        ProjectDetail projectDetail = null;
        Project projectRelatedToComment;
        for (Serializable result : searchResult.getData()) {
            /* If idea object is fetched */
            if (result instanceof Project) {
                projectDetail = new ProjectDetail();
                projectDetail.setProject((Project) result);
                /*
                 * Put IdeaDetail object into a map so that all comments related
                 * to the this very idea are added to the same IdeaDetail object
                 */
                projectDetailMap.put(((Project) result).getKey(), projectDetail);
            } else if (result instanceof ProjectComment) {
                /*
                 * If IdeaComment object is fetched,get the idea related to the
                 * comment and add it to the IdeaDetail object
                 */
                String projectKey = ((ProjectComment) result).getProjectKey();
                if (projectDetailMap.containsKey(projectKey)) {
                    projectDetail = projectDetailMap.get(projectKey);
                    projectDetail.addComment((ProjectComment) result);
                } else {
                    /* get the idea related to the comment */
                    projectRelatedToComment = projectService
                                    .getProjectById(((ProjectComment) result)
                                    .getProjectKey());
                    projectDetail = new ProjectDetail();
                    projectDetail.setProject(projectRelatedToComment);
                    projectDetail.addComment((ProjectComment) result);
                    projectDetailMap.put(projectRelatedToComment.getKey(), projectDetail);
                }
            }
        }
        /* Get the list of the objects contained in the map */
        List<ProjectDetail> projectDetailList = convertFromMapToListOfProjectDetail(projectDetailMap);
        return projectDetailList;
    }

    /**
     * Create a list from the map of {@link IdeaDetail} objects
     * 
     * @param ideaDetailMap the map containing the elements
     * @return the list of elements
     */
    private List<IdeaDetail> convertFromMapToListOfIdeaDetail(
                    Map<String, IdeaDetail> ideaDetailMap) {
        Iterator<String> iterator = ideaDetailMap.keySet().iterator();
        List<IdeaDetail> ideaDetailList = new ArrayList<IdeaDetail>();
        while (iterator.hasNext()) {
            ideaDetailList.add(ideaDetailMap.get(iterator.next()));
        }
        return ideaDetailList;
    }

    /**
     * Create a list from the map of {@link IdeaDetail} objects
     * 
     * @param ideaDetailMap the map containing the elements
     * @return the list of elements
     */
    private List<ProjectDetail> convertFromMapToListOfProjectDetail(
                    Map<String, ProjectDetail> projectDetailMap) {
        Iterator<String> iterator = projectDetailMap.keySet().iterator();
        List<ProjectDetail> ideaDetailList = new ArrayList<ProjectDetail>();
        while (iterator.hasNext()) {
            ideaDetailList.add(projectDetailMap.get(iterator.next()));
        }
        return ideaDetailList;
    }

    /**
     * @return the ideaBuilder
     */
    public IdeaBuilder getIdeaBuilder() {
        return ideaBuilder;
    }

    /**
     * @param ideaBuilder the ideaBuilder to set
     */
    public void setIdeaBuilder(IdeaBuilder ideaBuilder) {
        this.ideaBuilder = ideaBuilder;
    }

}

