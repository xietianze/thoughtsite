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

package com.google.ie.common.util;

import com.google.ie.business.domain.BadWord;
import com.google.ie.business.domain.Idea;
import com.google.ie.business.domain.IdeaComment;
import com.google.ie.business.domain.Project;
import com.google.ie.business.domain.ProjectComment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to map the entity name with the entity
 * 
 * @author adahiya
 * 
 */
public class EntityMapperUtility {

    /** Map to store name to entity mapping */
    private static Map<String, Class<? extends Serializable>> indexableEntities = new HashMap<String, Class<? extends Serializable>>();

    static {
        indexableEntities.put(Idea.class.getSimpleName(), Idea.class);
        indexableEntities.put(BadWord.class.getSimpleName(), BadWord.class);
        indexableEntities.put(IdeaComment.class.getSimpleName(), IdeaComment.class);
        indexableEntities.put(Project.class.getSimpleName(), Project.class);
        indexableEntities.put(ProjectComment.class.getSimpleName(), ProjectComment.class);
    }

    /**
     * Return the mapped entity identified by entity name
     * 
     * @param entityName name of the entity to be mapped
     * @return entity class
     */
    public static Class<? extends Serializable> getEntity(String entityName) {
        return indexableEntities.get(entityName);
    }
}

