// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.business.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.ie.common.util.StringUtility;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for Idea. Tests for the split and join methods
 * 
 * @author abraina
 * 
 */
public class IdeaTest {

    @Test
    public void convertStringToList_NullValue() {
        assertNull(StringUtility.convertStringToList(null));
    }

    @Test
    public void convertStringToList() {
        String veryLongString = getTestString();
        List<String> splitStrings = StringUtility.convertStringToList(veryLongString);

        assertNotNull(splitStrings);
        assertEquals("Number of split strings is 4", 4, splitStrings.size());

        // Check total string length
        int totalStringLength = 0;
        String totalString = "";
        for (String string : splitStrings) {
            totalStringLength += string.length();
            totalString += string;
        }

        assertEquals("Total number of characters is ", veryLongString.length(), totalStringLength);
        assertEquals("Total number of characters is ", veryLongString, totalString);
    }

    @Test
    public void convertListToString() {
        List<String> strings = new ArrayList<String>();
        strings.add("This ");
        strings.add("string ");
        strings.add("is ");
        strings.add("one");

        String fullString = StringUtility.convertListToString(strings);
        assertNotNull(fullString);
        assertEquals("Strings should match", fullString, "This string is one");
    }

    @Test
    public void convertListToString_NullValue() {
        assertNull(StringUtility.convertListToString(null));
    }

    /**
     * @return
     */
    private String getTestString() {
        // Taken from Wikipedia article:
        // http://en.wikipedia.org/wiki/The_Dark_Side_of_the_Moon
        return "The Dark Side of the Moon is the sixth studio album by English"
                        +
                        " progressive rock group Pink Floyd. Released in March 1973, the concept built"
                        +
                        " on the ideas that the band had explored in their live shows and previous "
                        +
                        "recordings, since the departure in 1968 of founding member, principal composer"
                        +
                        " and lyricist, Syd Barrett. The album's themes include conflict, greed, ageing,"
                        +
                        " and mental illness, the latter partly inspired by Barrett's deteriorating"
                        +
                        " mental state. The album was developed as part of a forthcoming tour of live "
                        +
                        "performances, and premièred several months before studio recording began. The"
                        +
                        " new material was further refined during the tour, and was recorded in two "
                        +
                        "sessions in 1972 and 1973 at Abbey Road Studios in London. The group used some"
                        +
                        " of the most advanced recording techniques of the time, including multitrack "
                        +
                        "recording and tape loops. Analogue synthesisers were given prominence in "
                        +
                        "several tracks, and a series of recorded interviews with staff and band "
                        +
                        "personnel provided the source material for a range of philosophical quotations"
                        +
                        " used throughout. Engineer Alan Parsons was directly responsible for some of "
                        +
                        "the most notable sonic aspects of the album, including the non-lexical "
                        +
                        "performance of Clare Torry.The Dark Side of the Moon was an immediate success,"
                        +
                        " topping the Billboard 200 for one week. It subsequently remained in the "
                        +
                        "charts for 741 weeks (fourteen years), longer than any other album in history."
                        +
                        " With an estimated 45 million units sold, it is Pink Floyd's most commercially"
                        +
                        " successful album and one of the best-selling albums worldwide. It has twice "
                        +
                        "been remastered and re-released, and has been covered by several other acts. "
                        +
                        "It spawned two singles, Money and Us and Them. In addition to its commercial "
                        +
                        "success, The Dark Side of the Moon is one of Pink Floyd's most popular albums"
                        +
                        " among fans and critics, and is frequently ranked as one of the greatest rock "
                        +
                        "albums of all time.";
    }
}
