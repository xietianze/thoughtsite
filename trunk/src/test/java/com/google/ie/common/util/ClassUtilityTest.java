// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.common.util;

import com.google.ie.business.domain.Idea;
import com.google.ie.test.ServiceTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ClassUtilityTest class
 * 
 * @author gmaurya
 * 
 */
public class ClassUtilityTest extends ServiceTest {
    Idea idea = null;

    /**
     */
    @Before
    public void setUp() {
        super.setUp();

        idea = new Idea();
        idea.setKey("userKey");
        idea.setTitle("ideaTitle");
        idea.setDescription("decription");

    }

    /**
     * Test method for
     * {@link com.google.ie.common.util.ClassUtility#getObject(Object,String)} .
     */
    @Test
    public void getObject() {

        try {
            String actual = (String) ClassUtility.getObject(idea, "title");
            Assert.assertEquals(idea.getTitle(), actual);

        } catch (Exception e) {

            Assert.fail();
        }

    }

}
