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

package com.google.ie.business.service;

import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;

import java.util.LinkedList;
import java.util.List;

import javax.jws.soap.SOAPBinding.Use;

/**
 * A service specification for the Idea entity.
 * 
 * @author Charanjeet singh
 * 
 */
public interface IdeaService {

    /**
     * Saves an idea into data store. <br />
     * If idea is new then it ensures to add the
     * new idea into data store. If an existing idea is being modified then it
     * updates the existing idea in data store.
     * 
     * @param idea {@link Idea} being saved.
     * @param user {@link User} saving the idea.
     * @return the saved idea.
     */
    Idea saveIdea(Idea idea, User user);

    /**
     * Publishes an idea to make it available for viewing by application users. <br >
     * If idea being published is new then it ensures to add the new idea into
     * data store and publish it. If a saved idea is being published then it
     * updates the changes in existing idea in data store and changes its status
     * to published from saved.
     * </br>
     * 
     * @param idea The {@link Idea} being saved.
     * @param user The {@link User} who is saving the idea.
     * @return the saved {@link Idea}.
     */
    Idea publishIdea(Idea idea, User user);

    /**
     * Retrieves the complete details of idea.
     * 
     * @param idea {@link Idea} object having the key for idea to be returned.
     * @return Returns the {@link Idea} object.
     */
    Idea getIdeaDetails(Idea idea);

    /**
     * Retrieves the ordered list of ideas in paginated mode.
     * All retrieval specific information can be passed using
     * {@link RetrievalInfo} object.
     * 
     * @param retrievalInfo The {@link RetrievalInfo} object containing the idea
     *        list
     *        retrieval parameters.
     * @return Returns the ordered list of ideas.
     */
    List<Idea> getIdeas(RetrievalInfo retrievalInfo);

    /**
     * Retrieves the recently published ideas on site.
     * 
     * @return list of {@link Idea} object
     */
    LinkedList<Idea> getRecentIdeas();

    /**
     * Returns the list of ideas saved or published by user, in paginated form.
     * 
     * @param user {@link Use} who's ideas are being fetched.
     * @param retrievalInfo {@link RetrievalInfo} object representing the
     *        auxiliary information to be passed.
     * @return the List of {@link Ideas} published or saved by user.
     */
    List<Idea> getIdeasForUser(User user, RetrievalInfo retrievalInfo);

    /**
     * Updates the status of idea into data store. <br />
     * It persists the changed idea status to duplicate or objectionable,
     * 
     * @param idea {@link Idea} object.
     * @return boolean return true or false on the basis of successful update.
     */
    boolean updateStatus(Idea idea);

    /**
     * Persists the updated idea with latest change in votes of the idea.
     * 
     * @param idea {@link Idea} object containing updated vote counts.
     * @return boolean return true or false on the basis of successful update.
     */
    boolean updateIdeaVotes(Idea idea);

    /**
     * 
     * This method return Idea on the basis of it's key.
     * 
     * @param key key of idea entity.
     * @return Populated idea object with details.
     */
    Idea getIdeaByKey(String key);

    /**
     * Retrieves the ideas associated with the specific tag.
     * 
     * @param tagName tag name
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        request parameters
     * @return the list of {@link Ideas}
     */
    List<Idea> getIdeasByTagName(String tagName, RetrievalInfo retrievalInfo);

    /**
     *Adds the idea summary to idea.<Br>
     *<p>
     * An Idea creator can add idea summary after publishing the idea.
     *</p>
     * 
     * @param idea {@link Idea} entity on which summary would be added.
     * @param summary Detailed summary abut the idea.
     * @param user {@link User} entity, which is owner of the idea and adding
     *        the summary
     * @return idea with the added summary.
     */
    boolean addSummary(String key, String summary, User user);

    /**
     * Retrieves the most popular ideas on idea exchange site.
     * 
     * @return list of {@link Idea} objects
     */
    LinkedList<Idea> getPopularIdeas();

    /**
     * Serves the request to flag an idea as objectionable with the given key.
     * 
     * @param ideaKey Key of Idea.
     * @param user User who is flagging the idea as objectionable.
     * @return the status of idea.
     */
    String flagObjectionableIdea(String ideaKey, User user);

    /**
     * Serves request to mark an idea duplicate of other.
     * 
     * @param ideaKey Key of {@link Idea}
     * @param user {@link User}, who has flagged the idea
     * @return Status of idea.
     */
    String flagDuplicateIdea(String ideaKey, String originalIdeakey, User user);

    /**
     * Updates {@link Idea} in to datastore.
     * 
     * @param idea {@link Idea} object.
     * @return Updated idea.
     */
    Idea updateIdea(Idea idea);

    /**
     * Get list of ideas with keys contained in the set received as parameter
     * 
     * @return list of {@link Idea} objects
     */
    LinkedList<Idea> getRecentlyPickedIdeas();

    /**
     * Add the idea to the list in cache.
     * 
     * @param idea the idea to be added to cache
     * @param keyOfTheList the key of list to be updated
     * @param noOfIdeas the size of the list
     * @param expiryDelay the expiration time for the data being added to cache
     */
    void addIdeaToListInCache(Idea idea, String keyOfTheList,
                    int noOfIdeas, int expiryDelay);

    /**
     * Remove the idea from the following lists in cache:<br>
     * 1. Popular ideas<br>
     * 2. Recent ideas<br>
     * 3. Recently picked ideas
     * 
     * @param ideaKey the key of the idea to be removed
     */
    void removeIdeaFromAllListsInCache(String ideaKey);

    /**
     * Get idea list on the basis of category.
     * 
     * @param categoryKey key of category on the basis of which idea will be
     *        fetched.
     * @param retrievalInfo {@link RetrievalInfo} object containing the
     *        request parameters
     */
    List<Idea> getIdeasByCategory(String categoryKey, RetrievalInfo retrievalInfo);

    /**
     * Deletes the saved idea of user.
     * 
     * @param key Key of {@link Idea} to be deleted
     * @param user {@link User} who is deleting the own idea.
     */
    void deleteIdea(String key, User user);
}

