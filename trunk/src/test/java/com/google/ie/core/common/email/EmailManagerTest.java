// Copyright 2009 Google Inc. All Rights Reserved.
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
