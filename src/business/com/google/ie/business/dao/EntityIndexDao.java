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

import com.google.appengine.api.datastore.Key;
import com.google.ie.business.domain.EntityIndex;

/**
 * A data access object specification for EntityIndex entity.
 * 
 * @author Ashish K. Dahiya
 * 
 */
public interface EntityIndexDao extends BaseDao {

    /**
     * Update the entity index status.
     * 
     * @param entityIndex to update
     * @return the updated entity index
     */
    EntityIndex updateEntityIndex(EntityIndex entityIndex);

    /**
     * Retrieves the unindexed entity based on the the entity's index flag.
     * 
     * @return one of the unindexed {@link EntityIndex} object
     */
    EntityIndex getUnIndexedEntity();

    /**
     * Creates the EntityIndex for the entity identified by parentKey
     * 
     * @param parentKey primary key of the parent entity
     * @return the Entity Index created for the entity
     */
    EntityIndex createEntityIndex(String parentKey);

    /**
     * Finds an entity by primary key.Works only for entities with {@link Key}
     * type as id
     * 
     * @return the updated entity index
     */
    EntityIndex findEntityByPrimaryKey(Key key);

}

