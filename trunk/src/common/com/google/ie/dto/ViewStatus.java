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

package com.google.ie.dto;

import static com.google.ie.web.controller.WebConstants.SUCCESS;

import com.google.ie.web.controller.WebConstants;

import org.apache.log4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An object that is used to messages and data to the view.
 * 
 * @author Akhil
 * 
 */
public class ViewStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Type of result - error, or global error
     */
    private String status;

    /**
     * Data to be sent in the string
     */
    private Map<String, Object> data;

    /**
     * Map of error messages where:
     * Key - Field name
     * Value - Error message
     * In case of global error key is WebConstants.GLOBAL_ERROR
     */
    private Map<String, String> messages;

    private static Logger log = Logger.getLogger(ViewStatus.class);

    /**
     * @param errors
     */
    public static ViewStatus createErrorViewStatus(BindingResult errors) {
        ViewStatus viewStatus = new ViewStatus();
        log.warn("Idea object has " + errors.getErrorCount() + " validation errors");
        viewStatus.setStatus(WebConstants.ERROR);
        for (Iterator<FieldError> iterator = errors.getFieldErrors().iterator(); iterator
                        .hasNext();) {
            FieldError fieldError = iterator.next();
            viewStatus.addMessage(fieldError.getField(), fieldError.getDefaultMessage());
            log.warn("Error found in field: " + fieldError.getField() + " Message :"
                            + fieldError.getDefaultMessage());
        }
        return viewStatus;

    }

    /**
     * @param errors
     */
    public static ViewStatus createProjectErrorViewStatus(BindingResult errors) {
        ViewStatus viewStatus = new ViewStatus();
        log.warn("Project object has " + errors.getErrorCount() + " validation errors");
        viewStatus.setStatus(WebConstants.ERROR);
        for (Iterator<ObjectError> iterator = errors.getAllErrors().iterator(); iterator.hasNext();) {
            ObjectError objError = iterator.next();
            if (objError instanceof FieldError) {
                FieldError fieldError = (FieldError) objError;
                viewStatus.addMessage(WebConstants.ERROR, fieldError.getField() + " - "
                                + fieldError.getDefaultMessage());
                log.warn("Error found in field: " + fieldError.getField() + " Message :"
                                + objError.getDefaultMessage());
            } else {
                viewStatus.addMessage(WebConstants.ERROR, objError.getDefaultMessage());
                log.warn(" Message :"
                                + objError.getDefaultMessage());
            }

        }
        return viewStatus;

    }

    /**
     * Create the {@link ViewStatus} object containing the data retrieved.This
     * object has the status codes set to success/error based on whether the
     * parameter list contains data or not.
     * 
     * @param listOfIdeas the list containing data
     * @param hitCount
     * @return object containing the data and status codes
     */
    public static ViewStatus createTheViewStatus(List<?> listOfData, String objectName,
                    Map<String, ?> parameters) {
        ViewStatus viewStatus = new ViewStatus();
        /*
         * If the list of data is not null and at least one data
         * exists create the view status object with status as 'success' and
         * also set the data.
         */
        if (listOfData != null && listOfData.size() > WebConstants.ZERO) {
            viewStatus.setStatus(SUCCESS);
            viewStatus.addData(objectName, listOfData);
            if (parameters != null) {
                Iterator<String> keysIter = parameters.keySet().iterator();
                String objectKey = null;
                while (keysIter.hasNext()) {
                    objectKey = keysIter.next();
                    viewStatus.addData(objectKey, parameters.get(objectKey));
                }
            }
        } else {/* In case the data list is null or empty */
            viewStatus.setStatus(WebConstants.ERROR);
            viewStatus.addMessage(objectName, WebConstants.NO_RECORDS_FOUND);
        }
        return viewStatus;
    }

    public void addData(String key, Object value) {
        initData();
        data.put(key, value);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public Map<String, Object> getData() {
        initData();
        return data;
    }

    /**
     * Get data object for the given key
     * 
     * @return
     */
    public Object getData(String key) {
        initData();
        return data.get(key);
    }

    public String getMessage(String key) {
        initMessages();
        return messages.get(key);
    }

    public void addMessage(String key, String message) {
        initMessages();
        this.messages.put(key, message);
    }

    /**
     * Initiates Map of data
     */
    private void initData() {
        if (null == data) {
            data = new LinkedHashMap<String, Object>();
        }
    }

    /**
     * Initiates Map of messages
     */
    private void initMessages() {
        if (null == this.messages) {
            this.messages = new LinkedHashMap<String, String>();
        }
    }

}

