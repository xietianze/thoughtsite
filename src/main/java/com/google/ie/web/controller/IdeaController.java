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

import static com.google.ie.web.controller.WebConstants.DUPLICATE;
import static com.google.ie.web.controller.WebConstants.ERROR;
import static com.google.ie.web.controller.WebConstants.FLAG;
import static com.google.ie.web.controller.WebConstants.SUCCESS;
import static com.google.ie.web.controller.WebConstants.VIEW_STATUS;

import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.IdeaVote;
import com.google.ie.business.domain.Tag;
import com.google.ie.business.domain.User;
import com.google.ie.business.domain.Vote;
import com.google.ie.business.service.CommentService;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.TagService;
import com.google.ie.business.service.UserService;
import com.google.ie.business.service.VoteService;
import com.google.ie.common.builder.IdeaBuilder;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.editor.StringEditor;
import com.google.ie.common.exception.IdeasExchangeException;
import com.google.ie.common.util.ReCaptchaUtility;
import com.google.ie.common.util.SearchUtility;
import com.google.ie.dto.IdeaDetail;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.dto.SearchResult;
import com.google.ie.dto.ViewStatus;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A controller that handles requests for ideas.
 * 
 * @author Akhil
 */
@Controller
@RequestMapping("/ideas")
@SessionAttributes("user")
public class IdeaController {
    /* Authentication cookie name read from properties file */
    @Value("${rcGlobalPublicKey}")
    private String rcGlobalPublicKey;
    private static final Logger log = Logger.getLogger(IdeaController.class);
    @Autowired
    private CommentService commentService;
    @Autowired
    private IdeaService ideaService;
    @Autowired
    private TagService tagService;
    @Autowired
    private IdeaBuilder ideaBuilder;
    @Autowired
    @Qualifier("ideaVoteServiceImpl")
    private VoteService voteService;
    @Autowired
    private ReCaptchaUtility reCaptchaUtility;
    @Autowired
    @Qualifier("tagTitleValidator")
    private Validator stringValidatorForTagTitle;
    @Autowired
    @Qualifier("ideaValidator")
    private Validator ideaValidator;
    @Autowired
    private UserService userService;

    /**
     * Handles the request for search/list idea.
     * 
     * @param model Carries data for the view
     * @return View name.
     */
    @RequestMapping(value = "/list")
    public String listIdeas() {
        return "ideas/list";
    }

    /**
     * Add the summary of an idea
     * 
     * @param key the key of the {@link Idea} object
     * @param origSumm the summary text
     * @param user the user adding the summary
     * @param model the data map
     * 
     * @return View name.
     */
    @RequestMapping(value = "/addsummary/{key}", method = RequestMethod.POST)
    public String addSummary(@PathVariable String key,
                    @RequestParam(required = true) String origSumm, HttpSession session,
                    Model model) {
        User user = (User) session.getAttribute(WebConstants.USER);
        /* Add the summary and update datastore */
        getIdeaService().addSummary(key, origSumm, user);
        ViewStatus viewStatus = new ViewStatus();
        model.addAttribute(VIEW_STATUS, viewStatus);
        viewStatus.setStatus(WebConstants.SUCCESS);
        return "ideas/show";
    }

    /**
     * Handles the request for search/list idea.
     * 
     * @param model Carries data for the view
     * @return View name.
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String listIdeas(@ModelAttribute RetrievalInfo retrievalInfo, Map<String, Object> model) {
        /* Fetch the range parameters as sent in the request */
        long startIndex = retrievalInfo.getStartIndex();
        long noOfRecordsRequested = retrievalInfo.getNoOfRecords();
        /* Get the idea list */
        List<IdeaDetail> ideas = ideaBuilder.getIdeasForListing(retrievalInfo);
        /* Map of data to be inserted into the view status object */
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        /* Map containing the previous and next index values */
        HashMap<String, Long> pagingMap = new HashMap<String, Long>();
        /*
         * If the size of the list is greater than the no. of records requested
         * ,set the parameter 'next' to be used as start index for the next
         * page retrieval.
         */
        if (ideas != null && ideas.size() > noOfRecordsRequested) {
            pagingMap.put(WebConstants.NEXT, startIndex + noOfRecordsRequested);
        } else {
            /*
             * If the list size is not greater than the number requested set
             * the 'next' parameter to minus one
             */
            pagingMap.put(WebConstants.NEXT, (long) WebConstants.MINUS_ONE);
        }
        /*
         * Set the parameter 'previous' to be used as the start index for the
         * previous page retrieval
         */
        pagingMap.put(WebConstants.PREVIOUS, startIndex - noOfRecordsRequested);
        /* Add the map containing the paging values to the map of parameters */
        parameters.put(WebConstants.PAGING, pagingMap);
        ViewStatus viewStatus = ViewStatus
                        .createTheViewStatus(ideas, WebConstants.IDEAS, parameters);

        model.put(VIEW_STATUS, viewStatus);
        return "ideas/list";
    }

    /**
     * Handles the request for saving of newly created idea.
     * 
     * @param idea Idea to be saved.
     * @param user Idea creator.
     * @return View name.
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveIdea(@ModelAttribute Idea idea, BindingResult errors,
                    HttpSession session, Model model) {
        String keyStr = null;
        ViewStatus viewStatus = null;
        if (!StringUtils.isBlank(idea.getTags())) {
            /* Validate the idea */
            stringValidatorForTagTitle.validate(idea.getTags(), errors);
        }
        if (errors.hasErrors()) {
            viewStatus = ViewStatus.createErrorViewStatus(errors);
            idea.setTags("");
            viewStatus.addData(WebConstants.IDEA, idea);
            model.addAttribute(WebConstants.VIEW_STATUS, viewStatus);
            return "ideas/edit";
        }
        idea = handleTag(idea);
        User user = (User) session.getAttribute(WebConstants.USER);
        Idea savedIdea = ideaService.saveIdea(idea, user);
        keyStr = savedIdea.getKey();
        if (!StringUtils.isBlank(keyStr)) {
            viewStatus = new ViewStatus();
            viewStatus.setStatus(SUCCESS);
            viewStatus.addMessage(WebConstants.VIEW_STATUS, "Idea saved Successfully");
            viewStatus.addData(WebConstants.IDEA, savedIdea);
            model.addAttribute(WebConstants.VIEW_STATUS, viewStatus);
        }
        return "redirect:edit/" + keyStr;
    }

    /**
     * Handles request for publishing an idea. Validates the idea and then
     * checks for duplicates. Publishes the idea if no validation errors or
     * duplicates are found.
     * 
     * @param request the {@link HttpServletRequest} object
     * @param user the user publishing the idea
     * @param idea the {@link Idea} object to be published
     * @param errors the {@link BindingResult} object
     * @param model the data map
     * @param recaptchaChallengeField the captcha challenge text
     * @param recaptchaResponseField the captcha response text
     * @param isDuplicate boolean specifying whether the idea is a duplicate or
     *        not
     * @return resource name to which the request should be redirected
     * @throws IOException
     */
    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public String publishIdea(HttpServletRequest request,
                    HttpSession session,
                    @ModelAttribute Idea idea,
                    BindingResult errors, Model model,
                    @RequestParam String recaptchaChallengeField,
                    @RequestParam String recaptchaResponseField,
                    @RequestParam boolean isDuplicate) throws IOException {

        log.info("Entering publish");
        /** call IdeaValidator to validate Idea object */
        ideaValidator.validate(idea, errors);
        ViewStatus viewStatus = null;
        /* If the errors exist in the data being posted, return to edit page */
        if (errors.hasErrors()) {
            viewStatus = ViewStatus.createErrorViewStatus(errors);
        }
        Boolean captchaValidation = reCaptchaUtility.verifyCaptcha(request.getRemoteAddr(),
                        recaptchaChallengeField,
                        recaptchaResponseField);
        /* If the captcha verification fails, return to edit page */
        if (!captchaValidation) {
            viewStatus = new ViewStatus();
            viewStatus.setStatus(WebConstants.ERROR);
            viewStatus.addMessage(WebConstants.CAPTCHA, WebConstants.CAPTCHA_MISMATCH);
        }
        if (null == viewStatus && !isDuplicate) {
            // Find duplicates idea only when no other errors are present.
            viewStatus = findDuplicates(idea);
            if (viewStatus != null) {
                model.addAttribute(WebConstants.IS_DUPLICATE, "true");
            }
        }
        // In case of errors go back to edit page
        if (viewStatus != null) {
            viewStatus.setStatus(WebConstants.ERROR);
            // Put current idea into viewStatus so that it can be displayed
            viewStatus.addData(WebConstants.IDEA, idea);
            // Add viewStatus to model
            model.addAttribute(WebConstants.VIEW_STATUS, viewStatus);
            return "ideas/edit";
        }
        // Everything is fine. Go ahead with idea publishing.
        idea = handleTag(idea);
        User user = (User) session.getAttribute(WebConstants.USER);
        user = userService.getUserByPrimaryKey(user.getUserKey());
        ideaService.publishIdea(idea, user);
        return "redirect:list";

    }

    /**
     * Searches for possible duplicates of a new idea.
     * 
     * @param idea Idea to be published
     * @return viewStatus the {@link ViewStatus} object
     * 
     */
    private ViewStatus findDuplicates(Idea idea) {
        ViewStatus viewStatus = null;
        /* Check duplicate idea. */
        SearchResult result = SearchUtility.search(idea.getTitle(), true,
                        "title", WebConstants.ZERO,
                        IdeaExchangeConstants.DEFAULT_PAGE_SIZE, Idea.class.getSimpleName());
        if (result != null && result.getTotalCount() > WebConstants.ZERO) {
            log.info("Found duplicate ideas");
            List<Idea> ideaList = convertIntoIdeaList(result);
            viewStatus = ViewStatus.createTheViewStatus(ideaList, WebConstants.DUPLICATE_IDEAS,
                            null);
            /* Set error message */
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(DUPLICATE, WebConstants.DUPLICATE_IDEAS_FOUND);
        }
        return viewStatus;
    }

    /**
     * Convert list of Serializable objects to list of {@link Idea} objects
     * 
     * @param searchResult {@link SearchResult} object containing the data
     * @return list of {@link Idea} objects
     */
    private List<Idea> convertIntoIdeaList(SearchResult searchResult) {
        List<Idea> ideaList = new ArrayList<Idea>();
        for (Serializable result : searchResult.getData()) {
            if (result instanceof Idea) {
                ideaList.add((Idea) result);
            }
        }
        return ideaList;
    }

    /**
     * performs handling of tag while saving or publishing the idea.
     * 
     * @param idea This Idea object.
     * @return Idea object
     */
    private Idea handleTag(Idea idea) {
        List<Tag> tags = null;
        Set<String> tagKeys = new LinkedHashSet<String>();
        if (idea != null) {
            /* save tags. */
            tags = getTagService().saveTags(idea.getTags());
            if (tags != null) {
                for (Tag tag : tags) {
                    tagKeys.add(tag.getKey());
                }
            }
            /* Set tag keys to the Idea object's tagKeys. */
            if (tagKeys.size() > WebConstants.ZERO)
                idea.setTagKeys(tagKeys);
        }
        return idea;
    }

    /**
     * Handles request to add vote on Idea with the given key.
     * 
     * @param ideaKey key of the comment on which the vote is to be added.
     * @param isPositiveVote true if positive vote is casted on idea else
     *        false.
     * @param user the user adding the vote
     * @param model the data map
     */
    @RequestMapping("/voteIdea/{ideaKey}.json")
    public void addVoteOnIdea(@PathVariable String ideaKey,
                    @RequestParam(required = false) boolean isPositive,
                    HttpSession session, Map<String, Object> model) {
        IdeaVote ideaVote = new IdeaVote();
        ideaVote.setIdeaKey(ideaKey);
        ideaVote.setPositiveVote(isPositive);
        /* Set points according the the negative or positive vote */
        if (isPositive) {
            ideaVote.setVotePoints(WebConstants.IDEA_POSITIVE_VOTE_POINTS);
        } else {
            ideaVote.setVotePoints(WebConstants.IDEA_NEGATIVE_VOTE_POINTS);
        }
        User user = (User) session.getAttribute(WebConstants.USER);
        ideaVote.setCreatorKey(user.getUserKey());
        Vote vote = null;
        ViewStatus viewStatus = new ViewStatus();
        try {
            /* Add the vote and update datastore */
            vote = voteService.addVote(ideaVote, user);
            if (vote != null) {
                viewStatus.setStatus(SUCCESS);
                viewStatus.addMessage(WebConstants.VOTE, WebConstants.VOTE_SUCCESSFUL);
            } else {
                viewStatus.setStatus(ERROR);
                viewStatus.addMessage(WebConstants.VOTE, WebConstants.VOTE_FAILED);
            }
        } catch (IdeasExchangeException e) {
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(WebConstants.VOTE, e.getMessage());
        }
        model.put(WebConstants.VIEW_STATUS, viewStatus);

    }

    /**
     * Fetch ideas associated with a particular tag.
     * 
     * @param tagName the title of the tag
     * @param retrievalInfo the {@link RetrievalInfo} object
     * @param model data map
     */
    @RequestMapping("/byTag/{tagName}")
    public void getIdeasByTagName(@PathVariable String tagName,
                    @ModelAttribute RetrievalInfo retrievalInfo, Map<String, Object> model) {
        /* Fetch the range parameters as sent in the request */
        long startIndex = retrievalInfo.getStartIndex();
        long noOfRecordsRequested = retrievalInfo.getNoOfRecords();
        List<IdeaDetail> ideas = ideaBuilder.getIdeasByTagName(tagName, retrievalInfo);
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        /* Map containing the previous and next index values */
        HashMap<String, Long> pagingMap = new HashMap<String, Long>();
        /*
         * If the size of the list is greater than the no. of records requested
         * ,set the parameter 'next' to be used as start index for the next
         * page retrieval.
         */
        if (ideas != null && ideas.size() > noOfRecordsRequested) {
            pagingMap.put(WebConstants.NEXT, startIndex + noOfRecordsRequested);
        } else {
            /*
             * If the list size is not greater than the number requested set
             * the 'next' parameter to minus one
             */
            pagingMap.put(WebConstants.NEXT, (long) WebConstants.MINUS_ONE);
        }
        /*
         * Set the parameter 'previous' to be used as the start index for the
         * previous page retrieval
         */
        pagingMap.put(WebConstants.PREVIOUS, startIndex - noOfRecordsRequested);
        /* Add the map containing the paging values to the map of parameters */
        parameters.put(WebConstants.PAGING, pagingMap);
        ViewStatus viewStatus = ViewStatus
                        .createTheViewStatus(ideas, WebConstants.IDEAS, parameters);
        model.put(VIEW_STATUS, viewStatus);
    }

    /**
     * Handles request for view idea details request
     * 
     * @param key Primary key of idea whose details are to be shown
     * @param model the data map
     * @return View Name
     */
    @RequestMapping("/show/{key}")
    public String show(@PathVariable String key, Map<String, Object> model) {
        log.info("Into get idea details");
        ViewStatus viewStatus = new ViewStatus();
        IdeaDetail ideaDetail = null;
        try {
            /* Create idea detail */
            ideaDetail = ideaBuilder.getIdeaDetail(key, false);
            viewStatus.addData(WebConstants.IDEA_DETAIL, ideaDetail);
            viewStatus.addData(WebConstants.RE_CAPTCHA_PUBLIC_KEY, rcGlobalPublicKey);
            viewStatus.setStatus(SUCCESS);

            if (ideaDetail != null && ideaDetail.getIdea() != null) {

                // If idea is duplicate, set original idea in viewstatus
                String originalIdeaKey = ideaDetail.getIdea().getOriginalIdeaKey();
                if (StringUtils.isNotBlank(originalIdeaKey)) {
                    /* If the idea is a duplicate then fetch the original idea */
                    Idea origIdea = ideaService.getIdeaByKey(originalIdeaKey);
                    if (origIdea != null) {
                        viewStatus.addData(WebConstants.ORIG_IDEA, origIdea);
                    }
                }
            } else {
                // Idea detail not found. Set error message
                viewStatus.setStatus(ERROR);
                viewStatus.addMessage(ERROR, WebConstants.RECORD_NOT_FOUND);
            }
        } catch (IdeasExchangeException e) {
            // Business exception while fetching idea detail. Set error message
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(ERROR, e.getMessage());
        }
        // Put viewstatus into model map
        model.put(VIEW_STATUS, viewStatus);
        return "ideas/show";
    }

    /**
     * Redirect to the idea edit page
     * 
     * @param model data map
     * @return View Name
     */
    @RequestMapping("/showForm")
    public String edit(Model model) {
        Idea idea = new Idea();
        model.addAttribute(idea);
        return "ideas/edit";
    }

    /**
     * Edit the idea
     * 
     * @param key the key of the idea
     * @param model data map
     * @return View Name
     */
    @RequestMapping("/edit/{key}")
    public String editIdea(@PathVariable String key, Model model) {
        log.info("Into edit idea. Key is:" + key);
        Idea idea = new Idea();
        idea.setKey(key);
        idea = ideaService.getIdeaDetails(idea);
        Set<String> tagKeys = idea.getTagKeys();
        /*
         * If the idea is associated with one or more tags,create a comma
         * separated string of tag titles and set into idea.tags
         */
        if (tagKeys != null && tagKeys.size() > WebConstants.ZERO) {
            List<Tag> tagList = tagService.getTagsByKeys(idea.getTagKeys());
            StringBuilder tagString = new StringBuilder("");
            for (Tag tag : tagList) {
                if (!StringUtils.isBlank(tag.getTitle())) {
                    tagString.append(tag.getTitle());
                    tagString.append(",");
                }
            }
            tagString.deleteCharAt(tagString.length() - 1);
            idea.setTags(tagString.toString());
        }

        ViewStatus viewStatus = new ViewStatus();
        viewStatus.addData(WebConstants.IDEA, idea);
        model.addAttribute(viewStatus);
        return "ideas/edit";
    }

    /**
     * Flag the idea as objectionable
     * 
     * @param ideaKey the key of the idea to be flagged
     * @param user the user flagging the idea
     * @param model data map
     * @return View Name
     */
    @RequestMapping("flag/abuse/{ideaKey}")
    public String flagObjectionableIdea(@PathVariable String ideaKey, HttpSession session,
                    Map<String, Object> model) {
        ViewStatus viewStatus = new ViewStatus();
        User user = (User) session.getAttribute(WebConstants.USER);
        String resultStatus = ideaService.flagObjectionableIdea(ideaKey, user);
        if (resultStatus.equalsIgnoreCase(IdeaExchangeConstants.SUCCESS)) {
            viewStatus.setStatus(SUCCESS);
            viewStatus.addMessage(FLAG,
                            WebConstants.IDEA_FLAGGING_SUCCESSFULL);
        } else if (resultStatus.equalsIgnoreCase(IdeaExchangeConstants.FAIL)) {
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(FLAG,
                            WebConstants.FLAGGING_FAILED);
        } else {
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(FLAG,
                            resultStatus);
        }

        model.put(VIEW_STATUS, viewStatus);
        return "ideas/show";
    }

    /**
     * Flag the idea as duplicate
     * 
     * @param ideaKey the key of the idea to be flagged
     * @param originalIdeaKey the key of the original idea
     * @param user the user flagging the idea
     * @param model data map
     * @return View Name
     */
    @RequestMapping("flag/duplicate/{ideaKey}/{originalIdeaKey}")
    public String flagDuplicateIdea(@PathVariable String ideaKey,
                    @PathVariable String originalIdeaKey, HttpSession session,
                    Map<String, Object> model) {
        ViewStatus viewStatus = new ViewStatus();
        User user = (User) session.getAttribute(WebConstants.USER);
        String resultStatus = ideaService.flagDuplicateIdea(ideaKey, originalIdeaKey, user);
        if (resultStatus.equalsIgnoreCase(IdeaExchangeConstants.SUCCESS)) {
            viewStatus.setStatus(SUCCESS);
            viewStatus.addMessage(DUPLICATE,
                            WebConstants.IDEA_DUPLICATE_SUCCESS);
        } else if (resultStatus.equalsIgnoreCase(IdeaExchangeConstants.FAIL)) {
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(DUPLICATE,
                            WebConstants.IDEA_DUPLICATE_FAILED);
        } else {
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(DUPLICATE,
                            resultStatus);
        }

        model.put(VIEW_STATUS, viewStatus);
        return "ideas/show";
    }

    /**
     * Handles request to delete saved idea request
     * 
     * @param key Primary key of saved idea whose details are to be
     *        shown
     * @return View name
     */
    @RequestMapping("/delete/{key}")
    public String delete(@PathVariable String key,
                    HttpSession session) {
        log.info("Into Idea Controller to delete Saved Idea");
        User user = (User) session.getAttribute(WebConstants.USER);
        ideaService.deleteIdea(key, user);
        return "users/ideas";
    }

    /**
     * Register custom binders for Spring. Needed to run on app engine
     * 
     * @param binder the {@link WebDataBinder} object
     * 
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor(true));
        binder.registerCustomEditor(String.class, new StringEditor(true));
    }

    /**
     * @return the ideaBuilder
     */
    public IdeaBuilder getIdeaBuilder() {
        return ideaBuilder;
    }

    /**
     * @param ideaBuilder the ideaBuilder to set
     */
    public void setIdeaBuilder(IdeaBuilder ideaBuilder) {
        this.ideaBuilder = ideaBuilder;
    }

    /**
     * Sets the comment service to controller.
     * 
     * @param commentService the commentService to set
     */
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Returns the comment service to controller.
     * 
     * @return the commentService
     */
    public CommentService getCommentService() {
        return commentService;
    }

    /**
     * Sets the idea service to controller.
     * 
     * @param ideaService the ideaService to set
     */
    public void setIdeaService(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    /**
     * @return the ideaService
     */
    public IdeaService getIdeaService() {
        return ideaService;
    }

    /**
     * Sets the tag service to controller.
     * 
     * @param tagService the tagService to set
     */
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * @return the tagService
     */
    public TagService getTagService() {
        return tagService;
    }

    /**
     * @return the voteService
     */
    public VoteService getVoteService() {
        return voteService;
    }

    /**
     * @param voteService the voteService to set
     */
    public void setVoteService(VoteService voteService) {
        this.voteService = voteService;
    }
}

