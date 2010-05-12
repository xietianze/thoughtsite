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

package com.google.ie.business.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.ie.business.domain.Tag;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.DatastoreTest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Test cases for TagDaoImpl class.
 * 
 * @author Charanjeet singh
 * 
 */
public class TagDaoImplTest extends DatastoreTest {
    TagDaoImpl tagDaoImpl = null;

    /**
     */
    @Before
    public void setUp() {
        super.setUp();
        if (tagDaoImpl == null) {
            tagDaoImpl = new TagDaoImpl();
            tagDaoImpl.setPersistenceManagerFactory(pmf);
        }

    }

    /**
     * Test method for
     * {@link com.google.ie.business.dao.impl.TagDaoImpl#saveTag(com.google.ie.business.domain.Tag)}
     * .
     */
    @Test
    public final void saveTag() {
        Tag expactedTag = new Tag();
        expactedTag.setTitle("TestTag");
        Tag actualTag = tagDaoImpl.saveTag(expactedTag);

        assertNotNull(actualTag);
        assertEquals(expactedTag.getTitle(), actualTag.getTitle());
    }

    @Test
    public final void getAllTags() {
        Tag expectedTag1 = new Tag();
        expectedTag1.setTitle("TestTag1");
        tagDaoImpl.saveTag(expectedTag1);
        Tag expectedTag2 = new Tag();
        expectedTag2.setTitle("TestTag2");
        tagDaoImpl.saveTag(expectedTag2);

        assertEquals(2, tagDaoImpl.getAllTags().size());
    }

    @Test
    public final void getTagsByKeys() {
        Tag tag = new Tag();
        tag.setTitle("testTag");

        Tag tag1 = new Tag();
        tag1.setTitle("testTag1");

        tag = tagDaoImpl.saveTag(tag);
        tag1 = tagDaoImpl.saveTag(tag1);

        Collection<String> keys = new ArrayList<String>();
        keys.add(tag.getKey());
        keys.add(tag1.getKey());

        assertNotNull(tagDaoImpl.getTagsByKeys(keys));
        assertEquals(tag.getKey(),
                        tagDaoImpl.getTagsByKeys(keys).get(0).getKey());
        assertEquals(2,
                        tagDaoImpl.getTagsByKeys(keys).size());
    }

    @Test
    public void getTagsForTagCloud() {
        Tag tag = new Tag();
        tag.setTitle("finance");
        tagDaoImpl.saveTag(tag);
        RetrievalInfo info = new RetrievalInfo();
        info.setNoOfRecords(10);
        List<Tag> tagList = tagDaoImpl.getTagsForTagCloud(info);
        assertNotNull(tagList);
    }

    @Test
    public void getTagsWithSpecificStartString() {
        String expectedString = "ab";

        Tag tag = new Tag();
        tag.setTitle("abstract");
        tagDaoImpl.saveTag(tag);

        Tag tag2 = new Tag();
        tag2.setTitle("bargain");
        tagDaoImpl.saveTag(tag2);

        Tag tag3 = new Tag();
        tag3.setTitle("science");
        tagDaoImpl.saveTag(tag3);

        Tag tag4 = new Tag();
        tag4.setTitle("agriculture");
        tagDaoImpl.saveTag(tag4);

        RetrievalInfo info = new RetrievalInfo();
        info.setOrderType("asc");
        info.setOrderBy("title");
        info.setNoOfRecords(10);
        List<Tag> listOfTags = tagDaoImpl.getTagsWithSpecificStartString(expectedString, info
                        );

        assertNotNull(listOfTags);

        String actualString = listOfTags.get(0).getTitle();
        assertTrue(actualString.startsWith(expectedString));
    }

}

