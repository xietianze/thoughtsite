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

import com.google.ie.business.domain.ShardedCounter;

import java.util.List;

/**
 * A data access object specification for ShardedCounter Entity.
 * 
 * @author gmaurya
 * 
 * 
 */
public interface ShardedCounterDao extends BaseDao {
    /**
     * Get shards by parent key.
     * 
     * @param parentKey String
     * @return List<ShardedCounter>
     */
    public List<ShardedCounter> getShardsByParentKey(String parentKey);

    /**
     * 
     * Get shards by parent key and shard number.
     * 
     * @param parentKey String primary key of parent object for which sharded
     *        counter stores data.
     * @param shardNum int shardnumber for particular parent key
     * @return List<ShardedCounter> list of ShardedCounter.
     */
    public List<ShardedCounter> getShardsByParentKeyAndShardNum(String parentKey, int shardNum);

    /**
     * Create or update ShardedCounter
     * 
     * @param shardedCounter
     * @return updated ShardedCounter instance.
     */
    public ShardedCounter createOrUpdateShardedCounter(ShardedCounter shardedCounter);

}

