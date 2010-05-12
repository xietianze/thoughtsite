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

package com.google.ie.business.dao.impl;

import com.google.ie.business.dao.IdeaCategoryDao;
import com.google.ie.business.domain.IdeaCategory;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * A JDO implementation object for IdeaCategoryDao.
 * 
 * @author Sachneet
 * 
 */
public class IdeaCategoryDaoImpl extends BaseDaoImpl implements IdeaCategoryDao {

    @Override
    public List<IdeaCategory> getIdeaCategories() {
        Collection<IdeaCategory> results = getJdoTemplate().find(
                        IdeaCategory.class);
        /* Detach the result */
        results = getJdoTemplate().detachCopyAll(results);
        return new ArrayList<IdeaCategory>(results);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public IdeaCategory saveIdeaCategory(IdeaCategory category) {
        Date date = new Date();
        category.setCreatedOn(date);
        category.setUpdatedOn(date);
        IdeaCategory savedCategory = getJdoTemplate().makePersistent(category);
        return savedCategory;
    }

    @Override
    public IdeaCategory getCategoryByName(String categoryName) {
        Collection<IdeaCategory> results = getJdoTemplate().find(
                        IdeaCategory.class,
                        "name == categoryName", "String categoryName", categoryName);
        /* Detach the result */
        results = getJdoTemplate().detachCopyAll(results);
        List<IdeaCategory> list = new ArrayList<IdeaCategory>(results);
        if (list.size() > DaoConstants.ZERO) {
            IdeaCategory category = list.get(DaoConstants.ZERO);
            return category;
        }
        return null;
    }
}

