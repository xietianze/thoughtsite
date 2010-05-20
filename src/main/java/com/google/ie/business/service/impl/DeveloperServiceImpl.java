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

import com.google.ie.business.dao.DeveloperDao;
import com.google.ie.business.domain.Developer;
import com.google.ie.business.service.DeveloperService;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.constants.IdeaExchangeErrorCodes;
import com.google.ie.common.exception.SystemException;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.web.controller.WebConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A service implementation of the DeveloperService
 * 
 * @author gmaurya
 * 
 */
@Service
public class DeveloperServiceImpl implements DeveloperService {
    private static final Logger LOGGER = Logger.getLogger(DeveloperServiceImpl.class);
    @Autowired
    private DeveloperDao developerDao;

    public DeveloperDao getDeveloperDao() {
        return developerDao;
    }

    public void setDeveloperDao(DeveloperDao developerDao) {
        this.developerDao = developerDao;
    }

    @Override
    public Developer getDeveloperById(String key) {
        return getDeveloperDao().findEntityByPrimaryKey(Developer.class, key);
    }

    @Override
    public List<Developer> getDevelopersByProjectKey(String projectKey) {
        return getDeveloperDao().getDevelopersByProjectKey(projectKey);
    }

    @Override
    public Developer saveDeveloper(Developer developer) {
        if (developer.getKey() == null) {
            return getDeveloperDao().saveDeveloper(developer);
        }
        Developer developerFromDatabase = getDeveloperById(developer.getKey());
        if (developer.getUserKey() != null)
            developerFromDatabase.setUserKey(developer.getUserKey());
        developerFromDatabase.setProjectKey(developer.getProjectKey());
        developerFromDatabase.setEmailId(developer.getEmailId());
        developerFromDatabase.setStatus(developer.getStatus());
        developerFromDatabase.setName(developer.getName());
        return getDeveloperDao().saveDeveloper(developerFromDatabase);

    }

    @Override
    public void updateStatus(String developerKey, String status) {
        Developer developer = getDeveloperDao()
                        .findEntityByPrimaryKey(Developer.class, developerKey);
        LOGGER.debug("Current status of developer is =" + developer.getStatus());
        developer.setStatus(status);
        getDeveloperDao().saveDeveloper(developer);
    }

    @Override
    public List<Developer> getDeveloperByUserKey(String userKey, RetrievalInfo retrievalInfo) {
        if (StringUtils.isBlank(userKey)) {
            throw new SystemException(IdeaExchangeErrorCodes.USER_NOT_FOUND,
                            IdeaExchangeConstants.Messages.INVALID_USER);
        }
        /* Fetch one more record than what is required for paging purpose */
        retrievalInfo.setNoOfRecords(retrievalInfo.getNoOfRecords() + WebConstants.ONE);
        List<Developer> developers = getDeveloperDao().getDevelopersByUserKey(userKey,
                        retrievalInfo);
        LOGGER.debug("No of developers found are =" + developers.size());
        return developers;
    }
}

