// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.common.util;

import static org.junit.Assert.assertEquals;

import com.google.ie.business.domain.IdeaCategory;
import com.google.ie.test.ServiceTest;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

/**
 * Test class for GsonUtility class
 * 
 * @author ssbains
 * 
 */
public class GsonUtilityTest extends ServiceTest {

    /**
     */
    @Before
    public void setUp() {
        super.setUp();
    }

    /**
     * Test method for
     * {@link com.google.ie.common.util.GsonUtility#convertToJson(java.lang.Object)}
     * .
     */
    @Test
    public void convertToJson() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("Education", "key1");
        hashMap.put("Sports", "key123");

        String expected = "{" + "\"Education\":\"key1\",\"Sports\":\"key123\"" + "}";
        String actual = GsonUtility.convertToJson(hashMap);

        assertEquals(expected, actual);
    }

    @Test
    /**
     * Test method for
     * {@link com.google.ie.common.util.GsonUtility#convertFromJson(java.lang.String,Class<T>)}
     * .
     */
    public void convertFromJson() {
        IdeaCategory expectedCategory = new IdeaCategory();
        expectedCategory.setKey("jsonTestCategory");
        expectedCategory.setName("testGsonUtility");
        /* Convert to json */
        String jsonString = GsonUtility.convertToJson(expectedCategory);
        /* Convert back from json */
        IdeaCategory actualCategory = GsonUtility.convertFromJson(jsonString, IdeaCategory.class);
        assertEquals(expectedCategory.getKey(), actualCategory.getKey());
    }

}
