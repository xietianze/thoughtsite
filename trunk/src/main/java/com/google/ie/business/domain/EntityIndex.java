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

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class EntityIndex {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private int indexed;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.parent-pk", value = "true")
    private Key parentKey;

    /**
     * Construct ideaIndexStatus entity whose parent idea identified by
     * ideaKey
     * 
     * @param ideaKey
     */
    public EntityIndex(String parentKey) {
        this.parentKey = KeyFactory.stringToKey(parentKey);
    }

    public EntityIndex() {

    }

    public int getIndexed() {
        return indexed;
    }

    public void setIndexed(int indexed) {
        this.indexed = indexed;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key getParentKey() {
        return parentKey;
    }

    public void setParentKey(Key parentKey) {
        this.parentKey = parentKey;
    }
}

