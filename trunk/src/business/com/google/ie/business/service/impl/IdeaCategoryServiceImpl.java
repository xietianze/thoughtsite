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

package com.google.ie.business.service.impl;

import com.google.ie.business.dao.IdeaCategoryDao;
import com.google.ie.business.domain.IdeaCategory;
import com.google.ie.business.service.IdeaCategoryService;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.common.cache.CacheConstants;
import com.google.ie.common.cache.CacheHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * A service implementation of the CategoryService
 * 
 * @author Sachneet
 * 
 */
@Service
public class IdeaCategoryServiceImpl implements IdeaCategoryService {
    @Autowired
    private IdeaCategoryDao ideaCategoryDao;

    @Override
    public IdeaCategory addIdeaCategory(IdeaCategory categoryToBeSaved) {
        /* Checks whether the category already exists */
        IdeaCategory ideaCategory = getCategoryByName(categoryToBeSaved.getName());
        /* If null is returned , the category does not yet exist */
        if (null == ideaCategory) {
            /* Save the new category to the datastore */
            ideaCategory = ideaCategoryDao.saveIdeaCategory(categoryToBeSaved);
            addToCache(ideaCategory);
        }
        return ideaCategory;
    }

    @Override
    public List<IdeaCategory> getAllIdeaCategories() {
        /* Check the cache first */
        List<IdeaCategory> listOfCategories = getFromCache();
        /*
         * If cache does not contain the data,fetch it from datastore and add to
         * the cache
         */
        if (null == listOfCategories) {
            listOfCategories = ideaCategoryDao.getIdeaCategories();
            if (listOfCategories != null && listOfCategories.size() > ServiceConstants.ZERO) {
                CacheHelper.putObject(CacheConstants.CATEGORY_NAMESPACE, CacheConstants.CATEGORIES,
                                (Serializable) listOfCategories,
                                CacheConstants.CATEGORIES_EXPIRATION_DELAY);
            }
        }
        return listOfCategories;
    }

    @Override
    public IdeaCategory getCategoryByName(String categoryName) {
        /* Get categories data from cache */
        List<IdeaCategory> listOfCategories = getFromCache();
        /*
         * If the list is not null ,iterate it and check if a category object
         * with the specified name exists
         */
        if (listOfCategories != null) {
            Iterator<IdeaCategory> categoriesIterator = listOfCategories.iterator();
            IdeaCategory ideaCategory = null;
            while (categoriesIterator.hasNext()) {
                ideaCategory = categoriesIterator.next();
                if (ideaCategory.getName().equalsIgnoreCase(categoryName)) {
                    return ideaCategory;
                }
            }
            /* If the cache does not contain the category, check the datastore */
            return ideaCategoryDao.getCategoryByName(categoryName);
        }
        /* Executed in case cache does not contain categories data */
        return ideaCategoryDao.getCategoryByName(categoryName);

    }

    /**
     * Checks the cache for the categories data
     * 
     * @return list of {@link IdeaCategory } objects
     */
    @SuppressWarnings("unchecked")
    private List<IdeaCategory> getFromCache() {
        /* Check the cache for the categories data */
        if (CacheHelper
                        .containsObject(CacheConstants.CATEGORY_NAMESPACE,
                        CacheConstants.CATEGORIES)) {
            List<IdeaCategory> listOfCategories = (List<IdeaCategory>) CacheHelper
                            .getObject(CacheConstants.CATEGORY_NAMESPACE, CacheConstants.CATEGORIES);
            return listOfCategories;
        }
        return null;
    }

    /**
     * Add the new category to the cache
     * 
     * @param category the category object to be added to the cache
     */
    private void addToCache(IdeaCategory category) {
        /* Fetch the categories data from cache */
        List<IdeaCategory> listOfCategories = getFromCache();
        /* If the cache does not contain categories data,fetch it from datastore */
        if (null == listOfCategories) {
            listOfCategories = ideaCategoryDao.getIdeaCategories();
        } else {/*
                 * Add the new category to the category list retrieved from
                 * cache
                 */
            listOfCategories.add(category);
        }/*
          * Add the categories to the cache.If the object already existed it
          * would be overwritten.
          */
        if (listOfCategories != null && listOfCategories.size() > ServiceConstants.ZERO) {
            CacheHelper.putObject(CacheConstants.CATEGORY_NAMESPACE, CacheConstants.CATEGORIES,
                            (Serializable) listOfCategories);
        }

    }

    public IdeaCategoryDao getIdeaCategoryDao() {
        return ideaCategoryDao;
    }

    public void setIdeaCategoryDao(IdeaCategoryDao ideaCategoryDao) {
        this.ideaCategoryDao = ideaCategoryDao;
    }
}

