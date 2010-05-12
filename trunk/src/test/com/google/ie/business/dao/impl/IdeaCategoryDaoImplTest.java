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

package com.google.ie.business.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.ie.business.domain.IdeaCategory;
import com.google.ie.test.DatastoreTest;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Test case for IdeaCategoryDaoImpl class
 * 
 * @author Sachneet
 * 
 */
public class IdeaCategoryDaoImplTest extends DatastoreTest {
    IdeaCategoryDaoImpl categoryDaoImpl = null;

    @Before
    public void setUp() {
        super.setUp();
        this.categoryDaoImpl = new IdeaCategoryDaoImpl();
        this.categoryDaoImpl.setPersistenceManagerFactory(pmf);
    }

    @Test
    public void getCategories() {
        IdeaCategory expectedCategory = new IdeaCategory();
        expectedCategory.setName("CategoryTest");
        categoryDaoImpl.saveIdeaCategory(expectedCategory);
        List<IdeaCategory> listOfCategoryObjects =
                        categoryDaoImpl.getIdeaCategories();

        assertNotNull(listOfCategoryObjects);
        IdeaCategory actualCategory = listOfCategoryObjects.get(0);
        assertEquals(expectedCategory.getName(), actualCategory.getName());
    }

    @Test
    public void saveCategory() {
        IdeaCategory expectedCategory = new IdeaCategory();
        expectedCategory.setName("CategoryTest");
        IdeaCategory actualCategory =
                        categoryDaoImpl.saveIdeaCategory(expectedCategory);

        assertNotNull(actualCategory);
        assertEquals(expectedCategory.getName(), actualCategory.getName());
    }

    @Test
    public void getCategoryByName() {
        IdeaCategory expectedCategory = new IdeaCategory();
        expectedCategory.setName("CategoryTest");
        categoryDaoImpl.saveIdeaCategory(expectedCategory);
        IdeaCategory actualCategory =
                        categoryDaoImpl.getCategoryByName(expectedCategory.getName());

        assertNotNull(actualCategory);
        assertEquals(expectedCategory.getName(), actualCategory.getName());
    }
}

