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

import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.Tag;
import com.google.ie.business.domain.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A data transfer object representing the idea information.
 * 
 * @author Charanjeet singh
 */
public class IdeaDetail implements Serializable {

    /** A unique identifier for the class */
    private static final long serialVersionUID = -2605556889201805705L;
    /**
     * Idea object.
     */
    private Idea idea;

    /**
     * List of tag
     */
    private List<Tag> tags;

    /** Creator of the idea */
    private User user;
    private List<IdeaComment> comments;

    private List<Project> projects;
    private Map<String, String> mapOfProjectIdAndName;

    public Map<String, String> getMapOfProjectIdAndName() {
        return mapOfProjectIdAndName;
    }

    public void setMapOfProjectIdAndName(Map<String, String> mapOfProjectIdAndName) {
        this.mapOfProjectIdAndName = mapOfProjectIdAndName;
    }

    public List<IdeaComment> getComments() {
        return comments;
    }

    public void addComments(IdeaComment ideaComment) {
        if (comments == null) {
            comments = new ArrayList<IdeaComment>();
        }
        comments.add(ideaComment);
    }

    /**
     * @return the projects
     */
    public List<Project> getProjects() {
        return projects;
    }

    /**
     * @param projects the projects to set
     */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public IdeaDetail() {
    }

    /**
     * @return the tagList
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * @param tagList the tagList to set
     */
    public void setTags(List<Tag> tagList) {
        this.tags = tagList;
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
    public void setIdea(Idea idea) {
        this.idea = idea;
    }

    /**
     * @return the idea
     */
    public Idea getIdea() {
        return idea;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}

