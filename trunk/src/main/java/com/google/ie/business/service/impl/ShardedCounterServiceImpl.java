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

import com.google.ie.business.dao.ShardedCounterDao;
import com.google.ie.business.dao.impl.DaoConstants;
import com.google.ie.business.domain.ShardedCounter;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.business.service.ShardedCounterService;
import com.google.ie.common.cache.CacheConstants;
import com.google.ie.common.cache.CacheHelper;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * A service implementation of the {@link ShardedCounterService}.
 * 
 * @author gmaurya
 * 
 */
@Service
public class ShardedCounterServiceImpl implements ShardedCounterService {

    private static Logger logger = Logger.getLogger(ShardedCounterServiceImpl.class);

    @Autowired
    private ShardedCounterDao shardedCounterDao;

    @Autowired
    private IdeaService ideaService;

    public IdeaService getIdeaService() {
        return ideaService;
    }

    public void setIdeaService(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    private static final int CACHEDELAY = 120;

    @Override
    public ShardedCounter getMergedShardedCounter(String parentKey) {
        logger.debug("Getting Merged sharded counter for parent key : " + parentKey);
        if (parentKey == null)
            return null;

        Object obj = CacheHelper.getObject(CacheConstants.IDEA_SHARD_NAMESPACE, parentKey);
        ShardedCounter shardedCounter = null;

        if (obj != null && obj instanceof ShardedCounter) {
            shardedCounter = (ShardedCounter) obj;
        }

        // Retrieve merged sharded counter from datastore
        if (shardedCounter == null) {

            shardedCounter = new ShardedCounter(parentKey);
            List<ShardedCounter> shards = shardedCounterDao.getShardsByParentKey(parentKey);
            if (shards != null && !shards.isEmpty()) {
                for (ShardedCounter shard : shards) {
                    shardedCounter.setTotalPoint(shard.getTotalPoint()
                                    + shardedCounter.getTotalPoint());
                    shardedCounter.setNegativePoint(shard.getNegativePoint()
                                    + shardedCounter.getNegativePoint());
                    shardedCounter.setPositivePoint(shard.getPositivePoint()
                                    + shardedCounter.getPositivePoint());
                }
            }

            CacheHelper.putObject(CacheConstants.IDEA_SHARD_NAMESPACE, parentKey, shardedCounter,
                            CACHEDELAY);

        }
        return shardedCounter;
    }

    @Override
    public int getNegativePoint(String parentKey) {
        logger.debug("Getting negative point count from sharded counter for parent key : "
                        + parentKey);
        ShardedCounter shard = getMergedShardedCounter(parentKey);
        int value;
        if (shard == null)
            value = ServiceConstants.ZERO;
        else
            value = shard.getNegativePoint();

        return value;
    }

    @Override
    public int getPositivePoint(String parentKey) {
        logger.debug("Getting negative point count from sharded counter for parent key : "
                        + parentKey);
        ShardedCounter shard = getMergedShardedCounter(parentKey);
        int value;
        if (shard == null)
            value = ServiceConstants.ZERO;
        else
            value = shard.getPositivePoint();

        return value;
    }

    @Override
    public long getTotalPoint(String parentKey) {
        logger.debug("Getting total points from sharded counter for parent key : " + parentKey);
        ShardedCounter shard = getMergedShardedCounter(parentKey);
        long value;
        if (shard == null)
            value = ServiceConstants.ZERO;
        else
            value = shard.getTotalPoint();

        return value;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void incrementNegativePoints(String parentKey) {

        logger.debug("Incrementing negative point in sharded counter for parent key: "
                        + parentKey);
        if (parentKey == null) {
            logger.debug("Parent key value is null : ");
            return;
        }
        Random generator = new Random();
        int shardNum = generator.nextInt(DaoConstants.SHARDED_COUNTERS);
        List<ShardedCounter> shards = shardedCounterDao.getShardsByParentKeyAndShardNum(
                        parentKey, shardNum);
        ShardedCounter shard = null;
        ShardedCounter shardToUpdate = null;
        if (shards == null || shards.isEmpty()) {
            shardToUpdate = new ShardedCounter(parentKey);
            shardToUpdate.setNegativePoint(DaoConstants.ONE);
            shardToUpdate.setShardNumber(shardNum);
        } else {
            shard = shards.get(0);
            shardToUpdate = getShardCopy(shard);
            shardToUpdate.setNegativePoint(shardToUpdate.getNegativePoint() + DaoConstants.ONE);
        }
        shardedCounterDao.createOrUpdateShardedCounter(shardToUpdate);
        logger.debug("Negative point incremented in sharded counter for parent key : " + parentKey);

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void incrementPositivePoints(String parentKey) {
        logger.debug("Incrementing positive point in sharded counter for parent key: "
                        + parentKey);
        if (parentKey == null) {
            logger.debug("Parent key value is null : ");
            return;
        }
        Random generator = new Random();
        int shardNum = generator.nextInt(DaoConstants.SHARDED_COUNTERS);
        List<ShardedCounter> shards = shardedCounterDao.getShardsByParentKeyAndShardNum(
                        parentKey, shardNum);
        ShardedCounter shard = null;
        ShardedCounter shardToUpdate = null;
        if (shards == null || shards.isEmpty()) {
            shardToUpdate = new ShardedCounter(parentKey);
            shardToUpdate.setPositivePoint(DaoConstants.ONE);
            shardToUpdate.setShardNumber(shardNum);
        } else {
            shard = shards.get(0);
            shardToUpdate = getShardCopy(shard);
            shardToUpdate.setPositivePoint(shardToUpdate.getPositivePoint() + DaoConstants.ONE);
        }
        shardedCounterDao.createOrUpdateShardedCounter(shardToUpdate);
        logger.debug("Negative point incremented in sharded counter for parent key : " + parentKey);

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updateTotalPoints(String parentKey, long points) {
        logger.debug("updating total point in sharded counter for parent key: "
                        + parentKey + "with points " + points);
        if (parentKey == null) {
            logger.debug("Parent key value is null : ");
            return;
        }
        // Choose the shard randomly from the available shards.
        Random generator = new Random();
        int shardNum = generator.nextInt(DaoConstants.SHARDED_COUNTERS);
        List<ShardedCounter> shards = shardedCounterDao.getShardsByParentKeyAndShardNum(
                        parentKey, shardNum);
        ShardedCounter shard = null;
        ShardedCounter shardToUpdate = null;
        if (shards == null || shards.isEmpty()) {
            shardToUpdate = new ShardedCounter(parentKey);
            shardToUpdate.setTotalPoint(points);
            shardToUpdate.setShardNumber(shardNum);

        } else {
            shard = shards.get(0);
            shardToUpdate = getShardCopy(shard);
            shardToUpdate.setTotalPoint(shardToUpdate.getTotalPoint() + points);
        }

        shardedCounterDao.createOrUpdateShardedCounter(shardToUpdate);
        logger.debug("Total point updated in sharded counter for parent key : " + parentKey);

    }

    /**
     * @param points
     * @param shard
     * @return
     */
    private ShardedCounter getShardCopy(ShardedCounter shard) {
        ShardedCounter shardToUpdate;
        shardToUpdate = new ShardedCounter(shard.getParentKey());
        shardToUpdate.setKey(shard.getKey());
        shardToUpdate.setNegativePoint(shard.getNegativePoint());
        shardToUpdate.setShardNumber(shard.getShardNumber());
        shardToUpdate.setPositivePoint(shard.getPositivePoint());
        shardToUpdate.setTotalPoint(shard.getTotalPoint());

        return shardToUpdate;
    }

    /**
     * Getter for sharded counter dao.
     * 
     * @return ShardedCounterDao
     */
    public ShardedCounterDao getShardedCounterDao() {
        return shardedCounterDao;
    }

    /**
     * Setter for sharded counter dao.
     * 
     * @param shardedCounterDao ShardedCounterDao
     */
    public void setShardedCounterDao(ShardedCounterDao shardedCounterDao) {
        this.shardedCounterDao = shardedCounterDao;
    }

}

