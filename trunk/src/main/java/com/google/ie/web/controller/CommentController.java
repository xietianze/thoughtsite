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
import com.google.ie.business.domain.CommentVote;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.User;
import com.google.ie.business.domain.Vote;
import com.google.ie.business.service.CommentService;
import com.google.ie.business.service.UserService;
import com.google.ie.business.service.VoteService;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.editor.StringEditor;
import com.google.ie.common.exception.IdeasExchangeException;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A controller that handles request for comments crude operation.
 * 
 * @author asirohi
 * 
 */
@Controller
@RequestMapping("/comments")
@SessionAttributes("user")
public class CommentController {
    /* Logger instance for logging */
    private static Logger logger = Logger.getLogger(CommentController.class);
    @Autowired
    @Qualifier("commentVoteServiceImpl")
    private VoteService voteService;
    @Autowired
    @Qualifier("ideaCommentServiceImpl")
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReCaptchaUtility reCaptchaUtility;

    /**
     * Handles request to add comment on Idea.
     * 
     * @param ideaComment key of the Idea on which the comment is to be added.
     * @param user the User object
     * @throws IOException
     */
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public void postCommentOnIdea(HttpServletRequest request, HttpSession session,
                    @ModelAttribute IdeaComment ideaComment,
                    BindingResult result, Map<String, Object> map,
                    @RequestParam String recaptchaChallengeField,
                    @RequestParam String recaptchaResponseField) throws IOException {

        /*
         * get captcha fields from request call CommentValidator to validate
         * input IdeaComment object
         */
        ViewStatus viewStatus = new ViewStatus();
        Boolean captchaValidation = reCaptchaUtility.verifyCaptcha(request.getRemoteAddr(),
                        recaptchaChallengeField,
                        recaptchaResponseField);

        new CommentValidator().validate(ideaComment, result);
        /*
         * check for validation or captcha error and if error occured display
         * the error messages.
         */
        if (result.hasErrors() || !captchaValidation) {
            logger.warn("Comment object has " + result.getErrorCount() + " validation errors");
            viewStatus.setStatus(WebConstants.ERROR);
            if (!captchaValidation) {
                viewStatus.addMessage(WebConstants.CAPTCHA, WebConstants.CAPTCHA_MISMATCH);
            }
            for (Iterator<FieldError> iterator = result.getFieldErrors().iterator(); iterator
                            .hasNext();) {
                FieldError fieldError = iterator.next();
                viewStatus.addMessage(fieldError.getField(), fieldError.getDefaultMessage());
                logger.warn("Error found in field: " + fieldError.getField() + " Message :"
                                + fieldError.getDefaultMessage());
            }

        } else {
            User user = (User) session.getAttribute(WebConstants.USER);
            /* Call comment service to add new comment */
            Comment comment = commentService.addComment(ideaComment, user);

            if (comment != null) {
                viewStatus.setStatus(WebConstants.SUCCESS);
                viewStatus.addMessage(WebConstants.COMMENTS, WebConstants.COMMENT_SUCCESSFULL);
            } else {
                viewStatus.setStatus(WebConstants.ERROR);
                viewStatus.addMessage(WebConstants.COMMENTS, WebConstants.COMMENT_FAILED);
            }
        }
        map.remove("ideaComment");
        map.put(WebConstants.VIEW_STATUS, viewStatus);
    }

    /**
     * Handles request to add vote on the idea comment.
     * 
     * @param commentKey key of the comment on which the vote is to be added.
     * @param isPositiveVote true if positive vote is casted on comment else
     *        false.
     * @param user the User object
     * @param model request map
     */
    @RequestMapping("/vote/{commentKey}")
    public void addVoteOnComment(@PathVariable String commentKey,
                    @RequestParam(required = false) boolean isPositive,
                    HttpSession session,
                    Map<String, Object> model) {
        User user = (User) session.getAttribute(WebConstants.USER);
        CommentVote commentVote = new CommentVote();
        commentVote.setCommentKey(commentKey);
        commentVote.setCreatorKey(user.getUserKey());
        commentVote.setVotingDate(new Date());
        commentVote.setPositiveVote(isPositive);
        /* check for positive and negative vote */
        if (isPositive) {
            commentVote.setVotePoints(WebConstants.COMMENT_POSITIVE_VOTE_POINTS);
        } else {
            commentVote.setVotePoints(WebConstants.COMMENT_NEGATIVE_VOTE_POINTS);
        }

        ViewStatus viewStatus = new ViewStatus();
        Vote vote = null;
        try {
            /* Invoke service to add vote */
            vote = voteService.addVote(commentVote, user);
            if (vote != null) {
                viewStatus.setStatus(WebConstants.SUCCESS);
                viewStatus.addMessage(WebConstants.VOTE, WebConstants.VOTE_SUCCESSFUL);
            } else {
                viewStatus.setStatus(WebConstants.ERROR);
                viewStatus.addMessage(WebConstants.VOTE, WebConstants.VOTE_FAILED);
            }
        } catch (IdeasExchangeException e) {
            viewStatus.setStatus(WebConstants.ERROR);
            viewStatus.addMessage(WebConstants.VOTE, e.getMessage());
        }

        model.put(WebConstants.VIEW_STATUS, viewStatus);

    }

    /**
     * Handle request to get lists of the comments on Idea with the given key.
     * 
     * @param ideaKey String
     * @param retrievalInfo {@link RetrievalInfo}
     * @param map request map.
     */
    @RequestMapping("list/{ideaKey}")
    public void listIdeaComments(@PathVariable String ideaKey,
                    @ModelAttribute RetrievalInfo retrievalInfo, Map<String, Object> map) {
        /* Fetch the parameters as sent in the request */
        long startIndex = retrievalInfo.getStartIndex();
        long noOfRecordsRequested = retrievalInfo.getNoOfRecords();
        retrievalInfo.setNoOfRecords(retrievalInfo.getNoOfRecords() + WebConstants.ONE);
        /* Get the comment list */
        List<IdeaComment> ideaComments = commentService.getComments(ideaKey, retrievalInfo);
        List<CommentDetail> commentDetailList = getDetailedComments(ideaComments);
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
     * Convert idea Comment to comment detail.
     * 
     * @param ideaComments
     * @return
     */
    private List<CommentDetail> getDetailedComments(List<IdeaComment> ideaComments) {
        List<CommentDetail> commentDetailList = new ArrayList<CommentDetail>();
        User user;
        CommentDetail commentDetail;
        if (ideaComments != null && ideaComments.size() > 0) {
            for (IdeaComment comment : ideaComments) {
                user = userService.getUserByPrimaryKey(comment.getCreatorKey());
                commentDetail = new CommentDetail();
                comment.setCommentTextAsString(comment.getText());
                commentDetail.setComment(comment);
                commentDetail.setUser(user);
                commentDetailList.add(commentDetail);
            }
            if (commentDetailList.size() > WebConstants.ZERO) {
                return commentDetailList;
            }
        }
        return null;
    }

    /**
     * 
     * @param commentKey primary key of comment Entity.
     * @param user User info object
     * @param model request map
     * @return String path to which request forwarded.
     */
    @RequestMapping("flag/abuse/{commentKey}")
    public String flagComment(@PathVariable String commentKey,
                    HttpSession session,
                    Map<String, Object> model) {
        ViewStatus viewStatus = new ViewStatus();
        User user = (User) session.getAttribute(WebConstants.USER);
        /* Invoke service method to flag comment */
        String resultString = commentService.flagComment(commentKey, user);

        if (resultString.equalsIgnoreCase(IdeaExchangeConstants.SUCCESS)) {
            viewStatus.setStatus(SUCCESS);
            viewStatus.addMessage(FLAG,
                            WebConstants.COMMENT_FLAGGING_SUCCESSFULL);
        } else if (resultString.equalsIgnoreCase(IdeaExchangeConstants.FAIL)) {
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(FLAG,
                            WebConstants.FLAGGING_FAILED);
        } else {
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(FLAG,
                            resultString);
        }

        model.put(VIEW_STATUS, viewStatus);
        return "ideas/show";
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

}

