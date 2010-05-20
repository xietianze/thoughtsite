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

import com.google.ie.common.util.StringUtility;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
@Searchable(alias = "Idea")
public class Idea implements Serializable {
    /** A unique identifier for the class */
    private static final long serialVersionUID = -2605556889201805705L;

    /** variable for storing idea status value */
    public static final String STATUS_OBJECTIONABLE = "Objectionable";
    public static final String STATUS_PUBLISHED = "Published";
    public static final String STATUS_SAVED = "Saved";
    public static final String STATUS_DELETED = "Deleted";
    public static final String STATUS_DUPLICATE = "Duplicate";

    /** variable for storing FlagType value */
    public static final String FLAG_TYPE_OBJECTIONABLE = "Objectionable";
    public static final String FLAG_TYPE_DUPLICATE = "Duplicate";

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    @SearchableId
    private String key;

    @Persistent
    @SearchableProperty
    private String title;

    @Persistent
    @SearchableProperty
    private List<String> description = new ArrayList<String>();

    @Persistent
    @SearchableProperty
    private String competition;

    @Persistent
    private boolean ideaRightsGivenUp = false;

    @Persistent
    private List<String> ideaSummary = new ArrayList<String>();

    @Persistent
    private boolean ipGivenUp = false;

    @Persistent
    @SearchableProperty
    private String monetization;

    @Persistent
    @SearchableProperty
    private String targetAudience;
    @Persistent
    private long totalNegativeVotes;
    /* This would hold the algeberic sum of votes */
    @Persistent
    private long totalVotes;
    @Persistent
    private long totalPositiveVotes;
    @Persistent
    private Date publishDate;
    @Persistent
    private Date lastUpdated;
    /**
     * Key of original idea
     */
    @Persistent
    private String originalIdeaKey;
    @Persistent
    private String status;
    /**
     * Set of tag keys
     */
    @Persistent
    private Set<String> tagKeys;

    @SearchableProperty
    @Persistent
    private String tags;

    @Persistent
    private String creatorKey;

    @Persistent
    @SearchableProperty
    private String ideaCategoryKey;

    @NotPersistent
    private String category;

    private Set<String> flagType;

    public Idea() {

    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the flagType
     */
    public Set<String> getFlagType() {
        if (flagType == null)
            flagType = new HashSet<String>();
        return flagType;
    }

    /**
     * @param flagType the flagType to set
     */
    public void setFlagType(Set<String> flagType) {
        this.flagType = flagType;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the competition
     */
    public String getCompetition() {
        return competition;
    }

    /**
     * @param competition the competition to set
     */
    public void setCompetition(String competition) {
        this.competition = competition;
    }

    /**
     * @return the ideaRightsGivenUp
     */
    public boolean isIdeaRightsGivenUp() {
        return ideaRightsGivenUp;
    }

    /**
     * @param ideaRightsGivenUp the ideaRightsGivenUp to set
     */
    public void setIdeaRightsGivenUp(boolean ideaRightsGivenUp) {
        this.ideaRightsGivenUp = ideaRightsGivenUp;
    }

    /**
     * @return the ipGivenUp
     */
    public boolean isIpGivenUp() {
        return ipGivenUp;
    }

    /**
     * @param ipGivenUp the ipGivenUp to set
     */
    public void setIpGivenUp(boolean ipGivenUp) {
        this.ipGivenUp = ipGivenUp;
    }

    /**
     * @return the monetization
     */
    public String getMonetization() {
        return monetization;
    }

    /**
     * @param monetization the monetization to set
     */
    public void setMonetization(String monetization) {
        this.monetization = monetization;
    }

    /**
     * @return the targetAudience
     */
    public String getTargetAudience() {
        return targetAudience;
    }

    /**
     * @param targetAudience the targetAudience to set
     */
    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
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
     * @return the lastUpdated
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @param lastUpdated the lastUpdated to set
     */
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * @return the originalIdeaKey
     */
    public String getOriginalIdeaKey() {
        return originalIdeaKey;
    }

    /**
     * @param originalIdeaKey the originalIdeaKey to set
     */
    public void setOriginalIdeaKey(String originalIdeaKey) {
        this.originalIdeaKey = originalIdeaKey;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

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
     * @param tags the tags to set
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * @return the tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = StringUtility.convertStringToList(description);
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return StringUtility.convertListToString(description);
    }

    /**
     * @param ideaSummary the ideaSummary to set
     */
    public void setIdeaSummary(String ideaSummary) {
        this.ideaSummary = StringUtility.convertStringToList(ideaSummary);
    }

    /**
     * @return the ideaSummary
     */
    public String getIdeaSummary() {
        return StringUtility.convertListToString(ideaSummary);
    }

    public void setIdeaCategoryKey(String ideaCategoryKey) {
        this.ideaCategoryKey = ideaCategoryKey;
    }

    public String getIdeaCategoryKey() {
        return ideaCategoryKey;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    /**
     * @param tagKeys the tagKeys to set
     */
    public void setTagKeys(Set<String> tagKeys) {
        this.tagKeys = tagKeys;
    }

    /**
     * @return the tagKeys
     */
    public Set<String> getTagKeys() {
        return tagKeys;
    }

    /**
     * @param totalVtes the totalVtes to set
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
     * @param publishDate the publishDate to set
     */
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    /**
     * @return the publishDate
     */
    public Date getPublishDate() {
        return publishDate;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Idea [category=" + category + ", competition=" + competition + ", creatorKey="
                        + creatorKey + ", description=" + description + ", flagType=" + flagType
                        + ", ideaCategoryKey=" + ideaCategoryKey + ", ideaRightsGivenUp="
                        + ideaRightsGivenUp + ", ideaSummary=" + ideaSummary + ", ipGivenUp="
                        + ipGivenUp + ", key=" + key + ", lastUpdated=" + lastUpdated
                        + ", monetization=" + monetization + ", originalIdeaKey=" + originalIdeaKey
                        + ", publishDate=" + publishDate + ", status=" + status + ", tagKeys="
                        + tagKeys + ", tags=" + tags + ", targetAudience=" + targetAudience
                        + ", title=" + title + ", totalNegativeVotes=" + totalNegativeVotes
                        + ", totalPositiveVotes=" + totalPositiveVotes + ", totalVotes="
                        + totalVotes + "]";
    }

}

