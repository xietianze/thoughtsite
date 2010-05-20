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

import com.google.appengine.api.datastore.Key;
import com.google.ie.business.domain.EntityIndex;

/**
 * A service specification for an entity {@link EntityIndex}
 * 
 * @author Ashish K. Dahiya
 * 
 */
public interface EntityIndexService {

    /**
     * Retrieves the unindexed entity based on the index flag.
     * 
     * @return the Entity Index which determines the index status
     */
    EntityIndex getUnIndexedEntity();

    /**
     * Update the entities's index status.
     * 
     * @param entityIndex Index to update
     * @return the updated entity index
     */
    EntityIndex updateEntityIndex(EntityIndex entityIndex);

    /**
     * Get the entity using the primary key.
     * 
     * @param key Primary key for the entity
     * @param clazz Class of entity to be retrieved
     * @return retrieved entity.
     */
    <T> T getEntity(String key, java.lang.Class<T> clazz);

    /**
     * Get the entity using the primary key.
     * 
     * @param key Primary key for the entity
     * @return retrieved entity.
     */
    EntityIndex getEntity(Key key);

    /**
     * Creates the EntityIndex for the entity identified by primary
     * 
     * @param primaryKey primary key of the entity to be indexed
     * @return the Entity Index created for the entity
     */
    EntityIndex createEntityIndex(String primaryKey);
}

