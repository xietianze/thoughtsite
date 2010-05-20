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

package com.google.ie.common.cache;

/**
 * Contains constants used by {@link CacheHelper} class
 * 
 * @author Sachneet
 * 
 */
public interface CacheConstants {
    /* Name spaces for entities */
    String IDEA_NAMESPACE = "idea";
    String PROJECT_NAMESPACE = "project";
    String TAG_NAMESPACE = "tag";
    String CATEGORY_NAMESPACE = "category";
    String IDEA_SHARD_NAMESPACE = "idea_shard";
    String UNDERSCORE = "_";
    String RECENT_IDEAS = "recentIdeas";
    String POPULAR_IDEAS = "popularIdeas";
    String RECENTLY_PICKED_IDEAS = "recentlyPickedIdeas";
    String RECENTLY_PICKED = "recently_picked";

    /* Keys used to put list of specific entities */
    String CATEGORIES = "categories";
    String TAG_CLOUD = "tagCloud";
    /* Message for IllegalArgumentException */
    String NAMESPACE_OR_KEY_IS_NULL = "The namespace or the key is null !";
    /* Popular ideas expiry time for cache.Currently 120 seconds */
    int POPULAR_IDEAS_EXPIRATION_DELAY = 120;
    /* Recent ideas expiry time for cache.Currently 120 seconds */
    int RECENT_IDEAS_EXPIRATION_DELAY = 120;
    /*
     * Recently picked ideas expiry time for cache.Currently 120 seconds
     */
    int RECENTLY_PICKED_IDEAS_EXPIRATION_DELAY = 120;
    /*
     * Expiration time for the categories data in cache.Currently 86400 seconds
     * (one day)
     */
    int CATEGORIES_EXPIRATION_DELAY = 86400;
    /* Expiration time for the tag cloud data in cache.Currently 120 seconds */
    int TAG_CLOUD_DATA_EXPIRATION_DELAY = 120;

}

