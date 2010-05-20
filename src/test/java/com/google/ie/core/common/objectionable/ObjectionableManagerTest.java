// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * 
 */
package com.google.ie.core.common.objectionable;

import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.labs.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.test.LocalTaskQueueTest;

import org.junit.Test;

import junit.framework.Assert;

/**
 * @author gmaurya
 * 
 */
public class ObjectionableManagerTest extends LocalTaskQueueTest {

    /**
     * 
     */
    @Test
    public void checkObjectionable() {       
        QueueFactory.getDefaultQueue().add(TaskOptions.Builder.taskName(IdeaExchangeConstants.CHECK_OBJECTIONABLE_TASK));
        ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();
        LocalTaskQueue ltq = (LocalTaskQueue) proxy.getService(LocalTaskQueue.PACKAGE);
        String defaultQueueName = QueueFactory.getDefaultQueue().getQueueName();
        QueueStateInfo qsi = ltq.getQueueStateInfo().get(defaultQueueName);
        Assert.assertEquals(1, qsi.getTaskInfo().size());
        Assert.assertEquals(IdeaExchangeConstants.CHECK_OBJECTIONABLE_TASK, qsi.getTaskInfo().get(0).getTaskName());

    }
}
