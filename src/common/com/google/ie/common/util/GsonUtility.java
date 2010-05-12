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

package com.google.ie.common.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

/**
 * Utility methods for using GSON
 * 
 * @author Sachneet
 */
public class GsonUtility {
    private static GsonBuilder gsonBuilder = new GsonBuilder();
    private static Gson gson;
    static {
        /* Register date serializer. */
        gsonBuilder.registerTypeAdapter(Date.class, new GsonUtility.DateAdapter());
        /* Use the custom serializer to serialize Map */
        gsonBuilder.registerTypeAdapter(Map.class, new GsonUtility.MapSerializer());
        gson = gsonBuilder.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT,
                        Modifier.VOLATILE).setExclusionStrategies(new JdoExclusionStrategy())
                        .create();
    }

    /*
     * Suppresses default constructor, ensuring no instance is created from
     * outside the class.
     */
    private GsonUtility() {

    }

    /**
     * This method serializes the specified object into its equivalent Json
     * representation.
     * 
     * @param object the object to be converted to JSON string
     * @return the GSON string of the param object
     */
    public static String convertToJson(Object object) {
        String gsonString = null;
        gsonString = gson.toJson(object);
        return gsonString;
    }

    /**
     * This method deserializes the specified Json into an object of the
     * specified type
     * 
     * @param <T> the type of the desired object
     * @param json the string from which the object is to be deserialized
     * @param classOfT the class of T
     * @return an object of type T from the string
     * 
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertFromJson(String json, Class<T> classOfT) {
        T target = (T) gson.fromJson(json, (Type) classOfT);
        return target;
    }

    /**
     * A date type adapter for a {@link Date} object.
     * 
     * @author Charanjeet
     */
    private static class DateAdapter implements JsonSerializer<Date> {

        private final String pattern = "dd.MM.yyyy HH:mm:ss z";
        private final TimeZone indianTime = TimeZone.getTimeZone("IST");
        private final DateFormat format = new SimpleDateFormat(pattern);

        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                        context) {

            format.setTimeZone(indianTime);
            String dateFormatAsString = format.format(src);
            return new JsonPrimitive(dateFormatAsString);
        }
    }

    /**
     * A custom serializer for the {@link Map} class.
     * 
     * @author Sachneet
     * 
     */
    private static class MapSerializer implements JsonSerializer<Map<String, Object>> {

        @SuppressWarnings("unchecked")
        @Override
        public JsonElement serialize(Map<String, Object> src, Type typeOfSrc,
                        JsonSerializationContext context) {
            /*
             * This JsonObject would be used to hold the same keys with their
             * values serialized
             */
            JsonObject map = new JsonObject();
            Iterator<?> iterator = src.entrySet().iterator();
            Map.Entry<String, Object> entry;
            Object value;
            JsonElement valueElementSerialized;
            /* Iterate the map and serialize the individual values */
            while (iterator.hasNext()) {
                entry = (Entry<String, Object>) iterator.next();
                value = entry.getValue();
                valueElementSerialized = context.serialize(value, value.getClass());
                /*
                 * Put the serialized value into the JsonObject corresponding to
                 * the respective key
                 */
                map.add(entry.getKey().toString(), valueElementSerialized);
            }
            return map;
        }
    }

    /**
     * This class provides exclusion strategy to GSON builder.According to
     * ExclusionStrategy GSON builder decides whether to convert objects into
     * Json or not.
     * 
     * @author gmaurya
     * 
     */
    public static class JdoExclusionStrategy implements ExclusionStrategy {

        private static final String JDO = "jdo";

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            String fieldName = f.getName();
            /*
             * Skip fields that are used to save JDO state in detached objects
             */
            if (StringUtils.startsWith(fieldName, JDO)) {
                return true;
            }
            return false;
        }
    }
}

