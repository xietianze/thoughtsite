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

package com.google.ie.common.audit;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.ie.common.constants.IdeaExchangeConstants;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;

/**
 * An object that manages the audit trail of the system.
 * This class is used to queue a task for saving a track of every updation
 * activity on the Idea Exchange website.
 * 
 * @author asirohi
 * 
 */
@Component
public class AuditManager {

    private static final Logger log = Logger.getLogger(AuditManager.class);

    /**
     * Add the task to a {@link Queue} to audit the user action.
     * 
     * @param userKey the key of the user whose action is to be audited
     * @param entityKey the key of the entity on which the action was performed
     * @param action the action performed
     */
    public void audit(String userKey, String entityKey, String entityType, String action) {
        /* Use Task Queue to queue the task to audit. */
        Queue queue = QueueFactory.getQueue(IdeaExchangeConstants.TASK_QUEUE);
        TaskOptions taskOptions = TaskOptions.Builder.url(IdeaExchangeConstants.AUDIT_URL)
                        .param(IdeaExchangeConstants.USER_KEY, userKey)
                        .param(IdeaExchangeConstants.ENTITY_KEY, entityKey)
                        .param(IdeaExchangeConstants.ENTITY_TYPE, entityType)
                        .param(
                        IdeaExchangeConstants.AUDIT_DATE,
                        DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(
                        new Date()))
                        .param(IdeaExchangeConstants.ACTION, action);
        queue.add(taskOptions);
        if (log.isDebugEnabled()) {
            log.debug("Task for auditing added to queue :"
                            + IdeaExchangeConstants.TASK_QUEUE);
        }

    }
}

