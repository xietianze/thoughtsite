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

import com.google.ie.business.domain.ShardedCounter;

/**
 * A Service specification for the {@link ShardedCounter} entity.
 * 
 * @author gmaurya
 * 
 */
public interface ShardedCounterService {
    /**
     * This method get the merged shardedcounter for a specific parentKey;
     * 
     * @param parentKey key of the object for which merging of shareded counter
     *        is being done.
     * @return ShardedCounter Aggregate sharded counter.
     */
    public ShardedCounter getMergedShardedCounter(String parentKey);

    /**
     * Get total point for a specific parent key.
     * 
     * @param parentKey String
     * @return long
     */
    public long getTotalPoint(String parentKey);

    /**
     * Increment negative points for a specific parent key.
     * 
     * @param parentKey Key of object
     */

    public void incrementNegativePoints(String parentKey);

    /**
     * increment positive points for a specific parent key.
     * 
     * @param parentKey Key of object
     */
    public void incrementPositivePoints(String parentKey);

    /**
     * Retrieves negative points for a specific parent key.
     * 
     * @param parentKey Key of object.
     * @return total negative points.
     */
    public int getNegativePoint(String parentKey);

    /**
     * Get positive points for a specific parent key.
     * 
     * @param parentKey Key of object.
     * @return total positive points.
     */
    public int getPositivePoint(String parentKey);

    /**
     * update Total Points for a specific key.
     * 
     * @param parentKey Key of object.
     * @param points
     */

    public void updateTotalPoints(String parentKey, long points);

}

