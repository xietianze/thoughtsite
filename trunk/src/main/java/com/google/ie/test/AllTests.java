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

