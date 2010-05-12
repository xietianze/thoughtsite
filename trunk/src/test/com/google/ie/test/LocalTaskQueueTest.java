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

import com.google.appengine.api.labs.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;

public class LocalTaskQueueTest extends ServiceTest {

    @Override
    public void setUp() {
        super.setUp();
        ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();
        // prevent tasks from automatically executing
        proxy.setProperty(LocalTaskQueue.DISABLE_AUTO_TASK_EXEC_PROP, Boolean.TRUE.toString());
    }

    @Override
    public void tearDown() {
        ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();
        LocalTaskQueue ltq = (LocalTaskQueue) proxy.getService(LocalTaskQueue.PACKAGE);
        // clear out all tasks in all queues
        for (String queueName : ltq.getQueueStateInfo().keySet()) {
            ltq.flushQueue(queueName);
        }
        super.tearDown();
    }
}

