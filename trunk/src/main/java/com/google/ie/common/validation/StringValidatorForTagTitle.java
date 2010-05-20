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

import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.constants.IdeaExchangeErrorCodes;

import org.apache.log4j.Logger;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * {@link Validator} implementation for validating title of tags associated with
 * an idea
 * 
 * @author Sachneet
 * 
 */
@Component("tagTitleValidator")
public class StringValidatorForTagTitle implements Validator {
    private static Logger log = Logger.getLogger(StringValidatorForTagTitle.class);
    private static final int ZERO = 0;
    private static final String TAGS = "tags";
    /*
     * The validation expression for tags.The expression means:
     * Start with a character.
     * May contain alphabets in any case,numbers,underscore(_).
     * Should have a minimum length of 3 and maximum of 20.
     */
    private static final String EXPRESSION = "[a-zA-Z][\\w]{2,19}";
    private SpelExpressionParser parser = new SpelExpressionParser();

    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    @Override
    public void validate(Object object, Errors errors) {
        String tagString = (String) object;
        /* In case multiple titles exist,split them and create an array */
        String[] tagTitles = tagString.trim().split("[\\s,]");
        for (String title : tagTitles) {
            if (title.trim().length() > ZERO) {
                boolean validTitle = parser.parseExpression(
                                "'" + title + "' matches '" + EXPRESSION + "'").getValue(
                                Boolean.class);
                /* If the title is not valid, register an error and return */
                if (!validTitle) {
                    errors
                                    .rejectValue(
                                                    TAGS,
                                                    IdeaExchangeErrorCodes.INVALID_CHARACTER,
                                                    IdeaExchangeConstants.Messages.TAG_TITLE_MAY_CONTAIN_ONLY_WORDS);
                    log.debug("The tag title '" + title + "' contains invalid characters");
                    return;
                }
            }

        }
    }
}

