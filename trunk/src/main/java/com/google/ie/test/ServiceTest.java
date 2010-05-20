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

