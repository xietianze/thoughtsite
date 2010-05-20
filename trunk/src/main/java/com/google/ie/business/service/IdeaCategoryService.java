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

import com.google.ie.business.domain.IdeaCategory;

import java.util.List;

/**
 * A service specification for the {@link IdeaCategory} entity
 * 
 * @author Sachneet
 * 
 */
public interface IdeaCategoryService {
    /**
     * Saves a new category into the datastore
     * 
     * @param category the {@link IdeaCategory} object to be saved
     * @return the {@link IdeaCategory} object saved to the datastore
     */
    IdeaCategory addIdeaCategory(IdeaCategory category);

    /**
     * 
     * Returns a List of all {@link IdeaCategory} objects.First checks the cache
     * for the data and if not available with cache, gets the data from the
     * datastore.
     * 
     * @return a list of all {@link IdeaCategory} objects.
     */
    List<IdeaCategory> getAllIdeaCategories();

    /**
     * Fetches the category with name equal to categoryName param.
     * First checks the cache.If the category is not available in cache then
     * checks in the datastore.
     * 
     * @param categoryName Name of category.
     * @return the {@link IdeaCategory} object that has the category name equal
     *         to categoryName param.If no such object exists then null.
     */
    IdeaCategory getCategoryByName(String categoryName);
}

