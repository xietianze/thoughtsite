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

import com.google.appengine.api.datastore.Key;
import com.google.ie.business.dao.EntityIndexDao;
import com.google.ie.business.domain.EntityIndex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * A JDO implementation object for {@link EntityIndexDao}.
 * 
 * @author Ashish K. Dahiya
 */
public class EntityIndexDaoImpl extends BaseDaoImpl implements EntityIndexDao {

    public EntityIndexDaoImpl() {
    }

    @Override
    public EntityIndex updateEntityIndex(EntityIndex entityIndex) {
        if (entityIndex != null) {

            /*
             * Parent key is reset to null as either id or parent key can be
             * set for an entity
             */
            entityIndex.setParentKey(null);
            return getJdoTemplate().makePersistent(entityIndex);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public EntityIndex getUnIndexedEntity() {
        Query query = null;
        try {

            PersistenceManager pm = getJdoTemplate().getPersistenceManagerFactory()
                            .getPersistenceManager();
            query = pm.newQuery(EntityIndex.class);
            query.setFilter("indexed == 0");
            // query.setRange(0, 1);
            List<EntityIndex> results = (List<EntityIndex>) query.execute();

            if (results != null && results.size() > 0) {
                EntityIndex entityIndex = results.get(DaoConstants.ZERO);
                entityIndex = pm.detachCopy(entityIndex);
                return entityIndex;
            }
        } finally {
            if (query != null)
                query.closeAll();
        }
        return null;
    }

    @Override
    public EntityIndex createEntityIndex(String parentKey) {

        EntityIndex entityIndex = new EntityIndex(parentKey);
        /* Set the indexd flag to UnIndexed */
        entityIndex.setIndexed(DaoConstants.UNINDEXED);
        return getJdoTemplate().makePersistent(entityIndex);
    }

    @Override
    public EntityIndex findEntityByPrimaryKey(Key key) {
        Collection<EntityIndex> results = getJdoTemplate().find(EntityIndex.class,
                        "key== :keyParam", null, key);
        results = getJdoTemplate().detachCopyAll(results);
        List<EntityIndex> list = new ArrayList<EntityIndex>(results);
        if (list.size() > DaoConstants.ZERO) {
            EntityIndex entity = list.get(DaoConstants.ZERO);
            return entity;
        }
        return null;
    }
}

