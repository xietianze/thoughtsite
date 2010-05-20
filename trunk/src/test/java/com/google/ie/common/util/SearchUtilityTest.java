// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.common.util;

import static org.junit.Assert.assertEquals;

import com.google.ie.business.domain.BadWord;
import com.google.ie.business.domain.Idea;
import com.google.ie.dto.SearchResult;
import com.google.ie.test.ServiceTest;

import org.compass.annotations.Searchable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SearchUtilityTest class
 * 
 * @author gmaurya
 * 
 */
public class SearchUtilityTest extends ServiceTest {
    BadWord badWord1 = null;
    BadWord badWord2 = null;
    BadWord badWord3 = null;
    Idea idea = null;

    /**
     */
    @Before
    public void setUp() {
        super.setUp();

        badWord1 = new BadWord();
        badWord1.setKey("key1");
        badWord1.setWord("Test");
        badWord2 = new BadWord();
        badWord2.setKey("key2");
        badWord2.setWord("Sing");
        badWord3 = new BadWord();
        badWord3.setKey("key3");
        badWord3.setWord("room");
        idea = new Idea();
        idea.setKey("userKey");
        idea.setTitle("ideaTitle");
        idea.setDescription("decription");

    }

    /**
     * Test method for
     * {@link com.google.ie.common.util.SearchUtility#search(String,boolean,boolean,String)}
     * .
     */
    @Test
    public void indexEntity() {
        try {
            SearchUtility.indexEntity(badWord1);
            SearchUtility.indexEntity(badWord2);
            SearchUtility.indexEntity(badWord3);
            SearchUtility.indexEntity(idea);

        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Test method for
     * {@link com.google.ie.common.util.SearchUtility#search(String,boolean,boolean,String)}
     * .
     */
    @Test
    public void checkObjectionableUsingSearch() {

        int expected = 3;

        SearchResult result = SearchUtility.search("testing singed rooms", true, true, "word", 0,
                        1,
                        "BadWord");
        int actual = result.getTotalCount();
        assertEquals(expected, actual);

    }

    /**
     * Test method for
     * {@link com.google.ie.common.util.SearchUtility#search(String,Class)} .
     */
    @Test
    public void searchTestingForOpenSearch() {

        int expected = 1;

        SearchResult result = SearchUtility.search("ideaTitle", Idea.class, 0, 1);
        int actual = result.getTotalCount();
        assertEquals(expected, actual);

    }

    /**
     * Test method for
     * {@link com.google.ie.common.util.SearchUtility#deleteIndex(Searchable)} .
     */
    @Test
    public void deleteIndex() {

        boolean expected = true;
        boolean actual = SearchUtility.deleteEntityIndex(badWord1);
        assertEquals(expected, actual);

    }
}
