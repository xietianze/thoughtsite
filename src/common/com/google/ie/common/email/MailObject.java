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

/**
 * An object that represents an e-mail message.
 * This class is used to create a mail object containing the message to be sent
 * along with the addresses of the recipients and the sender.
 * 
 * 
 * @author asirohi
 * 
 */
public class MailObject {
    /* mail id of recipient */
    private String recipientEmail;
    /* message send in mail */
    private String message;
    /* Sender email id. */
    private String senderEmail;
    /* Subject of mail message */
    private String subject;

    public MailObject() {
    }

    /**
     * Initialize a new MailObject with the given recipientEmail,message and
     * subject.
     * 
     * @param recipientEmail email address of the recipient.
     * @param message the message to be sent in the mail.
     * @param subject subject of the mail
     */
    public MailObject(String recipientEmail, String message, String subject) {
        this.recipientEmail = recipientEmail;
        this.message = message;
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String val) {
        this.message = val;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String val) {
        this.recipientEmail = val;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String val) {
        this.senderEmail = val;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String val) {
        this.subject = val;
    }

}

