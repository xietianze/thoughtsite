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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class for accessing value for object by using reflection.
 * 
 * @author gmaurya
 * 
 */
public class ClassUtility {

    /**
     * Get the getter method names for attributes.
     * 
     * @param fieldName the name of the field
     * @param fieldType the type of the field
     * @return String the name of the getter method for the field
     */
    private static <T> String getGetterMethodName(String fieldName, java.lang.Class<T> fieldType) {

        String firstStringChar = fieldName.substring(0, 1).toUpperCase();
        String remaingString = fieldName.substring(1);

        String methodName = null;
        /* In case the the field is a boolean */
        if (fieldType != null && fieldType == boolean.class
                        || fieldType == Boolean.class) {

            methodName = "is" + firstStringChar + remaingString;
        } else {
            methodName = "get" + firstStringChar + remaingString;
        }
        return methodName;
    }

    /**
     * This method invoke the method and return the return value.
     * 
     * @param object the object
     * @param fileName String
     * @return the result of dispatching the method represented by this object
     *         on object
     * 
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    public static Object getObject(Object object, String fileName)
                    throws SecurityException, NoSuchMethodException,
                    IllegalArgumentException, IllegalAccessException,
                    InvocationTargetException {
        String methodName = getGetterMethodName(fileName, null);
        Method method = null;
        Class inputClass = object.getClass();
        method = getMethod(inputClass, methodName, null);

        return method.invoke(object, (Object[]) null);

    }

    /**
     * This method return the list of method of any class.
     * 
     * @param clazz Class
     * @param methodName String
     * @param parameterTypes Class[]
     * @return Method
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    private static <T, Y> Method getMethod(Class<T> clazz, String methodName,
                    Class<Y>[] parameterTypes) throws SecurityException,
                    NoSuchMethodException {
        Method method = null;
        try {
            method = clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            if (methodName.startsWith("get")) {
                methodName = methodName.replaceFirst("get", "is");
            } else if (methodName.startsWith("is")) {
                methodName = methodName.replaceFirst("get", "is");
            }

            method = clazz.getMethod(methodName, parameterTypes);
        }

        return method;
    }

}

