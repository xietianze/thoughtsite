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

import com.google.ie.business.dao.DeveloperDao;
import com.google.ie.business.domain.Developer;
import com.google.ie.dto.RetrievalInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Query;

/**
 * A JDO implementation for DeveloperDao.
 * 
 * @author gmaurya
 */
public class DeveloperDaoImpl extends BaseDaoImpl implements DeveloperDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<Developer> getDevelopersByProjectKey(String projectKey) {
        Map<String, Object> mapOfFilterValues = new HashMap<String, Object>();
        List<Developer> developers = null;
        Query query = null;
        try {
            query = getPersistenceManager()
                            .newQuery(Developer.class);
            query.setFilter("projectKey == '" + projectKey + "'");
            mapOfFilterValues.put("projectKey", projectKey);
            developers = (List<Developer>) getJdoTemplate().find(query.toString(),
                            mapOfFilterValues);
            /* Detach the list fetched from the attached persistence manager */
            developers = (List<Developer>) getJdoTemplate().detachCopyAll(developers);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
        return developers;
    }

    @Override
    public Developer saveDeveloper(Developer developer) {
        developer = getJdoTemplate().makePersistent(developer);
        return developer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Developer> getDevelopersByUserKey(String userKey, RetrievalInfo retrievalInfo) {
        Map<String, Object> mapOfFilterValues = new HashMap<String, Object>();
        List<Developer> developers;
        Query query = null;
        try {
            query = getPersistenceManager().newQuery(Developer.class);
            query.setRange(retrievalInfo.getStartIndex(), retrievalInfo.getNoOfRecords()
                            + retrievalInfo.getStartIndex());
            query.setFilter("userKey == '" + userKey + "'");
            mapOfFilterValues.put("userKey", userKey);
            developers = (List<Developer>) getJdoTemplate().find(query.toString(),
                            mapOfFilterValues);
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
        return developers;
    }

}

