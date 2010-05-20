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

import com.google.ie.business.domain.Audit;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.constants.IdeaExchangeErrorCodes;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Iterator;

/**
 * {@link Validator} implementation to validate {@link Audit} object.
 * 
 * @author asirohi
 * 
 */
@Component
public class AuditValidator implements Validator {
    private static Logger log = Logger.getLogger(AuditValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return Audit.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, IdeaExchangeConstants.USER_KEY,
                        IdeaExchangeErrorCodes.FIELD_REQUIRED,
                        IdeaExchangeConstants.Messages.REQUIRED_FIELD);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, IdeaExchangeConstants.ENTITY_KEY,
                        IdeaExchangeErrorCodes.FIELD_REQUIRED,
                        IdeaExchangeConstants.Messages.REQUIRED_FIELD);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, IdeaExchangeConstants.ACTION,
                        IdeaExchangeErrorCodes.FIELD_REQUIRED,
                        IdeaExchangeConstants.Messages.REQUIRED_FIELD);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, IdeaExchangeConstants.ENTITY_TYPE,
                        IdeaExchangeErrorCodes.FIELD_REQUIRED,
                        IdeaExchangeConstants.Messages.REQUIRED_FIELD);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, IdeaExchangeConstants.AUDIT_DATE,
                        IdeaExchangeErrorCodes.FIELD_REQUIRED,
                        IdeaExchangeConstants.Messages.REQUIRED_FIELD);

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

