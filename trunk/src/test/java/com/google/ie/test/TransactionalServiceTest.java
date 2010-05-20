// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.test;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;

import org.junit.After;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * Base test class for all service method tests that need spring transactions.
 * The setUp() and tearDown() methods defined below are responsible for creating
 * and resetting the test environment.
 * 
 * @author Akhil
 */
@ContextConfiguration(locations = { "test-app-context.xml" })
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional
public class TransactionalServiceTest extends AbstractJUnit4SpringContextTests {

    @Before
    public void setUp() {
        // Set environment as test environment
        ApiProxy.setEnvironmentForCurrentThread(new TestEnvironment());
        ApiProxy.setDelegate(new ApiProxyLocalImpl(new File(".")) {
        });
    }

    @After
    public void tearDown() {
        // Setting api proxy to null. Not necessary but a good practice.
        ApiProxy.setDelegate(null);
        ApiProxy.setEnvironmentForCurrentThread(null);
    }

}
