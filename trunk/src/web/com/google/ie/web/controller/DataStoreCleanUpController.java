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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to clean up the database
 * 
 * @author Sachneet
 * 
 */
@Controller
public class DataStoreCleanUpController {
    private static Logger log = Logger.getLogger(DataStoreCleanUpController.class);
    private static final int THREE_HUNDRED = 300;
    private static final int ZERO = 0;

    /**
     * Clean database data.
     * 
     * @return String
     */
    @RequestMapping("/cleanDatastore")
    public String clean() {
        DatastoreService datastore =
                        DatastoreServiceFactory.getDatastoreService();
         String[] arrayOfEntities = { "Audit", "Tag", "Idea", "AdminRequest",
         "CommentVote", "Developer",
         "EntityIndex", "IdeaComment", "IdeaVote", "Project",
         "ProjectComment",
         "ShardedCounter", "User", "IdeaCategory", "BadWord", "_ah_SESSION",
         "content",
         "meta", };
//        String[] arrayOfEntities = { "_ah_SESSION", "content" };
        /*
         * Fetch 300 records.Such a big number is used because other then the
         * last three entities all others won't have much data (in development)
         */
        FetchOptions options = FetchOptions.Builder.withLimit(THREE_HUNDRED);
        Query query;
        PreparedQuery results = null;
        for (String kind : arrayOfEntities) {
            /* prepare the query for the entity kind */
            query = new Query(kind);
            /* Makes this query fetch and return only keys, not full entities. */
            query.setKeysOnly();
            /* Get the PreparedQuery object */
            results = datastore.prepare(query);
            /*
             * No of entities existing in the datastore of the corresponding
             * type
             */
            int noOfEntities = results.countEntities();
            log.info(noOfEntities + "    no. of entities exist of kind   " + query.getKind());
            /* If entities exist delete */
            if (noOfEntities > ZERO) {
                try {

                    for (Entity session : results.asIterable(options)) {

                        datastore.delete(session.getKey());
                    }
                    log.info("                A max of " + options.getLimit()
                                    + " of them deleted if existent");
                } catch (Throwable e) {
                    log.error(e.getMessage());
                }
            }

        }
        return "queue/queue";
    }

}

