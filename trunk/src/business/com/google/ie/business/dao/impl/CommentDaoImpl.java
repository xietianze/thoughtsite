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

import com.google.ie.business.dao.CommentDao;
import com.google.ie.business.domain.Comment;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.ProjectComment;
import com.google.ie.dto.RetrievalInfo;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.Query;

/**
 * A JDO implementation object for CommentDao.
 * 
 * @author Sachneet
 * 
 */
public class CommentDaoImpl extends BaseDaoImpl implements CommentDao {

    private static final String IDEA_KEY = "ideaKey";

    public CommentDaoImpl() {

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Comment saveComment(Comment comment) {
        return getJdoTemplate().makePersistent(comment);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Comment> getComments(String key, RetrievalInfo retrievalInfo,
                    String keyType) {
        Query query = null;
        try {
            /* Create the query instance corresponding to the keyType */
            if (keyType.equals(IDEA_KEY)) {
                query = getJdoTemplate().getPersistenceManagerFactory().getPersistenceManager()
                                .newQuery(IdeaComment.class);
            } else {
                query = getJdoTemplate().getPersistenceManagerFactory().getPersistenceManager()
                                .newQuery(ProjectComment.class);
            }
            /* Set of status to be matched */
            Set<String> setOfStatus = new HashSet<String>();
            setOfStatus.add(IdeaComment.STATUS_SAVED);
            setOfStatus.add(IdeaComment.STATUS_FLAGGED);
            query.setFilter(keyType + " == '" + key + "' && status == :setOfStatus");
            query.setOrdering(retrievalInfo.getOrderBy() + " " + retrievalInfo.getOrderType());
            /*
             * Add the start index to the number of records required since
             * internally the second argument is treated as the index up to
             * which
             * the entities are to be fetched
             */
            query.setRange(retrievalInfo.getStartIndex(), retrievalInfo.getStartIndex()
                            + retrievalInfo.getNoOfRecords());
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("setOfStatus", setOfStatus);
            Collection<Comment> collection = getJdoTemplate().find(query.toString(), hashMap);
            if (collection != null && collection.size() > DaoConstants.ZERO) {
                List<Comment> commentList = (new ArrayList<Comment>(collection));
                return commentList;
            }
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
        return null;
    }

}

