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

package com.google.ie.business.dao.impl;

/**
 * Contains the constants used in the data access objects
 * 
 * @author Sachneet
 */
public interface DaoConstants {
    String CATEGORIES = "categories";
    String ORDERING_ASCENDING = "asc";
    String ORDERING_DESCENDING = "desc";
    int ZERO = 0;

    /*
     * Constant for unindexd entity flag
     */
    int UNINDEXED = 0;
    // Idea listing constants.
    int USER_IDEAS_LIST_DEFAULT_SIZE = 10;
    String DEFAULT_IDEA_ORDERING_TYPE = ORDERING_DESCENDING;
    String IDEA_ORDERING_FIELD_PUBLISH_DATE = "publishDate";
    String IDEA_ORDERING_FIELD_LAST_UPDATE_DATE = "lastUpdated";
    String IDEA_ORDERING_FIELD_VOTE = "totalVotes";
    String DEFAULT_IDEA_ORDERING_FIELD = IDEA_ORDERING_FIELD_PUBLISH_DATE;
    String IDEA_ORDERING_FIELD_CREATED_ON = "";

    /*
     * Constants for TagDao
     */
    String TAG_FIELD_WEIGHTAGE = "weightage";
    // Default parameters for a fetch query on tags
    String DEFAULT_ORDER_BY_FIELD_FOR_TAG = "title";
    String DEFAULT_ORDER_TYPE_FOR_TAG = "asc";

    String IDEA_LISTING_TYPE_GET_USER_IDEAS = "getUserIdeas";
    String IDEA_LISTING_TYPE_GET_PUBLISHED_IDEAS = "getPublishedIdeas";
    int SHARDED_COUNTERS = 10;
    int ONE = 1;

}

