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
import static com.google.ie.web.controller.WebConstants.SUCCESS;
import static com.google.ie.web.controller.WebConstants.VIEW_STATUS;

import com.google.appengine.api.datastore.Blob;
import com.google.ie.business.domain.Developer;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.User;
import com.google.ie.business.service.DeveloperService;
import com.google.ie.business.service.IdeaService;
import com.google.ie.business.service.ProjectService;
import com.google.ie.business.service.UserService;
import com.google.ie.common.builder.ProjectBuilder;
import com.google.ie.common.editor.StringEditor;
import com.google.ie.common.email.EmailManager;
import com.google.ie.common.util.ReCaptchaUtility;
import com.google.ie.dto.ProjectDetail;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.dto.ViewStatus;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A controller that handles request for Project entities.
 * 
 * @author Charanjeet singh
 * 
 */
@Controller
@RequestMapping("/projects")
@SessionAttributes("user")
public class ProjectController {
    private static Logger logger = Logger.getLogger(ProjectController.class);
    private static final String PROJECT_EMAIL_TYPE_INVITE_TO_JOIN = "joinProject";
    private static final String SEMICOLON = ";";
    private static final String NAME = "name";
    private static final String EMAILID = "emailId";
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectBuilder projectBuilder;

    @Autowired
    private IdeaService ideaService;
    @Autowired
    private DeveloperService developerService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReCaptchaUtility reCaptchaUtility;
    @Autowired
    @Qualifier("projectValidator")
    private Validator projectValidator;

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
     * Handles the request for search/list idea.
     * 
     * @param model Carries data for the view
     * @return View name.
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listProjects() {
        return "projects/list";
    }

    /**
     * Creates project for the idea identified by ideaKey.
     * 
     * @param ideaKey for the {@link Idea}
     * @param model Carries data for the view
     * @return
     */
    @RequestMapping("/showForm/{ideaKey}")
    public String edit(@PathVariable String ideaKey, Model model) {
        ProjectDetail projectDetail = new ProjectDetail();
        projectDetail.setProject(new Project());
        ViewStatus viewStatus = new ViewStatus();
        viewStatus.addData(WebConstants.PROJECT_DETAIL, projectDetail);
        viewStatus.setStatus(SUCCESS);
        Idea idea = ideaService.getIdeaByKey(ideaKey);
        viewStatus.addData(WebConstants.IDEA_TITLE, idea.getTitle());
        model.addAttribute(VIEW_STATUS, viewStatus);
        model.addAttribute("ideaKey", ideaKey);
        return "projects/edit";
    }

    /**
     * Displays the project details identified by projectKey
     * 
     * @param projectKey the id of {@link Project} to display
     * @param model Carries data for the view
     * @param req reference of the {@link HttpServletRequest}
     * @return
     */

    @RequestMapping("/show/{projectKey}")
    public String show(@PathVariable String projectKey, Map<String, Object> model,
                    HttpServletRequest req) {
        ViewStatus viewStatus = new ViewStatus();
        User user = null;
        if (req.getSession(true).getAttribute(WebConstants.USER) != null)
            user = (User) req.getSession(true).getAttribute(WebConstants.USER);

        ProjectDetail projectDetail = projectBuilder.getProjectDetail(projectKey);
        if (null != projectDetail && null != projectDetail.getProject()) {
            Project project = projectDetail.getProject();
            Idea associatedIdea = ideaService.getIdeaByKey(project.getIdeaKey());
            /*
             * Set containing the status for which the idea should not be
             * displayed
             */
            Set<String> setOfStatus = new HashSet<String>();
            setOfStatus.add(Idea.STATUS_DELETED);
            setOfStatus.add(Idea.STATUS_OBJECTIONABLE);
            /*
             * If the idea has status contained in the set created above,set the
             * idea key for the project to null
             */
            if (setOfStatus.contains(associatedIdea.getStatus())) {
                project.setIdeaKey(null);

                logger.debug("The idea is either deleted or objectionable," +
                                "hence setting the idea key for project to null");
            } else {
                projectDetail.setIdeaTitle(associatedIdea.getTitle());
            }
            if (user != null)
                logger.debug("Checking if project is editable to the user with userkey ="
                                + user.getUserKey());
            projectDetail.setProjectEditable(isProjectEditable(projectDetail, user));
            logger.info("projectDetail.projectEditable is set to ="
                            + projectDetail.isProjectEditable());
            viewStatus.addData(WebConstants.PROJECT_DETAIL, projectDetail);
            viewStatus.setStatus(SUCCESS);
        } else {
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(ERROR, WebConstants.RECORD_NOT_FOUND);
        }
        model.put(VIEW_STATUS, viewStatus);
        return "projects/show";
    }

    /**
     * Check for project edit-able attribute on the basis of logged in user.
     * 
     * @param projectDetail {@link ProjectDetail} to edit
     * @return boolean status of the action
     */
    private boolean isProjectEditable(ProjectDetail projectDetail, User user) {
        boolean isEditable = false;
        if (projectDetail.getProject() != null) {
            if (user != null) {
                if (projectDetail.getUser() != null) {
                    if (user.getUserKey() != null
                                    && user.getUserKey().equalsIgnoreCase(
                                                    projectDetail.getUser().getUserKey())) {
                        isEditable = true;
                    }
                }
                for (Developer developer : projectDetail.getDevelopers()) {
                    logger.debug("developer's status =" + developer.getStatus());
                    if (user.getUserKey().equalsIgnoreCase(developer.getUserKey())) {
                        isEditable = true;
                        break;
                    }
                }
            }

        }
        return isEditable;
    }

    /**
     * Handles request for editing an project. Validates the Project details
     * and saves the project if no validation errors are found.
     * 
     * @return name of project detail JSP to be shown.
     */
    @RequestMapping("/editProject/{projectKey}")
    public String editProject(@PathVariable String projectKey,
                    HttpSession session,
                    Map<String, Object> model) {
        User user = (User) session.getAttribute(WebConstants.USER);
        ViewStatus viewStatus = new ViewStatus();
        ProjectDetail projectDetail = projectBuilder.getProjectDetail(projectKey);
        projectDetail.setProjectEditable(isProjectEditable(projectDetail, user));
        if (null != projectDetail.getProject()) {
            projectDetail.setIdeaTitle(ideaService.getIdeaByKey(
                            projectDetail.getProject().getIdeaKey()).getTitle());
            viewStatus.addData(WebConstants.PROJECT_DETAIL, projectDetail);
            viewStatus.setStatus(SUCCESS);
        } else {
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(ERROR, WebConstants.RECORD_NOT_FOUND);
        }
        Idea idea = ideaService.getIdeaByKey(projectDetail.getProject().getIdeaKey());
        model.put(VIEW_STATUS, viewStatus);
        viewStatus.addData(WebConstants.IDEA_TITLE, idea.getTitle());
        model.put("ideaKey", projectDetail.getProject().getIdeaKey());
        model.put("projectKey", projectDetail.getProject().getKey());
        model.put("editProject", true);
        return "projects/edit";
    }

    /**
     * Handles request for creating an project. Validates the Project details
     * and saves the project if no validation errors are found.
     * 
     * @return name of project detail JSP to be shown.
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createProject(HttpServletRequest request,
                    @RequestParam(required = true) String ideaKey,
                    @RequestParam(required = false) String projectKey,
                    @RequestParam(required = false, value = "devName") String[] devNames,
                    @RequestParam(required = false, value = "email") String[] emails,
                    @RequestParam(required = false, value = "status") String[] statusArr,
                    @RequestParam(required = false, value = "devKey") String[] devKeys,
                    @RequestParam(required = false, value = "logoFile") MultipartFile logoFile,
                    @ModelAttribute Project project,
                    BindingResult errors,
                    Map<String, Object> model,
                    @RequestParam String recaptchaChallengeField,
                    @RequestParam String recaptchaResponseField,
                    HttpSession session) throws IOException {

        ViewStatus viewStatus = new ViewStatus();
        Boolean captchaValidation = reCaptchaUtility.verifyCaptcha(request.getRemoteAddr(),
                        recaptchaChallengeField,
                        recaptchaResponseField);
        User user = (User) session.getAttribute(WebConstants.USER);
        ProjectDetail projectDetail = new ProjectDetail();
        /* create developer list. */
        projectDetail.setDevelopers(createDeveloperList(devNames, emails, statusArr,
                        devKeys, user));
        projectDetail.setProject(project);
        String ideaTitle = ideaService.getIdeaByKey(ideaKey).getTitle();
        projectDetail.getProject().setIdeaKey(ideaKey);
        if (projectKey != null) {
            projectDetail.getProject().setKey(projectKey);
        }
        if (!captchaValidation) {
            getViewStatusForInvalidCaptcha(model, viewStatus, projectDetail, ideaTitle, user);
            return "projects/edit";
        }
        uploadLogo(projectKey, logoFile, projectDetail);
        /* call projectValidator to validate projectDetail object */
        projectValidator.validate(projectDetail, errors);
        /* If the errors exist in the data being posted, return to edit page */
        if (errors.hasErrors()) {
            viewStatus = ViewStatus.createProjectErrorViewStatus(errors);
            model.put(WebConstants.VIEW_STATUS, viewStatus);
            return "projects/edit";
        }
        projectDetail = createProjectAndGetDetail(user, projectDetail, ideaTitle);
        if (null != projectDetail.getProject()) {
            viewStatus.addData(WebConstants.PROJECT_DETAIL, projectDetail);
            viewStatus.setStatus(SUCCESS);
        } else {
            viewStatus.setStatus(ERROR);
            viewStatus.addMessage(ERROR, WebConstants.RECORD_NOT_FOUND);
        }
        model.put("editProject", "");
        model.put(VIEW_STATUS, viewStatus);
        // redirect to project detail page
        if (null != projectKey) {
            return "redirect:show/" + projectKey;
        } else if (null != projectDetail.getProject()) {
            return "redirect:show/" + projectDetail.getProject().getKey();
        } else {
            return "projects/show";
        }
    }

    /**
     * Get View Status For Captcha Invalid
     * 
     * @param model Carries data for the view
     * @param viewStatus
     * @param projectDetail
     * @param ideaTitle
     */
    private void getViewStatusForInvalidCaptcha(Map<String, Object> model, ViewStatus viewStatus,
                    ProjectDetail projectDetail, String ideaTitle, User user) {
        viewStatus.addMessage(WebConstants.ERROR, WebConstants.CAPTCHA_MISMATCH);
        viewStatus.addData(WebConstants.IDEA_TITLE, ideaTitle);
        model.put("ideaKey", projectDetail.getProject().getIdeaKey());
        model.put("projectKey", projectDetail.getProject().getKey());
        /*
         * If project is not created then delete user information from developer
         * list.
         */
        if (projectDetail.getProject().getKey() == null) {
            Developer developerToDelete = null;
            for (Developer developer : projectDetail.getDevelopers()) {
                if (user.getDisplayName() != null && user.getEmailId() != null) {
                    if (user.getDisplayName().equalsIgnoreCase(developer.getName())
                                    && user.getEmailId().equalsIgnoreCase(developer.getEmailId())) {
                        developerToDelete = developer;
                        break;
                    }
                }
            }
            if (developerToDelete != null) {
                projectDetail.getDevelopers().remove(developerToDelete);
            }
        } else {
            model.put("editProject", true);
        }
        viewStatus.addData(WebConstants.PROJECT_DETAIL, projectDetail);
        model.put(VIEW_STATUS, viewStatus);
    }

    /**
     * Creates the project and retrieves the project details.
     * 
     * @param user creating the project.
     * @param projectDetail
     * @param ideaTitle
     * @return
     */
    private ProjectDetail createProjectAndGetDetail(User user, ProjectDetail projectDetail,
                    String ideaTitle) {
        Project project = projectService.createOrUpdateProject(projectDetail.getProject(), user);
        // handle developers functionality.
        handleDeveloper(projectDetail.getDevelopers(), user, project);
        projectDetail = projectBuilder.getProjectDetail(project.getKey());
        projectDetail.setIdeaTitle(ideaTitle);
        projectDetail.setProjectEditable(isProjectEditable(projectDetail, user));
        return projectDetail;
    }

    /**
     * @param projectKey
     * @param logoFile
     * @param projectDetail
     * @throws IOException
     */
    private void uploadLogo(String projectKey, MultipartFile logoFile, ProjectDetail projectDetail)
                    throws IOException {
        if (logoFile != null && logoFile.getBytes().length > 0) {
            try {
                Blob file = new Blob(logoFile.getBytes());
                projectDetail.getProject().setLogo(file);
            } catch (IOException e) {
                logger.error("couldn't find an Image at " + logoFile, e);
            }
        } else {
            Project project1 = new Project();
            if (projectService.getProjectById(projectKey) != null)
                project1 = projectService.getProjectById(projectKey);
            if (project1.getLogo() != null && project1.getLogo().getBytes().length > 0) {
                Blob file = new Blob(project1.getLogo().getBytes());
                projectDetail.getProject().setLogo(file);
            }
        }
    }

    /**
     * create Developer List
     * 
     * @param devName
     * @param email
     * @param status
     * @param devKey
     * @param user
     * @return List<Developer>
     */
    private List<Developer> createDeveloperList(String[] devName, String[] email, String[] status,
                    String[] devKey, User user) {
        List<Developer> developers = new ArrayList<Developer>();
        Set<String> emailsSet = new HashSet<String>();

        if ((devName != null && devName.length > 0) && (email != null && email.length > 0)) {
            Developer developer = null;
            for (int i = 0; i < devName.length; i++) {
                if (validDeveloperData(devName, email, i)) {
                    // Check if email ids are unique
                    boolean unique = emailsSet.add(email[i]);
                    if (unique) {
                        developer = new Developer();
                        developer.setEmailId(email[i]);
                        developer.setName(devName[i]);
                        // If devKey is not present, it is a create request
                        if (devKey[i] != null && devKey[i].trim().length() > 0) {
                            developer.setKey(devKey[i]);
                        }
                        // If status is not present, it is a create request
                        if (status[i] != null && status[i].trim().length() > 0) {
                            developer.setStatus(status[i]);
                        }
                        // While editing, set user key into developer object, if
                        // developer's email id is same as users email id.
                        if (developer.getEmailId().equals(user.getEmailId())) {
                            // If user explicitly adding himself as a developer
                            // while creating the project then set its user key
                            // and status as accepted.
                            developer.setUserKey(user.getUserKey());
                            developer.setStatus(Developer.STATUS_REQUEST_ACCEPTED);
                        }
                        developers.add(developer);
                    }
                }
            }
        }
        // Check if creator has not already added himself as developer.
        boolean unique = emailsSet.add(user.getEmailId());
        if (unique) {
            Developer developer = new Developer();
            developer.setEmailId(user.getEmailId());
            developer.setName(user.getDisplayName());
            developer.setUserKey(user.getUserKey());
            developer.setStatus(Developer.STATUS_REQUEST_ACCEPTED);
            logger.debug("Added project creator to it's developer list.");
            developers.add(developer);
        }
        return developers;
    }

    /**
     * check for valid Developer Data.
     * 
     * @param devName
     * @param email
     * @param index
     * @return boolean
     */
    private boolean validDeveloperData(String[] devName, String[] email, int index) {
        if (devName.length > index && email.length > index) {
            String name = devName[index];
            String emailId = email[index];
            if ((name != null && name.trim().length() > 0 && !name.trim().equals(NAME))
                            && (emailId != null && emailId.trim().length() > 0 && !emailId.trim()
                            .equals(EMAILID))) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method check for new user and save them and send mail for joining
     * the project.
     * 
     * @param developers list of developer
     * @param user
     * @param project
     * @param isUpdateRequest
     * @return List<Developer>
     */
    private List<Developer> handleDeveloper(List<Developer> developers, User user, Project project) {
        List<Developer> updatedDevelopers = new ArrayList<Developer>();
        List<String> emailList = new ArrayList<String>();
        for (Developer developer : developers) {
            logger.debug("Developer status =" + developer.getStatus() + " , user.userkey="
                            + user.getUserKey() + " ,developer.userkey =" + developer.getUserKey());
            if (developer.getStatus().equalsIgnoreCase(Developer.STATUS_JOIN_REQUEST)) {
                developer.setStatus(Developer.STATUS_REQUEST_ALLREADY_SENT);
                developer.setProjectKey(project.getKey());
                developer = developerService.saveDeveloper(developer);
                updatedDevelopers.add(developer);
                emailList.add(developer.getEmailId() + SEMICOLON + developer.getName() + SEMICOLON
                                + developer.getKey());
            } else if (developer.getStatus().equalsIgnoreCase(Developer.STATUS_REQUEST_ACCEPTED)) {
                developer.setProjectKey(project.getKey());
                developer = developerService.saveDeveloper(developer);
                updatedDevelopers.add(developer);
            } else {
                updatedDevelopers.add(developer);
            }
        }
        // /* Send email to developer */
        if (emailList.size() > 0) {
            List<String> otherInfoList = new ArrayList<String>();
            otherInfoList.add(user.getDisplayName());
            otherInfoList.add(project.getName());
            otherInfoList.add(project.getKey());
            EmailManager.sendMail(PROJECT_EMAIL_TYPE_INVITE_TO_JOIN, emailList, otherInfoList);
        }
        return updatedDevelopers;
    }

    @RequestMapping("/joinProject/{projectKey}/{developerKey}/{emailId}")
    public String joinProject(@PathVariable String projectKey, @PathVariable String developerKey,
                    @PathVariable String emailId,
                    Map<String, Object> model, HttpServletRequest req) {
        User user = null;
        ProjectDetail projectDetail = projectBuilder.getProjectDetail(projectKey);
        Developer developer = developerService.getDeveloperById(developerKey);
        if (projectDetail == null || projectDetail.getProject() == null || developer == null) {
            return "error/data-access-error";
        }
        if (req.getSession(true).getAttribute(WebConstants.USER) != null) {
            user = (User) req.getSession(true).getAttribute(WebConstants.USER);
            if (null == user.getUserKey()) {
                user = userService.addOrUpdateUser(user);
                req.getSession(true).setAttribute(WebConstants.USER, user);
            }
        }

        if (user != null) {
            developer.setStatus(Developer.STATUS_REQUEST_ACCEPTED);
            developer.setUserKey(user.getUserKey());
            developerService.saveDeveloper(developer);
            ViewStatus viewStatus = new ViewStatus();
            viewStatus.addData(WebConstants.PROJECT_DETAIL, projectDetail);
            viewStatus.setStatus(WebConstants.SUCCESS);
            model.put(VIEW_STATUS, viewStatus);
            // return "projects/show";
        }
        return "users/activate";
    }

    /**
     * Handles the request for search/list Project.
     * 
     * @param model Carries data for the view
     * @return View name.
     */
    // @RequestMapping(value = "/list", method = RequestMethod.GET)
    // public String listProjects(Map<String, Object> model) {
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String listProjects(@ModelAttribute RetrievalInfo retrievalInfo,
                    Map<String, Object> model) {
        /* Fetch the range parameters as sent in the request */
        long startIndex = retrievalInfo.getStartIndex();
        long noOfRecordsRequested = retrievalInfo.getNoOfRecords();
        /* Get the idea list */
        // List<Project> projects = new ArrayList<Project>();
        List<Project> projects = projectService.listProjects(retrievalInfo);
        Iterator<Project> iterator = projects.iterator();
        Project projectWithDesc = null;
        while (iterator.hasNext()) {
            projectWithDesc = iterator.next();
            projectWithDesc.setDescriptionAsString(projectWithDesc.getDescription());
            shortenFields(projectWithDesc);
        }
        /* Map of data to be inserted into the view status object */
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        /* Map containing the previous and next index values */
        HashMap<String, Long> pagingMap = new HashMap<String, Long>();
        /*
         * If the size of the list is greater than the no. of records requested
         * ,set the parameter 'next' to be used as start index for the next
         * page retrieval.
         */
        if (projects != null && projects.size() > noOfRecordsRequested) {
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
        // Create viewStatus
        ViewStatus viewStatus = createTheViewStatus(projects, parameters);
        model.put(VIEW_STATUS, viewStatus);
        return "projects/list";
    }

    /**
     * Shortens the length of title and description fields
     * 
     * @param project
     */
    private void shortenFields(Project project) {
        if (null != project) {
            /* 50 chars for title */
            project.setName(StringUtils.abbreviate(project.getName(), 50));
            /* 150 chars for description */
            project.setDescriptionAsString(StringUtils.abbreviate(project.getDescriptionAsString(),
                            150));
        }
    }

    /**
     * Create the {@link ViewStatus} object containing the data retrieved.This
     * object has the status codes set to success/error based on whether the
     * parameter list contains data or not.
     * 
     * @param listOfProjects the list containing data
     * @param hitCount
     * @return object containing the data and status codes
     */
    private ViewStatus createTheViewStatus(List<?> listProjects, Map<String, ?> parameters) {
        ViewStatus viewStatus = new ViewStatus();
        /*
         * If the list of ideas is not null and at least one idea
         * exists create the view status object with status as 'success' and
         * also set the data.
         */
        if (listProjects != null && listProjects.size() > WebConstants.ZERO) {
            viewStatus.setStatus(SUCCESS);
            viewStatus.addData(WebConstants.PROJECTS, listProjects);
            viewStatus.addData(WebConstants.MY_IDEAS_COUNT, listProjects.size());
            if (parameters != null) {
                Iterator<String> keysIter = parameters.keySet().iterator();
                String objectKey = null;
                while (keysIter.hasNext()) {
                    objectKey = keysIter.next();
                    viewStatus.addData(objectKey, parameters.get(objectKey));
                }
            }
        } else {/* In case the idea list is null or empty */
            viewStatus.setStatus(WebConstants.ERROR);
            viewStatus.addMessage(WebConstants.PROJECTS, WebConstants.NO_RECORDS_FOUND);
        }
        return viewStatus;
    }

    /**
     * @param projectService the projectService to set
     */
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 
     * @return the projectService
     */
    public ProjectService getProjectService() {
        return projectService;
    }

}

