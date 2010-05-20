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
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Transient;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
@Searchable(alias = "Comment")
public abstract class Comment implements Serializable {

    /** A unique identifier for the class */
    private static final long serialVersionUID = -7116365690016325012L;
    @NotPersistent
    public static final String STATUS_OBJECTIONABLE = "Objectionable";
    @NotPersistent
    public static final String STATUS_SAVED = "Saved";
    @NotPersistent
    public static final String STATUS_FLAGGED = "Flagged";
    @NotPersistent
    public static final String FIELD_NAME_TEXT = "text";

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    @SearchableId
    private String key;

    @Persistent
    @SearchableProperty
    private List<String> text = new ArrayList<String>();
    @Persistent
    private String status;
    @Persistent
    private Date createdOn;

    @Persistent
    private String creatorKey;
    @Transient
    private String commentTextAsString;

    /**
     * @return the commentTextAsString
     */
    public String getCommentTextAsString() {
        return commentTextAsString;
    }

    /**
     * @param commentTextAsString the commentTextAsString to set
     */
    public void setCommentTextAsString(String commentTextAsString) {
        this.commentTextAsString = commentTextAsString;
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
     * @return the text
     */
    public String getText() {
        return convertListToString(text);
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = StringUtility.convertStringToList(text);
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
     * This method concatenates a list of strings into a single string which
     * are actually stored as list of string into database.
     * 
     * @param strings List of Strings
     */
    private String convertListToString(List<String> strings) {
        StringBuilder content = new StringBuilder("");
        if (strings != null && strings.size() > 0) {
            for (String description : strings) {
                content.append(description);
            }
            return content.toString();
        }
        return null;
    }

}

