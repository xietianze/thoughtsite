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

/**
 * A data access object specification base class for all data access objects
 * that use JDO to interact with the datastore.
 * 
 * @author Charanjeet singh
 */
public interface BaseDao {

    /**
     * Finds entity by primary key.
     * 
     * @param clazz the class of the entity to be fetched
     * @param key the key of the entity
     * 
     */
    <T> T findEntityByPrimaryKey(java.lang.Class<T> clazz, String key);

}

