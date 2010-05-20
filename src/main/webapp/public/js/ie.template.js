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

/**
 * Library for ideaexchange js templating
 * 
 * @author Abhishek
 */
ie.Template = {
    DELEMETER : '@',
    data : '',
    template : ''
};

/*
 * @param type possible types are ('form', 'select', 'html') @param data json
 * object
 */
ie.Template.render = function(data, directive) {
    // it will render html based on id no looping possible useing this render
    // method
    if (data.length > 0) {
        if ($.isArray(directive)) {
            for ( var element in directive) {
                alert($(directive[element]).tagName.toLowerCase());
            }
        } else {
        }
    }
};

/*
 * @param type possible types are ('form', 'select', 'html') @param data json
 * object
 */
ie.Template.renderByToken = function(data, template, directive) {
    var html = '';
    this.data = data;
    this.template = template;
    if (data.length > 0) {
        if ($.isArray(directive)) {
            for ( var i = 0; i < data.length; i++) {
                var interimTemplate = template;
                for ( var element in directive) {
                    interimTemplate = interimTemplate
                            .replace(
                                    /(this.DELEMETER + directive[element] + this.DELEMETER)/gi,
                                    data[i][directive[element]]);
                }
                html += interimTemplate;
            }
        } else {

            // it should work in iterative manner
            for ( var i = 0; i < data.length; i++) {
                var interimTemplate = template;
                for ( var element in data[i]) {
                    interimTemplate = interimTemplate.replace(this.DELEMETER
                            + element + this.DELEMETER, data[i][element]);
                }
                html += interimTemplate;
            }
        }
    } else {
        // direct assignment
        for ( var element in data) {
            html += template.replace(this.DELEMETER + element + this.DELEMETER,
                    data[element]);
        }
    }
    return html;
};
