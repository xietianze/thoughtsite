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

package com.google.ie.business.domain;

import java.io.Serializable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * ShardedCounter class for holding sharded value of idea vote and comment.
 * 
 * @author gmaurya
 * 
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class ShardedCounter implements Serializable {

    /** A unique identifier for the class */
    private static final long serialVersionUID = -2424299910086824104L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String key;

    @Persistent
    private String parentKey;

    @Persistent
    private int negativePoint = 0;
    @Persistent
    private int positivePoint = 0;
    @Persistent
    private long totalPoint = 0;

    @Persistent
    private int shardNumber;

    public int getNegativePoint() {
        return negativePoint;
    }

    public void setNegativePoint(int negativePoint) {
        this.negativePoint = negativePoint;
    }

    public int getPositivePoint() {
        return positivePoint;
    }

    public void setPositivePoint(int positivePoint) {
        this.positivePoint = positivePoint;
    }

    public long getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(long totalPoint) {
        this.totalPoint = totalPoint;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    /**
     * Construct Sharded Counter entity identified by counterName
     */
    public ShardedCounter(String parentKey) {
        this.parentKey = parentKey;
        this.negativePoint = 0;
        this.positivePoint = 0;
        this.totalPoint = 0;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getShardNumber() {
        return shardNumber;
    }

    public void setShardNumber(int shardNumber) {
        this.shardNumber = shardNumber;
    }

    public void incrementNegativePoint(int amount) {
        this.negativePoint = this.negativePoint + amount;
    }

    public void incrementPositivePoint(int amount) {
        this.positivePoint = this.positivePoint + amount;
    }

    public void incrementTotalPoint(int amount) {
        this.totalPoint = this.totalPoint + amount;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ShardedCounter [key=" + key + ", negativePoint=" + negativePoint + ", parentKey="
                        + parentKey + ", positivePoint=" + positivePoint + ", shardNumber="
                        + shardNumber + ", totalPoint=" + totalPoint + "]";
    }
}

