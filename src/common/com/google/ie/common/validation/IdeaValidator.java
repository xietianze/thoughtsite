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

import com.google.ie.business.domain.Idea;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.constants.IdeaExchangeErrorCodes;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Iterator;

/**
 * {@link Validator} implementation for validating {@link Idea} object.
 * 
 * @author asirohi
 * 
 * 
 */
@Component("ideaValidator")
public class IdeaValidator implements Validator {

    private static Logger log = Logger.getLogger(IdeaValidator.class);
    /* Attribute name on which validation runs */
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String IDEA_RIGHTS_GIVEN_UP = "ideaRightsGivenUp";
    private static final String IP_GIVEN_UP = "ipGivenUp";
    private static final String IDEA_SUMMARY = "ideaSummary";
    private static final String MONETIZATION = "monetization";
    private static final String TARGET_AUDIENCE = "targetAudience";
    private static final int LENGTH_500 = 500;
    private static final int ZERO = 0;
    private static final int LENGTH_3000 = 3000;

    private static final String COMPETITION = "competition";
    @Autowired
    @Qualifier("tagTitleValidator")
    private Validator stringValidatorForTagTitle;

    @Override
    public boolean supports(Class<?> clazz) {
        return Idea.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Idea idea = (Idea) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, TITLE,
                        IdeaExchangeErrorCodes.FIELD_REQUIRED,
                        IdeaExchangeConstants.Messages.REQUIRED_FIELD);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, DESCRIPTION,
                        IdeaExchangeErrorCodes.FIELD_REQUIRED,
                        IdeaExchangeConstants.Messages.REQUIRED_FIELD);

        if (!idea.isIdeaRightsGivenUp()) {
            errors.rejectValue(IDEA_RIGHTS_GIVEN_UP,
                            IdeaExchangeErrorCodes.FIELD_ALWAYS_TRUE,
                            IdeaExchangeConstants.Messages.FIELD_ALWAYS_TRUE);
            log.warn("Validation Error : " + IDEA_RIGHTS_GIVEN_UP + " -: "
                            + IdeaExchangeConstants.Messages.FIELD_ALWAYS_TRUE);
        }
        if (!idea.isIpGivenUp()) {
            errors.rejectValue(IP_GIVEN_UP,
                            IdeaExchangeErrorCodes.FIELD_ALWAYS_TRUE,
                            IdeaExchangeConstants.Messages.FIELD_ALWAYS_TRUE);
            log.warn("Validation Error : " + IP_GIVEN_UP + " -: "
                            + IdeaExchangeConstants.Messages.FIELD_ALWAYS_TRUE);
        }
        if (!StringUtils.isBlank(idea.getTitle())
                        && idea.getTitle().trim().length() > LENGTH_500) {
            errors.rejectValue(TITLE,
                            IdeaExchangeErrorCodes.LENGTH_EXCEED_LIMIT,
                            IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
            log.warn("Validation Error : " + TITLE + " -: "
                            + IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
        }
        if (!StringUtils.isBlank(idea.getDescription())
                        && idea.getDescription().trim().length() > LENGTH_3000) {
            errors.rejectValue(DESCRIPTION,
                            IdeaExchangeErrorCodes.LENGTH_EXCEED_LIMIT,
                            IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
            log.warn("Validation Error : " + DESCRIPTION + " -: "
                            + IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
        }
        if (!StringUtils.isBlank(idea.getIdeaSummary())
                        && idea.getIdeaSummary().trim().length() > LENGTH_3000) {
            errors.rejectValue(IDEA_SUMMARY,
                            IdeaExchangeErrorCodes.LENGTH_EXCEED_LIMIT,
                            IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
            log.warn("Validation Error : " + IDEA_SUMMARY + " -: "
                            + IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
        }
        if (!StringUtils.isBlank(idea.getMonetization())
                        && idea.getMonetization().trim().length() > LENGTH_500) {
            errors.rejectValue(MONETIZATION,
                            IdeaExchangeErrorCodes.LENGTH_EXCEED_LIMIT,
                            IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
            log.warn("Validation Error : " + MONETIZATION + " -: "
                            + IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
        }
        if (!StringUtils.isBlank(idea.getTargetAudience())
                        && idea.getTargetAudience().trim().length() > LENGTH_500) {
            errors.rejectValue(TARGET_AUDIENCE,
                            IdeaExchangeErrorCodes.LENGTH_EXCEED_LIMIT,
                            IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
            log.warn("Validation Error : " + TARGET_AUDIENCE + " -: "
                            + IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
        }
        if (!StringUtils.isBlank(idea.getCompetition())
                        && idea.getCompetition().trim().length() > LENGTH_500) {
            errors.rejectValue(COMPETITION,
                            IdeaExchangeErrorCodes.LENGTH_EXCEED_LIMIT,
                            IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
            log.warn("Validation Error : " + COMPETITION + " -: "
                            + IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
        }
        if (idea.getTags() != null && idea.getTags().length() > ZERO) {
            ValidationUtils.invokeValidator(stringValidatorForTagTitle, idea.getTags(),
                            errors);

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

