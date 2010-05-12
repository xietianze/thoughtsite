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

import com.google.appengine.api.datastore.Key;
import com.google.ie.business.dao.EntityIndexDao;
import com.google.ie.business.domain.EntityIndex;
import com.google.ie.business.service.EntityIndexService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * A service implementation of the {@link EntityIndexService}.
 * 
 * @author Ashish K. Dahiya
 */
public class EntityIndexServiceImpl implements EntityIndexService {

    @Autowired
    private EntityIndexDao entityIndexDao;

    @Override
    public EntityIndex updateEntityIndex(final EntityIndex entityIndex) {
        return entityIndexDao.updateEntityIndex(entityIndex);
    }

    @Override
    public EntityIndex getUnIndexedEntity() {
        return entityIndexDao.getUnIndexedEntity();
    }

    @Override
    public <T> T getEntity(final String key, final java.lang.Class<T> clazz) {
        return entityIndexDao.findEntityByPrimaryKey(clazz, key);
    }

    @Override
    public EntityIndex getEntity(final Key key) {
        return entityIndexDao.findEntityByPrimaryKey(key);
    }

    @Override
    public EntityIndex createEntityIndex(final String primaryKey) {
        return entityIndexDao.createEntityIndex(primaryKey);
    }

    public EntityIndexDao getEntityIndexDao() {
        return entityIndexDao;
    }

    public void setEntityIndexDao(final EntityIndexDao entityIndexDao) {
        this.entityIndexDao = entityIndexDao;
    }
}

