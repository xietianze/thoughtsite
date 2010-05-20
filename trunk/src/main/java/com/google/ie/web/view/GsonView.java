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

package com.google.ie.web.view;

import com.google.ie.business.domain.User;
import com.google.ie.common.util.GsonUtility;
import com.google.ie.dto.RetrievalInfo;

import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.AbstractView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A custom view that is used to write JSON response. Typically used for Ajax
 * requests. Uses the Google Gson library to serialize objects into JSON
 * 
 * @author abraina
 * 
 */
public class GsonView extends AbstractView {

    /**
     * Default content type for GsonView.
     */
    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    // Attributes of model map that need to be serialized
    private Set<String> modelAttributes;
    private String contentType = "text/plain";
    private String characterEncoding = "utf-8";
    private int statusCode = 200;

    /**
     * Construct a new GsonView, setting the content type to application/json.
     */
    public GsonView() {
        setContentType(DEFAULT_CONTENT_TYPE);
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {

        String jsonResult = getJsonString(filterModel(model));

        // Write the result to response
        response.setContentType(contentType);
        response.setStatus(statusCode);
        response.setCharacterEncoding(characterEncoding);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("Pragma", "No-cache");
        if (isGzipInRequest(request)) {
            response.addHeader("Content-Encoding", "gzip");
            GZIPOutputStream out = null;
            try {
                out = new GZIPOutputStream(response.getOutputStream());
                out.write(jsonResult.getBytes(characterEncoding));
            } finally {
                if (out != null) {
                    out.finish();
                    out.close();
                }
            }
        } else {
            response.getWriter().print(jsonResult);
        }

    }

    /**
     * Serializes the given model into a JSON string
     * 
     * @param model
     * @return
     */
    protected String getJsonString(Map<String, Object> model) {
        String jsonString = GsonUtility.convertToJson(model);
        return jsonString;
    }

    /**
     * Filters out undesired attributes from the given model.
     * Removes BindingResult instances and attributes not included in the
     * modelAttributes property.
     * 
     * @param model the model
     * @return the object to be rendered
     */
    private Map<String, Object> filterModel(Map<String, Object> model) {
        Map<String, Object> result = new HashMap<String, Object>(model.size());
        // If renderedAttributes is empty, then use all attributes of the model
        Set<String> modelAttributes =
                        !CollectionUtils.isEmpty(this.modelAttributes) ? this.modelAttributes
                        : model.keySet();

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            // Check for instance of BindingResult and check key in set of
            // modelAttributes
            if (!(entry.getValue() instanceof BindingResult) && !(entry.getValue() instanceof User)
                            && !(entry.getValue() instanceof RetrievalInfo)
                            && modelAttributes.contains(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * check whether gzip encoding is accepted by the browser
     * 
     * @param request
     * @return a boolean whether gzip encoding is accepted by the browser
     */
    private boolean isGzipInRequest(HttpServletRequest request) {
        String header = request.getHeader("Accept-Encoding");
        return header != null && header.indexOf("gzip") >= 0;
    }

    public void setModelAttributes(Set<String> modelAttributes) {
        this.modelAttributes = modelAttributes;
    }

    public Set<String> getModelAttributes() {
        return modelAttributes;
    }

}

