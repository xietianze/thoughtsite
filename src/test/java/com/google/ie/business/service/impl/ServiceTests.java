// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.business.service.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for service classes
 * 
 * @author Akhil
 */
@RunWith(Suite.class)
@SuiteClasses( { EntityIndexServiceImplTest.class, IdeaCategoryServiceImplTest.class,
        IdeaServiceImplTest.class, TagServiceImplTest.class, IdeaVoteServiceImplTest.class,
        ShardedCounterServiceImplTest.class, IdeaCommentServiceImplTest.class,
        CommentVoteServiceImplTest.class, ProjectServiceImplTest.class, AdminServiceImplTest.class,
        DeveloperServiceImplTest.class, UserServiceImplTest.class })
public class ServiceTests {

}
