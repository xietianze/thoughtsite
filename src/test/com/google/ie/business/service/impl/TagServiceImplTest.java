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

package com.google.ie.business.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.ie.business.dao.TagDao;
import com.google.ie.business.dao.impl.TagDaoImpl;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Tag;
import com.google.ie.business.domain.User;
import com.google.ie.common.cache.CacheHelper;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.ServiceTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test case for TagServiceImpl class
 * 
 * @author Charanjeet singh
 */
public class TagServiceImplTest extends ServiceTest {
    private static final String TAG = "tag";
    private static final String TAG_CLOUD = "tagCloud";
    private TagServiceImpl tagService;

    @Before
    public void setUp() {
        super.setUp();
        if (tagService == null)
            tagService = new TagServiceImpl();
        TagDao mockTagDao = mock(TagDaoImpl.class);
        tagService.setTagDao(mockTagDao);
        tagService.setIdeaService(mock(IdeaServiceImpl.class));
        tagService.setEntityIndexService(mock(EntityIndexServiceImpl.class));
    }

    @After
    public void tearDown() {
        super.setUp();
        tagService.setTagDao(null);
    }

    @Test
    public void saveTags_withNullTagString() {
        String tagString = null;
        assertNull(tagService.saveTags(tagString));
    }

    @Test
    public void parseTags() {
        String tagString = "tag1,tag2";
        List<Tag> tags = tagService.parseTags(tagString);
        assertNotNull(tags);
        assertEquals(2, tags.size());
        assertEquals("tag1", tags.get(0).getTitle());
        assertEquals("tag2", tags.get(1).getTitle());
    }

    @Test
    public void parseTags_checkTagLowerCase() {
        String tagString = "TAg1,TAG2";
        List<Tag> tags = tagService.parseTags(tagString);
        assertNotNull(tags);
        assertEquals(2, tags.size());
        assertEquals("tag1", tags.get(0).getTitle());
        assertEquals("tag2", tags.get(1).getTitle());
    }

    @Test
    public void parseTags_checkWhiteSpaceHandling() {
        String tagString = "TAg1, TAG2  TaG3 \n Tag4";
        List<Tag> tags = tagService.parseTags(tagString);
        assertNotNull(tags);
        assertEquals(4, tags.size());
        assertEquals("tag1", tags.get(0).getTitle());
        assertEquals("tag2", tags.get(1).getTitle());
        assertEquals("tag3", tags.get(2).getTitle());
        assertEquals("tag4", tags.get(3).getTitle());
    }

    @Test
    public void saveTags_withBlankTagString() {
        String tagString = null;
        assertNull(tagService.saveTags(tagString));
    }

    @Test
    public void getTagsByKeys_withNullKeys() {
        List<Tag> tagList = tagService.getTagsByKeys(null);
        assertNull(tagList);
    }

    @Test
    public void getTagsByKeys() {
        Tag expactedTag = new Tag();
        expactedTag.setKey("testKey");
        List<Tag> tagList = new ArrayList<Tag>();
        tagList.add(expactedTag);
        Collection<String> keys = new ArrayList<String>();
        keys.add("testKey");
        when(tagService.getTagDao().getTagsByKeys(keys)).thenReturn(tagList);
        assertNotNull(tagService.getTagsByKeys(keys));
        assertEquals("testKey", tagService.getTagsByKeys(keys).get(0).getKey());
    }

    @Test
    public void getTagsWithSpecificStartString() {
        Tag tag = new Tag();
        tag.setTitle("FINANCE");
        List<Tag> list = new ArrayList<Tag>();
        list.add(tag);
        RetrievalInfo info = new RetrievalInfo();
        String startString = "fin";
        when(tagService.getTagDao().getTagsWithSpecificStartString(startString, info)).thenReturn(
                        list);
        assertNotNull(list);
        String titleOfTagRetrieved = (list.get(0)).getTitle().toLowerCase();
        assertTrue(titleOfTagRetrieved.startsWith(startString.toLowerCase()));
    }

    @Test
    public void getTagsForAutoSuggestion() {
        Tag tag = new Tag();
        tag.setTitle("Finance");
        List<Tag> list = new ArrayList<Tag>();
        list.add(tag);
        RetrievalInfo info = new RetrievalInfo();

        String startString = "fin";
        when(tagService.getTagDao().getTagsWithSpecificStartString(startString, info)).thenReturn(
                        list);
        
        assertNotNull(list);
        String titleOfTagRetrieved = (list.get(0)).getTitle().toLowerCase();
        assertTrue(titleOfTagRetrieved.startsWith(startString.toLowerCase()));
    }

    @Test
    public void getTagsForTagCloud_fromCache() {
        List<Tag> expectedList = createSampleTagList();
        CacheHelper.putObject(TAG, TAG_CLOUD,
                        (Serializable) expectedList);
        int expectedListSize = expectedList.size();
        RetrievalInfo info = new RetrievalInfo();
        /*
         * the when condition below is used so that if the data is not available
         * in cache,the call to datastore within the method being tested should
         * return null
         */
        when(tagService.getTagDao().getTagsForTagCloud(info))
                        .thenReturn(null);
        List<Tag> actualList = tagService.getTagsForTagCloud(info);
        assertNotNull(actualList);
        assertEquals(expectedListSize, actualList.size());

    }

    @Test
    public void getTagsForTagCloud_fromDatastore() {
        /* Clear the data from cache if exists */
        if (CacheHelper.containsObject(TAG, TAG_CLOUD)) {
            CacheHelper.deleteObject(TAG, TAG_CLOUD);
        }
        RetrievalInfo info = new RetrievalInfo();
        when(tagService.getTagDao().getTagsForTagCloud(info))
                        .thenReturn(createSampleTagList());
        List<Tag> tags = tagService.getTagsForTagCloud(info);
        assertNotNull(tags);
    }

    @Test
    public void incrementWeights() {
        List<Tag> tags = new ArrayList<Tag>();

        Tag tmpTag = new Tag();
        tmpTag.setTitle("TestTitle");
        tmpTag.setWeightage(1);
        tags.add(tmpTag);

        when(tagService.getTagDao().getTagByTitle(tmpTag.getTitle())).thenReturn(tmpTag);
        when(tagService.getTagDao().saveTag(tmpTag)).thenReturn(tmpTag);
        tagService.getTagDao().saveTag(tmpTag);
        List<Tag> tagList = tagService.incrementWeights("TestTitle");
        assertEquals(1, tagList.size());
        assertEquals(2, tagList.get(0).getWeightage());
    }

    @Test
    public void getMyTagCloud() {
        Set<String> setOfTagKeys = new HashSet<String>();
        List<Tag> expectedListOfTags = new ArrayList<Tag>();
        /* The user */
        User user = new User();
        user.setUserKey("sunny");
        /* First tag */
        Tag tag1 = new Tag();
        tag1.setTitle("Tag 1");
        tag1.setKey("Tag1key");
        /* Added to the expected tag list */
        expectedListOfTags.add(tag1);
        /* Second tag */
        Tag tag2 = new Tag();
        tag2.setTitle("Tag 2");
        tag2.setKey("Tag2key");
        /* Added to the expected tag list */
        expectedListOfTags.add(tag2);
        /* First idea of the user */
        Idea idea1 = new Idea();
        idea1.setCreatorKey("sunny");
        Set<String> set1 = new HashSet<String>();
        /* tag1 and tag2 associated with idea1 */
        set1.add(tag1.getKey());
        set1.add(tag2.getKey());
        idea1.setTagKeys(set1);
        /* Second idea of the user */
        Idea idea2 = new Idea();
        idea2.setCreatorKey("sunny");
        Set<String> set2 = new HashSet<String>();
        /* tag1 associated with idea2 */
        set2.add(tag1.getKey());
        idea2.setTagKeys(set2);
        /* Combined set of both tag sets */
        setOfTagKeys.addAll(set1);
        setOfTagKeys.addAll(set2);
        /* Create a list containing both ideas */
        List<Idea> ideaList = new ArrayList<Idea>();
        ideaList.add(idea1);
        ideaList.add(idea2);
        when(tagService.getIdeaService().getIdeasForUser(user, null)).thenReturn(ideaList);
        when(tagService.getTagDao().getTagsByKeys(setOfTagKeys)).thenReturn(expectedListOfTags);

        List<Tag> actualTagList = tagService.getMyTagCloud(user);
        assertNotNull(actualTagList);
        assertEquals(expectedListOfTags.size(), actualTagList.size());

    }

    @Test
    public void saveTag() {
        Tag expectedTag = new Tag();
        expectedTag.setTitle("Tag 1");
        expectedTag.setKey("Tag1key");
        when(tagService.getTagDao().saveTag(expectedTag)).thenReturn(expectedTag);
        Tag actualTag = tagService.saveTag(expectedTag);
        assertEquals(expectedTag, actualTag);
    }

    private List<Tag> createSampleTagList() {
        Tag tag;
        String[] titles = { "Finance Tag", "Medicine Tag", "Education Tag", "Finance second Tag",
                "Medicine second Tag", "Education second Tag" };
        List<Tag> tagList = new ArrayList<Tag>();
        int i = 0;
        for (String title : titles) {
            tag = new Tag();
            tag.setTitle(title);
            tag.setWeightage(i++);
            // tag = mockTagDao.saveTag(tag);
            tagList.add(tag);
        }
        return tagList;
    }

}

