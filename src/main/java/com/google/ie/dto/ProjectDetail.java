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

import com.google.ie.business.domain.Developer;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.ProjectComment;
import com.google.ie.business.domain.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A data transfer object representing the idea information.
 * 
 * @author Charanjeet singh
 */
public class ProjectDetail implements Serializable {

    /** A unique identifier for the class */
    private static final long serialVersionUID = -2605556889201805705L;
    /**
     * Project object.
     */
    private Project project;
    /**
     * Title of idea with which project is being created.
     */
    private String ideaTitle;
    /**
     * Total number of developer count.
     */
    private int developerCount;
    /**
     * List of comments
     */
    private List<ProjectComment> comments;

    /** Creator of the project */
    private User user;
    /**
     * Developer list
     */
    private List<Developer> developers = new ArrayList<Developer>();
    /**
     * check for project is editable by user or not.
     */
    private boolean projectEditable;

    /**
     * @return the projectEditable
     */
    public boolean isProjectEditable() {
        return projectEditable;
    }

    /**
     * @param projectEditable the projectEditable to set
     */
    public void setProjectEditable(boolean projectEditable) {
        this.projectEditable = projectEditable;
    }

    public ProjectDetail() {
    }

    /**
     * @return the comments
     */
    public List<ProjectComment> getComments() {
        if (comments == null)
            comments = new ArrayList<ProjectComment>();
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(List<ProjectComment> comments) {
        this.comments = comments;
    }

    public void addComment(ProjectComment comment) {
        if (comments == null)
            comments = new ArrayList<ProjectComment>();
        comments.add(comment);
    }

    public int getDeveloperCount() {
        return developerCount;
    }

    public void setDeveloperCount(int developerCount) {
        this.developerCount = developerCount;
    }

    public List<Developer> getDevelopers() {
        if (developers == null)
            developers = new ArrayList<Developer>();
        return developers;
    }

    public void setDevelopers(List<Developer> developers) {
        this.developers = developers;
    }

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    /**
     * @param idea the idea to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * @return the idea
     */
    public Project getProject() {
        return project;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    /**
     * @param ideaTitle the ideaTitle to set
     */
    public void setIdeaTitle(String ideaTitle) {
        this.ideaTitle = ideaTitle;
    }

    /**
     * @return the ideaTitle
     */
    public String getIdeaTitle() {
        return ideaTitle;
    }
}

