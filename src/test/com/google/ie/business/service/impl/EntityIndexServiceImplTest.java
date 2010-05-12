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

import com.google.appengine.api.datastore.KeyFactory;
import com.google.ie.business.dao.EntityIndexDao;
import com.google.ie.business.dao.impl.EntityIndexDaoImpl;
import com.google.ie.business.domain.EntityIndex;
import com.google.ie.business.domain.Idea;
import com.google.ie.test.TransactionalServiceTest;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for EntityIndexServiceImpl class
 * 
 * @author Ashish K. Dahiya
 * 
 */
public class EntityIndexServiceImplTest extends TransactionalServiceTest {
    private EntityIndexServiceImpl entityIndexService;
    private EntityIndexDao mockEntityIndexDao = mock(EntityIndexDaoImpl.class);

    @Before
    public void setUp() {
        super.setUp();
        entityIndexService = new EntityIndexServiceImpl();
        entityIndexService.setEntityIndexDao(mockEntityIndexDao);
    }

    @Test
    public void getUnIndexedEntity() {
        String key = KeyFactory.createKeyString("Idea", 1);
        EntityIndex entityIndex = new EntityIndex(key);
        when(entityIndexService.getEntityIndexDao().getUnIndexedEntity()).thenReturn(entityIndex);
        assertNotNull(entityIndexService.getUnIndexedEntity());
        assertEquals(0, entityIndexService.getUnIndexedEntity().getIndexed());
    }

    @Test
    public void updateIdeaEntityStatus() {
        String key = KeyFactory.createKeyString("Idea", 1);
        EntityIndex entityIndex = new EntityIndex(key);
        entityIndex.setIndexed(1);
        when(entityIndexService.getEntityIndexDao().updateEntityIndex(entityIndex)).thenReturn(
                        entityIndex);
        assertNotNull(entityIndexService.updateEntityIndex(entityIndex));
        assertEquals(1, entityIndexService.updateEntityIndex(entityIndex).getIndexed());
    }

    @Test
    public void createEntityIndex() {
        Idea actualIdea = new Idea();
        actualIdea.setTitle("Test_Idea");

        actualIdea.setKey(KeyFactory.createKeyString(Idea.class.getSimpleName(), 1));

        EntityIndex actualEntityIndex = new EntityIndex(actualIdea.getKey());

        when(mockEntityIndexDao.createEntityIndex(actualIdea.getKey()))
                        .thenReturn(actualEntityIndex);
        EntityIndex entityIndex = entityIndexService
                        .createEntityIndex(actualIdea.getKey());
        assertEquals(entityIndex.getParentKey(), actualEntityIndex.getParentKey());
    }

    @Test
    public void getEntity() {
        Idea actualIdea = new Idea();
        actualIdea.setTitle("Test_Idea");
        actualIdea.setKey("TestKey");
        when(mockEntityIndexDao.findEntityByPrimaryKey(Idea.class, actualIdea.getKey()))
                        .thenReturn(actualIdea);
        actualIdea = entityIndexService
                        .getEntity(actualIdea.getKey(), Idea.class);
        assertEquals("Test_Idea", actualIdea.getTitle());
    }
}

