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

package com.google.ie.common.exception;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An object of type {@link SimpleMappingExceptionResolver} which will log the
 * exception before resolving it to the view name based on the given
 * exception mappings.
 * 
 * 
 * @author asirohi
 * 
 */
public class IdeaExchangeExceptionResolver extends SimpleMappingExceptionResolver {
    /** Logger for logging information */
    private static Logger log = Logger.getLogger(IdeaExchangeExceptionResolver.class);

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,
                    HttpServletResponse response, Object handler, Exception ex) {
        log.error("Unhandled exception caught : " + ex.toString(), ex);
        return super.doResolveException(request, response, handler, ex);
    }
}

