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

import com.google.ie.business.dao.ShardedCounterDao;
import com.google.ie.business.domain.ShardedCounter;

import java.util.List;

import javax.jdo.Query;

/**
 * A data access object specification base class for all data access objects
 * that use JDO to interact with the datastore.
 * 
 * @author gmaurya
 * 
 */
public class ShardedCounterDaoImpl extends BaseDaoImpl implements ShardedCounterDao {

    /**
     * Get the shards related to an entity.
     * 
     * @param parentKey the key of the parent entity
     * 
     * @return List of {@link ShardedCounter} objects
     */
    @SuppressWarnings("unchecked")
    public List<ShardedCounter> getShardsByParentKey(String parentKey) {
        Query query = null;
        List<ShardedCounter> shards = null;
        try {
            query = getJdoTemplate().getPersistenceManagerFactory()
                            .getPersistenceManager().newQuery(ShardedCounter.class);
            query.setFilter("parentKey == :parentName");
            shards = (List<ShardedCounter>) query.execute(parentKey);
        } finally {
            if (query != null)
                query.closeAll();
        }
        return shards;
    }

    /**
     * Get shards by parent key and shard number.
     * 
     * @param parentKey the key of the parent entity
     * @param shardNum an int specifying the shard number
     * 
     * @return List of {@link ShardedCounter} objects
     */
    @SuppressWarnings("unchecked")
    public List<ShardedCounter> getShardsByParentKeyAndShardNum(String parentKey, int shardNum) {
        Query query = null;
        List<ShardedCounter> shards = null;
        try {
            query = getJdoTemplate().getPersistenceManagerFactory()
                            .getPersistenceManager().newQuery(ShardedCounter.class);
            query.setFilter("parentKey == '" + parentKey + "' && " + "shardNumber == " + shardNum);
            shards = (List<ShardedCounter>)
                            query.execute();
        } finally {
            if (query != null)
                query.closeAll();
        }
        return shards;
    }

    @Override
    public ShardedCounter createOrUpdateShardedCounter(ShardedCounter shardedCounter) {
        if (shardedCounter == null)
            return null;
        shardedCounter = getJdoTemplate().makePersistent(shardedCounter);

        return shardedCounter;
    }

}

