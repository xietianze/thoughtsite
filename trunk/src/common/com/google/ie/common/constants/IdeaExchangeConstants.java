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

package com.google.ie.common.constants;

/**
 * Contains the constants used in common components of IdeaExchange.
 * 
 * @author asirohi
 * 
 */
public interface IdeaExchangeConstants {

    /* Constants for AuditManager */
    String TASK_QUEUE = "audit-queue";
    String AUDIT_URL = "/audits/save";
    String AUDIT_TASK = "audit";
    String USER_KEY = "userKey";
    String ENTITY_KEY = "entityKey";
    String ACTION = "action";
    String ENTITY_TYPE = "entityType";
    String AUDIT_DATE = "auditDate";

    /* Constants for ObjectionableManager */
    String OBJECTIONABLE_QUEUE = "objectionable-queue";
    String OBJECTIONABLE_WORKER_QUEUE = "objectionable-worker-queue";
    String CHECK_OBJECTIONABLE_URL = "/objectionable/check";
    String CHECK_OBJECTIONABLE_WORKER_URL = "/objectionable/worker";
    String CHECK_OBJECTIONABLE_TASK = "checkObjectionable";
    String CHECK_OBJECTIONABLE_WORKER_TASK = "checkObjectionableWorker";

    String BACKSLASH = "/";
    String SPACE = " ";
    String STATUS = "status";

    /* Constants for Tag objects weight increment */
    String TAGS_WEIGHT_UPDATION_QUEUE = "tags-weight-updation-queue";
    String TAGS_WEIGHT_UPDATION_URL = "/tags/incrementweight";
    String TAGS_WEIGHT_DECREMENT_URL = "/tags/decrementweight";
    String COMMA_SEPARATED_TAG_STRING = "tagString";
    /* Email related constants */
    String INDEX_QUEUE = "index-queue";
    String INDEX_URL = "/indexer/indexentity";
    String INDEX_KEY = "indexKey";
    String APPENGINE_TASKRETRYCOUNT = "X-AppEngine-TaskRetryCount";

    /* Constant for Search utility */
    String PACKAGE_TO_INDEX = "com.google.ie.business.domain";
    String COLON = ":";
    int TAG_WEIGHT_INCREMENT_SIZE = 1;
    int DEFAULT_PAGE_SIZE = 10;
    int ONE = 1;
    int DEFAULT_OFFSET = 0;
    String ASTERISK = "*";
    String COMMA = ",";

    /* Interface containing messages for validators. */
    interface Messages {

        /* Default Messages for Validators */
        String REQUIRED_FIELD = "Required Field";

        /* Messages for IdeasExchangeException */
        String VOTING_NOT_ALLOWED = "You are not allowed to vote for this Idea/Comment";

        String REPEAT_VOTE_MESSAGE = "User is not allowed to vote on Idea/Comment" +
                        " for which he/she has already voted";

        String OWNER_VOTE_MESSAGE = "User is not allowed to vote on his own Idea/Comment";

        String INVALID_PUBLISH = "User is not allowed to save/publish a published idea.";

        String COMMENT_LENGTH_EXCEEDS = "Comment length is too large";
        String INVALID_IDEA_SUMMARY = "The user does not have the persmission to add the idea summary.";

        String FIELD_ALWAYS_TRUE = "Checkbox should be selected";

        String LENGTH_EXCEED_LIMIT_MESSAGE = "Field length is out of range";

        String PROJECT_CREATION_FAILURE_MESSAGE = "Can not create a project from idea, as user or idea key not found.";
        String PROJECT_CREATION_FAILURE_UNPUBLISHED_IDEA_MESSAGE = "Can not create a project from unpublished idea.";
        String PROJECT_DETAILS_EXCEPTION_MESSAGE = "Project key not found.";

        String TAG_TITLE_TOO_LONG = "The tags should not be more than 20 characters";
        String TAG_TITLE_MAY_CONTAIN_ONLY_WORDS = "The tags should start with a character and have a min length of 3 and max of 20 characters and should contain only alphabets ,numbers or underscores.";
        String INVALID_USER = "Invalid user";
    }

    /* Reputation points constants */
    int REPUTATION_POINTS_TITLE = 5;
    int REPUTATION_POINTS_DESCRIPTION = 5;
    int REPUTATION_POINTS_MONETIZATION = 5;
    int REPUTATION_POINTS_COMPETITION = 5;
    int REPUTATION_POINTS_TARGET_AUDIENCE = 5;

    int REPUTATION_POINTS_COMMENT_POST = 5;
    int ZERO = 0;

    /* message related to flagging and duplicate marking */
    String SUCCESS = "success";
    String FAIL = "fail";
    String IDEA_ALLREADY_FLAGED = "Idea allready flaged";
    String IDEA_ALLREADY_MARKED_DUPLICATE = "Idea allready marked as dublicate.";
    String IDEA_COMMENT_ALLREADY_FLAGED = "Comment allready flaged";
    String PROJETC_COMMENT_ALLREADY_FLAGED = "Comment allready flaged";

}

