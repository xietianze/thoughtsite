// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.ie.test;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;

import org.junit.After;
import org.junit.Before;

import java.io.File;

/**
 * Base class for testing all non transactional service classes. The setUp() and
 * tearDown() methods defined below are responsible for creating and resetting
 * the test environment.
 * 
 * @author asirohi
 * 
 */
public class ServiceTest {

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
