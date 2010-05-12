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

import static com.google.ie.web.controller.WebConstants.ERROR;
import static com.google.ie.web.controller.WebConstants.FLAG;
import static com.google.ie.web.controller.WebConstants.SUCCESS;
import static com.google.ie.web.controller.WebConstants.VIEW_STATUS;

import com.google.ie.business.domain.Comment;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.ProjectComment;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.CommentService;
import com.google.ie.business.service.UserService;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.editor.StringEditor;
import com.google.ie.common.util.ReCaptchaUtility;
import com.google.ie.common.validation.CommentValidator;
import com.google.ie.dto.CommentDetail;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.dto.ViewStatus;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A controller that handles request for comments.
 * 
 * @author asirohi
 * 
 */
@Controller
@RequestMapping("/projectComments")
@SessionAttributes("user")
public class ProjectCommentController {
    private static Logger logger = Logger.getLogger(ProjectCommentController.class);
    @Autowired
    @Qualifier("projectCommentServiceImpl")
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReCaptchaUtility reCaptchaUtility;
    @Autowired
    private CommentValidator commentValidator;

    /**
     * Handles request to add comment on a Project.
     * 
     * @param projectComment key of the Project on which the comment is to be
     *        added.
     * @param user the User object
     * @throws IOException
     */
    @RequestMapping(value = "/postProjectComments", method = RequestMethod.POST)
    public void postCommentOnProject(HttpServletRequest request,
                    @ModelAttribute ProjectComment projectComment,
                    BindingResult result, Map<String, Object> map,
                    @RequestParam String recaptchaChallengeField,
                    @RequestParam String recaptchaResponseField, HttpSession session)
                    throws IOException {
        ViewStatus viewStatus = new ViewStatus();
        Boolean captchaValidation = reCaptchaUtility.verifyCaptcha(request.getRemoteAddr(),
                        recaptchaChallengeField,
                        recaptchaResponseField);
        /* call CommentValidator to validate input ProjectComment object */
        getCommentValidator().validate(projectComment, result);
        if (result.hasErrors() || !captchaValidation) {
            logger.warn("Comment object has " + result.getErrorCount() + " validation errors");
            viewStatus.setStatus(WebConstants.ERROR);
            /* Add a message if the captcha validation fails */
            if (!captchaValidation) {
                viewStatus.addMessage(WebConstants.CAPTCHA, WebConstants.CAPTCHA_MISMATCH);
            }
            /* Iterate the errors and add a message for each error */
            for (Iterator<FieldError> iterator = result.getFieldErrors().iterator(); iterator
                            .hasNext();) {
                FieldError fieldError = iterator.next();
                viewStatus.addMessage(fieldError.getField(), fieldError.getDefaultMessage());
                logger.warn("Error found in field: " + fieldError.getField() + " Message :"
                                + fieldError.getDefaultMessage());
            }

        } else {
            User user = (User) session.getAttribute(WebConstants.USER);
            Comment comment = commentService.addComment(projectComment, user);
            if (comment != null) {
                viewStatus.setStatus(WebConstants.SUCCESS);
                viewStatus.addMessage(WebConstants.COMMENTS, WebConstants.COMMENT_SUCCESSFULL);
            } else {
                viewStatus.setStatus(WebConstants.ERROR);
                viewStatus.addMessage(WebConstants.COMMENTS, WebConstants.COMMENT_FAILED);
            }
        }
        map.remove("projectComment");
        map.put(WebConstants.VIEW_STATUS, viewStatus);
    }

    /**
     * Handle request to get lists of the comments on Idea with the given key.
     * 
     * @param ideaKey the key of the {@link Idea} object
     * @param retrievalInfo the {@link RetrievalInfo} object
     * @param map data map for the view status
     */
    @RequestMapping("listProjectComments/{projectKey}")
    public void listProjectComments(@PathVariable String projectKey,
                    @ModelAttribute RetrievalInfo retrievalInfo, Map<String, Object> map) {

        /* Fetch the parameters as sent in the request */
        long startIndex = retrievalInfo.getStartIndex();
        long noOfRecordsRequested = retrievalInfo.getNoOfRecords();
        /* Get the comment list */
        List<ProjectComment> comments = commentService.getComments(projectKey, retrievalInfo);
        List<CommentDetail> commentDetailList = getDetailedCommentsForProject(comments);
        /* Map of data to be inserted into the view status object */
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        /* Map containing the previous and next index values */
        HashMap<String, Long> pagingMap = new HashMap<String, Long>();
        /*
         * If the size of the list is greater than the no. of records requested
         * ,set the parameter 'next' to be used as start index for the next
         * page retrieval.
         */
        if (commentDetailList != null && commentDetailList.size() > noOfRecordsRequested) {
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

        ViewStatus viewStatus = ViewStatus.createTheViewStatus(commentDetailList,
                        WebConstants.COMMENTS, parameters);
        map.put(WebConstants.VIEW_STATUS, viewStatus);

    }

    /**
     * Flag the comment abusive
     * 
     * @param commentKey the key of the {@link Comment} to be flagged abusive
     * @param user the {@link User} initiating the flagging
     * @param model the data map
     * @return the resource to which the request should be forwarded
     */
    @RequestMapping("flag/abuse/{commentKey}")
    public String flagComment(@PathVariable String commentKey,
                    HttpSession session,
                    Map<String, Object> model) {
        ViewStatus viewStatus = new ViewStatus();

        User user = (User) session.getAttribute(WebConstants.USER);
        /* Flag the comment */
        String resultStatus = commentService.flagComment(commentKey, user);
        if (resultStatus.equalsIgnoreCase(IdeaExchangeConstants.SUCCESS)) {
            viewStatus.setStatus(SUCCESS);
            viewStatus.addMessage(FLAG,
                            WebConstants.COMMENT_FLAGGING_SUCCESSFULL);
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
        return "projects/show";
    }

    /**
     * Convert idea Comment to comment detail.
     * 
     * @param projectComments the list of {@link ProjectComment} objects
     * @return list of {@link CommentDetail} objects
     */
    private List<CommentDetail> getDetailedCommentsForProject(List<ProjectComment> projectComments) {
        List<CommentDetail> commentDetailList = new ArrayList<CommentDetail>();
        User user = null;
        CommentDetail commentDetail = null;
        if (projectComments != null && projectComments.size() > WebConstants.ZERO) {
            for (ProjectComment comment : projectComments) {
                /* Fetch the owner of the comment */
                user = userService.getUserByPrimaryKey(comment.getCreatorKey());
                commentDetail = new CommentDetail();
                comment.setCommentTextAsString(comment.getText());
                commentDetail.setComment(comment);
                commentDetail.setUser(user);
                /* Add the object to list */
                commentDetailList.add(commentDetail);
            }
            if (commentDetailList.size() > WebConstants.ZERO) {
                return commentDetailList;
            }
        }
        return null;
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
     * @param userService the userService to set
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * @return the userService
     */
    public UserService getUserService() {
        return userService;
    }

    public CommentValidator getCommentValidator() {
        return commentValidator;
    }

    public void setCommentValidator(CommentValidator commentValidator) {
        this.commentValidator = commentValidator;
    }

}

