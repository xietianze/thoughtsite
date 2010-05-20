// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.ie.business.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.ie.business.dao.DeveloperDao;
import com.google.ie.business.dao.impl.DeveloperDaoImpl;
import com.google.ie.business.domain.Developer;
import com.google.ie.business.domain.User;
import com.google.ie.dto.RetrievalInfo;
import com.google.ie.test.ServiceTest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test case for DeveloperServiceImpl class
 * 
 * @author Surabhi Bhatnagar
 * 
 */
public class DeveloperServiceImplTest extends ServiceTest {
    private DeveloperServiceImpl developerService;
    private DeveloperDao mockDeveloperDao = mock(DeveloperDaoImpl.class);

    @Before
    public void setUp() {
        super.setUp();
        developerService = new DeveloperServiceImpl();
        developerService.setDeveloperDao(mockDeveloperDao);

    }

    @Test
    public void updateStatus() {

        Developer dev = new Developer();
        dev.setKey("developerKey");

        Developer newDev = new Developer();
        newDev.setKey("developerKey");
        newDev.setStatus("New_Request");

        when(mockDeveloperDao.findEntityByPrimaryKey(Developer.class, dev.getKey()))
                        .thenReturn(dev);
        when(mockDeveloperDao.saveDeveloper(dev)).thenReturn(newDev);
        assertEquals("New_Request", newDev.getStatus());
    }

    @Test
    public void getDeveloperByUserKey() {
        RetrievalInfo retrievalInfo = new RetrievalInfo();

        Developer dev = new Developer();
        dev.setKey("developerKey");

        List<Developer> devList = new ArrayList<Developer>();
        devList.add(dev);
        Developer newDev = new Developer();
        newDev.setKey("developerKey");
        newDev.setStatus("New_Request");

        User user = new User();
        user.setUserKey("userKey");

        when(mockDeveloperDao.getDevelopersByUserKey(user.getUserKey(), retrievalInfo))
                        .thenReturn(devList);
        assertEquals("developerKey", devList.get(0).getKey());
    }

}
