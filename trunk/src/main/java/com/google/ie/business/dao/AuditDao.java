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

package com.google.ie.business.dao;

import com.google.ie.business.domain.Audit;

/**
 * A data access object specification for Audit entity.
 * 
 * @author asirohi
 * 
 */
public interface AuditDao extends BaseDao {

    /**
     * Saves the Audit entity to a data store.
     * 
     * @param audit the {@link Audit} object to be saved
     */
    void saveAudit(Audit audit);
}

