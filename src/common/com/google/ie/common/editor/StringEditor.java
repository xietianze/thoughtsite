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

package com.google.ie.common.editor;

import java.beans.PropertyEditorSupport;

/**
 * Property editor that trims Strings and replaces the carriage return with an
 * empty string.
 * 
 * <p>
 * Optionally allows transforming an empty string into a <code>null</code>
 * value. Needs to be explictly registered, e.g. for command binding.
 * 
 * @author Sachneet
 * 
 */
public class StringEditor extends PropertyEditorSupport {
    private static final String REGEX_EXPRESSION = "[\r]";
    private static final String EMPTY_STRING = "";
    private boolean emptyAsNull;

    /**
     * Create a new instance.
     * 
     * @param emptyAsNull <code>true</code> if an empty String is to be
     *        transformed into <code>null</code>
     */
    public StringEditor(boolean emptyAsNull) {
        super();
        this.emptyAsNull = emptyAsNull;
    }

    @Override
    public void setAsText(String text) {
        if (text == null) {
            setValue(null);
        } else {
            String value = text.trim();
            if (this.emptyAsNull && "".equals(value)) {
                setValue(null);
            } else {
                String replacedString = text.replaceAll(REGEX_EXPRESSION, EMPTY_STRING);
                setValue(replacedString);
            }
        }

    }

    @Override
    public String getAsText() {
        Object value = getValue();
        return (value != null ? value.toString() : "");
    }
}

