
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
