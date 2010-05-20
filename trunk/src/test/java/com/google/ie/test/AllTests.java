// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.test;

import com.google.ie.business.dao.impl.DaoTests;
import com.google.ie.business.domain.IdeaTest;
import com.google.ie.business.service.impl.ServiceTests;
import com.google.ie.common.CommonTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Akhil
 * 
 */
@RunWith(Suite.class)
@SuiteClasses( { DaoTests.class, ServiceTests.class, CommonTests.class, IdeaTest.class,
        RegularExpressionTest.class })
public class AllTests {

}
