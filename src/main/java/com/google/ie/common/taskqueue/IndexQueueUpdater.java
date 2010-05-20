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

package com.google.ie.common.taskqueue;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.ie.common.constants.IdeaExchangeConstants;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * An object that manages the indexing queue of the system.
 * This class is used to queue a task for indexing an entity.
 * 
 * @author asirohi
 * 
 */
@Component
public class IndexQueueUpdater {
    /** Logger for the class */
    private static Logger log = Logger.getLogger(IndexQueueUpdater.class);

    /**
     * Add the task to a {@link Queue} to index entity.
     * 
     */
    public void indexEntity(Key indexKey) {
        /* Use Task Queue to queue the task to index. */
        Queue queue = QueueFactory.getQueue(IdeaExchangeConstants.INDEX_QUEUE);
        String keyString = KeyFactory.keyToString(indexKey);
        TaskOptions taskOptions = TaskOptions.Builder.url(IdeaExchangeConstants.INDEX_URL
                        + IdeaExchangeConstants.BACKSLASH + keyString);

        queue.add(taskOptions);
        if (log.isDebugEnabled()) {
            log.debug("Task for indexing added to queue :"
                            + IdeaExchangeConstants.INDEX_QUEUE);
        }
    }
}

