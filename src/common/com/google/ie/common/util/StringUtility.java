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

package com.google.ie.common.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gmaurya
 * 
 */
public class StringUtility {
    private static final int DATASTORE_ALLOWED_LENGTH_FOR_STRING = 500;

    /**
     * This method breaks a single String into a list of Strings. The string is
     * split after every 500 characters.
     * 
     * @param string The string to be split
     */
    public static final List<String> convertStringToList(String string) {
        int length = StringUtils.length(string);
        /* Calculate the number of times the string is to be split */
        int numOfSplits = length / DATASTORE_ALLOWED_LENGTH_FOR_STRING;
        if (length % DATASTORE_ALLOWED_LENGTH_FOR_STRING > 0) {
            numOfSplits += 1;
        }
        List<String> strings = null;
        if (numOfSplits > 0) {
            strings = new ArrayList<String>();
            int offset = 0;
            for (int i = 0; i < numOfSplits; i++) {
                strings.add(StringUtils.substring(string, offset, offset + DATASTORE_ALLOWED_LENGTH_FOR_STRING));
                offset += DATASTORE_ALLOWED_LENGTH_FOR_STRING;
            }
        }
        return strings;
    }

    /**
     * This method concatenates a list of strings into a single string which
     * are actually stored as list of string into database.
     * 
     * @param strings List of Strings
     */
    public static String convertListToString(List<String> strings) {
        StringBuilder content = new StringBuilder("");
        if (strings != null && strings.size() > 0) {
            for (String description : strings) {
                content.append(description);
            }
            return content.toString();
        }
        return null;
    }

}

