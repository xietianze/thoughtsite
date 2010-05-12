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

import com.google.ie.business.dao.TagDao;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Tag;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.EntityIndexService;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.business.service.TagService;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.common.cache.CacheConstants;
import com.google.ie.common.cache.CacheHelper;
import com.google.ie.common.comparator.TagTitleComparator;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.util.SearchUtility;
import com.google.ie.dto.RetrievalInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A service implementation of the TagService
 * 
 * @author Sachneet
 * 
 */
@Service
public class TagServiceImpl implements TagService {
    private static Logger log = Logger.getLogger(TagServiceImpl.class);
    @Autowired
    private AuditManager auditManager;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private IdeaService ideaService;
    @Autowired
    private EntityIndexService entityIndexService;

    public TagServiceImpl() {
    }

    @Override
    public List<Tag> getTagsByKeys(Collection<String> keys) {
        if (keys == null || keys.size() == 0)
            return null;
        return getTagDao().getTagsByKeys(keys);
    }

    @Override
    public List<Tag> saveTags(String tagString) {
        /* If not valid tag string, return null. */
        if (StringUtils.isEmpty(tagString)) {
            return null;
        }
        List<Tag> tagsToSave = new ArrayList<Tag>();
        List<Tag> existingTag = new ArrayList<Tag>();
        // Parse tags.
        List<Tag> tags = parseTags(tagString);
        // Prepare a list of tags to be saved.
        for (Tag tag : tags) {
            Tag tagObj = checkForDuplicate(tag);
            if (StringUtils.isBlank(tagObj.getKey()))
                tagsToSave.add(tagObj);
            else
                existingTag.add(tagObj);
        }
        if (tagsToSave.size() > 0) {
            List<Tag> tagList = saveTags(tagsToSave);
            if (tagList != null && tagList.size() > 0)
                tagList.addAll(existingTag);
            return tagList;
        }
        return existingTag;
    }

    @Override
    public boolean retagIdea(ArrayList<Tag> newTag, Idea idea, User user) {
        return true;
    }

    @Override
    public List<Tag> getTagsForAutoSuggestion(String startString, RetrievalInfo retrievalInfo) {
        if (startString != null && startString.length() > ServiceConstants.ZERO) {
            /*
             * Configure the RetrievalInfo object to be used to set the query
             * parameters
             */
            retrievalInfo = getRetrievalInfoSetToQueryParameters(retrievalInfo,
                            ServiceConstants.DEFAULT_NO_OF_RECORDS_FOR_TAG);
            List<Tag> tagList = tagDao.getTagsWithSpecificStartString(startString, retrievalInfo);
            return tagList;
        }
        return null;
    }

    @Override
    public List<Tag> getTagsWithSpecificStartString(String startString, RetrievalInfo retrievalInfo) {
        if (startString != null && startString.length() > ServiceConstants.ZERO) {
            /*
             * Configure the RetrievalInfo object to be used to set the query
             * parameters
             */
            retrievalInfo = getRetrievalInfoSetToQueryParameters(retrievalInfo,
                            ServiceConstants.DEFAULT_NO_OF_RECORDS_FOR_TAG_LIST);
            List<Tag> tagList = tagDao.getTagsWithSpecificStartString(startString, retrievalInfo);
            /* Remove the tags with zero weight */
            if (tagList.size() > ServiceConstants.ZERO) {
                removeTagWithZeroWeight(tagList);
            }
            return tagList;
        }
        return null;
    }

    @Override
    public List<Tag> removeTagWithZeroWeight(List<Tag> tagList) {
        Collection<Tag> tagsToBeRemoved = new ArrayList<Tag>();
        Iterator<Tag> iterator = tagList.iterator();
        Tag tag;
        /* Iterate the list and remove the tags with zero weight */
        while (iterator.hasNext()) {
            tag = iterator.next();
            if (tag.getWeightage() <= ServiceConstants.ZERO) {
                tagsToBeRemoved.add(tag);
            }
        }
        if (tagsToBeRemoved.size() > ServiceConstants.ZERO) {
            log.debug("Removing " + tagsToBeRemoved.size()
                            + " tags from the fetched list because they have ZERO weight");
            tagList.removeAll(tagsToBeRemoved);
        }
        return tagList;
    }

    @Override
    public List<Tag> getTagsForTagCloud(RetrievalInfo retrievalInfo) {
        /* First check the cache for the tag cloud data */
        List<Tag> tagList = getTagCloudDataFromCache();
        if (tagList != null && tagList.size() > ServiceConstants.ZERO) {
            return tagList;
        }
        /*
         * If not available in cache get the data from datastore.
         * Configure the RetrievalInfo object to be used to set the query
         * parameters.
         */
        retrievalInfo = getRetrievalInfoSetToQueryParameters(retrievalInfo,
                        ServiceConstants.DEFAULT_NO_OF_RECORDS_FOR_TAG);
        tagList = tagDao.getTagsForTagCloud(retrievalInfo);
        /*
         * If the taglist is not null and contains data ,add the list to the
         * cache
         */
        if (tagList != null && tagList.size() > ServiceConstants.ZERO) {
            CacheHelper.putObject(CacheConstants.TAG_NAMESPACE,
                            CacheConstants.TAG_CLOUD,
                            (Serializable) tagList, CacheConstants.TAG_CLOUD_DATA_EXPIRATION_DELAY);
        }
        return tagList;
    }

    @Override
    public Tag getTagByName(String tagName) {
        if (tagName != null && tagName.length() > ServiceConstants.ZERO) {
            Tag tag = tagDao.getTagByTitle(tagName);
            return tag;
        }
        return null;
    }

    @Override
    public List<Tag> getMyTagCloud(User user) {
        /*
         * Second argument is null so that idea service instantiates a new
         * RetrievalInfo object with default parameters
         */
        List<Idea> userIdeas = ideaService.getIdeasForUser(user, null);
        if (userIdeas != null && userIdeas.size() > ServiceConstants.ZERO) {
            Set<String> completeSetOfTagKeys = getSetOfTagKeys(userIdeas);
            List<Tag> myTags = getTagsByKeys(completeSetOfTagKeys);
            if (myTags != null && myTags.size() > ServiceConstants.ZERO) {
                myTags = removeTagWithZeroWeight(myTags);
                /* Sort the list alphabetically */
                Collections.sort(myTags, TagTitleComparator.TAG_TITLE_COMPARATOR);
            }
            return myTags;
        }
        return null;
    }

    @Override
    public List<Tag> incrementWeights(String tagsString) {
        log.debug("Tag string =" + tagsString);
        List<Tag> tagList = getTagsFromTagString(tagsString);
        List<Tag> tags = new ArrayList<Tag>();
        if (tagList != null && tagList.size() > 0) {
            for (Tag tag : tagList) {
                Tag tagObj = tagDao.getTagByTitle(tag.getTitle());
                tagObj.setWeightage(tagObj.getWeightage()
                                + IdeaExchangeConstants.TAG_WEIGHT_INCREMENT_SIZE);
                tagObj.setUpdatedOn(new Date());
                tagObj = saveTagLocal(tagObj);

                if (tagObj != null) {
                    tags.add(tagObj);
                }
            }
        }
        if (tags.size() > 0) {
            log
                            .info("Weights of the following tags:  " + tagsString
                            + "  successfully decremented");
            return tags;
        }
        return null;
    }

    @Override
    public List<Tag> decrementWeights(String tagsString) {
        log.debug("Tag string =" + tagsString + " for decrementing weights");
        List<Tag> tagList = getTagsFromTagString(tagsString);
        List<Tag> tags = new ArrayList<Tag>();
        if (tagList != null && tagList.size() > ServiceConstants.ZERO) {
            for (Tag tag : tagList) {
                Tag tagObj = tagDao.getTagByTitle(tag.getTitle());
                tagObj.setWeightage(tagObj.getWeightage()
                                - IdeaExchangeConstants.TAG_WEIGHT_INCREMENT_SIZE);
                tagObj.setUpdatedOn(new Date());
                tagObj = saveTagLocal(tagObj);

                if (tagObj != null) {
                    tags.add(tagObj);
                }
            }
        }
        if (tags.size() > ServiceConstants.ZERO) {
            log
                            .info("Weights of the following tags:  " + tagsString
                            + "  successfully decremented");
            return tags;
        }
        return null;
    }

    @Override
    public Tag saveTag(Tag tag) {
        return saveTagLocal(tag);
    }

    private Tag saveTagLocal(Tag tag) {
        Tag savedTag = tagDao.saveTag(tag);
        // Synchronous indexing of data.
        SearchUtility.indexEntity(savedTag);
        return savedTag;
    }

    private List<Tag> saveTags(List<Tag> tagsToSave) {
        List<Tag> tagsToReturn = new ArrayList<Tag>();
        /* Iterate and save tag objects. */
        for (Tag tag : tagsToSave) {
            tag.setCreatedOn(new Date());
            Tag savedTag = saveTag(tag);
            if (!StringUtils.isBlank(savedTag.getKey()))
                tagsToReturn.add(savedTag);
        }
        return tagsToReturn;
    }

    /**
     * Prepares the {@link RetrievalInfo} object with values to be used as query
     * parameters.
     * Checks the received RetrievalInfo object attributes for valid
     * data.Updates the attributes if they contain garbage values.If the
     * received {@link RetrievalInfo} object is null,sets it to a new instance
     * with its attributes set to default values.
     * 
     * @param retrievalInfo the {@link RetrievalInfo} object containing the
     *        values to be used as query parameters
     * @param defaultNoOfRecords the number of records to be fetched in case the
     *        number is not explicitly specified by the request parameter
     * @return the {@link RetrievalInfo} object containing the query parameters
     */
    private RetrievalInfo getRetrievalInfoSetToQueryParameters(RetrievalInfo retrievalInfo,
                    long defaultNoOfRecords) {
        if (null == retrievalInfo) {
            retrievalInfo = new RetrievalInfo();
            retrievalInfo.setNoOfRecords(defaultNoOfRecords);
            retrievalInfo.setOrderBy(ServiceConstants.DEFAULT_ORDER_BY_FIELD_FOR_TAG);
            retrievalInfo.setOrderBy(ServiceConstants.DEFAULT_ORDER_TYPE_FOR_TAG);

        } else {
            if (retrievalInfo.getStartIndex() < ServiceConstants.ZERO) {
                retrievalInfo.setStartIndex(ServiceConstants.DEFAULT_START_INDEX_FOR_TAG);
            }
            if (retrievalInfo.getNoOfRecords() <= ServiceConstants.ZERO) {
                retrievalInfo.setNoOfRecords(defaultNoOfRecords);
            }
            if (null == retrievalInfo.getOrderType()
                            || retrievalInfo.getOrderType().length() <= ServiceConstants.ZERO) {
                retrievalInfo.setOrderType(ServiceConstants.DEFAULT_ORDER_TYPE_FOR_TAG);
            }
            if (null == retrievalInfo.getOrderBy()
                            || retrievalInfo.getOrderBy().length() <= ServiceConstants.ZERO) {
                retrievalInfo.setOrderBy(ServiceConstants.DEFAULT_ORDER_BY_FIELD_FOR_TAG);
            }
        }
        return retrievalInfo;
    }

    /**
     * Checks the cache for the tags data
     * 
     * @return list of {@link Tag } objects
     */
    @SuppressWarnings("unchecked")
    private List<Tag> getTagCloudDataFromCache() {
        /* Check the cache for the tag cloud data */
        if (CacheHelper.containsObject(CacheConstants.TAG_NAMESPACE,
                        CacheConstants.TAG_CLOUD)) {
            List<Tag> tagList = (List<Tag>) CacheHelper
                            .getObject(CacheConstants.TAG_NAMESPACE, CacheConstants.TAG_CLOUD);
            return tagList;
        }
        return null;
    }

    /**
     * Imposes duplicate check on Tag object passed as a parameter. If duplicate
     * of parameter Tag found then it returns the already stored Tag object,
     * else it returns the Tag object passed to it.
     * 
     * @param tag Tag object to be checked for duplicate.
     * 
     * @return The already stored duplicate Tag object if found, else it
     *         returns the Tag object passed to it.
     */

    private Tag checkForDuplicate(Tag tag) {
        Tag savedTag = null;
        // check if tag already exist with same title.
        savedTag = getTagDao().getTagByTitle(tag.getTitle());
        if (savedTag != null) {
            return savedTag;
        }
        return tag;
    }

    /**
     * Converts the comma separated string of tags to list of tag objects.
     * 
     * @param tags Comma separated tags string.
     * @return List of Tag objects.
     */
    private List<Tag> getTagsFromTagString(String tags) {
        /* Split the string */
        String[] tagArray = tags.trim().split(",");
        List<Tag> tagList = new ArrayList<Tag>();
        /* Set of the tag titles. */
        Set<String> setOfTagTitle = new LinkedHashSet<String>();
        /* Add the data to the set */
        for (String tagTitle : tagArray) {
            if (!StringUtils.isEmpty(tagTitle)) {
                setOfTagTitle.add(tagTitle);
            }
        }
        Iterator<String> tagTitleIterator = setOfTagTitle.iterator();
        /* Iterate the set and create tag objects */
        while (tagTitleIterator.hasNext()) {
            Tag tagObj = new Tag();
            tagObj.setTitle(tagTitleIterator.next());
            /* Add the tags to the list */
            tagList.add(tagObj);
        }
        if (tagList.size() > ServiceConstants.ZERO)
            return tagList;
        return null;
    }

    /**
     * Parses string to get tags and removes the white spaces and changes into
     * lower case string.
     * 
     * @param tagString String object having the comma separated tags string.
     * @return list of Tag objects.
     */
    protected final List<Tag> parseTags(String tagString) {
        Tag tmpTag = null;
        List<Tag> tagList = new ArrayList<Tag>();
        String[] arrayOfTagTitle = tagString.trim().split("[\\s,]");
        /* Set of tag titles. */
        Set<String> setOfTagTitle = new LinkedHashSet<String>();
        for (String tagTitle : arrayOfTagTitle) {
            if (!StringUtils.isEmpty(tagTitle)) {
                /* change to lower case */
                tagTitle = tagTitle.toLowerCase();
                setOfTagTitle.add(tagTitle);
            }
        }
        Iterator<String> titleIterator = setOfTagTitle.iterator();
        /* Iterate and create tag objects */
        while (titleIterator.hasNext()) {
            tmpTag = new Tag();
            tmpTag.setTitle(titleIterator.next());
            tagList.add(tmpTag);
        }
        return tagList;
    }

    /**
     * Create a combined set of tag keys from a list of ideas
     * 
     * @param ideas the list of ideas
     * @return a {@link Set} of tag keys
     */
    private Set<String> getSetOfTagKeys(List<Idea> ideas) {
        Set<String> setOfTagKeys = new HashSet<String>();
        Iterator<Idea> iteratorOfIdeas = ideas.iterator();
        Set<String> individualSets;
        while (iteratorOfIdeas.hasNext()) {
            individualSets = iteratorOfIdeas.next().getTagKeys();
            setOfTagKeys.addAll(individualSets);
        }
        return setOfTagKeys;
    }

    /**
     * @param tagDao the tagDao to set
     */
    public void setTagDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    /**
     * @return the tagDao
     */
    public TagDao getTagDao() {
        return tagDao;
    }

    /**
     * @param auditManager the auditManager to set
     */
    public void setAuditManager(AuditManager auditManager) {
        this.auditManager = auditManager;
    }

    /**
     * @return the auditManager
     */
    public AuditManager getAuditManager() {
        return auditManager;
    }

    public IdeaService getIdeaService() {
        return ideaService;
    }

    public void setIdeaService(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    public EntityIndexService getEntityIndexService() {
        return entityIndexService;
    }

    public void setEntityIndexService(EntityIndexService entityIndexService) {
        this.entityIndexService = entityIndexService;
    }

}

