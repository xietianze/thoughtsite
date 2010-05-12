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

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.apphosting.api.DeadlineExceededException;
import com.google.ie.business.domain.EntityIndex;
import com.google.ie.business.service.EntityIndexService;
import com.google.ie.common.constants.IdeaExchangeConstants;
import com.google.ie.common.util.EntityMapperUtility;
import com.google.ie.common.util.SearchUtility;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;

/**
 * A controller that handles request for indexing un-indexed entities.
 * 
 * @author Ashish K. Dahiya
 * 
 */

@Controller
@RequestMapping("/indexer")
public class IndexController {
    private static final Logger LOG = Logger.getLogger(IndexController.class);
    @Autowired
    private EntityIndexService entityIndexService;

    /**
     * Index the {@link EntityIndex} entity represented by the key
     * 
     * @param request {@link HttpServletRequest} object
     * @param encodedKey the key of the entity to be indexed
     * @return View Name
     */
    @RequestMapping("/indexentity/{key}")
    public String indexEntity(HttpServletRequest request, @PathVariable String key) {
        LOG.debug("IndexController: Index Entity");
        String count = request.getHeader(IdeaExchangeConstants.APPENGINE_TASKRETRYCOUNT);

        if (count == null || count.trim().equals("0")) {
            long startTime = GregorianCalendar.getInstance().getTimeInMillis();
            try {
                Key indexKey = KeyFactory.stringToKey(key);
                /* retrieve and un-indexed entity */
                EntityIndex entityIndex = entityIndexService.getEntity(indexKey);
                if (entityIndex != null && entityIndex.getIndexed() == 0) {
                    boolean flag = indexEntity(entityIndex);
                    if (flag && LOG.isDebugEnabled()) {
                        LOG.debug("Record for "
                                        + entityIndex.getKey().getKind()
                                        + " index in "
                                        + (GregorianCalendar.getInstance()
                                        .getTimeInMillis() - startTime)
                                        + " ms");
                    }
                }

            } catch (DeadlineExceededException e) {
                LOG.error("DeadlineExceededException in indexIdeas", e);
            }
        } else {
            LOG.debug("Bypassing the entity index as the count is " + count);
        }
        return "queue/queue";
    }

    /**
     * Index an un-indexed entity and mark it as indexed.
     * 
     */
    @RequestMapping("/index")
    public String indexEntity() {
        LOG.debug("IndexController: Indexing Entities");
        try {
            /* retrieve and un-indexed entity */
            EntityIndex entityIndex = entityIndexService.getUnIndexedEntity();
            boolean flag = indexEntity(entityIndex);
            if (flag) {
                LOG.info("indexing of entity with key :" + entityIndex.getParentKey()
                                + "  successful");
            }

        } catch (DeadlineExceededException e) {
            LOG.error("DeadlineExceededException in indexIdeas", e);
        }
        return "queue/queue";
    }

    /**
     * Index the {@link EntityIndex} object received as argument
     * 
     * @param entityIndex {@link EntityIndex} object to be indexed
     * @return boolean whether he entity was indexed or not
     */
    private boolean indexEntity(final EntityIndex entityIndex) {
        boolean flag = false;
        try {

            Serializable entity = null;
            if (entityIndex != null) {
                String key = KeyFactory.keyToString(entityIndex
                                .getParentKey());
                /* fetch entity details */
                entity = entityIndexService.getEntity(key, EntityMapperUtility
                                .getEntity(entityIndex
                                .getParentKey().getKind()));
            }
            if (entity != null) {
                LOG.debug("indexing entity with key :" + entityIndex.getParentKey());
                /* index the idea using compass */
                boolean entityIndexed = SearchUtility.indexEntity(entity);
                if (entityIndexed) {
                    entityIndex.setIndexed(1);
                    /* update the entity status from un-indexed to indexed */
                    entityIndexService.updateEntityIndex(entityIndex);
                    LOG.info("indexing of entity with key :" + entityIndex.getParentKey()
                                    + "  successful");
                    flag = true;
                }
            }

        } catch (DeadlineExceededException e) {
            LOG.error("DeadlineExceededException in indexIdeas", e);
        }
        return flag;
    }

    public EntityIndexService getEntityIndexService() {
        return entityIndexService;
    }

    public void setEntityIndexService(EntityIndexService entityIndexService) {
        this.entityIndexService = entityIndexService;
    }

}

