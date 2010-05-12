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

import com.google.ie.business.dao.AuditDao;
import com.google.ie.business.domain.Audit;
import com.google.ie.business.service.AuditService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service implementation of the {@link AuditService}.
 * 
 * @author asirohi
 * 
 */
@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private AuditDao auditDao;

    @Override
    public void saveAudit(Audit audit) {
        auditDao.saveAudit(audit);
    }

    /**
     * @return the auditDao
     */
    public AuditDao getAuditDao() {
        return auditDao;
    }

    /**
     * @param auditDao the auditDao to set
     */
    public void setAuditDao(AuditDao auditDao) {
        this.auditDao = auditDao;
    }

}

