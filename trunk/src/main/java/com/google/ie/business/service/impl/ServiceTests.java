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

