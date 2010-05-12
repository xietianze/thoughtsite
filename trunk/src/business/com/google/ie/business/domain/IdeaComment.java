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

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableProperty;

import java.io.Serializable;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * 
 * @author asirohi
 * 
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
@Searchable(alias = "IdeaComment")
public class IdeaComment extends Comment implements Serializable {
    public static final String STATUS_SAVED = "Saved";
    /** A unique identifier for the class */
    private static final long serialVersionUID = 8728151976934031986L;

    @Persistent
    private long totalNegativeVotes;

    @Persistent
    private long totalVotes;

    @Persistent
    private long totalPositiveVotes;

    @Persistent
    @SearchableProperty
    private String ideaKey;

    public IdeaComment() {

    }

    /**
     * @return the totalNegativeVotes
     */
    public long getTotalNegativeVotes() {
        return totalNegativeVotes;
    }

    /**
     * @param totalNegativeVotes the totalNegativeVotes to set
     */
    public void setTotalNegativeVotes(long totalNegativeVotes) {
        this.totalNegativeVotes = totalNegativeVotes;
    }

    /**
     * @return the totalPositiveVotes
     */
    public long getTotalPositiveVotes() {
        return totalPositiveVotes;
    }

    /**
     * @param totalPositiveVotes the totalPositiveVotes to set
     */
    public void setTotalPositiveVotes(long totalPositiveVotes) {
        this.totalPositiveVotes = totalPositiveVotes;
    }

    /**
     * @param totalVotes the totalVotes to set
     */
    public void setTotalVotes(long totalVotes) {
        this.totalVotes = totalVotes;
    }

    /**
     * @return the totalVotes
     */
    public long getTotalVotes() {
        return totalVotes;
    }

    /**
     * @return the ideaKey
     */
    public String getIdeaKey() {
        return ideaKey;
    }

    /**
     * @param ideaKey the ideaKey to set
     */
    public void setIdeaKey(String ideaKey) {
        this.ideaKey = ideaKey;
    }
}

