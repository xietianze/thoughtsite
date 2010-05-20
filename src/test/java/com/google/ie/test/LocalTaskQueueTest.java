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