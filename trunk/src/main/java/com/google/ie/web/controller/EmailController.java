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

package com.google.ie.web.controller;

import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.exception.IdeasExchangeException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.servlet.http.HttpServletRequest;

/**
 * Handle request to send mails.
 * 
 * @author asirohi
 * 
 */
@Controller
@RequestMapping("/mail")
public class EmailController {
    private static Logger log = Logger.getLogger(EmailController.class);

    private static final String PROJECT_INVITE_MAIL_SUBJECT = "Invite to join a Project";
    private static final String COMMA = ",";
    private static final String SEMICOLON = ";";
    private static final String MAIL_PROJECT_INVITE_KEY = "mail.project.invite";
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int FOUR = 4;
    @Autowired
    private MessageSource messageSource;
    @Value("${adminMailId}")
    private String adminMailId;

    /**
     * Handle request for sending mails when a user create a project
     * and invite friends to become members.
     * 
     * @throws IdeasExchangeException
     * @throws MessagingException
     * @throws AddressException
     * 
     */
    @RequestMapping(value = "/joinProject", method = RequestMethod.POST)
    public String inviteToJoinProject(@RequestParam(required = false) String recepientEmailIds,
                    @RequestParam(required = false) String otherInfoString, Locale locale,
                    HttpServletRequest req)
                    throws IdeasExchangeException, AddressException, MessagingException {
        String serverName = req.getServerName();
        String projectKey;
        String ownerName;
        String projectName;
        if (otherInfoString != null) {
            /* getting data which are required for sending mail. */
            String infoData[] = otherInfoString.split(COMMA);
            if (infoData.length > TWO) {
                ownerName = infoData[ZERO];
                projectName = infoData[ONE];
                projectKey = infoData[TWO];
                /* Iterating through all mail ids and sending messages. */
                for (String emailIdAndName : recepientEmailIds.split(COMMA)) {
                    String info[] = emailIdAndName.split(SEMICOLON);
                    if (info.length > TWO) {
                        String displayName = info[ONE];
                        String emailId = info[ZERO];
                        String developerKey = info[TWO];
                        String[] message = getMessageToSend(projectKey, ownerName, projectName,
                                        displayName,
                                        developerKey, emailId, serverName);
                        String emailText = messageSource.getMessage(MAIL_PROJECT_INVITE_KEY,
                                        message, locale);
                        log.info("Sending Mail to : " + emailId + " Text: " + emailText);
                        sendMail(emailId, emailText, PROJECT_INVITE_MAIL_SUBJECT);
                    }
                }
            }
        }
        return "queue/queue";
    }

    /**
     * @param displayName
     * @param projectName
     * @param ownerName
     * @param projectKey
     * @return
     */
    private String[] getMessageToSend(String projectKey, String ownerName, String projectName,
                    String displayName, String developerKey, String emailId, String serverName) {
        StringBuilder messages = new StringBuilder();
        messages.append(ownerName);
        messages.append("," + projectName);
        messages.append("," + serverName + "/projects/joinProject"
                        + IdeaExchangeConstants.BACKSLASH
                        + projectKey
                        + IdeaExchangeConstants.BACKSLASH + developerKey
                        + IdeaExchangeConstants.BACKSLASH + emailId);
        return messages.toString().split(COMMA);
    }

    /**
     * Send mail to the given email id with the provided text and subject.
     * 
     * @param recepientEmailId email id of the recepient
     * @param emailText text of the mail
     * @param subject subject of the mail
     * @throws IdeasExchangeException
     * @throws MessagingException
     * @throws AddressException
     */
    protected void sendMail(String recepientEmailId, String emailText,
                    String subject) throws IdeasExchangeException, AddressException,
                    MessagingException {
        Properties prop = new Properties();
        Session session = Session.getDefaultInstance(prop, null);

        Message message = new MimeMessage(session);

        message.setRecipient(RecipientType.TO, new InternetAddress(recepientEmailId));
        message.setFrom(new InternetAddress(getAdminMailId()));

        message.setText(emailText);
        message.setSubject(subject);

        Transport.send(message);
        log.info("Mail sent successfully to : " + recepientEmailId + " for " + subject);
    }

    /**
     * Handle request for sending mails to only one email id with the given ","
     * separated information string.
     * 
     * @param recepientEmailId Email id of the recepient
     * @param otherInfoString "'" separated string containing the message key
     *        and other message parameters.
     * @param locale
     * @return
     * @throws IdeasExchangeException
     * @throws MessagingException
     * @throws AddressException
     */
    @RequestMapping(value = "/singleMail", method = RequestMethod.POST)
    public String singleMail(@RequestParam(required = false) String recepientEmailIds,
                    @RequestParam(required = false) String otherInfoString, Locale locale)
                    throws IdeasExchangeException, AddressException, MessagingException {
        if (!StringUtils.isBlank(otherInfoString)) {
            String infoData[] = otherInfoString.split(COMMA);

            if (!StringUtils.isBlank(recepientEmailIds)) {
                String emailText = messageSource.getMessage(infoData[FOUR],
                                infoData, locale);
                log.info("Sending Mail to : " + recepientEmailIds + " Text: " + emailText);
                sendMail(recepientEmailIds, emailText, infoData[ZERO] + " is " + infoData[TWO]);
            }
        }
        return "queue/queue";

    }

    /**
     * @return the messageSource
     */
    public MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * @param messageSource the messageSource to set
     */
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * This method return the admin mail id which will be used for from mailid.
     * 
     * @return String
     */
    public String getAdminMailId() {
        if (adminMailId != null) {
            return adminMailId.trim();
        }
        return adminMailId;
    }

}

