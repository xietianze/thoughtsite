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

package com.google.ie.core.common.email;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.google.ie.common.email.EmailManager;
import com.google.ie.test.TestEnvironment;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author asirohi
 * 
 */
public class EmailManagerTest {

    @Before
    public void setUp() throws Exception {
        ApiProxy.setEnvironmentForCurrentThread(new TestEnvironment());
        ApiProxy.setDelegate(new ApiProxyLocalImpl(new File(".")) {
        });

    }

    @After
    public void tearDown() throws Exception {
        // not strictly necessary to null these out but there's no harm either
        ApiProxy.setDelegate(null);
        ApiProxy.setEnvironmentForCurrentThread(null);
    }

    @Test
    public void sendMail() {

        // try {
        // EmailManager.sendMail("createProject", "anuj",
        // "anujsirohi@gmail.com,anuj.sirohi@impetus.co.in", "CloudB");
        // } catch (IdeasExchangeException e) {
        // Assert.fail("Unexpected exception encountered");
        // }
    }

    @Test
    public void getOtherString() {

        List<String> list = new ArrayList<String>();
        list.add("name");
        list.add("projectName");
        list.add("otherInfo");

        Assert.assertEquals("name,projectName,otherInfo", EmailManager.getStringFromList(list));

    }
}

