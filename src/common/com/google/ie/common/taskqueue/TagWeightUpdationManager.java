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

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.ie.common.constants.IdeaExchangeConstants;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * An object that helps manage the weight calculation of tags {@link Tag}.
 * This class is used to queue a task for incrementing the weight of tag when
 * tag is associated with a published idea.
 * 
 * @author Charanjeet singh
 */
@Component
public class TagWeightUpdationManager {
    /** Logger for the class */
    private static Logger logger = Logger.getLogger(TagWeightUpdationManager.class);
    private final boolean isDebug = logger.isDebugEnabled();

    /**
     * Publishes the task of incrementing the weight of tags to a task queue.
     * It adds a task to increment the weight of tags passed as a comma
     * separated string.
     * 
     * @param tagString a comma separated string.
     * 
     */
    public void incrementWeight(String tagString) {
        /* Use Task Queue to queue the task to increment the weight of tags. */
        Queue queue = QueueFactory.getQueue(IdeaExchangeConstants.TAGS_WEIGHT_UPDATION_QUEUE);
        TaskOptions taskOptions = TaskOptions.Builder.url(
                        IdeaExchangeConstants.TAGS_WEIGHT_UPDATION_URL)
                        .param(IdeaExchangeConstants.COMMA_SEPARATED_TAG_STRING, tagString);
        queue.add(taskOptions);
        if (isDebug)
            logger.debug("Task to increment the weight of tag in tag string '" + tagString
                            + "', added to queue :"
                            + IdeaExchangeConstants.TAGS_WEIGHT_UPDATION_QUEUE);
    }

    /**
     * Publishes the task of decrementing the weight of tags to a task queue.
     * It adds a task to decrement the weight of tags passed as a comma
     * separated string.
     * 
     * @param tagString a comma separated string.
     * 
     */
    public void decrementWeight(String tagString) {
        /* Use Task Queue to queue the task to decrement the weight of tags. */
        Queue queue = QueueFactory.getQueue(IdeaExchangeConstants.TAGS_WEIGHT_UPDATION_QUEUE);
        TaskOptions taskOptions = TaskOptions.Builder.url(
                        IdeaExchangeConstants.TAGS_WEIGHT_DECREMENT_URL)
                        .param(IdeaExchangeConstants.COMMA_SEPARATED_TAG_STRING, tagString);
        queue.add(taskOptions);
        if (isDebug)
            logger.debug("Task to decrement the weight of tag in tag string '" + tagString
                            + "', added to queue :"
                            + IdeaExchangeConstants.TAGS_WEIGHT_UPDATION_QUEUE);
    }
}

