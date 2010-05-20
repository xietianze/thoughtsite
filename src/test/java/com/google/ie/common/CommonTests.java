// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.common;

import com.google.ie.common.cache.CacheHelperTest;
import com.google.ie.common.util.GsonUtilityTest;
import com.google.ie.core.common.audit.AuditManagerTest;
import com.google.ie.core.common.email.EmailManagerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for common package
 * 
 * @author Akhil
 * 
 */
@RunWith(Suite.class)
@SuiteClasses( { CacheHelperTest.class, GsonUtilityTest.class, AuditManagerTest.class,
        EmailManagerTest.class })
public class CommonTests {

}
