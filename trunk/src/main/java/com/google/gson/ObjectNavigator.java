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

package com.google.gson;

import com.google.appengine.repackaged.com.google.common.base.Preconditions;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Provides ability to apply a visitor to an object and all of its fields
 * recursively.
 * 
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
final class ObjectNavigator {

    public interface Visitor {
        public void start(ObjectTypePair node);

        public void end(ObjectTypePair node);

        /**
         * This is called before the object navigator starts visiting the
         * current object
         */
        void startVisitingObject(Object node);

        /**
         * This is called to visit the current object if it is an array
         */
        void visitArray(Object array, Type componentType);

        /**
         * This is called to visit an object field of the current object
         */
        void visitObjectField(Field f, Type typeOfF, Object obj);

        /**
         * This is called to visit an array field of the current object
         */
        void visitArrayField(Field f, Type typeOfF, Object obj);

        /**
         * This is called to visit an object using a custom handler
         * 
         * @return true if a custom handler exists, false otherwise
         */
        public boolean visitUsingCustomHandler(ObjectTypePair objTypePair);

        /**
         * This is called to visit a field of the current object using a custom
         * handler
         */
        public boolean visitFieldUsingCustomHandler(Field f, Type actualTypeOfField, Object parent);

        /**
         * Retrieve the current target
         */
        Object getTarget();

        void visitPrimitive(Object primitive);
    }

    private final ExclusionStrategy exclusionStrategy;
    private final ObjectTypePair objTypePair;

    /**
     * @param objTypePair The object,type (fully genericized) being navigated
     * @param exclusionStrategy the concrete strategy object to be used to
     *        filter out fields of an object.
     */
    ObjectNavigator(ObjectTypePair objTypePair, ExclusionStrategy exclusionStrategy) {
        Preconditions.checkNotNull(exclusionStrategy);

        this.objTypePair = objTypePair;
        this.exclusionStrategy = exclusionStrategy;
    }

    /**
     * Navigate all the fields of the specified object.
     * If a field is null, it does not get visited.
     */
    public void accept(Visitor visitor) {
        TypeInfo objTypeInfo = new TypeInfo(objTypePair.getType());
        if (exclusionStrategy.shouldSkipClass(objTypeInfo.getRawClass())) {
            return;
        }
        boolean visitedWithCustomHandler = visitor.visitUsingCustomHandler(objTypePair);
        if (!visitedWithCustomHandler) {
            Object obj = objTypePair.getObject();
            Object objectToVisit = (obj == null) ? visitor.getTarget() : obj;
            if (objectToVisit == null) {
                return;
            }
            objTypePair.setObject(objectToVisit);
            visitor.start(objTypePair);
            try {
                if (objTypeInfo.isArray()) {
                    visitor.visitArray(objectToVisit, objTypePair.getType());
                } else if (objTypeInfo.getActualType() == Object.class
                                && isPrimitiveOrString(objectToVisit)) {
                    // "primitives"
                    // we should rethink this!!!
                    visitor.visitPrimitive(objectToVisit);
                    objectToVisit = visitor.getTarget();
                } else {
                    visitor.startVisitingObject(objectToVisit);
                    ObjectTypePair currObjTypePair = objTypePair.toMoreSpecificType();
                    Class<?> topLevelClass = new TypeInfo(currObjTypePair.getType()).getRawClass();
                    for (Class<?> curr = topLevelClass; curr != null && !curr.equals(Object.class); curr = curr
                                    .getSuperclass()) {
                        if (!curr.isSynthetic()) {
                            navigateClassFields(objectToVisit, curr, visitor);
                        }
                    }
                }
            } finally {
                visitor.end(objTypePair);
            }
        }
    }

    /**
     * Check for primitive or string type
     * 
     * @param objectToVisit Object
     * @return boolean
     */
    private boolean isPrimitiveOrString(Object objectToVisit) {
        Class<?> realClazz = objectToVisit.getClass();
        return realClazz == Object.class || realClazz == String.class
                        || Primitives.unwrap(realClazz).isPrimitive();
    }

    /**
     * Method navigate all fields of class and check for exclusion strategy.
     * 
     * @param obj Object
     * @param clazz Class
     * @param visitor Visitor
     */
    private void navigateClassFields(Object obj, Class<?> clazz, Visitor visitor) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            FieldAttributes fieldAttributes = new FieldAttributes(f);
            if (exclusionStrategy.shouldSkipField(fieldAttributes)
                            || exclusionStrategy
                            .shouldSkipClass(fieldAttributes.getDeclaredClass())) {
                continue; // skip
            }
            // Set field to be accessible
            f.setAccessible(true);
            TypeInfo fieldTypeInfo = TypeInfoFactory.getTypeInfoForField(f, objTypePair
                            .getType());
            Type declaredTypeOfField = fieldTypeInfo.getActualType();
            boolean visitedWithCustomHandler =
                            visitor.visitFieldUsingCustomHandler(f, declaredTypeOfField, obj);
            if (!visitedWithCustomHandler) {
                if (fieldTypeInfo.isArray()) {
                    visitor.visitArrayField(f, declaredTypeOfField, obj);
                } else {
                    visitor.visitObjectField(f, declaredTypeOfField, obj);
                }
            }
        }
    }
}

