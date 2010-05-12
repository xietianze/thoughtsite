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
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class Vote implements Serializable {

    /** A unique identifier for the class */
    private static final long serialVersionUID = 8590211958439336159L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String key;

    @Persistent
    private boolean positiveVote;

    @Persistent
    private long votePoints;

    @Persistent
    private Date votingDate;

    @Persistent
    private String creatorKey;

    /**
     * @return the creatorKey
     */
    public String getCreatorKey() {
        return creatorKey;
    }

    /**
     * @param creatorKey the creatorKey to set
     */
    public void setCreatorKey(String creatorKey) {
        this.creatorKey = creatorKey;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the positiveVote
     */
    public boolean isPositiveVote() {
        return positiveVote;
    }

    /**
     * @param positiveVote the positiveVote to set
     */
    public void setPositiveVote(boolean positiveVote) {
        this.positiveVote = positiveVote;
    }

    /**
     * @return the votePoints
     */
    public long getVotePoints() {
        return votePoints;
    }

    /**
     * @param votePoints the votePoints to set
     */
    public void setVotePoints(long votePoints) {
        this.votePoints = votePoints;
    }

    /**
     * @return the votingDate
     */
    public Date getVotingDate() {
        return votingDate;
    }

    /**
     * @param votingDate the votingDate to set
     */
    public void setVotingDate(Date votingDate) {
        this.votingDate = votingDate;
    }

}

