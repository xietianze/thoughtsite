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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.ie.business.dao.impl.IdeaCategoryDaoImpl;
import com.google.ie.business.domain.IdeaCategory;
import com.google.ie.common.cache.CacheConstants;
import com.google.ie.common.cache.CacheHelper;
import com.google.ie.test.ServiceTest;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Test case for IdeaCategoryServiceImpl class
 * 
 * @author Sachneet
 * 
 */
public class IdeaCategoryServiceImplTest extends ServiceTest {
    IdeaCategoryServiceImpl categoryService;

    @Before
    public void setUp() {
        super.setUp();
        categoryService = new IdeaCategoryServiceImpl();
        categoryService.setIdeaCategoryDao(mock(IdeaCategoryDaoImpl.class));
    }

    @Test
    public void addCategory() {
        IdeaCategory category = new IdeaCategory();
        category.setName("testCategory1");
        category.setDescription("testCategory1 description");
        when(categoryService.getIdeaCategoryDao().saveIdeaCategory(category))
                        .thenReturn(category);

        assertNotNull(categoryService.addIdeaCategory(category));
    }

    @Test
    public void getAllCategories_fromCache() {
        CacheHelper.putObject(CacheConstants.CATEGORY_NAMESPACE, CacheConstants.CATEGORIES, this
                        .getMockCategoriesList());
        List<IdeaCategory> listOfCategoryObjects = categoryService.getAllIdeaCategories();

        assertNotNull(listOfCategoryObjects);
    }

    @Test
    public void getAllCategories_fromDatastore() {
        when(categoryService.getIdeaCategoryDao().getIdeaCategories())
                        .thenReturn(this.getMockCategoriesList());
        List<IdeaCategory> listOfCategoryObjects = categoryService.getAllIdeaCategories();
        assertNotNull(listOfCategoryObjects);
    }

    @Test
    public void getCategoryByName() {
        when(categoryService.getIdeaCategoryDao().getCategoryByName("testCategory")).thenReturn(
                        this.getMockCategoriesList().get(0));
        IdeaCategory category = categoryService.getCategoryByName("testCategory");
        assertNotNull(category);
        assertEquals("testCategory", category.getName());
    }

    /**
     * Creates sample category list
     * 
     * @return a sample list of {@link IdeaCategory} objects
     */
    private LinkedList<IdeaCategory> getMockCategoriesList() {
        IdeaCategory category = new IdeaCategory();
        category.setName("testCategory");
        LinkedList<IdeaCategory> categories = new LinkedList<IdeaCategory>();
        categories.add(category);

        return categories;
    }
}

