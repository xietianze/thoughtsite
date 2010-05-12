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

package com.google.ie.common.objectionable;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.ie.common.constants.IdeaExchangeConstants;

/**
 * An object that check the objectionable content in idea.
 * This class is used to queue a task for checking objectionable content
 * when ever an Idea gets published.
 * 
 * @author gmaurya
 */
public class ObjectionableManager {

    /**
     * Add the task to check objectionable content to the specific queue.
     * 
     * @param ideaKey key of the idea to be checked
     */
    public static void checkObjectionable(String key) {
        /* Use Task Queue to queue the task for checking objectionable content. */
        Queue queue = QueueFactory.getQueue(IdeaExchangeConstants.OBJECTIONABLE_QUEUE);
        TaskOptions taskOptions = TaskOptions.Builder.url(
                        IdeaExchangeConstants.CHECK_OBJECTIONABLE_URL
                        + IdeaExchangeConstants.BACKSLASH + key);
        queue.add(taskOptions);

    }

    /**
     * It's a worker task queue which is initiated by main task queue for
     * checking the objectionable content on particular attribute of Idea.
     * 
     * @param ideaKey key of Idea
     * @param fieldType marker for the field of Idea to be checked
     */
    public static void startCheckObjectionableWorker(String key, String fieldName) {
        /* Use Task Queue to check objectionable content in idea attributes. */
        Queue queue = QueueFactory.getQueue(IdeaExchangeConstants.OBJECTIONABLE_WORKER_QUEUE);
        TaskOptions taskOptions = TaskOptions.Builder.url(
                        IdeaExchangeConstants.CHECK_OBJECTIONABLE_WORKER_URL
                        + IdeaExchangeConstants.BACKSLASH + key
                        + IdeaExchangeConstants.BACKSLASH + fieldName);
        queue.add(taskOptions);

    }
}

