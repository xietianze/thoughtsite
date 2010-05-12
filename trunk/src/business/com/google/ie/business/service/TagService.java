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
import com.google.ie.business.domain.Tag;
import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A service specification for the Tag entity
 * 
 * @author Sachneet
 */
public interface TagService {

    boolean retagIdea(ArrayList<Tag> newTag, Idea idea, User user);

    /**
     * Saves the tags provided as a comma separate string.
     * It Parses the tag string and checks for de-dupe and finally save them
     * into data store.
     * 
     * @param tags The String object representing the comma separated tag
     *        strings.
     * @return list of saved tag objects.
     */
    List<Tag> saveTags(String tags);

    /**
     * Retrieves a list of Tags by keys.
     * 
     * @param keys the collection of keys of Tag objects.
     * @return Returns the list of Tag objects corresponding to keys.
     */
    List<Tag> getTagsByKeys(Collection<String> keys);

    /**
     * Retrieves the tags staring with a specific string.
     * 
     * @param startString the start string to be matched with the title of
     *        the tags
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        request parameters
     * 
     * @return list of fetched tags
     */
    List<Tag> getTagsWithSpecificStartString(String startString, RetrievalInfo retrievalInfo);

    /**
     * Get the data for auto suggestion of tags.
     * 
     * @param startString the start string for which the suggestions are to
     *        be fetched.
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        request parameters
     * @return the list of fetched tags
     */
    List<Tag> getTagsForAutoSuggestion(String startString, RetrievalInfo retrievalInfo);

    /**
     * Get the data for Tag Cloud.The tags returned are sorted alphabetically.
     * 
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        request parameters
     * @return the list of tags for tag cloud
     */
    List<Tag> getTagsForTagCloud(RetrievalInfo retrievalInfo);

    /**
     * Get the tag with title equal to the tagName param.
     * 
     * @param tagName the title of the tag to be fetched
     * @return the {@link Tag} object
     */
    Tag getTagByName(String tagName);

    /**
     * Get the tags associated with ideas owned by a user.
     * 
     * @param user the user whose tags are to be fetched
     * @return list of tags associated with ideas of the user
     */
    List<Tag> getMyTagCloud(User user);

    /**
     * Iterates through the list of objects and increment the weight if each Tag
     * {@link Tag} object.
     * 
     * @param tags comma separated tags string.
     * @return list of tags for which, weight successfully incremented.
     */

    List<Tag> incrementWeights(String tags);

    /**
     * Save a tag
     * 
     * @param tag the {@link Tag} object to be saved
     * @return the saved {@link Tag} object
     */
    Tag saveTag(Tag tag);

    /**
     * Iterates through the list of objects and decrement the weight of each Tag
     * {@link Tag} object.
     * 
     * @param tags comma separated tags string.
     * @return list of tags for which, weight successfully decremented.
     */

    List<Tag> decrementWeights(String tags);

    /**
     * Remove tags with zero weight from the list
     * 
     * @param tagList the list of {@link Tag} objects from which to remove
     *        objects with zero weight
     * @return the filtered list
     */
    List<Tag> removeTagWithZeroWeight(List<Tag> tagList);
}

