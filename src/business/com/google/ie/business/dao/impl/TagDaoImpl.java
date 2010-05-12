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

import com.google.ie.business.dao.TagDao;
import com.google.ie.business.domain.Tag;
import com.google.ie.common.comparator.TagTitleComparator;
import com.google.ie.dto.RetrievalInfo;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Query;

/**
 * A JDO implementation object for TagDao.
 * 
 * @author Charanjeet singh
 */
public class TagDaoImpl extends BaseDaoImpl implements TagDao {

    public TagDaoImpl() {
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Tag saveTag(Tag tag) {
        Tag savedTag = getJdoTemplate().makePersistent(tag);
        return savedTag;
    }

    @Override
    public List<Tag> getAllTags() {
        List<Tag> tagList = null;
        Collection<Tag> tags = getJdoTemplate().find(Tag.class, null,
                        "title asc");
        tagList = (List<Tag>) tags;
        return tagList;
    }

    @Override
    public Tag getTagByTitle(String title) {
        Collection<Tag> tags = getJdoTemplate()
                        .find(Tag.class, "title == :title", null, title);
        if (tags == null) {
            return null;
        }
        Iterator<Tag> tagIterator = tags.iterator();
        if (tagIterator.hasNext()) {
            Tag tag = tagIterator.next();
            return tag;
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public List<Tag> getTagsByKeys(Collection<String> keys) {
        Collection<Tag> tags = null;
        tags = getJdoTemplate()
                        .find(Tag.class, ":keyList.contains(key)", null, keys, null);
        if (tags == null)
            return null;
        return new ArrayList<Tag>(tags);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Tag> getTagsWithSpecificStartString(String startString, RetrievalInfo retrievalInfo) {
        List<Tag> tagList = null;
        Query query = null;
        try {
            query = getJdoTemplate().getPersistenceManagerFactory().getPersistenceManager()
                            .newQuery(Tag.class);
            query.setFilter("title.startsWith(\"" + startString + "\") ");
            /* Set the ordering parameters */
            query.setOrdering(retrievalInfo.getOrderBy() + " " + retrievalInfo.getOrderType());
            /*
             * Add the start index to the number of records required since
             * internally the second argument is treated as the index up to
             * which the entities are to be fetched
             */
            query.setRange(retrievalInfo.getStartIndex(), retrievalInfo.getStartIndex()
                            + retrievalInfo.getNoOfRecords());
            Collection<Tag> collection = getJdoTemplate().find(query.toString());
            /* Detach the collection object returned as result */
            collection = getJdoTemplate().detachCopyAll(collection);
            /* Create a new list with the collection returned */
            tagList = new ArrayList<Tag>(collection);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
        return tagList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Tag> getTagsForTagCloud(RetrievalInfo retrievalInfo) {
        List<Tag> tagList = null;
        Query query = null;
        try {
            query = getJdoTemplate().getPersistenceManagerFactory().getPersistenceManager()
                            .newQuery(Tag.class);
            /*
             * Add the start index to the number of records required since
             * internally the second argument is treated as the index up to
             * which the entities are to be fetched
             */
            query.setRange(retrievalInfo.getStartIndex(), retrievalInfo.getStartIndex()
                            + retrievalInfo.getNoOfRecords());
            /* Fetch the tags associated with at least one idea */
            query.setFilter(Tag.FIELD_WEIGHTAGE + " > " + DaoConstants.ZERO);
            /* Set the ordering of the tags by title - alphabetically */
            query.setOrdering(Tag.FIELD_WEIGHTAGE + " " + DaoConstants.ORDERING_DESCENDING);
            Collection<Tag> collection = getJdoTemplate().find(query.toString());
            /* Create a new list with the collection returned */
            tagList = new ArrayList<Tag>(collection);
            /* Sort the list alphabetically */
            Collections.sort(tagList, TagTitleComparator.TAG_TITLE_COMPARATOR);

        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
        return tagList;
    }
}

