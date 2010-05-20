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

import com.google.ie.business.domain.Idea;
import com.google.ie.business.service.IdeaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller for handling the data required for home page.
 * 
 * @author aksrivastava
 * 
 */
@Controller
public class HomeController {
    @Autowired
    private IdeaService ideaService;

    public HomeController() {

    }

    /**
     * Handles the request for serving home page.
     * 
     * @return View name.
     */
    @RequestMapping("/")
    public String home(Model model) {
        /* Retrieve the recent idea list and put it into the Model */
        List<Idea> recentIdeas = ideaService.getRecentIdeas();
        model.addAttribute(WebConstants.RECENT_IDEAS_MODEL_KEY, recentIdeas);
        /* Retrieve the popular idea list and put it into the Model */
        List<Idea> popularIdeas = ideaService.getPopularIdeas();
        model.addAttribute(WebConstants.POPULAR_IDEAS_MODEL_KEY, popularIdeas);
        /* Retrieve the recently picked idea list and put it into the Model */
        List<Idea> recentlyPickedIdeas = ideaService.getRecentlyPickedIdeas();
        model.addAttribute(WebConstants.RECENTLY_PICKED_IDEA_MODEL_KEY,
                        recentlyPickedIdeas);

        return "home";
    }

}

