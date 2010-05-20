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

import java.io.Serializable;
import java.util.List;

/**
 * @author gmaurya
 * 
 */
public class SearchResult implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 6106216651541779349L;
    private int totalCount;
    private List<? extends Serializable> data;

    /**
     * @param data the data to set
     */
    public <T extends Serializable> void setData(List<T> data) {
        this.data = data;
    }

    /**
     * @return the data
     */
    public List<? extends Serializable> getData() {
        return data;
    }

    /**
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }

}

