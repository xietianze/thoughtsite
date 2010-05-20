package com.google.ie.test;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.google.ie.dto.RetrievalInfo;

import org.junit.After;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import javax.jdo.JDOHelper;

/**
 * Base class for testing all data access classes.
 * 
 * @author abraina
 * 
 */
@ContextConfiguration(locations = { "test-app-context.xml" })
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional
public class DatastoreTest extends AbstractJUnit4SpringContextTests {

    /** Persistence manager factory to be used in tests */
    protected static javax.jdo.PersistenceManagerFactory pmf = JDOHelper
                    .getPersistenceManagerFactory("transactions-optional");

    @Before
    public void setUp() {

        // Set test environment
        ApiProxy.setEnvironmentForCurrentThread(new TestEnvironment());
        ApiProxy.setDelegate(new ApiProxyLocalImpl(new File(".")) {
        });
        ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();
        // Local datastore should not save test objects to file
        proxy.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY, Boolean.TRUE.toString());

    }

    @After
    public void tearDown() {
        // Set environment as test environment
        ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();
        LocalDatastoreService datastoreService =
                        (LocalDatastoreService) proxy.getService(LocalDatastoreService.PACKAGE);
        datastoreService.clearProfiles();
        // Setting api proxy to null. Not necessary but a good practice.
        ApiProxy.setDelegate(null);
        ApiProxy.setEnvironmentForCurrentThread(null);
    }

    protected RetrievalInfo createDummyRetrievalParam(int startindex, int noOfRecords,
                    String orderOn, String orderBy) {
        return new RetrievalInfo(startindex, noOfRecords, orderOn, orderBy);
    }
}
