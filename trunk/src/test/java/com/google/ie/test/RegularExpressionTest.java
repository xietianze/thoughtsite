// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Test class for testing regular expressions used in the system
 * 
 * @author Sachneet
 * 
 */
public class RegularExpressionTest extends ServiceTest {
    private SpelExpressionParser expressionParser;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        expressionParser = new SpelExpressionParser();
    }

    @Test
    public void expressionForTagTitle() {
        /*
         * Start with a character.
         * May contain alphabets in any case,numbers,underscore(_).
         * Should have a minimum length of 3 and maximum of 20.
         */
        String tagTitleRegex = "[a-zA-Z][\\w]{2,19}";
        String validTitle = "hello555";
        boolean valid;
        valid = expressionParser.parseExpression(
                        "'" + validTitle + "' matches '" + tagTitleRegex + "'").getValue(
                        Boolean.class);
        assertTrue(valid);

        /* Not starting with a character */
        String invalidTitle = "5hello555";
        valid = expressionParser.parseExpression(
                        "'" + invalidTitle + "' matches '" + tagTitleRegex + "'").getValue(
                        Boolean.class);
        assertFalse(valid);

        /* Less than minimum length of 3 */
        invalidTitle = "he";
        valid = expressionParser.parseExpression(
                        "'" + invalidTitle + "' matches '" + tagTitleRegex + "'").getValue(
                        Boolean.class);
        assertFalse(valid);

        /* Greater than maximum length of 20 */
        invalidTitle = "h1234567891011121314151617181920";
        valid = expressionParser.parseExpression(
                        "'" + invalidTitle + "' matches '" + tagTitleRegex + "'").getValue(
                        Boolean.class);
        assertFalse(valid);

        /* Contains chracters not allowed */
        invalidTitle = ">>((**&&%%";
        valid = expressionParser.parseExpression(
                        "'" + invalidTitle + "' matches '" + tagTitleRegex + "'").getValue(
                        Boolean.class);
        assertFalse(valid);
    }
}
