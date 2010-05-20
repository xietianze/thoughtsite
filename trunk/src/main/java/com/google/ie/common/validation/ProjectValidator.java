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

import com.google.ie.business.domain.Developer;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.constants.IdeaExchangeErrorCodes;
import com.google.ie.dto.ProjectDetail;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.util.Iterator;
import java.util.List;

/**
 * {@link Validator} implementation for validating {@link ProjectDetail} object.
 * 
 * @author sbhatnagar
 * 
 * 
 */
@Component("projectValidator")
public class ProjectValidator implements Validator {

    private static Logger log = Logger.getLogger(ProjectValidator.class);
    /* Attribute name on which validation runs */
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String DEVELOPER_NAME = "name";
    private static final String DEVELOPER_EMAIL = "emailId";
    private static final int LENGTH_500 = 500;
    private static final int LENGTH_50 = 50;
    private static final int LENGTH_3000 = 3000;

    @Override
    public boolean supports(Class<?> clazz) {
        return ProjectDetail.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProjectDetail projectDetail = (ProjectDetail) target;
        if (StringUtils.isBlank(projectDetail.getProject().getName())) {
            errors.rejectValue(NAME,
                            IdeaExchangeErrorCodes.FIELD_REQUIRED,
                            IdeaExchangeConstants.Messages.REQUIRED_FIELD);
            log.warn("Validation Error : " + NAME + " -: "
                            + IdeaExchangeConstants.Messages.REQUIRED_FIELD);
        }
        if (!StringUtils.isBlank(projectDetail.getProject().getName())
                        && projectDetail.getProject().getName().trim().length() > LENGTH_500) {
            errors.rejectValue(NAME,
                            IdeaExchangeErrorCodes.LENGTH_EXCEED_LIMIT,
                            IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
            log.warn("Validation Error : " + NAME + " -: "
                            + IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
        }
        if (!StringUtils.isBlank(projectDetail.getProject().getDescription())
                        && projectDetail.getProject().getDescription().trim().length() > LENGTH_3000) {
            errors.rejectValue(DESCRIPTION,
                            IdeaExchangeErrorCodes.LENGTH_EXCEED_LIMIT,
                            IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
            log.warn("Validation Error : " + DESCRIPTION + " -: "
                            + IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
        }
        List<Developer> developers = projectDetail.getDevelopers();
        if (developers != null) {
            for (Developer developer : developers) {
                if (!StringUtils.isBlank(developer.getName())
                                && developer.getName().trim().length() > LENGTH_50) {
                    errors.rejectValue(DEVELOPER_NAME,
                                    IdeaExchangeErrorCodes.LENGTH_EXCEED_LIMIT,
                                    IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
                    log.warn("Validation Error : " + DEVELOPER_NAME + " -: "
                                    + IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
                }
                if (!StringUtils.isBlank(developer.getEmailId())
                                && developer.getEmailId().trim().length() > LENGTH_50) {
                    errors.reject(IdeaExchangeErrorCodes.LENGTH_EXCEED_LIMIT, DEVELOPER_EMAIL
                                    + ":" +
                                    IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
                    log.warn("Validation Error : " + DEVELOPER_EMAIL + " -: "
                                    + IdeaExchangeConstants.Messages.LENGTH_EXCEED_LIMIT_MESSAGE);
                }
            }
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

