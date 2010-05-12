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

import com.google.ie.business.dao.AdminRequestDao;
import com.google.ie.business.dao.IdeaDao;
import com.google.ie.business.dao.impl.DaoConstants;
import com.google.ie.business.domain.AdminRequest;
import com.google.ie.business.domain.EntityIndex;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.Tag;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.EntityIndexService;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.ProjectService;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.business.service.ShardedCounterService;
import com.google.ie.business.service.TagService;
import com.google.ie.business.service.UserService;
import com.google.ie.common.audit.AuditManager;
import com.google.ie.common.cache.CacheConstants;
import com.google.ie.common.cache.CacheHelper;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.constants.IdeaExchangeErrorCodes;
import com.google.ie.common.exception.SystemException;
import com.google.ie.common.objectionable.ObjectionableManager;
import com.google.ie.common.taskqueue.IndexQueueUpdater;
import com.google.ie.common.taskqueue.TagWeightUpdationManager;
import com.google.ie.dto.RetrievalInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A service implementation of the IdeaService
 * 
 * @author Charanjeet singh
 */
@Service
public class IdeaServiceImpl implements IdeaService {
    private static Logger logger = Logger.getLogger(IdeaServiceImpl.class);
    private static boolean isDebug = logger.isDebugEnabled();
    /* Default number of recent ideas to be fetched */
    private static final int DEFAULT_NO_OF_RECENT_IDEAS = 3;
    /* Default number of popular ideas to be fetched */
    private static final int DEFAULT_NO_OF_POPULAR_IDEAS = 3;
    @Autowired
    private IdeaDao ideaDao;
    @Autowired
    private EntityIndexService entityIndexService;

    @Autowired
    private AuditManager auditManager;
    @Autowired
    private TagService tagService;
    @Autowired
    private TagWeightUpdationManager weightUpdationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private ShardedCounterService shardedCounterService;
    @Autowired
    private AdminRequestDao adminRequestDao;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private IndexQueueUpdater indexQueueUpdater;

    public IndexQueueUpdater getIndexQueueUpdater() {
        return indexQueueUpdater;
    }

    public void setIndexQueueUpdater(IndexQueueUpdater indexQueueUpdater) {
        this.indexQueueUpdater = indexQueueUpdater;
    }

    public IdeaServiceImpl() {
    }

    @Override
    public Idea saveIdea(Idea idea, User user) {
        user = userService.getUserById(user.getId());
        if (user != null) {

            if (idea != null && !StringUtils.isBlank(idea.getTitle()) &&
                            !StringUtils.isBlank(user.getUserKey())) {
                if (isDebug) {
                    logger.debug("Idea title=" + idea.getTitle() + " ,user key="
                                    + user.getUserKey());
                }
                Idea savedIdea = saveIdeaLocal(idea, user);
                return savedIdea;
            }
        }
        return null;
    }

    @Override
    public void deleteIdea(String key, User user) {
        Idea idea = new Idea();
        idea = ideaDao.findEntityByPrimaryKey(idea.getClass(), key);
        if (idea != null &&
                        user != null && !StringUtils.isBlank(user.getUserKey())) {
            // Soft delete the idea.
            idea.setStatus(Idea.STATUS_DELETED);
            ideaDao.saveIdea(idea);
        }
    }

    /**
     * Saves an idea.
     * 
     * @param idea Idea to be saved.
     * @return Returns the saved idea object.
     */
    private Idea saveIdeaLocal(Idea idea, User user) {
        if (!StringUtils.isBlank(idea.getStatus())
                        && idea.getStatus().equals(Idea.STATUS_PUBLISHED)) {
            throw new SystemException(IdeaExchangeErrorCodes.INVALID_PUBLISH,
                            IdeaExchangeConstants.Messages.INVALID_PUBLISH);
        }
        /* Handle tags. */
        String tagString = idea.getTags();
        if (!StringUtils.isBlank(tagString)) {
            logger.debug("Submitted tags=" + tagString);
            idea.setTags(formatTagString(tagString));
        }
        /* Update the status and save in data store */
        idea.setCreatorKey(user.getUserKey());
        idea.setStatus(Idea.STATUS_SAVED);
        idea.setLastUpdated(new Date(System.currentTimeMillis()));
        idea = getIdeaDao().saveIdea(idea);
        if (idea != null && !StringUtils.isBlank(idea.getKey())) {
            /* Add saved idea key into users ideaKeys set. */
            addIdeaKeyToUser(idea, user);
            logger.info("Idea with title='" + idea.getTitle() + "', saved successfully");
        }
        return idea;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Idea publishIdea(Idea idea, User user) {
        if (user != null) {
            /*
             * boolean specifying whether the idea to be published was earlier
             * marked as objectionable.If true no points would be added to the
             * user
             */
            boolean wasObjectionable = false;
            if (!StringUtils.isBlank(idea.getKey())) {
                /* If the idea is already published,throw exception */
                if (Idea.STATUS_PUBLISHED.equals(idea.getStatus())) {
                    throw new SystemException(IdeaExchangeErrorCodes.INVALID_PUBLISH,
                                    IdeaExchangeConstants.Messages.INVALID_PUBLISH);
                }
                /*
                 * If the idea was earlier objectionable,set the boolean to
                 * true.
                 */
                if (Idea.STATUS_OBJECTIONABLE.equalsIgnoreCase(idea.getStatus())) {
                    wasObjectionable = true;
                }
            }
            if (!StringUtils.isBlank(idea.getTitle()) &&
                            !StringUtils.isBlank(user.getUserKey())) {
                if (isDebug) {
                    logger.debug("User key=" + user.getUserKey() + " ,idea title="
                                    + idea.getTitle());
                }
                String tagString = idea.getTags();
                if (!StringUtils.isBlank(tagString)) {
                    if (isDebug) {
                        logger.debug("Submitted tags=" + tagString);
                    }
                    idea.setTags(formatTagString(tagString));
                }
                idea.setCreatorKey(user.getUserKey());
                idea.setStatus(Idea.STATUS_PUBLISHED);
                idea.setLastUpdated(new Date(System.currentTimeMillis()));
                idea.setPublishDate(new Date(System.currentTimeMillis()));
                /* Persist idea object. */
                idea = publishIdeaLocal(idea);
                if (idea != null && !StringUtils.isBlank(idea.getKey())) {
                    return performAfterPublish(idea, user, wasObjectionable);
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedList<Idea> getRecentIdeas() {
        /* Check the cache for the data */
        LinkedList<Idea> listOfRecentIdeas = (LinkedList<Idea>) CacheHelper.getObject(
                        CacheConstants.IDEA_NAMESPACE,
                        CacheConstants.RECENT_IDEAS);
        if (listOfRecentIdeas != null && listOfRecentIdeas.size() > ServiceConstants.ZERO) {
            logger.info("Recent ideas successfully fetched from cache");
            return listOfRecentIdeas;
        }
        /* If not found in cache then retrieve from data store. */
        RetrievalInfo retrievalInfo = new RetrievalInfo();
        retrievalInfo.setNoOfRecords(DEFAULT_NO_OF_RECENT_IDEAS);
        retrievalInfo.setOrderBy(ServiceConstants.DEFAULT_IDEA_ORDERING_FIELD);
        retrievalInfo.setOrderType(ServiceConstants.ORDERING_TYPE_FOR_RECENT_IDEAS);
        /* Fetch the ideas and create a linked list from the fetched data */
        listOfRecentIdeas = createLinkedListFromTheFetchedData(getIdeas(retrievalInfo));
        /* If the list contains data, add it to the cache */
        if (listOfRecentIdeas.size() > ServiceConstants.ZERO) {
            CacheHelper.putObject(CacheConstants.IDEA_NAMESPACE, CacheConstants.RECENT_IDEAS,
                            listOfRecentIdeas, CacheConstants.RECENT_IDEAS_EXPIRATION_DELAY);
            logger.info("Recent ideas successfully added to the cache");
        }
        return listOfRecentIdeas;
    }

    /**
     * It performs all other activity, which are needed after publishing the
     * idea.
     * 
     * @param idea The published {@link Idea} object.
     * @param user The {@link User} who has published the idea.
     * @param wasObjectionale whether the idea was earlier an objectionable one
     * @return Idea object.
     */
    private Idea performAfterPublish(Idea idea, User user, boolean wasObjectionale) {
        logger.info("Idea with title=" + idea.getTitle() + ", saved successfully");
        /* If the idea is a new one add points to the user */
        if (!wasObjectionale) {
            /* Add saved idea key into users ideaKeys set. */
            user = addIdeaKeyToUser(idea, user);
            addPointsToUserOnIdeaPublish(user.getUserKey(), idea);
        }
        /* Place object into cache. */
        CacheHelper.putObject(CacheConstants.IDEA_NAMESPACE, idea.getKey(), idea);
        /* Place object into cache for recent ideas. */
        addIdeaToListInCache(idea, CacheConstants.RECENT_IDEAS,
                        DEFAULT_NO_OF_RECENT_IDEAS, CacheConstants.RECENT_IDEAS_EXPIRATION_DELAY);
        String tagStr = idea.getTags();
        /* Increment Tags weights asynchronously. */
        if (tagStr != null && !tagStr.equals(""))
            getWeightUpdationManager().incrementWeight(tagStr);
        /* Audit the published activity. */
        getAuditManager().audit(user.getUserKey(), idea.getKey(), idea.getClass().getName(),
                        ServiceConstants.AUDIT_ACTION_TYPE_PUBLISH_IDEA);
        /* Check idea for objectionable. */
        ObjectionableManager.checkObjectionable(idea.getKey());
        return idea;
    }

    @SuppressWarnings("unchecked")
    public void addIdeaToListInCache(Idea originalIdea, String keyOfTheList,
                    int noOfIdeas, int expiryDelay) {
        LinkedList<Idea> listOfIdeas = (LinkedList<Idea>) CacheHelper.getObject(
                        CacheConstants.IDEA_NAMESPACE,
                        keyOfTheList);
        if (listOfIdeas != null) {
            if (listOfIdeas.size() >= noOfIdeas) {
                /* Remove the last element which is also the oldest */
                listOfIdeas.pollLast();
            }
        } else {
            listOfIdeas = new LinkedList<Idea>();
        }
        /* Create a new idea object to contain the required data only */
        Idea ideaWithTheRequiredDataOnly = new Idea();
        ideaWithTheRequiredDataOnly.setTitle(StringUtils.abbreviate(originalIdea.getTitle(),
                        ServiceConstants.FIFTY));
        /* Limit the description to hundred characters */
        ideaWithTheRequiredDataOnly.setDescription(StringUtils.abbreviate(originalIdea
                        .getDescription(), ServiceConstants.HUNDRED));
        ideaWithTheRequiredDataOnly.setKey(originalIdea.getKey());
        /* Add the idea to the head of the list */
        listOfIdeas.addFirst(ideaWithTheRequiredDataOnly);
        /* Put the updated list back to the cache */
        CacheHelper.putObject(CacheConstants.IDEA_NAMESPACE,
                        keyOfTheList, listOfIdeas, expiryDelay);
    }

    /**
     * Update sharded counters for the users reputation point calculation.
     * 
     * @param user
     * @param idea
     * @return
     */

    private void addPointsToUserOnIdeaPublish(String userKey, Idea idea) {
        int points = 0;
        if (!StringUtils.isBlank(idea.getTitle())) {
            points = points + IdeaExchangeConstants.REPUTATION_POINTS_TITLE;
        }
        if (!StringUtils.isBlank(idea.getDescription())) {
            points = points + IdeaExchangeConstants.REPUTATION_POINTS_DESCRIPTION;
        }
        if (!StringUtils.isBlank(idea.getMonetization())) {
            points = points + IdeaExchangeConstants.REPUTATION_POINTS_MONETIZATION;
        }
        if (!StringUtils.isBlank(idea.getCompetition())) {
            points = points + IdeaExchangeConstants.REPUTATION_POINTS_COMPETITION;
        }
        if (!StringUtils.isBlank(idea.getTargetAudience())) {
            points = points + IdeaExchangeConstants.REPUTATION_POINTS_TARGET_AUDIENCE;
        }
        updateShardedCounter(userKey, points);
    }

    /**
     * Update counter.
     * 
     * @param user
     * @param points
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    private void updateShardedCounter(String userKey, int points) {
        shardedCounterService.updateTotalPoints(userKey, points);
    }

    /**
     * Adds the {@link Idea } key to user's idea key set on save and publish
     * idea.
     * 
     * @param idea The saved/published {@link Idea} object.
     * @param user The {@link User} who has saved/published the idea.
     * @return User object.
     */

    private User addIdeaKeyToUser(Idea idea, User user) {
        if (idea.getKey() != null) {
            user.getIdeaKeys().add(idea.getKey());
            userService.saveUser(user);

        }

        return user;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Idea publishIdeaLocal(Idea idea) {
        // Persist idea object.
        idea = getIdeaDao().saveIdea(idea);
        if (idea != null) {
            /*
             * Index the entity.Create an EntityIndex object for the entity
             * to be indexed and then queue the job to task queue
             */
            EntityIndex entityIndex = entityIndexService.createEntityIndex(idea.getKey());
            getIndexQueueUpdater().indexEntity(entityIndex.getKey());

        }
        return idea;
    }

    /**
     * Formats the tag string submitted by application user for display on view.
     * 
     * @param tagString String having tags submitted by application user.
     * @return formated comma separate tags string.
     */
    protected final String formatTagString(String tagString) {
        String[] tags = tagString.trim().split("[\\s,]");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tags.length; i++) {
            String tmpString = tags[i].trim().toLowerCase();
            if (!tmpString.equals(""))
                sb.append(tmpString + ",");
            if (i == tags.length - 1)
                sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public Idea getIdeaDetails(Idea idea) {
        if (idea != null && !StringUtils.isBlank(idea.getKey())) {
            if (isDebug) {
                logger.debug("Retrieving idea details for the idea with key=" + idea.getKey());
            }
            return ideaDao.getIdea(idea);
        }
        return null;
    }

    @Override
    public List<Idea> getIdeas(RetrievalInfo retrievalInfo) {
        /*
         * Retrieve ideas based on the retrieval information. If retrievalInfo
         * object is null then use default parameter information.
         */
        retrievalInfo = prepareRetrievalInfoForQuery(retrievalInfo);
        /* Prepare the Set of status */
        Set<String> statusOfIdeas = new HashSet<String>();
        statusOfIdeas.add(Idea.STATUS_PUBLISHED);
        statusOfIdeas.add(Idea.STATUS_DUPLICATE);

        return ideaDao.getIdeas(retrievalInfo, statusOfIdeas);
    }

    @Override
    public boolean addSummary(String key, String summary, User user) {
        Idea idea = new Idea();
        idea = ideaDao.findEntityByPrimaryKey(idea.getClass(), key);
        if (idea == null || user == null
                        || StringUtils.isBlank(idea.getKey())
                        || StringUtils.isBlank(user.getUserKey())
                        || StringUtils.isBlank(idea.getStatus())
                        || !(idea.getStatus().equals(Idea.STATUS_PUBLISHED))
                        || StringUtils.isBlank(idea.getCreatorKey())
                        || !(idea.getCreatorKey().equals(user.getUserKey()))) {

            throw new SystemException(IdeaExchangeErrorCodes.INVALID_IDEA_SUMMARY,
                            IdeaExchangeConstants.Messages.INVALID_IDEA_SUMMARY);
        }
        // Allow user to add Idea summary only once.
        if (StringUtils.isBlank(idea.getIdeaSummary())) {
            idea.setIdeaSummary(summary);
            getIdeaDao().saveIdea(idea);
            return true;
        }
        return false;
    }

    /* Returns the ideas saved or published by user */
    @Override
    public List<Idea> getIdeasForUser(User user, RetrievalInfo retrievalInfo) {
        if (user == null || StringUtils.isBlank(user.getUserKey())) {
            return null;
        }
        if (isDebug) {
            logger.debug("Retrieving ideas for the user with key=" + user.getUserKey());
        }
        /*
         * Retrieve user's ideas based on the retrieval information. In case of
         * listing user's ideas, ordering will be done on the field
         * 'lastUpdated'.
         */
        if (retrievalInfo == null) {
            retrievalInfo = new RetrievalInfo();
        }
        retrievalInfo.setOrderBy(DaoConstants.IDEA_ORDERING_FIELD_LAST_UPDATE_DATE);
        // Prepare retrieval parameters to fetch users idea.
        retrievalInfo = prepareRetrievalInfoForQuery(retrievalInfo);
        /* Prepare the Set of status */
        Set<String> statusOfIdeas = new HashSet<String>();
        statusOfIdeas.add(Idea.STATUS_SAVED);
        statusOfIdeas.add(Idea.STATUS_PUBLISHED);
        statusOfIdeas.add(Idea.STATUS_OBJECTIONABLE);
        statusOfIdeas.add(Idea.STATUS_DUPLICATE);

        return ideaDao.getUserIdeas(user, statusOfIdeas, retrievalInfo);
    }

    @Override
    public boolean updateStatus(Idea idea) {
        if (idea == null || StringUtils.isBlank(idea.getKey())) {
            return false;
        }
        if (isDebug) {
            logger.debug("updating status of idea for the  key=" + idea.getKey());
        }
        return ideaDao.updateStatus(idea);
    }

    @Override
    public boolean updateIdeaVotes(Idea idea) {
        boolean flag = false;
        if (idea != null && !StringUtils.isBlank(idea.getKey())) {

            if (isDebug) {
                logger.debug("updating status of idea for the  key=" + idea.getKey());
            }

            flag = ideaDao.updateIdeaPoints(idea);
        }
        return flag;
    }

    @Override
    public List<Idea> getIdeasByTagName(String tagName, RetrievalInfo retrievalInfo) {
        /*
         * If the tagname param is not null and is not empty then fetch the Tag
         * object through tag service
         */
        if (tagName != null && tagName.length() > ServiceConstants.ZERO) {
            Tag tag = tagService.getTagByName(tagName);
            /* If tag is not null then get the key of the tag */
            if (tag != null) {
                String tagKey = tag.getKey();
                /*
                 * If the tag key contains value then fetch the
                 * list of ideas associated with the tag key
                 */
                if (tagKey.length() > ServiceConstants.ZERO) {
                    retrievalInfo = prepareRetrievalInfoForQuery(retrievalInfo);
                    Set<String> statusOfIdeas = new HashSet<String>();
                    statusOfIdeas.add(Idea.STATUS_PUBLISHED);
                    statusOfIdeas.add(Idea.STATUS_DUPLICATE);
                    List<Idea> ideaList = ideaDao.getIdeasByTagKey(tagKey, statusOfIdeas,
                                    retrievalInfo);
                    return ideaList;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedList<Idea> getPopularIdeas() {
        /* First check the cache for the data */
        LinkedList<Idea> popularIdeas = (LinkedList<Idea>) CacheHelper.getObject(
                        CacheConstants.IDEA_NAMESPACE,
                        CacheConstants.POPULAR_IDEAS);
        if (popularIdeas != null && popularIdeas.size() > ServiceConstants.ZERO) {
            logger.info("Popular ideas successfully fetched from cache");
            return popularIdeas;
        }
        /* If not in cache ,fetch from data store */
        RetrievalInfo retrievalInfo = new RetrievalInfo();
        /* Set the RetrievalInfo object to contain query parameters */
        retrievalInfo.setNoOfRecords(DEFAULT_NO_OF_POPULAR_IDEAS);
        retrievalInfo.setOrderBy(ServiceConstants.IDEA_ORDERING_FIELD_TOTAL_POSITIVE_VOTES);
        retrievalInfo.setOrderType(ServiceConstants.ORDERING_DESCENDING);
        /* Create the set of status conditions to be matched in the query */
        Set<String> statusOfIdeas = new HashSet<String>();
        statusOfIdeas.add(Idea.STATUS_PUBLISHED);
        /*
         * Fetch the ideas from the datastore and create a linked list from the
         * fetched ideas
         */
        popularIdeas = createLinkedListFromTheFetchedData(ideaDao.getIdeas(retrievalInfo,
                        statusOfIdeas));
        if (popularIdeas.size() > ServiceConstants.ZERO) {
            /* Put popular ideas into cache */
            CacheHelper.putObject(CacheConstants.IDEA_NAMESPACE, CacheConstants.POPULAR_IDEAS,
                            popularIdeas, CacheConstants.POPULAR_IDEAS_EXPIRATION_DELAY);
            logger.info("Popular ideas successfully added to the cache");
        }
        return popularIdeas;
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
     * @return the {@link RetrievalInfo} object containing the query parameters
     */

    private RetrievalInfo prepareRetrievalInfoForQuery(RetrievalInfo retrievalInfo) {
        if (retrievalInfo == null) {
            retrievalInfo = new RetrievalInfo();
            retrievalInfo.setStartIndex(ServiceConstants.ZERO);
            retrievalInfo.setNoOfRecords(ServiceConstants.IDEAS_LIST_DEFAULT_SIZE);
            retrievalInfo.setOrderType(ServiceConstants.DEFAULT_IDEA_ORDERING_TYPE);
            retrievalInfo.setOrderBy(ServiceConstants.DEFAULT_IDEA_ORDERING_FIELD);
        } else {
            // Handle garbage values if any.
            String orderBY = retrievalInfo.getOrderBy();
            String orderType = retrievalInfo.getOrderType();
            if (retrievalInfo.getStartIndex() < ServiceConstants.ZERO)
                retrievalInfo.setStartIndex(ServiceConstants.ZERO);
            if (retrievalInfo.getNoOfRecords() <= ServiceConstants.ZERO)
                retrievalInfo.setNoOfRecords(ServiceConstants.IDEAS_LIST_DEFAULT_SIZE);
            if (orderType == null || !((orderType.equals(DaoConstants.ORDERING_ASCENDING)
                            || orderType.equals(DaoConstants.ORDERING_DESCENDING))))
                retrievalInfo.setOrderType(ServiceConstants.DEFAULT_IDEA_ORDERING_TYPE);
            if (orderBY == null
                            || !((orderBY.equals(DaoConstants.IDEA_ORDERING_FIELD_LAST_UPDATE_DATE)
                            || orderBY.equals(DaoConstants.IDEA_ORDERING_FIELD_PUBLISH_DATE)
                            || orderBY.equals(DaoConstants.IDEA_ORDERING_FIELD_VOTE)))) {
                retrievalInfo.setOrderBy(ServiceConstants.DEFAULT_IDEA_ORDERING_FIELD);
            }
        }
        return retrievalInfo;
    }

    /**
     * Create a linked list from the list of ideas received.Removes the data not
     * required on the homepage.This method is intended to create
     * list for Recent ideas and Popular ideas method
     * 
     * @param ideas list of ideas
     * @return a {@link LinkedList} object containing the ideas
     */
    private LinkedList<Idea> createLinkedListFromTheFetchedData(List<Idea> ideas) {
        LinkedList<Idea> linkedList = new LinkedList<Idea>();
        Iterator<Idea> iterator = ideas.iterator();
        Idea originalIdea = null;
        Idea ideaWithTheRequiredDataOnly = null;
        while (iterator.hasNext()) {
            originalIdea = iterator.next();
            if (originalIdea.getStatus().equals(Idea.STATUS_OBJECTIONABLE))
                continue;
            ideaWithTheRequiredDataOnly = new Idea();
            ideaWithTheRequiredDataOnly.setTitle(StringUtils.abbreviate(originalIdea.getTitle()
                            , ServiceConstants.FIFTY));
            /* Limit the description to hundred characters */
            ideaWithTheRequiredDataOnly.setDescription(StringUtils.abbreviate(originalIdea
                            .getDescription(), ServiceConstants.HUNDRED));
            ideaWithTheRequiredDataOnly.setKey(originalIdea.getKey());
            /* Add the idea to the linked list */
            linkedList.add(ideaWithTheRequiredDataOnly);
        }
        return linkedList;
    }

    @Override
    public String flagObjectionableIdea(String ideaKey, User user) {
        String status = IdeaExchangeConstants.FAIL;
        /* Get title of the idea */
        Idea idea = this.getIdeaByKey(ideaKey);
        if (idea != null
                        && isFlagTypeAllreadyExist(idea.getFlagType(), Idea.FLAG_TYPE_OBJECTIONABLE)) {
            status = IdeaExchangeConstants.IDEA_ALLREADY_FLAGED;
            return status;
        }
        /*
         * Create an admin request to handle the objectionabe flag request for
         * idea
         */
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setEntityKey(ideaKey);
        adminRequest.setEntityType(Idea.class.getSimpleName());
        adminRequest.setRequesterkey(user.getUserKey());
        adminRequest.setRequestType(AdminRequest.REQUEST_OBJECTIONABLE);
        adminRequest.setCreatedOn(new Date());
        adminRequest.setStatus(AdminRequest.STATUS_PENDING);

        if (!StringUtils.isBlank(user.getEmailId())) {
            adminRequest.setRequesterEmail(user.getEmailId());
        }

        /* Get Idea to get title and update FlagType of the entity */
        if (idea != null && !StringUtils.isBlank(idea.getKey())) {
            adminRequest.setEntityTitle(idea.getTitle());

            idea.getFlagType().add(Idea.FLAG_TYPE_OBJECTIONABLE);
            ideaDao.saveIdea(idea);
        } else {
            throw new SystemException(IdeaExchangeErrorCodes.IDEA_NULL_EXCEPTION,
                            "There is no idea that belongs to the given key in data store");
        }
        if (adminRequestDao.saveRequest(adminRequest)) {
            status = IdeaExchangeConstants.SUCCESS;
        }
        return status;
    }

    @Override
    public String flagDuplicateIdea(String ideaKey, String originalIdeakey, User user) {
        String status = IdeaExchangeConstants.FAIL;
        /* Get title of the idea */
        Idea idea = this.getIdeaByKey(ideaKey);
        if (idea != null && isFlagTypeAllreadyExist(idea.getFlagType(), Idea.FLAG_TYPE_DUPLICATE)) {
            status = IdeaExchangeConstants.IDEA_ALLREADY_MARKED_DUPLICATE;
            return status;
        }
        /*
         * Create an admin request to handle the duplicate flag request for
         * idea.
         */
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setEntityKey(ideaKey);
        adminRequest.setEntityType(Idea.class.getSimpleName());
        adminRequest.setRequesterkey(user.getUserKey());
        if (!StringUtils.isBlank(user.getEmailId())) {
            adminRequest.setRequesterEmail(user.getEmailId());
        }

        adminRequest.setRequestType(AdminRequest.REQUEST_DUPLICATE);
        adminRequest.setCreatedOn(new Date());

        /* Creating string for other information set of AdminRequest */
        StringBuilder otherInfoString = new StringBuilder();
        otherInfoString.append(AdminRequest.INFO_ORIGINAL_IDEA_KEY);
        otherInfoString.append(AdminRequest.INFO_SEPARATOR);
        otherInfoString.append(originalIdeakey);

        Set<String> otherInfo = new HashSet<String>();
        otherInfo.add(otherInfoString.toString());
        adminRequest.setOtherInfo(otherInfo);
        adminRequest.setStatus(AdminRequest.STATUS_PENDING);
        /* Mark Idea as duplicate and save into data store. */
        if (idea != null && !StringUtils.isBlank(idea.getKey())) {
            adminRequest.setEntityTitle(idea.getTitle());

            idea.getFlagType().add(Idea.FLAG_TYPE_DUPLICATE);
            idea.setOriginalIdeaKey(originalIdeakey);
            ideaDao.saveIdea(idea);
        } else {

            throw new SystemException(IdeaExchangeErrorCodes.IDEA_NULL_EXCEPTION,
                            "There is no idea that belongs to the given key in data store");

        }
        /* Save admin request */
        if (adminRequestDao.saveRequest(adminRequest)) {
            status = IdeaExchangeConstants.SUCCESS;
        }
        return status;
    }

    /**
     * check for flag type.
     * 
     * @param flagTypes Type of flags associated with idea for Example
     *        Objectionable,Duplicate.
     * @param flagTypeToCheck Type of flags associated with the idea.
     * @return True if flag already exist in set of flagTypes.
     */
    private boolean isFlagTypeAllreadyExist(Set<String> flagTypes, String flagTypeToCheck) {
        for (String flagType : flagTypes) {
            if (flagType != null && flagType.equalsIgnoreCase(flagTypeToCheck)) {
                return true;
            }

        }
        return false;
    }

    @Override
    public Idea updateIdea(Idea idea) {
        return ideaDao.saveIdea(idea);

    }

    @SuppressWarnings("unchecked")
    @Override
    public LinkedList<Idea> getRecentlyPickedIdeas() {
        /* First check the cache for the data */
        LinkedList<Idea> recentlyPickedIdeas = (LinkedList<Idea>) CacheHelper.getObject(
                        CacheConstants.IDEA_NAMESPACE,
                        CacheConstants.RECENTLY_PICKED_IDEAS);
        if (recentlyPickedIdeas != null && recentlyPickedIdeas.size() > ServiceConstants.ZERO) {
            logger.info("Recently picked ideas successfully fetched from cache");
            return recentlyPickedIdeas;
        }
        /*
         * Get the recently created projects.Iterate through the list and fetch
         * key of idea related to the project
         */
        List<Project> recentProjects = projectService.getRecentProjects();
        List<Idea> ideas = new ArrayList<Idea>();
        Iterator<Project> iterator = recentProjects.iterator();
        Set<String> setOfIdeaKeys = new HashSet<String>();
        while (iterator.hasNext()) {
            String ideaKey = iterator.next().getIdeaKey();
            if (!setOfIdeaKeys.contains(ideaKey)) {
                setOfIdeaKeys.add(ideaKey);
                ideas.add(getIdeaByKey(ideaKey));
            }
        }
        recentlyPickedIdeas = createLinkedListFromTheFetchedData(ideas);
        if (recentlyPickedIdeas.size() > ServiceConstants.ZERO) {
            /* Put the recently picked ideas into the cache */
            CacheHelper.putObject(CacheConstants.IDEA_NAMESPACE,
                            CacheConstants.RECENTLY_PICKED_IDEAS,
                            recentlyPickedIdeas,
                            CacheConstants.RECENTLY_PICKED_IDEAS_EXPIRATION_DELAY);
            logger.info("Recently picked ideas successfully added to the cache");
        }
        return recentlyPickedIdeas;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeIdeaFromAllListsInCache(String ideaKey) {
        /*
         * Create a map containing all list names as keys and the corressponding
         * expiration delays as values
         */
        Map<String, Integer> map = new HashMap<String, Integer>();
        /* Recent ideas list */
        map.put(CacheConstants.RECENT_IDEAS, CacheConstants.RECENT_IDEAS_EXPIRATION_DELAY);
        /* Recently picked ideas list */
        map.put(CacheConstants.RECENTLY_PICKED_IDEAS,
                        CacheConstants.RECENTLY_PICKED_IDEAS_EXPIRATION_DELAY);
        /* Popular ideas list */
        map.put(CacheConstants.POPULAR_IDEAS, CacheConstants.POPULAR_IDEAS_EXPIRATION_DELAY);
        Iterator<String> iterator = map.keySet().iterator();
        LinkedList<Idea> listOfIdeas = null;
        String keyOfTheList = null;
        while (iterator.hasNext()) {
            keyOfTheList = iterator.next();
            /* Fetch the list */
            listOfIdeas = (LinkedList<Idea>) CacheHelper.getObject(
                            CacheConstants.IDEA_NAMESPACE,
                            keyOfTheList);
            /* If the list is not null check and remove the idea */
            if (listOfIdeas != null) {
                removeIdeaFromList(listOfIdeas, ideaKey, keyOfTheList, map.get(keyOfTheList));
            }
        }

        /* First check in Recent ideas list and remove if available */

    }

    /**
     * Remove idea from the list
     * 
     * @param ideas the list of the ideas
     * @param ideaKey the key of the idea to be removed
     * @param keyOfTheList the key of the ideas list as used in the cache
     * @param expiryDelay the expiry delay for the list in cache
     */
    private void removeIdeaFromList(LinkedList<Idea> ideas, String ideaKey, String keyOfTheList,
                    int expiryDelay) {
        Iterator<Idea> iterator = ideas.iterator();
        Idea idea = null;
        Idea ideaToBeRemoved = null;
        while (iterator.hasNext()) {
            idea = iterator.next();
            if (ideaKey.equalsIgnoreCase(idea.getKey())) {
                ideaToBeRemoved = idea;
                break;
            }
        }
        if (ideaToBeRemoved != null) {
            ideas.remove(ideaToBeRemoved);
            /* Put the updated list back to the cache */
            CacheHelper.putObject(CacheConstants.IDEA_NAMESPACE,
                            keyOfTheList, ideas, expiryDelay);
        }

    }

    /**
     * Returns the IdeaDao type implementation
     * 
     * @return Returns the TagService
     */
    public IdeaDao getIdeaDao() {
        return ideaDao;
    }

    /**
     * Sets the IdeaDao type implementation.
     * 
     * @param val The IdeaDao object
     */
    public void setIdeaDao(IdeaDao val) {
        this.ideaDao = val;
    }

    /**
     * Sets the AuditManager.
     * 
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

    @Override
    public Idea getIdeaByKey(String key) {
        return ideaDao.findEntityByPrimaryKey(Idea.class, key);

    }

    public TagService getTagService() {
        return tagService;
    }

    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * @param weightUpdationManager the weightUpdationManager to set
     */
    public void setWeightUpdationManager(TagWeightUpdationManager weightUpdationManager) {
        this.weightUpdationManager = weightUpdationManager;
    }

    /**
     * @return the weightUpdationManager
     */
    public TagWeightUpdationManager getWeightUpdationManager() {
        return weightUpdationManager;
    }

    /**
     * @param userService the userService to set
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * @return the userService
     */
    public UserService getUserService() {
        return userService;
    }

    public EntityIndexService getEntityIndexService() {
        return entityIndexService;
    }

    public void setEntityIndexService(EntityIndexService entityIndexService) {
        this.entityIndexService = entityIndexService;
    }

    /**
     * @param shardedCounterService the shardedCounterService to set
     */
    public void setShardedCounterService(ShardedCounterService shardedCounterService) {
        this.shardedCounterService = shardedCounterService;
    }

    /**
     * @return the shardedCounterService
     */
    public ShardedCounterService getShardedCounterService() {
        return shardedCounterService;
    }

    /**
     * @return the adminRequestDao
     */
    public AdminRequestDao getAdminRequestDao() {
        return adminRequestDao;
    }

    /**
     * @param adminRequestDao the adminRequestDao to set
     */
    public void setAdminRequestDao(AdminRequestDao adminRequestDao) {
        this.adminRequestDao = adminRequestDao;
    }

    @Override
    public List<Idea> getIdeasByCategory(String categoryKey, RetrievalInfo retrievalInfo) {
        if (categoryKey != null && categoryKey.length() > ServiceConstants.ZERO) {
            retrievalInfo = prepareRetrievalInfoForQuery(retrievalInfo);
            List<Idea> ideaList = ideaDao.getIdeasByCategoryKey(categoryKey, Idea.STATUS_PUBLISHED,
                            retrievalInfo);
            return ideaList;
        }
        return null;
    }
}

