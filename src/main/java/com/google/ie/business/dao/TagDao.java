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

import com.google.ie.business.domain.Tag;
import com.google.ie.dto.RetrievalInfo;

import java.util.Collection;
import java.util.List;

/**
 * A data access object specification for Tag entity.
 * 
 * @author Sachneet
 */
public interface TagDao extends BaseDao {
    /**
     * Saves the tag associated with given idea into the data store.
     * 
     * @param tag Tag to be saved.
     * @return Saved tag object or null if save operation failed.
     */
    Tag saveTag(Tag tag);

    /**
     * Retrieves all the tag objects from the data store.
     * 
     * @return list of {@link Tag} objects retrieved from the datastore
     */
    List<Tag> getAllTags();

    /**
     * Retrieves the tag object from data store by title.
     * 
     * @param title Title of the tag.
     * @return Returns the tag object.
     */
    Tag getTagByTitle(String title);

    /**
     * Retrieves all the tag objects from the data store corresponding to the
     * keys supplied.
     * 
     * @param keys collection having Tag's keys.
     * @return Returns the list of Tags corresponding to the keys.
     */
    List<Tag> getTagsByKeys(Collection<String> keys);

    /**
     * Retrieves the tags starting with a specific string from the datastore.
     * 
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        query parameters
     * @return list of fetched tags
     */
    List<Tag> getTagsWithSpecificStartString(String startString, RetrievalInfo retrievalInfo);

    /**
     * Retrieves the tags for the tag cloud.The tags are sorted alphabetically.
     * 
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        query parameters
     * @return list of fetched tags.
     */
    List<Tag> getTagsForTagCloud(RetrievalInfo retrievalInfo);

}

