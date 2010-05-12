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

package com.google.ie.business.dao;

import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;

import java.util.List;
import java.util.Set;

/**
 * A data access object specification for Idea entity.
 * 
 * @author Sachneet
 * 
 */
public interface IdeaDao extends BaseDao {

    /**
     * Saves the given idea into the datastore. The saved idea is not visible to
     * other users.
     * 
     * @param idea {@link Idea} object
     * @return Returns the saved idea object.
     */
    Idea saveIdea(Idea idea);

    /**
     * Retrieves the complete idea details for given Idea object with key.
     * 
     * @param idea The Idea object having key.
     * @return returns the Idea object with full details.
     */
    Idea getIdea(Idea idea);

    /**
     * Retrieves the published ideas.
     * It retrieves the ordered list of ideas in paginated mode.
     * All retrieval specific information can be passed using RetrievalInfo
     * object as a parameter.
     * 
     * @param retrievalInfo {@link RetrievalInfo} object having information of
     *        startIndex and total number of records
     * @param statusOfIdeas the set of status to which the idea status should be
     *        matched
     * @return Returns the idea list.
     */
    List<Idea> getIdeas(RetrievalInfo retrievalInfo, Set<String> statusOfIdeas);

    /**
     * Retrieves the ideas saved or published by user. Number of idea to be
     * fetched can be specified using RetrievalInfo.
     * 
     * @param user User object who's ideas are being fetched
     * @param statusOfIdeas the set of status to which the idea status should be
     *        matched
     * @param retrievalInfo {@link RetrievalInfo} object having information of
     *        startIndex and total number of records
     * @return List of idea objects saved or published by user.
     */
    List<Idea> getUserIdeas(User user, Set<String> statusOfIdeas, RetrievalInfo retrievalInfo);

    /**
     * Update the status of the idea.
     * 
     * @param idea Idea object containing updated status and key.
     * @return boolean return true or false on the basis of successful update.
     */
    boolean updateStatus(Idea idea);

    /**
     * Update of the idea vote points of the given idea.
     * 
     * @param idea Idea object containing updated status and key.
     * @return boolean return true or false on the basis of successful update.
     */
    boolean updateIdeaPoints(Idea idea);

    /**
     * Retrieves the ideas associated with the specific tag.
     * 
     * @param tagKey the key of tag whose associated ideas are to be fetched
     * @param statusOfIdeas the set of status to which the idea status should be
     *        matched
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        query parameters
     * @return the list of ideas
     */
    List<Idea> getIdeasByTagKey(String tagKey, Set<String> statusOfIdeas,
                    RetrievalInfo retrievalInfo);

    /**
     * Retrieves the ideas associated with the specific category.
     * 
     * @param categoryKey the key of category whose related ideas are to be
     *        fetched
     * @param statusOfIdeas the set of status to which the idea status should be
     *        matched
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        query parameters
     * @return the list of ideas
     */
    List<Idea> getIdeasByCategoryKey(String categoryKey, String statusOfIdeas,
                    RetrievalInfo retrievalInfo);

}

