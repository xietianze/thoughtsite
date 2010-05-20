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

import com.google.ie.business.dao.BaseDao;

import org.springframework.orm.jdo.support.JdoDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * A data access object specification base class for all data access objects
 * that use JDO to interact with the datastore.
 * 
 * @author Charanjeet singh
 */
@Transactional(readOnly = true)
public abstract class BaseDaoImpl extends JdoDaoSupport implements BaseDao {

    /**
     * Finds an entity by primary key.
     * 
     * @param clazz the class type of the entity to be fetched
     * @param key the key of the entity to be fetched
     * @return the fetched entity
     */
    @Override
    public <T> T findEntityByPrimaryKey(java.lang.Class<T> clazz, String key) {
        return key == null ? null : getJdoTemplate().getObjectById(clazz, key);
    }

    /**
     * Persist entity after checking persist manager availability.
     * 
     * @param object the object to be persisted
     * @return the persisted object
     */
    public <T> T persist(T object) {
        if (javax.jdo.JDOHelper.getPersistenceManager(object) == null) {
            getJdoTemplate().makePersistent(object);
        } else {
            javax.jdo.JDOHelper.getPersistenceManager(object)
                            .makePersistent(object);
        }
        return object;

    }

}

