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

import com.google.ie.business.dao.UserDao;
import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Query;

/**
 * * A JDO implementation object for UserDao.
 * 
 * @author Charanjeet singh
 * 
 */
public class UserDaoImpl extends BaseDaoImpl implements UserDao {

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public User saveUser(User user) {
        if (user.getUserKey() == null) {
            user.setCreatedOn(new Date(System.currentTimeMillis()));
        }
        user = persist(user);
        return user;
    }

    @SuppressWarnings("unchecked")
    @Override
    public User getUserById(String id) {
        User userObj = null;
        Query query = null;
        List<User> list;
        try {
            query = getJdoTemplate().getPersistenceManagerFactory()
                            .getPersistenceManager().newQuery(User.class);
            /* Filter for the id received from GFC */
            query.setFilter("id == :idParam");
            Map<String, Object> mapOfValues = new HashMap<String, Object>();
            mapOfValues.put("idParam", id);
            Collection collection = getJdoTemplate().find(query.toString(), mapOfValues);
            /* Detach the results */
            collection = getJdoTemplate().detachCopyAll(collection);
            list = new ArrayList<User>(collection);
            if (list.size() > DaoConstants.ZERO) {
                userObj = list.get(DaoConstants.ZERO);
            }
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }

        return userObj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> getUsers(RetrievalInfo retrievalInfo, String role, String status) {
        Query query = null;
        try {
            query = getJdoTemplate().getPersistenceManagerFactory().getPersistenceManager()
                            .newQuery(User.class);
            if (!StringUtils.isBlank(role)) {
                query.setFilter("roleName == '" + role + "'");
            }
            if (!StringUtils.isBlank(status)) {
                query.setFilter("status == '" + status + "'");
            }
            /*
             * Add the start index to the number of records required since
             * internally the second argument is treated as the index up to
             * which the entities are to be fetched
             */
            query.setRange(retrievalInfo.getStartIndex(), retrievalInfo.getStartIndex()
                            + retrievalInfo.getNoOfRecords());
            query.setOrdering(retrievalInfo.getOrderBy() + " " + retrievalInfo.getOrderType());

            Collection<User> collection = getJdoTemplate().find(query.toString());
            if (collection != null && collection.size() > DaoConstants.ZERO) {
                logger.info(collection.size() + " users found with the role : " + role
                                + " and with status : " + status);
                List<User> userList = (new ArrayList<User>(collection));
                return userList;
            }
            logger.info("No users found with the role : " + role + " and with status : "
                            + status);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }

        return null;
    }
}

