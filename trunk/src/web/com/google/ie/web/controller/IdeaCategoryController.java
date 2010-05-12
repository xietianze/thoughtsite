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

import com.google.ie.business.domain.IdeaCategory;
import com.google.ie.business.service.IdeaCategoryService;
import com.google.ie.dto.ViewStatus;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * A controller that handles requests for idea categories.
 * 
 * @author Sachneet
 * 
 */
@Controller
public class IdeaCategoryController {
    private static Logger log = Logger.getLogger(IdeaCategoryController.class);
    @Autowired
    private IdeaCategoryService ideaCategoryService;

    public IdeaCategoryController() {
    }

    /**
     * Handles the request for getting all categories.
     * 
     * @return View name.
     */
    @RequestMapping("/ideas/categories.json")
    public void getAllCategories(Map<String, Object> model) {
        List<IdeaCategory> listOfCategories =
                        ideaCategoryService.getAllIdeaCategories();
        log
                        .debug("Number of categories fetched are:    " + listOfCategories != null ? listOfCategories
                                        .size()
                                        : WebConstants.ZERO);
        ViewStatus status = ViewStatus.createTheViewStatus(listOfCategories,
                        WebConstants.CATEGORIES, null);
        model.put(WebConstants.VIEW_STATUS, status);
    }

    public void setIdeaCategoryService(IdeaCategoryService ideaCategoryService) {
        this.ideaCategoryService = ideaCategoryService;
    }

    public IdeaCategoryService getIdeaCategoryService() {
        return ideaCategoryService;
    }

}

