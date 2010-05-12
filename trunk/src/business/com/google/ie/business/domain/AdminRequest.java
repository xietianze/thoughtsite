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
import java.util.Set;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Entity for the encapsulating admin user requests.
 * 
 * @author Anuj Sirohi.
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class AdminRequest implements Serializable {
    private static final long serialVersionUID = -1558527401635789838L;

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_APPROVED = "appproved";
    public static final String STATUS_REJECTED = "rejected";

    public static final String REQUEST_OBJECTIONABLE = "Objectionable";
    public static final String REQUEST_DUPLICATE = "Duplicate";
    public static final String REQUEST_BANNED = "Banned";
    public static final String REQUEST_ACTIVATE = "activate";
    public static final String REQUEST_DELETED = "Deleted";

    public static final String INFO_ORIGINAL_IDEA_KEY = "originalIdeaKey";
    public static final String INFO_SEPARATOR = "~~";

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String key;

    /** Duplicate,Flagged etc */
    @Persistent
    private String requestType;

    /** Idea or Comment key */
    @Persistent
    private String entityKey;

    /** Idea or Comment */
    @Persistent
    private String entityType;

    /** Title of the idea or first few words of comment */
    @Persistent
    private String entityTitle;

    /** user key of requester */
    @Persistent
    private String requesterkey;

    /** Email id of the requester */
    @Persistent
    private String requesterEmail;

    /**
     * Set of Strings where each string consist of key value pair separated by
     * the separator '~~'.For example "originalIdeaKey~~ideaKey" is a string in
     * the set.
     */
    @Persistent
    private Set<String> otherInfo;

    @Persistent
    private Date createdOn;

    /** Approved,Rejected or Pending */
    @Persistent
    private String status;

    /** Key of the admin user */
    @Persistent
    private String adminUserKey;

    /** Reason for approval or denial */
    @Persistent
    private String adminReason;

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
     * @return the adminUserKey
     */
    public String getAdminUserKey() {
        return adminUserKey;
    }

    /**
     * @param adminUserKey the adminUserKey to set
     */
    public void setAdminUserKey(String adminUserKey) {
        this.adminUserKey = adminUserKey;
    }

    /**
     * @return the otherInfo
     */
    public Set<String> getOtherInfo() {
        return otherInfo;
    }

    /**
     * @param otherInfo the otherInfo to set
     */
    public void setOtherInfo(Set<String> otherInfo) {
        this.otherInfo = otherInfo;
    }

    /**
     * @return the createdOn
     */
    public Date getCreatedOn() {
        return createdOn;
    }

    /**
     * @param createdOn the createdOn to set
     */
    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * @return the requesterEmail
     */
    public String getRequesterEmail() {
        return requesterEmail;
    }

    /**
     * @param requesterEmail the requesterEmail to set
     */
    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
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
     * @return the requestType
     */
    public String getRequestType() {
        return requestType;
    }

    /**
     * @param requestType the requestType to set
     */
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    /**
     * @return the entityKey
     */
    public String getEntityKey() {
        return entityKey;
    }

    /**
     * @param entityKey the entityKey to set
     */
    public void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
    }

    /**
     * @return the entityType
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @param entityType the entityType to set
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * @return the entityTitle
     */
    public String getEntityTitle() {
        return entityTitle;
    }

    /**
     * @param entityTitle the entityTitle to set
     */
    public void setEntityTitle(String entityTitle) {
        this.entityTitle = entityTitle;
    }

    /**
     * @return the requesterkey
     */
    public String getRequesterkey() {
        return requesterkey;
    }

    /**
     * @param requesterkey the requesterkey to set
     */
    public void setRequesterkey(String requesterkey) {
        this.requesterkey = requesterkey;
    }

    /**
     * @return the adminReason
     */
    public String getAdminReason() {
        return adminReason;
    }

    /**
     * @param adminReason the adminReason to set
     */
    public void setAdminReason(String adminReason) {
        this.adminReason = adminReason;
    }

}

