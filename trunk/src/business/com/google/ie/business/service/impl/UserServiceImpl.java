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

import com.google.ie.business.dao.UserDao;
import com.google.ie.business.dao.impl.DaoConstants;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.ServiceConstants;
import com.google.ie.business.service.UserService;
import com.google.ie.common.util.SearchUtility;
import com.google.ie.dto.RetrievalInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A service implementation of the UserService
 * 
 * @author Charanjeet singh
 */
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class);
    @Autowired
    private UserDao userDao;

    @Override
    public User saveUser(User user) {
        User savedUser = null;
        if (user == null) {
            return null;
        }
        savedUser = userDao.saveUser(user);
        return savedUser;
    }

    /**
     * Getter method for user dao.
     * 
     * @return
     */
    public UserDao getUserDao() {
        return userDao;
    }

    /**
     * Setter method for user dao.
     * 
     * @param userDao
     */
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User getUserById(String id) {
        if (id == null) {
            return null;
        }
        return userDao.getUserById(id);
    }

    @Override
    public User addOrUpdateUser(User user) {
        /* Get the user from datastore */
        User userFromDatastore = getUserById(user.getId());
        if (userFromDatastore != null) {
            LOGGER.info("User found. Updating it with user info from GFC");
            /* In case the user is banned,return null */
            if (userFromDatastore.getStatus() != null
                            && userFromDatastore.getStatus().equalsIgnoreCase(User.STATUS_BANNED)) {
                return null;
            }
            userFromDatastore.setDisplayName(user.getDisplayName());
            userFromDatastore.setThumbnailUrl(user.getThumbnailUrl());
            if (!StringUtils.isBlank(user.getEmailId())) {
                userFromDatastore.setEmailId(user.getEmailId());
            }
            /* Set of permitted status for the user entity */
            Set<String> setOfUserStatus = new HashSet<String>();
            setOfUserStatus.add(User.STATUS_ACTIVE);
            setOfUserStatus.add(User.STATUS_BANNED);
            /* Check if the status is the one permissible */
            if (!StringUtils.isBlank(user.getStatus())
                            && setOfUserStatus.contains(user.getStatus())) {
                userFromDatastore.setStatus(user.getStatus());
            } else {
                userFromDatastore.setStatus(User.STATUS_ACTIVE);
            }
            userFromDatastore = userDao.saveUser(userFromDatastore);

            /* Make user object searchable by adding index */
            SearchUtility.indexEntity(userFromDatastore);

        } else {
            /*
             * Since the user does not exist in data store ,save the details
             * of the new user.
             */
            if (StringUtils.isBlank(user.getRoleName())) {
                user.setRoleName(User.ROLE_USER);
            }
            user.setStatus(User.STATUS_ACTIVE);
            LOGGER.info("Saving new user");
            userFromDatastore = userDao.saveUser(user);
        }
        return userFromDatastore;
    }

    @Override
    public User getUserByPrimaryKey(String key) {
        return userDao.findEntityByPrimaryKey(User.class, key);
    }

    @Override
    public User banUser(User user) {
        User savedUser = null;
        if (user != null) {
            /* Get the user from datastore */
            User userFromDatastore = userDao.findEntityByPrimaryKey(User.class, user.getUserKey());
            userFromDatastore.setStatus(User.STATUS_BANNED);
            savedUser = userDao.saveUser(userFromDatastore);
            /* Delete index of user object */
            SearchUtility.deleteEntityIndex(userFromDatastore);

        }
        return savedUser;
    }

    @Override
    public User activate(User user) {
        User savedUser = null;
        if (user != null) {
            /* Get the user from datastore */
            User userFromDatastore = userDao.findEntityByPrimaryKey(User.class, user.getUserKey());
            userFromDatastore.setStatus(User.STATUS_ACTIVE);
            savedUser = userDao.saveUser(userFromDatastore);

            /* Make user object searchable by adding index */
            SearchUtility.indexEntity(userFromDatastore);

        }
        return savedUser;
    }

    @Override
    public List<User> getUsers(RetrievalInfo retrievalInfo, String role) {
        retrievalInfo = prepareRetrievalInfoForQuery(retrievalInfo);
        return userDao.getUsers(retrievalInfo, role, null);
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
            retrievalInfo.setNoOfRecords(ServiceConstants.USERS_LIST_DEFAULT_SIZE);
            retrievalInfo.setOrderType(ServiceConstants.DEFAULT_USER_ORDERING_TYPE);
            retrievalInfo.setOrderBy(ServiceConstants.USER_ORDERING_FIELD);
        } else {
            // Handle garbage values if any.
            String orderBY = retrievalInfo.getOrderBy();
            String orderType = retrievalInfo.getOrderType();
            if (retrievalInfo.getStartIndex() < ServiceConstants.ZERO)
                retrievalInfo.setStartIndex(ServiceConstants.ZERO);
            if (retrievalInfo.getNoOfRecords() <= ServiceConstants.ZERO)
                retrievalInfo.setNoOfRecords(ServiceConstants.USERS_LIST_DEFAULT_SIZE);
            if (orderType == null || !((orderType.equals(DaoConstants.ORDERING_ASCENDING)
                            || orderType.equals(DaoConstants.ORDERING_DESCENDING))))
                retrievalInfo.setOrderType(ServiceConstants.DEFAULT_USER_ORDERING_TYPE);
            if (orderBY == null) {
                retrievalInfo.setOrderBy(ServiceConstants.USER_ORDERING_FIELD);
            }
        }
        return retrievalInfo;
    }
}

