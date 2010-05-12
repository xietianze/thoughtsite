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

package com.google.ie.business.service.impl;

import com.google.ie.business.dao.BadWordDao;
import com.google.ie.business.domain.BadWord;
import com.google.ie.business.domain.EntityIndex;
import com.google.ie.business.service.BadWordService;
import com.google.ie.business.service.EntityIndexService;
import com.google.ie.common.taskqueue.IndexQueueUpdater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * A Service implementation for the {@link BadWord} entity.
 * 
 * @author Anuj Sirohi
 */
@Service
public class BadWordServiceImpl implements BadWordService {

    @Autowired
    private BadWordDao badWordDao;
    @Autowired
    private EntityIndexService entityIndexService;
    @Autowired
    private IndexQueueUpdater indexQueueUpdater;

    public IndexQueueUpdater getIndexQueueUpdater() {
        return indexQueueUpdater;
    }

    public void setIndexQueueUpdater(IndexQueueUpdater indexQueueUpdater) {
        this.indexQueueUpdater = indexQueueUpdater;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public BadWord saveBadWord(BadWord badWord) {
        badWord = badWordDao.saveBadWord(badWord);
        /*
         * Index the entity.Create an EntityIndex object for the entity to be
         * indexed and then queue the job of indexing to task queue
         */
        EntityIndex entityIndex = entityIndexService.createEntityIndex(badWord.getKey());
        getIndexQueueUpdater().indexEntity(entityIndex.getKey());

        return badWord;
    }
}

