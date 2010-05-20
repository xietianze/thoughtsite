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

package com.google.ie.business.service;

/**
 * An interface for service layer constants.
 * 
 */
public interface ServiceConstants {
    int ZERO = 0;
    int ONE = 1;
    int MINUSONE = -1;
    int HUNDRED = 100;
    int FIFTY = 50;
    String AUDIT_ACTION_TYPE_SAVE_IDEA = "SAVE_IDEA";
    String AUDIT_ACTION_TYPE_PUBLISH_IDEA = "PUBLISH_IDEA";
    String AUDIT_ACTION_TYPE_SAVE_COMMENT = "SAVE_COMMENT";
    String AUDIT_ACTION_TYPE_CREATE_PROJECT = "CREATE_PROJECT";
    /* Default parameters for a fetch query on tags */
    String DEFAULT_ORDER_BY_FIELD_FOR_TAG = "title";
    String DEFAULT_ORDER_TYPE_FOR_TAG = "asc";
    long DEFAULT_START_INDEX_FOR_TAG = 0;
    // Default value for list of tags starting with a specific string.
    long DEFAULT_NO_OF_RECORDS_FOR_TAG_LIST = 1000;
    // Default value for auto suggestion for tags and tag cloud.
    long DEFAULT_NO_OF_RECORDS_FOR_TAG = 50;

    String SAVE = "save";
    String ORDERING_ASCENDING = "asc";
    String ORDERING_DESCENDING = "desc";
    // Idea listing constants.
    int USER_IDEAS_LIST_DEFAULT_SIZE = 10;
    int IDEAS_LIST_DEFAULT_SIZE = 10;
    int USERS_LIST_DEFAULT_SIZE = 20;
    String DEFAULT_USER_ORDERING_TYPE = ORDERING_DESCENDING;
    String DEFAULT_IDEA_ORDERING_TYPE = ORDERING_DESCENDING;
    String ORDERING_TYPE_FOR_RECENT_IDEAS = ORDERING_DESCENDING;

    String USER_ORDERING_FIELD = "displayName";
    String IDEA_ORDERING_FIELD_PUBLISH_DATE = "publishDate";
    String IDEA_ORDERING_FIELD_LAST_UPDATE_DATE = "lastUpdated";
    String IDEA_ORDERING_FIELD_VOTE = "totalVotes";
    String IDEA_ORDERING_FIELD_TOTAL_POSITIVE_VOTES = "totalPositiveVotes";
    String DEFAULT_IDEA_ORDERING_FIELD = IDEA_ORDERING_FIELD_PUBLISH_DATE;
    String IDEA_ORDERING_FIELD_CREATED_ON = "";
    // Project listing constant.
    int PROJECT_LIST_DEFAULT_SIZE = 10;
    String PROJECT_DEFAULT_ORDERING_TYPE = ORDERING_DESCENDING;
    String PROJECT_ORDERING_FIELD_UPDATED_ON = "updatedOn";
    String DEFAULT_PROJECT_ORDERING_FIELD = PROJECT_ORDERING_FIELD_UPDATED_ON;
    // Idea comment listing default constants.
    int IDEA_COMMENT_LIST_DEFAULT_SIZE = 20;
    String IDEA_COMMENT_ORDERING_FIELD_CREATED_ON = "createdOn";
    String DEFAULT_IDEA_COMMENT_ORDERING_TYPE = ORDERING_DESCENDING;
    String DEFAULT_IDEA_COMMENT_ORDERING_FIELD = IDEA_COMMENT_ORDERING_FIELD_CREATED_ON;
    String ALL = "all";
    String IDEA = "Idea";
    String PROJECT = "Project";
    String COMMENT = "Comment";
}

