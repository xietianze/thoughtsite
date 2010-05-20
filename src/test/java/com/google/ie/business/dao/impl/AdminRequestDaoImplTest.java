// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.business.dao.impl;

import static org.junit.Assert.assertEquals;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.ie.business.domain.AdminRequest;
import com.google.ie.business.domain.Idea;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.DatastoreTest;

import org.junit.Test;

import java.util.Date;

/**
 * @author asirohi
 * 
 */
public class AdminRequestDaoImplTest extends DatastoreTest {
    private AdminRequestDaoImpl adminRequestDao;

    @Override
    public void setUp() {
        super.setUp();
        if (adminRequestDao == null) {
            adminRequestDao = new AdminRequestDaoImpl();
            adminRequestDao.setPersistenceManagerFactory(pmf);
        }
    }

    @Test
    public void addRequest() {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setEntityKey("ideakey");
        adminRequest.setEntityType(Idea.class.getSimpleName());
        adminRequest.setRequesterkey("userkey");

        adminRequest.setRequestType(AdminRequest.REQUEST_OBJECTIONABLE);
        adminRequest.setCreatedOn(new Date());

        /* Get title of the idea */
        Idea idea = new Idea();
        idea.setTitle("Great Idea");

        adminRequest.setEntityTitle(idea.getTitle());

        adminRequestDao.saveRequest(adminRequest);

        Query query = new Query(AdminRequest.class.getSimpleName());
        assertEquals(1, DatastoreServiceFactory.getDatastoreService().prepare(query)
                        .countEntities());
    }

    /**
     * 
     */
    @Test
    public void getAllAdminRequests() {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setEntityKey("ideakey");
        adminRequest.setEntityType(Idea.class.getSimpleName());
        adminRequest.setRequesterkey("userkey");
        adminRequest.setRequestType(AdminRequest.REQUEST_OBJECTIONABLE);
        adminRequest.setCreatedOn(new Date());
        adminRequest.setStatus(AdminRequest.STATUS_PENDING);
        adminRequest.setEntityTitle("idea title");

        adminRequestDao.saveRequest(adminRequest);

        AdminRequest adminRequest2 = new AdminRequest();
        adminRequest2.setEntityKey("ideakey");
        adminRequest2.setEntityType(Idea.class.getSimpleName());
        adminRequest2.setRequesterkey("userkey");
        adminRequest2.setRequestType(AdminRequest.REQUEST_OBJECTIONABLE);
        adminRequest2.setCreatedOn(new Date());
        adminRequest.setStatus(AdminRequest.STATUS_REJECTED);
        adminRequest2.setEntityTitle("idea title2");

        adminRequestDao.saveRequest(adminRequest2);

        RetrievalInfo retrievalInfo = this.createDummyRetrievalParam(0, 10, "createdOn", "asc");
        adminRequestDao.getAllAdminRequests(retrievalInfo);

        assertEquals(1, adminRequestDao.getAllAdminRequests(retrievalInfo).size());
    }
}
