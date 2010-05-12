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

public interface IdeaExchangeErrorCodes {

    String MAIL_ADDRESS_EXCETION = "mail.address.exception";
    String MAIL_MESSAGING_EXCETION = "mail.address.exception";
    String OWNER_VOTE_EXCEPTION = "owner.vote.exception";
    String REPEAT_VOTE_EXCEPTION = "repeat.vote.exception";
    /*
     * Error code to be used when user tries to save a published idea or publish
     * the published idea
     */
    String INVALID_PUBLISH = "idea.publish.exception";
    /*
     * Error code to be used when user key not found while retrieveing the
     * user's projects.
     */
    String USER_NOT_FOUND = "user.getdeveloper.exception";
    /*
     * Error code to be used when user tries to create a project from a idea.
     */
    String PROJECT_CREATION_FAILURE_EXCEPTION = "project.creation.failure.exception";
    String PROJECT_DETAILS_EXCEPTION = "project.details.exception";
    /*
     * Error code to be used when user tries to create a project from a
     * unpublished idea.
     */
    String PROJECT_CREATION_FAILURE_UNPUBLISHED_IDEA_EXCEPTION = "project.creation.failure.unpublished.idea.exception";
    /*
     * Error code for validation errors while adding the idea summary
     * to the idea.
     */
    String INVALID_IDEA_SUMMARY = "idea.summary.exception";
    /* Validator Error codes */

    String FIELD_REQUIRED = "field.required";
    String COMMENT_LENGTH_EXCEEDS = "comment.length.exceeds";
    String FIELD_ALWAYS_TRUE = "field.always.true";
    String LENGTH_EXCEED_LIMIT = "field.length.exceed.limit";
    String IDEA_NULL_EXCEPTION = "idea.null.exception";
    String COMMENT_NULL_EXCEPTION = "comment.null.exception";
    String PROJECT_NULL_EXCEPTION = "project.null.exception";
    String INVALID_CHARACTER = "character.invalid";

}

