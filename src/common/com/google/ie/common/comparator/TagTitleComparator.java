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

package com.google.ie.common.comparator;

import com.google.ie.business.domain.Tag;

import java.util.Comparator;

/**
 * An object that compares {@link Tag} objects with respect to their titles
 * 
 * @author Sachneet
 * 
 */
public final class TagTitleComparator implements Comparator<Tag> {
    /**
     * A shared default instance of this comparator.This is the only instance
     * available for this comparator.
     */
    public static final TagTitleComparator TAG_TITLE_COMPARATOR = new TagTitleComparator();

    @Override
    public int compare(Tag tag1, Tag tag2) {
        return tag1.getTitle().compareTo(tag2.getTitle());
    }

    /*
     * Suppresses default constructor, ensuring no instance is created from
     * outside the class.
     */
    private TagTitleComparator() {

    }
}

