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

import com.google.appengine.api.datastore.Blob;
import com.google.ie.common.util.StringUtility;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Transient;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
@Searchable(alias = "Project")
public class Project implements Serializable {
    public static final String STATUS_CREATED = "Created";
    public static final String STATUS_DELETED = "Deleted";
    public static final String PROJECT_FIELD_CREATED_ON = "createdOn";
    /** A unique identifier for the class */
    private static final long serialVersionUID = 6752251754854682164L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    @SearchableId
    private String key;
    @Persistent
    @SearchableProperty
    private String name;
    @Persistent
    private String status;
    @Persistent
    private String creatorKey;
    @Persistent
    @SearchableProperty
    private List<String> description = new ArrayList<String>();
    @Persistent
    private String ideaKey;

    @Persistent
    private Date createdOn;
    @Persistent
    private Date updatedOn;
    @Persistent
    private Blob logo;
    @Transient
    private String descriptionAsString;

    /**
     * @return the descriptionAsString
     */
    public String getDescriptionAsString() {
        return descriptionAsString;
    }

    /**
     * @param descriptionAsString the descriptionAsString to set
     */
    public void setDescriptionAsString(String descriptionAsString) {
        this.descriptionAsString = descriptionAsString;
    }

    private byte[] img;

    /**
     * @return the img
     */
    public byte[] getImg() {
        if (logo != null)
            img = logo.getBytes();
        return img;
    }

    /**
     * @param img the img to set
     */
    public void setImg(byte[] img) {
        this.img = img;
    }

    public Blob getLogo() {
        return logo;
    }

    public void setLogo(Blob logo) {
        this.logo = logo;
    }

    public Project() {

    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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

    /**
     * @param updatedOn the updatedOn to set
     */
    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    /**
     * @return the updatedOn
     */
    public Date getUpdatedOn() {
        return updatedOn;
    }

    /**
     * @param creatorKey the creatorKey to set
     */
    public void setCreatorKey(String creatorKey) {
        this.creatorKey = creatorKey;
    }

    /**
     * @return the creatorKey
     */
    public String getCreatorKey() {
        return creatorKey;
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
}

