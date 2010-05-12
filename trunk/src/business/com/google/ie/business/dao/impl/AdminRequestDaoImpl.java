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

import com.google.ie.business.dao.AdminRequestDao;
import com.google.ie.business.domain.AdminRequest;
import com.google.ie.dto.RetrievalInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Query;

/**
 * A JDO implementation object for {@link AdminRequestDao}.
 * 
 * @author asirohi
 * 
 */
public class AdminRequestDaoImpl extends BaseDaoImpl implements AdminRequestDao {
    private static Logger logger = Logger.getLogger(AdminRequestDaoImpl.class);

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean saveRequest(AdminRequest adminRequest) {
        adminRequest = getJdoTemplate().makePersistent(adminRequest);
        if (adminRequest != null && !StringUtils.isBlank(adminRequest.getKey())) {
            logger.info("Admin Request for " + adminRequest.getRequestType() + " "
                            + adminRequest.getEntityType() + " saved successfully");
            return true;
        }
        logger.error("Saving of AdminRequest object failed");
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AdminRequest> getAllAdminRequests(RetrievalInfo retrievalInfo) {
        Query query = null;
        try {
            query = getJdoTemplate().getPersistenceManagerFactory()
                            .getPersistenceManager().newQuery(AdminRequest.class);
            /*
             * Add the start index to the number of records required since
             * internally the second argument is treated as the index up to
             * which
             * the entities are to be fetched
             */
            query.setRange(retrievalInfo.getStartIndex(), retrievalInfo.getStartIndex()
                            + retrievalInfo.getNoOfRecords());
            query.setFilter("status == '" + AdminRequest.STATUS_PENDING + "'");
            Map<String, Object> mapOfFilterValues = new HashMap<String, Object>();
            mapOfFilterValues.put("status", AdminRequest.STATUS_PENDING);
            Collection<AdminRequest> collection = getJdoTemplate().find(query.toString(),
                            mapOfFilterValues);
            if (collection != null && collection.size() > DaoConstants.ZERO) {
                logger.info(collection.size() + " admin requests found for the Idea or Comment");
                List<AdminRequest> requestList = (new ArrayList<AdminRequest>(collection));
                return requestList;
            }
        } finally {
            if (query != null) {
                query.closeAll();
            }
        }
        logger.info("No admin requests for the Idea or Comment");
        return null;
    }
}

