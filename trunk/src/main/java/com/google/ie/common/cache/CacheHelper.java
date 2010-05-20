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

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import java.io.Serializable;

/**
 * A Utility class to manage cache.
 * 
 * @author asirohi
 * 
 */
public class CacheHelper {
    /* Default expiry time.Currently 5 minutes */
    private static final int DEFAULT_EXPIRY_TIME = 300;
    /**
     * Memcache service instance.
     */
    private static MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

    /**
     * Fetch a previously-stored value, or null if not set.
     * 
     * @param nameSpace the namespace to be prefixed to the key
     * @param key the key object used to store the cache entry
     * @return an object stored with the given key.
     */
    public static Object getObject(String nameSpace, Serializable key) {
        key = prefixNamespaceToKey(nameSpace, key);
        return cache.get(key);
    }

    /**
     * Store a new value into the cache using the key.
     * 
     * @param nameSpace the namespace to be prefixed to the key
     * @param key the key object used to store the new cache entry
     * @param value value for the new cache entry
     */
    public static void putObject(String nameSpace, Serializable key, Serializable value) {
        key = prefixNamespaceToKey(nameSpace, key);
        /* add default expiry time */
        cache.put(key, value, Expiration.byDeltaSeconds(DEFAULT_EXPIRY_TIME));
    }

    /**
     * Store a new value into the cache using key along with the expiration
     * time.
     * 
     * @param nameSpace the namespace to be prefixed to the key
     * @param key the key object used to store the new cache entry
     * @param value value for the new cache entry
     * @param expirationDelay expire delay in seconds.
     */
    public static void putObject(String nameSpace, Serializable key, Serializable value,
                    int expirationDelay) {
        key = prefixNamespaceToKey(nameSpace, key);
        cache.put(key, value, Expiration.byDeltaSeconds(expirationDelay));

    }

    /**
     * Tests whether a given value is in cache, even if its value is null.
     * 
     * @param nameSpace the namespace to be prefixed to the key
     * @param key the key object used to store the cache entry
     * @return true if the cache contains an entry for the key
     */
    public static boolean containsObject(String nameSpace, Serializable key) {
        key = prefixNamespaceToKey(nameSpace, key);
        return cache.contains(key);
    }

    /**
     * Removes key from the cache.
     * 
     * @param nameSpace the namespace to be prefixed to the key
     * @param key the key object used to store the cache entry
     */
    public static boolean deleteObject(String nameSpace, Serializable key) {
        key = prefixNamespaceToKey(nameSpace, key);
        return cache.delete(key);
    }

    /**
     * Empties the cache of all values. Statistics are not affected.
     * Note that clearAll() does not respect namespaces - this flushes the cache
     * for every namespace.
     */
    public static void clearAllObjects() {
        cache.clearAll();
    }

    /**
     * Prefixes the namespace to the key.
     * 
     * @param nameSpace the namespace to be prefixed to the key
     * @param key the key object to which the namespace is to be prefixed
     * @return the key prefixed with the namespace with an underscore joining
     *         them
     */
    private static Serializable prefixNamespaceToKey(String nameSpace, Serializable key) {
        if (null == nameSpace || null == key) {
            throw new IllegalArgumentException(CacheConstants.NAMESPACE_OR_KEY_IS_NULL);
        }
        key = nameSpace + CacheConstants.UNDERSCORE + key;
        return key;
    }

}

