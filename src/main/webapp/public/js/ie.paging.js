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
 * Library for ideaexchange js Paging
 * 
 * @author Abhishek
 */
ie.Paging = {
    previousCss : 'previous',
    nextCss : 'next',
    selectedPageCss : 'page_sel',
    pageCss : 'page_no',
    recordPerPage : '20',
    pageRange : 10
};


/**
 * get HTML by providing offset
 */
ie.Paging.getHTML = function(jsonData, callback) {
    var outputHtml = '';
    if(undefined != jsonData.previous && jsonData.previous >= 0) {
        outputHtml += '<a title="Previous" onclick="' + callback + '(' + jsonData.previous + ')" href="javascript:void(0);" class="' + this.previousCss + '">&laquo; Previous</a>';
    }
    if(undefined != jsonData.next && jsonData.next >= 0) {
        outputHtml += '<a title="Next" onclick="' + callback + '(' + jsonData.next + ')" href="javascript:void(0);" class="' + this.previousCss + '">Next &raquo;</a>';
    }
    return outputHtml;
}

/**
 * get HTML by providing offset
 */
ie.Paging.getHTMLByOffset = function(totalRecords, startOffset) {
    if(0 == totalRecords % this.recordPerPage) {
        var totalPageCount = totalRecords/this.recordPerPage + 1;
    }
    else {
        var totalPageCount = totalRecords/this.recordPerPage;
    }
        selectedPage = (startOffset / this.recordPerPage) + 1;
    this.getHTMLByPage(totalPageCount, startOffset);
}

/**
 * get HTML by providing offset
 */
ie.Paging.getHTMLByPage = function(totalPageCount, selectedPage) {
    var minRange = (this.pageRange % 2 == 0) ? (this.pageRange / 2) - 1 : (this.pageRange - 1) / 2;
    var maxRange = (this.pageRange % 2 == 0) ? minRange + 1 : minRange;
    // selectedPage will be current page
    var minPage = selectedPage - minRange;
    var maxPage = selectedPage + maxRange;
    minPage = (minPage < 1) ? 1 : minPage;
    maxPage = (maxPage < (minPage + this.pageRange - 1)) ? minPage + this.pageRange - 1 : maxPage;
    
    // totalPageCount is total pages for records
    if (maxPage > totalPageCount) {
        minPage = (minPage > 1) ? totalPageCount - this.pageRange + 1 : 1;
        maxPage = totalPageCount;
    }
    
    minPage = (minPage < 1) ? 1 : minPage;

    
    
    
    var outputHtml = '';
    // Add previous page link
    if (selectedPage != 1) {
        outputHtml += '<a title="Previous" href="#" class="' + this.previousCss + '">&laquo;</a>';
    }
    
    var pageCount = 1;
    for ( var counter = minPage; counter < maxPage; counter++) {
        if (selectedPage == counter) {
            outputHtml += '<a href="#" class="' + this.selectedPageCss + '">'
                    + counter + '</a>';
        } else {
            outputHtml += '<a href="#" class="' + this.pageCss + ' '
                    + this.pageCss + '">' + counter + '</a>';
        }
        pageCount++;
    }

    if (selectedPage < totalPageCount) {
        outputHtml += '<a title="Next" href="#" class="' + this.nextCss + '">&raquo;</a>';
    }
    return outputHtml;
}
