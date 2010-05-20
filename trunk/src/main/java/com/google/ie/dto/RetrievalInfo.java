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

package com.google.ie.dto;

/**
 * A Class representing the auxiliary information to be passed to idea exchange
 * services as a parameter.
 * 
 * @author Charanjeet singh
 */
public class RetrievalInfo {
    /** Index of record from where to fetch the objects.Default is 0 */
    private long startIndex = 0;
    /** Total number of objects to be fetched.Default is 10 */
    private long noOfRecords = 10;
    /** Ordering field of records */
    private String orderBy = null;
    /** Ordering type "asc" or "desc" */
    private String orderType = null;

    public RetrievalInfo() {
    }

    public RetrievalInfo(long startindex, long noOfRecords, String orderOn, String orderBy) {
        this.startIndex = startindex;
        this.noOfRecords = noOfRecords;
        this.orderBy = orderOn;
        this.orderType = orderBy;
    }

    /**
     * @return the startIndex
     */
    public long getStartIndex() {
        return startIndex;
    }

    /**
     * @param startIndex the startIndex to set
     */
    public void setStartIndex(long startindex) {
        this.startIndex = startindex;
    }

    /**
     * @return the noOfRecords
     */
    public long getNoOfRecords() {
        return noOfRecords;
    }

    /**
     * @param noOfRecords the noOfRecords to set
     */
    public void setNoOfRecords(long noOfRecords) {
        this.noOfRecords = noOfRecords;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderType() {
        return orderType;
    }
}

