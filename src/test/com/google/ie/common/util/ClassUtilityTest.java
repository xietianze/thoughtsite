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

