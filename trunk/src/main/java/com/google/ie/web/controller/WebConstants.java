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

package com.google.ie.web.controller;

/**
 * An interface that stores constants for the web tier
 * 
 * @author abraina
 */
public interface WebConstants {
    String USER = "user";

    String SUCCESS = "success";
    String SEARCH = "search";
    String ERROR = "error";
    String ASTERISK = "*";
    String GLOBAL_MESSAGE = "globalMessage";
    String CAPTCHA = "captcha";
    String IDEA = "idea";
    String IDEA_CATEGORY = "ideaCategory";
    String TAG = "tag";
    String VIEW_STATUS = "viewStatus";
    String MIN_WEIGHT = "minWeight";
    String MAX_WEIGHT = "maxWeight";
    String CAPTCHA_MISMATCH = "Please provide valid captcha";
    String NO_RECORDS_FOUND = "No records found";
    String RECORD_NOT_FOUND = "Record not found";
    String NO_IDEAS_FOUND = "No ideas found";
    String DUPLICATE_IDEAS_FOUND = "Duplicate ideas found.";
    String SUMMARY_ADDED_SUCCESSFULLY = "Idea summary added successfully.";
    String NO_TAGS_FOUND = "No tags found";
    String EMAIL_REQUIRED = "emailRequired";
    int ZERO = 0;
    int ONE = 1;
    int HUNDRED = 100;
    int THOUSAND = 1000;
    int MINUS_ONE = -1;
    int PAGE_LIMIT = 50;
    int IDEA_POSITIVE_VOTE_POINTS = 10;
    int IDEA_NEGATIVE_VOTE_POINTS = 5;
    int COMMENT_POSITIVE_VOTE_POINTS = 10;
    int COMMENT_NEGATIVE_VOTE_POINTS = 5;
    String MY_IDEAS_COUNT = "count";
    String INBOX_ITEM_COUNT = "inboxCount";
    String TAGS = "tags";
    String WEIGHTS = "weights";
    String CATEGORIES = "categories";
    String IDEAS = "ideas";
    String ORIG_IDEA = "origIdea";
    String TOTAL_COUNT = "totalCount";
    String NOT_FOUND = "No object found";
    String VOTE = "vote";

    String RE_CAPTCHA_PUBLIC_KEY = "rcPublicKey";

    String VOTE_SUCCESSFUL = "Vote is Succesfully added";
    String VOTE_FAILED = "Vote is failed to add";
    String RECENT_IDEAS_MODEL_KEY = "recentIdeas";
    String POPULAR_IDEAS_MODEL_KEY = "popularIdeas";
    String RECENTLY_PICKED_IDEA_MODEL_KEY = "recentlyPickedIdeas";

    String COMMENTS = "comments";
    String PROJECT_COMMENT = "projComment";
    String IDEA_COMMENT = "ideaComment";
    String NO_COMMENTS_FOUND = "No Comments found";

    String COMMENT_SUCCESSFULL = "Comment is successfully added";
    String ALL_IDEAS_SEARCH_STRING = "title:[* TO *]";
    String IDEA_DETAIL = "ideaDetail";
    String IDEA_TITLE = "ideaTitle";
    String COMMENT_KEY_NULL = "Comment key is null or empty";

    String VALIDATION_ERROR = "Input data is invalid";

    String COMMENT_FAILED = "Comment failed to add";
    // Number of duplicate ideas to be shown at the time of publishing
    int DUPLICATE_IDEAS_SHOWN = 3;
    String PREVIOUS = "previous";
    String NEXT = "next";
    String PAGING = "paging";
    String PROJECTS = "projects";
    String USERS = "users";
    String INBOX_ITEMS = "inboxItems";
    String IDEA_AND_COMMENTS = "ideaAndComments";
    String PROJECT_DETAIL = "project";
    String FLAG = "flag";
    String DUPLICATE = "duplicate";
    String DUPLICATE_IDEAS = "duplicateIdeas";

    String IDEA_FLAGGING_SUCCESSFULL = "Idea is flagged successfully and it's waiting for admin approval";
    String FLAGGING_FAILED = "Problem occurs while flagging";
    String PROJECT_COMMENT_ALREADY_FLAGGED = "Comment is already flagged.";

    String COMMENT_FLAGGING_SUCCESSFULL = "Comment is flagged successfully and it's waiting for admin approval";

    String IDEA_DUPLICATE_SUCCESS = "Idea is flagged duplicate successfully and it's waiting for admin approval";

    String IDEA_DUPLICATE_FAILED = "Problem occurs while marking idea as duplicate";

    String ADMIN_REQUESTS = "adminRequests";

    String AUDIT = "audit";

    String EMAIL_SUCCESS = "email";

    String EMAIL_ERROR = "email";

    String IS_DUPLICATE = "isDuplicate";

    String INVALID_USER = "User is not an admin user or not logged in.";
    String UN_AUTHORIZED_USER = "Either user credentials are wrong or the user has been banned by the site administrator.";
    String REFERER = "referer";

}

