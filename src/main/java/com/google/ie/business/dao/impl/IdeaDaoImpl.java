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

import com.google.ie.business.dao.IdeaDao;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.Query;

/**
 * A JDO implementation object for IdeaDao.
 * 
 * @author Sachneet
 */

public class IdeaDaoImpl extends BaseDaoImpl implements IdeaDao {

    @Override
    public Idea getIdea(Idea idea) {
        return getJdoTemplate().getObjectById(Idea.class, idea.getKey());
    }

    @Override
    public List<Idea> getIdeas(RetrievalInfo retrievalInfo, Set<String> statusOfIdeas) {
        /* Execute query and return published ideas */
        return executeGetIdeas(null, retrievalInfo, statusOfIdeas);
    }

    @Override
    public List<Idea> getUserIdeas(User user, Set<String> statusOfIdeas, RetrievalInfo retrievalInfo) {
        /* Execute query and return result */
        return executeGetIdeas(user, retrievalInfo,
                        statusOfIdeas);
    }

    @SuppressWarnings("unchecked")
    public List<Idea> getIdeasByTagKey(String tagKey, Set<String> statusOfIdeas,
                    RetrievalInfo retrievalInfo) {
        Query query = getJdoTemplate().getPersistenceManagerFactory()
                        .getPersistenceManager().newQuery(Idea.class);
        /*
         * Add the start index to the number of records required since
         * internally the second argument is treated as the index up to which
         * the entities are to be fetched
         */
        query.setRange(retrievalInfo.getStartIndex(), retrievalInfo.getStartIndex()
                        + retrievalInfo.getNoOfRecords());
        query.setOrdering("" + retrievalInfo.getOrderBy() + " " + retrievalInfo.getOrderType());
        query.setFilter("tagKeys == :tagKey && status == :statusOfIdeas");
        Map<String, Object> mapOfValues = new HashMap<String, Object>();
        mapOfValues.put("statusOfIdeas", statusOfIdeas);
        mapOfValues.put("tagKey", tagKey);
        Collection<Idea> collection = getJdoTemplate()
                        .find(query.toString(), mapOfValues);
        if (collection != null) {
            return new ArrayList<Idea>(collection);
        }
        return null;
    }

    /**
     * Method to retrieve the list of ideas for different cases.
     * 
     * @param user The user object.
     * @param retrievalInfo the {@link RetrievalInfo} object having information
     *        of startIndex and total number of records.
     * @param statusOfProject {@link Set} of strings holding the Idea
     *        status.
     * @return List of idea objects
     */
    @SuppressWarnings("unchecked")
    private List<Idea> executeGetIdeas(User user, RetrievalInfo retrievalInfo,
                    Set<String> statusOfIdeas) {
        Query query = null;
        Collection<Idea> collection;
        try {
            query = getJdoTemplate().getPersistenceManagerFactory()
                            .getPersistenceManager().newQuery(Idea.class);
            /*
             * Add the start index to the number of records required since
             * internally the second argument is treated as the index up to
             * which the entities are to be fetched
             */
            query.setRange(retrievalInfo.getStartIndex(), retrievalInfo.getStartIndex()
                            + retrievalInfo.getNoOfRecords());
            query.setOrdering("" + retrievalInfo.getOrderBy() + " " + retrievalInfo.getOrderType());
            Map<String, Object> mapOfFilterValues = new HashMap<String, Object>();
            if (user != null && user.getUserKey() != null) {
                query.setFilter("status == :statusOfIdeas && creatorKey == :creatorKeyParam");
                mapOfFilterValues.put("creatorKeyParam", user.getUserKey());
            } else {
                query.setFilter("status == :statusOfIdeas");
            }
            mapOfFilterValues.put("statusOfIdeas", statusOfIdeas);
            collection = getJdoTemplate()
                            .find(query.toString(), mapOfFilterValues);
            return new ArrayList<Idea>(collection);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Idea saveIdea(Idea idea) {
        idea = getJdoTemplate().makePersistent(idea);
        return idea;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public boolean updateStatus(Idea idea) {
        String status = idea.getStatus();
        idea = getJdoTemplate().getObjectById(Idea.class, idea.getKey());
        idea.setStatus(status);
        idea = getJdoTemplate().makePersistent(idea);
        if (idea != null)
            return true;
        return false;
    }

    /**
     * Update of the idea vote points of the given idea.
     * 
     * @param idea Idea object containing updated status and key.
     * @return boolean return true or false on the basis of successful update.
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public boolean updateIdeaPoints(final Idea idea) {
        Idea originalIdea = getJdoTemplate().getObjectById(Idea.class, idea.getKey());
        boolean flag = false;
        if (originalIdea != null) {
            originalIdea.setTotalVotes(idea.getTotalVotes());
            originalIdea.setTotalPositiveVotes(idea.getTotalPositiveVotes());
            originalIdea.setTotalNegativeVotes(idea.getTotalNegativeVotes());
            if (getJdoTemplate().makePersistent(originalIdea) != null) {
                flag = true;
            }
        }
        return flag;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Idea> getIdeasByCategoryKey(String ideaCategoryKey, String statusOfIdeas,
                    RetrievalInfo retrievalInfo) {
        Query query = getJdoTemplate().getPersistenceManagerFactory()
                        .getPersistenceManager().newQuery(Idea.class);
        query.setFilter("ideaCategoryKey == '" + ideaCategoryKey + "' && status == '"
                        + statusOfIdeas + "'");

        List<Idea> collection = (List<Idea>) query.execute();
        if (collection != null) {
            return new ArrayList<Idea>(collection);
        }
        return null;
    }
}

