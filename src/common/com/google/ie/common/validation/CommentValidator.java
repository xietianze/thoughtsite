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

package com.google.ie.common.validation;

import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.ProjectComment;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.constants.IdeaExchangeErrorCodes;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Iterator;

/**
 * {@link Validator} implementation to validate {@link Comment} object.
 * 
 * @author asirohi
 * 
 */
@Component
public class CommentValidator implements Validator {

    private static Logger log = Logger.getLogger(CommentValidator.class);

    private static final String IDEA_KEY = "ideaKey";
    private static final String PROJECT_KEY = "projectKey";
    private static final String TEXT = "text";
    private static final int COMMENT_LENGTH = 3000;

    @Override
    public boolean supports(Class<?> clazz) {
        return IdeaComment.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {

        if (object.getClass().equals(IdeaComment.class)) {
            validateIdeaComments(object, errors);
        } else if (object.getClass().equals(ProjectComment.class)) {
            validateProjectComments(object, errors);
        }

    }

    public void validateIdeaComments(Object object, Errors errors) {
        IdeaComment ideaComment = (IdeaComment) object;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, IDEA_KEY,
                        IdeaExchangeConstants.Messages.REQUIRED_FIELD);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, TEXT,
                        IdeaExchangeConstants.Messages.REQUIRED_FIELD);
        if (StringUtils.length(ideaComment.getText()) > COMMENT_LENGTH) {
            errors.rejectValue(TEXT, IdeaExchangeErrorCodes.COMMENT_LENGTH_EXCEEDS,
                            IdeaExchangeConstants.Messages.COMMENT_LENGTH_EXCEEDS);
        }

        if (log.isDebugEnabled()) {
            if (errors.hasErrors()) {
                log.debug("Validator found " + errors.getErrorCount() + " errors");
                for (Iterator<FieldError> iterator = errors.getFieldErrors().iterator(); iterator
                                .hasNext();) {
                    FieldError fieldError = iterator.next();
                    log.debug("Error found in field: " + fieldError.getField() + " Message :"
                                    + fieldError.getDefaultMessage());
                }
            } else {
                log.debug("Validator found no errors");
            }

        }
    }

    public void validateProjectComments(Object object, Errors errors) {

        ProjectComment projectComment = (ProjectComment) object;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, PROJECT_KEY,
                        IdeaExchangeConstants.Messages.REQUIRED_FIELD);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, TEXT,
                        IdeaExchangeConstants.Messages.REQUIRED_FIELD);
        if (StringUtils.length(projectComment.getText()) > COMMENT_LENGTH) {
            errors.rejectValue(TEXT, IdeaExchangeErrorCodes.COMMENT_LENGTH_EXCEEDS,
                            IdeaExchangeConstants.Messages.COMMENT_LENGTH_EXCEEDS);
        }

        if (log.isDebugEnabled()) {
            if (errors.hasErrors()) {
                log.debug("Validator found " + errors.getErrorCount() + " errors");
                for (Iterator<FieldError> iterator = errors.getFieldErrors().iterator(); iterator
                                .hasNext();) {
                    FieldError fieldError = iterator.next();
                    log.debug("Error found in field: " + fieldError.getField() + " Message :"
                                    + fieldError.getDefaultMessage());
                }
            } else {
                log.debug("Validator found no errors");
            }

        }
    }
}

