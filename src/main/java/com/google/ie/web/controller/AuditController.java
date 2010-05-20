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

import com.google.ie.business.domain.Audit;
import com.google.ie.business.service.AuditService;
import com.google.ie.dto.ViewStatus;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

/**
 * A controller that handles request for Auditing.It store information regarding
 * all the update operation.
 * 
 * @author asirohi
 * 
 */

@Controller
public class AuditController {
    /* Logger for logging system */
    private static Logger log = Logger.getLogger(AuditController.class);

    /* Interface of audit service */
    @Autowired
    private AuditService auditService;

    /**
     * Handles the request for saving Audit entity.
     * This request is initiated by TaskQueue for auditing the user actions on
     * the entity.
     */
    @RequestMapping("/audits/save")
    public String saveAudit(@ModelAttribute Audit audit, BindingResult result,
                    Map<String, Object> model) {

        ViewStatus viewstStatus = new ViewStatus();

        if (result.hasErrors()) {
            log.warn("Audit object has " + result.getErrorCount() + " validation errors");
            viewstStatus.setStatus(WebConstants.ERROR);
            viewstStatus.addMessage(WebConstants.ERROR, "Audit object has validation errors");
        } else {
            auditService.saveAudit(audit);
            viewstStatus.setStatus(WebConstants.SUCCESS);
            viewstStatus.addMessage(WebConstants.SUCCESS, "Auditing is successfully done.");
        }
        model.put(WebConstants.AUDIT, viewstStatus);
        return "queue/queue";
    }

    /**
     * Register custom binders for Spring. Needed to run on app engine
     * 
     * @param binder
     * @param request
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor(true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(DateFormat
                        .getDateTimeInstance(DateFormat.FULL, DateFormat.FULL), true));
    }

    /**
     * Getter for Audit Service.
     * 
     * @return the auditService
     */
    public AuditService getAuditService() {
        return auditService;
    }

    /**
     * Setter for audit service.
     * 
     * @param auditService the auditService to set
     */
    public void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

}

