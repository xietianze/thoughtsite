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

import com.google.ie.business.domain.AdminRequest;
import com.google.ie.dto.RetrievalInfo;

import java.util.List;

/**
 * @author asirohi
 * 
 */
public interface AdminRequestDao extends BaseDao {
    /**
     * Save admin request.
     * 
     * @param adminRequest
     * @return boolean
     */
    boolean saveRequest(AdminRequest adminRequest);

    /**
     * Get All Admin Requests.
     * 
     * @param retrievalInfo
     * @return List<AdminRequest>
     */
    List<AdminRequest> getAllAdminRequests(RetrievalInfo retrievalInfo);

}

