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
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class Developer implements Serializable {
    public static final String STATUS_JOIN_REQUEST = "New_Request";
    public static final String STATUS_REQUEST_ACCEPTED = "Request_Accepted";
    public static final String STATUS_REQUEST_ALLREADY_SENT = "Request_AllReady_Sent";

    /** A unique identifier for the class */
    private static final long serialVersionUID = -8721845963073021935L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String key;

    @Persistent
    private String name;

    @Persistent
    private String emailId;

    @Persistent
    private String userKey;
    @Persistent
    private String status;
    @Persistent
    private String projectKey;

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public Developer() {
        this.status = STATUS_JOIN_REQUEST;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Developer(String displayName, String emailId) {
        this.name = displayName;
        this.emailId = emailId;
        this.status = STATUS_JOIN_REQUEST;
    }

    public Developer(String displayName) {
        this.name = displayName;
        this.status = STATUS_JOIN_REQUEST;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

}

