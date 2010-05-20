// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.business.dao.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for data access classes
 * 
 * @author Akhil
 * 
 */
@RunWith(Suite.class)
@SuiteClasses( { AuditDaoImplTest.class, EntityIndexDaoImplTest.class,
        IdeaCategoryDaoImplTest.class, IdeaDaoImplTest.class, TagDaoImplTest.class,
        UserDaoImplTest.class, VoteDaoImplTest.class, ShardedCounterDaoImplTest.class,
        CommentDaoImplTest.class, ProjectDaoImplTest.class, AdminRequestDaoImplTest.class })
public class DaoTests {

}
