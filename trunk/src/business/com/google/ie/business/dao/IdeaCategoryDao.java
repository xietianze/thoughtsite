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

import com.google.ie.business.domain.IdeaCategory;

import java.util.List;

/**
 * A data access object specification for IdeaCategory entity.
 * 
 * @author Sachneet
 * 
 */
public interface IdeaCategoryDao extends BaseDao {
    /**
     * Retrieves all the categories from the datastore.
     * 
     * @return a list of all the {@link IdeaCategory} objects from the
     *         datastore
     * 
     */
    List<IdeaCategory> getIdeaCategories();

    /**
     * Saves the category object to the datastore
     * 
     * @param category the {@link IdeaCategory} object to be persisted to the
     *        datastore
     * 
     * @return the {@link IdeaCategory} object saved to the datastore
     */
    IdeaCategory saveIdeaCategory(IdeaCategory category);

    /**
     * Fetches the category from the datastore with category name equal to name
     * param.
     * 
     * @param name the name of category to be fetched
     * @return {@link IdeaCategory} object that has the category name equal to
     *         the name param
     */
    IdeaCategory getCategoryByName(String name);
}

