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
 * Library for ideaexchange js Tag cloud
 * 
 * @author Abhishek
 */
ie.TagCloud = {
    /** @Private */
    maxSize : 10,
    /** @Private */
    miSize : 1,
    /** @Private */
    maxWeight : 10,
    /** @Private */
    minWeight : 1
};

/**
 * prepare tag cloud
 */
ie.TagCloud.render = function(jsonData, weights, directive, options) {
    if (undefined != weights.maxWeight) {
        this.maxWeight = weights.maxWeight;
    }
    if (undefined != weights.minWeight) {
        this.minWeight = weights.minWeight;
    }
    // ((M-m)/100)*p + m
    // TODO: Abhishek, Need to do in exponential series which will work better
    var outpuTagCloudHtml = '';
    if (0 < jsonData.length) {
        // calculate maxWeight and minWeight for tags

        for ( var i in jsonData) {
            var actualSize = Math.ceil(((jsonData[i][directive['weightage']]/this.maxWeight)*100)/10);
            outpuTagCloudHtml += '<a href="';
            outpuTagCloudHtml += options.url + jsonData[i][directive['title']];
            outpuTagCloudHtml += '" class="' + directive['css']
                    + actualSize + '">';
            outpuTagCloudHtml += jsonData[i][directive['title']];
            outpuTagCloudHtml += '(' + jsonData[i][directive['weightage']] + ')</a> ';
        }
    } else {
        // return blank string as no need to generate html
        // TODO: Abhishek, Need to define message
    }
    return outpuTagCloudHtml;
}
