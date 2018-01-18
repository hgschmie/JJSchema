/*
 * Copyright (c) 2014, Danilo Reinert (daniloreinert@growbit.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.reinert.jjschema;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.reinert.jjschema.annotations.Nullable;
import com.github.reinert.jjschema.annotations.SchemaIgnore;
import com.github.reinert.jjschema.exception.TypeException;
import com.google.common.reflect.TypeToken;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * Generates JSON schema from Java Types
 *
 * @author reinert
 */
public abstract class JsonSchemaGenerator {

    private final Set<ManagedReference> forwardReferences = new LinkedHashSet<>();
    private final Set<ManagedReference> backReferences = new LinkedHashSet<>();

    protected final JsonNodeFactory nodeFactory;
    protected final JsonSchemaGeneratorConfiguration config;

    protected JsonSchemaGenerator(JsonSchemaGeneratorConfiguration config) {
        this.nodeFactory = config.nodeFactory();
        this.config = config;
    }

    private Set<ManagedReference> getForwardReferences() {
        return forwardReferences;
    }

    private void pushForwardReference(ManagedReference forwardReference) {
        getForwardReferences().add(forwardReference);
    }

    private boolean isForwardReferencePiled(ManagedReference forwardReference) {
        return getForwardReferences().contains(forwardReference);
    }

    private boolean pullForwardReference(ManagedReference forwardReference) {
        return getForwardReferences().remove(forwardReference);
    }

    private Set<ManagedReference> getBackwardReferences() {
        return backReferences;
    }

    private void pushBackwardReference(ManagedReference backReference) {
        getBackwardReferences().add(backReference);
    }

    private boolean isBackwardReferencePiled(ManagedReference backReference) {
        return getBackwardReferences().contains(backReference);
    }

    private boolean pullBackwardReference(ManagedReference backReference) {
        return getBackwardReferences().remove(backReference);
    }

    protected ObjectNode createRefSchema(String ref) {
        return createInstance().put("$ref", ref);
    }

    /**
     * Reads annotations and put its values into the generating schema. Usually, some verification is done for not putting the default values.
     */
    protected abstract void processSchemaProperty(ObjectNode schema, AttributeHolder attributeHolder, boolean isRoot);

    protected ObjectNode createInstance() {
        return nodeFactory.objectNode();
    }

    public <T> Optional<ObjectNode> generateSchema(Class<T> type) {
        ObjectNode schema = createInstance();
        return checkAndProcessType(type, schema);
    }

    /**
     * Checks whether the type is SimpleType (mapped by {@link SimpleTypeMappings}), Collection or Iterable (for mapping arrays), Void type (returning null), or
     * custom Class (for mapping objects).
     *
     * @return the full schema represented as an ObjectNode.
     */
    protected <T> Optional<ObjectNode> checkAndProcessType(Class<T> type, ObjectNode schema) {
        String s = SimpleTypeMappings.forClass(type);
        // If it is a simple type, then just put the type
        if (s != null) {
            schema.put("type", s);
        }
        // If it is a Collection or Iterable the generate the schema as an array
        else if (Iterable.class.isAssignableFrom(type)
                || Collection.class.isAssignableFrom(type)) {
            checkAndProcessArrayType(type, schema);
        }
        // If it is void then return null
        else if (type == Void.class || type == void.class) {
            schema = null;
        }
        // If it is an Enum than process like enum
        else if (type.isEnum()) {
            processEnum(type, schema);
        }
        // If none of the above possibilities were true, then it is a custom object
        else {
            schema = processCustomType(type, schema);
        }
        return Optional.ofNullable(schema);
    }

    /**
     * Generates the schema of custom java types
     *
     * @return the full schema of custom java types
     */
    protected <T> ObjectNode processCustomType(Class<T> type, ObjectNode schema) {
        schema.put("type", "object");
        // fill root object properties
        processRootAttributes(type, schema, true);

        if (config.processProperties()) {
            processProperties(type, schema);
        }

        if (config.processFields()) {
            processFields(type, schema);
        }

        return schema;
    }

    /**
     * Generates the schema of collections java types
     */
    private <T> void checkAndProcessArrayType(Class<T> type, ObjectNode schema) {
        // missing out on any item references. Needs to be fixed.
        schema.put("type", "array");
    }

    private <T> void processEnum(Class<T> type, ObjectNode schema) {
        ArrayNode enumArray = schema.putArray("enum");
        for (T constant : type.getEnumConstants()) {
            String value = constant.toString();
            // Check if value is numeric
            try {
                // First verifies if it is an integer
                Long integer = Long.parseLong(value);
                enumArray.add(integer);
            }
            // If not then verifies if it is an floating point number
            catch (NumberFormatException e) {
                try {
                    BigDecimal number = new BigDecimal(value);
                    enumArray.add(number);
                }
                // Otherwise add as String
                catch (NumberFormatException e1) {
                    enumArray.add(value);
                }
            }
        }
    }

    private void processPropertyCollection(Method method, ObjectNode schema) {
        schema.put("type", "array");
        Type methodType = method.getGenericReturnType();
        if (!ParameterizedType.class.isAssignableFrom(methodType.getClass())) {
            throw new IllegalStateException("Collection property must be parameterized: " + method.getName());
        }
        ParameterizedType genericType = (ParameterizedType) methodType;
        checkState(genericType.getActualTypeArguments().length > 0,
                "No type arguments in return type of %s found!", method.getName());
        Class<?> genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
        generateSchema(genericClass).ifPresent(objectNode -> schema.set("items", objectNode));
    }

    protected <T> void processRootAttributes(Class<T> type, ObjectNode schema, boolean isRoot) {
        Optional<AttributeHolder> rootAttributes = AttributeHolder.locate(type);
        rootAttributes.ifPresent(attributeHolder -> processSchemaProperty(schema, attributeHolder, isRoot));
    }

    protected <T> void processProperties(Class<T> type, ObjectNode schema) {
        Map<String, ObjectNode> methodSchemaProperties = findSchemaPropertiesFromMethods(type);
        for (Map.Entry<String, ObjectNode> entry : methodSchemaProperties.entrySet()) {
            String fieldName = entry.getKey();
            ObjectNode objectNode = entry.getValue();
            addPropertyToSchema(schema, fieldName, objectNode);
        }
    }

    protected <T> void processFields(Class<T> type, ObjectNode schema) {
        Map<String, ObjectNode> fieldSchemaProperties = findSchemaPropertiesFromFields(type);
        for (Map.Entry<String, ObjectNode> entry : fieldSchemaProperties.entrySet()) {
            String fieldName = entry.getKey();
            ObjectNode objectNode = entry.getValue();
            addPropertyToSchema(schema, fieldName, objectNode);
        }
    }

    protected <T> ObjectNode generatePropertySchema(Class<T> type, Method method, Field field) {
        Class<?> returnType = method != null ? method.getReturnType() : field.getType();

        AccessibleObject propertyReflection = field != null ? field : method;

        SchemaIgnore ignoreAnn = propertyReflection.getAnnotation(SchemaIgnore.class);
        if (ignoreAnn != null) {
            return null;
        }

        ObjectNode schema = createInstance();

        JsonManagedReference refAnn = propertyReflection.getAnnotation(JsonManagedReference.class);
        if (refAnn != null) {
            ManagedReference forwardReference;
            Class<?> genericClass;
            Class<?> collectionClass;
            if (Collection.class.isAssignableFrom(returnType)) {
                if (method != null) {
                    ParameterizedType genericType = (ParameterizedType) method.getGenericReturnType();
                    genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
                } else {
                    genericClass = field.getClass();
                }
                collectionClass = returnType;
            } else {
                genericClass = returnType;
            }
            forwardReference = new ManagedReference(type, refAnn.value(), genericClass);

            if (!isForwardReferencePiled(forwardReference)) {
                pushForwardReference(forwardReference);
            } else {
                pullForwardReference(forwardReference);
                pullBackwardReference(forwardReference);
                //return null;
                return createRefSchema("#");
            }
        }

        JsonBackReference backRefAnn = propertyReflection.getAnnotation(JsonBackReference.class);
        if (backRefAnn != null) {
            ManagedReference backReference;
            Class<?> genericClass;
            Class<?> collectionClass;
            if (Collection.class.isAssignableFrom(returnType)) {
                ParameterizedType genericType = (ParameterizedType) method
                        .getGenericReturnType();
                genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
                collectionClass = returnType;
            } else {
                genericClass = returnType;
            }
            backReference = new ManagedReference(genericClass, backRefAnn.value(), type);

            if (isForwardReferencePiled(backReference) &&
                    !isBackwardReferencePiled(backReference)) {
                pushBackwardReference(backReference);
            } else {
                return null;
            }
        }

        if (Collection.class.isAssignableFrom(returnType)) {
            processPropertyCollection(method, schema);
        } else {
            schema = generateSchema(returnType);
        }

        // Check the field annotations, if the get method references a field, or the
        // method annotations on the other hand, and processSchemaProperty them to
        // the JsonSchema object

        Optional<AttributeHolder> fieldAttributes = AttributeHolder.locate(propertyReflection);
        if (fieldAttributes.isPresent()) {
            processSchemaProperty(schema, fieldAttributes.get(), false);
        }

        // Check if the Nullable annotation is present, and if so, add 'null' to type attr
        Nullable nullable = propertyReflection.getAnnotation(Nullable.class);
        if (nullable != null) {
            if (returnType.isEnum()) {
                ((ArrayNode) schema.get("enum")).add("null");
            } else {
                String oldType = schema.get("type").asText();
                ArrayNode typeArray = schema.putArray("type");
                typeArray.add(oldType);
                typeArray.add("null");
            }
        }

        return schema;
    }

    private void addPropertyToSchema(ObjectNode schema, String name, ObjectNode property) {

        if (property.has("selfRequired")) {
            addToRequired(schema, name);
            property.remove("selfRequired");
        }

        addToProperties(schema, name, property);
    }

    private void addToRequired(ObjectNode schema, String name) {
        ArrayNode requiredNode;
        if (schema.has("required")) {
            requiredNode = (ArrayNode) schema.get("required");
        } else {
            requiredNode = schema.putArray("required");
        }
        requiredNode.add(name);
    }

    private void addToProperties(ObjectNode schema, String name, ObjectNode property) {

        ObjectNode propertiesNode;
        if (schema.has("properties")) {
            propertiesNode = (ObjectNode) schema.get("properties");
        } else {
            propertiesNode = schema.putObject("properties");
        }
        propertiesNode.set(name, property);
    }

    /**
     * Utility method to find properties from a Java Type following Beans Convention.
     */
    private <T> Map<String, ObjectNode> findSchemaPropertiesFromMethods(Class<T> type) {
        Map<String, ObjectNode> propertyMap = config.sortSchemaProperties() ? new TreeMap<>() : new LinkedHashMap<>();

        TypeToken<T> typeToken = TypeToken.of(type);

        for (TypeToken<? super T> implementingTypeToken : typeToken.getTypes()) {
            Class<?> clazz = implementingTypeToken.getRawType();
            Method[] methods = clazz.getMethods();

            for (Method method : methods) {
                Optional<AttributeHolder> attributeHolder = acceptElement(method);
                if (attributeHolder.isPresent()) {
                    String propertyName = propertyNameFromMethod(method);

                    if (propertyMap.containsKey(propertyName)) {
                        throw new IllegalStateException(format(Locale.ENGLISH,
                                "Property %s defined multiple times (saw %s)", propertyName, clazz.getSimpleName()));
                    }
                    Optional<ObjectNode> propertyNode = generateObjectNode(type, method, attributeHolder.get());
                    propertyNode.ifPresent(node -> propertyMap.put(propertyName, node));
                }
            }
        }

        return propertyMap;
    }

    private Optional<AttributeHolder> acceptElement(Method method) {
        // must be readable
        if (!method.isAccessible()) {
            return Optional.empty();
        }
        // ignore weird stuff
        if (method.isBridge() || method.isSynthetic() || method.isDefault()) {
            return Optional.empty();
        }

        // fetch annotations for method
        return AttributeHolder.locate(method);
    }

    private String propertyNameFromMethod(Method method) {
        Class<?> clazz=method.getDeclaringClass();
        try {
            BeanInfo info = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : props) {
                if (method.equals(pd.getWriteMethod()) || method.equals(pd.getReadMethod())) {
                    return pd.getName();
                }
            }
        } catch (IntrospectionException e) {
            // ignore, let the throw statement below execute.
        }
        throw new IllegalStateException(format(Locale.ENGLISH, "Could not locate property name for %s", method.getName()));
    }

    private Optional<ObjectNode> generateObjectNode(Class<?> type, Method method, AttributeHolder attributeHolder) {
        Class<?> returnType = method.getReturnType();

        if (attributeHolder.ignored()) {
            return Optional.empty();
        }

        ObjectNode schema = createInstance();

        JsonManagedReference refAnn = method.getAnnotation(JsonManagedReference.class);
        if (refAnn != null) {
            Class<?> genericClass;
            if (Collection.class.isAssignableFrom(returnType)) {
                ParameterizedType genericType = (ParameterizedType) method.getGenericReturnType();
                genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
            } else {
                genericClass = returnType;
            }
            ManagedReference forwardReference = new ManagedReference(type, refAnn.value(), genericClass);

            if (!isForwardReferencePiled(forwardReference)) {
                pushForwardReference(forwardReference);
            } else {
                pullForwardReference(forwardReference);
                pullBackwardReference(forwardReference);
                //return null;
                return Optional.of(createRefSchema("#"));
            }
        }

        JsonBackReference backRefAnn = method.getAnnotation(JsonBackReference.class);
        if (backRefAnn != null) {
            Class<?> genericClass;
            if (Collection.class.isAssignableFrom(returnType)) {
                ParameterizedType genericType = (ParameterizedType) method.getGenericReturnType();
                genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
            } else {
                genericClass = returnType;
            }
            ManagedReference backReference = new ManagedReference(genericClass, backRefAnn.value(), type);

            if (isForwardReferencePiled(backReference) &&
                    !isBackwardReferencePiled(backReference)) {
                pushBackwardReference(backReference);
            } else {
                return Optional.empty();
            }
        }

        if (Collection.class.isAssignableFrom(returnType)) {
            processPropertyCollection(method, schema);
        } else {
            schema = generateSchema(returnType);
        }

        processSchemaProperty(schema, attributeHolder, false);

        // Check if the Nullable annotation is present, and if so, add 'null' to type attr
        if (attributeHolder.nullable()) {
            if (returnType.isEnum()) {
                ((ArrayNode) schema.get("enum")).add("null");
            } else {
                JsonNode typeNode = schema.get("type");
                if (typeNode instanceof ArrayNode) {
                    ArrayNode arrayNode = (ArrayNode) typeNode;
                    arrayNode.add("null");
                } else if (typeNode instanceof TextNode) {
                    ArrayNode typeArray = schema.putArray("type");
                    typeArray.add(typeNode);
                    typeArray.add("null");
                } else {
                    throw new IllegalStateException("Return type is not nullable");
                }
            }
        }

        return Optional.of(schema);
    }

    private <T> List<Field> findFields(Class<T> type) {
        Field[] fields = type.getDeclaredFields();
        if (config.sortSchemaProperties()) {
            // Order the fields
            Arrays.sort(fields, new Comparator<Field>() {
                public int compare(Field m1, Field m2) {
                    return m1.getName().compareTo(m2.getName());
                }
            });
        }
        List<Field> props = new ArrayList<Field>();
        // get fields
        for (Field field : fields) {
            Class<?> declaringClass = field.getDeclaringClass();

            int fieldModifiers = field.getModifiers();

            if (field.isSynthetic() || field.isEnumConstant() || Modifier.isStatic(fieldModifiers) || Modifier.isTransient(fieldModifiers)) {
                continue;
            }

            if (declaringClass.equals(Object.class)
                    || Collection.class.isAssignableFrom(declaringClass)) {
                continue;
            }

            String name = field.getName();
            if (field.getName().equalsIgnoreCase(name)) {
                Optional<AttributeHolder> attributeHolder = AttributeHolder.locate(field);
                if (attributeHolder.isPresent() || !config.processAnnotatedOnly()) {
                    props.add(field);
                }
            }
        }
        return props;
    }
}
