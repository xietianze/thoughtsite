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

package com.google.ie.common.email;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.ie.common.constants.IdeaExchangeConstants;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

/**
 * A utility class to add mailing task to a mail-queue.
 * 
 * 
 * @author asirohi
 * 
 */
@Component
public class EmailManager {
    /* logger for logging information */
    private static Logger log = Logger.getLogger(EmailManager.class);
    /* Email URL for sending mail */
    private static final String EMAIL_URL = "mail";
    /* Queue used for mailing */
    private static final String MAIL_QUEUE = "mail-queue";
    /* constant used for comma */
    private static final String COMMA = ",";

    /**
     * Create task of sending email based on the given parameters.
     * 
     * @param emailType type of email like invitation to join a project
     * @param recepientEmailIdList list containing email ids.
     * @param otherInfoList list containing other information
     *        For eg. sender's name and project name in case of 'createProject'
     *        type of mail
     */
    public static void sendMail(String emailType, List<String> recepientEmailIdList,
                    List<String> otherInfoList) {
        Queue queue = QueueFactory.getQueue(MAIL_QUEUE);
        String otherInfoString = getStringFromList(otherInfoList);
        String recepientEmailIds = getStringFromList(recepientEmailIdList);
        TaskOptions taskOptions = TaskOptions.Builder.url(
                        IdeaExchangeConstants.BACKSLASH + EMAIL_URL
                        + IdeaExchangeConstants.BACKSLASH + emailType).param(
                        "recepientEmailIds",
                        recepientEmailIds).param("otherInfoString", otherInfoString);
        queue.add(taskOptions);
        log.info("Task for emailing added to queue : "
                        + MAIL_QUEUE);

    }

    /**
     * Convert list of Strings to one comma separated String.
     * 
     * @param otherInfoList list of strings
     * @return comma separated string
     */
    public static String getStringFromList(List<String> otherInfoList) {
        Iterator<String> iterator = otherInfoList.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            String info = iterator.next();
            stringBuilder.append(info);
            if (iterator.hasNext()) {
                stringBuilder.append(COMMA);
            }

        }
        return stringBuilder.toString();
    }
}

