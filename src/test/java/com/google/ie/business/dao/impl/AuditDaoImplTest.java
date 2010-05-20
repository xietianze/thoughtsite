// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.business.dao.impl;

import static org.junit.Assert.assertEquals;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.ie.business.domain.Audit;
import com.google.ie.test.DatastoreTest;

import org.junit.Test;
import org.springframework.context.annotation.Scope;

/**
 * @author asirohi
 * 
 */
@Scope
public class AuditDaoImplTest extends DatastoreTest {

    private AuditDaoImpl auditDao;

    @Override
    public void setUp() {
        super.setUp();
        if (null == auditDao) {
            auditDao = new AuditDaoImpl();
            auditDao.setPersistenceManagerFactory(pmf);

        }
    }

    /**
     * Test method for
     * {@link com.google.ie.business.dao.impl.AuditDaoImpl#saveAudit(com.google.ie.business.domain.Audit)}
     * .
     */
    @Test
    public void saveAudit() {
        Audit audit = new Audit();
        auditDao.saveAudit(audit);

        Query query = new Query(Audit.class.getSimpleName());
        assertEquals(1, DatastoreServiceFactory.getDatastoreService().prepare(query)
                        .countEntities());
    }

}
