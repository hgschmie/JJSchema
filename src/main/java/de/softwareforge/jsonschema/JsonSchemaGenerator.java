/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.softwareforge.jsonschema;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.reflect.TypeToken;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

public final class JsonSchemaGenerator {

    private final JsonNodeFactory nodeFactory;
    private final JsonSchemaGeneratorConfiguration config;

    private final Set<Type> dictionary = new HashSet<>();

    JsonSchemaGenerator(JsonSchemaGeneratorConfiguration config) {
        this.nodeFactory = config.nodeFactory();
        this.config = config;
    }

    private static Optional<AttributeHolder> acceptMethod(Method method) {
        // ignore weird stuff
        int modifiers = method.getModifiers();
        if (method.isBridge()
                || method.isSynthetic()
                || method.isDefault()
                || Modifier.isStatic(modifiers)) {
            return Optional.empty();
        }

        // fetch annotations for method
        return AttributeHolder.locate(method);
    }

    private static Optional<AttributeHolder> acceptField(Field field) {
        // ignore weird stuff
        int modifiers = field.getModifiers();
        if (field.isEnumConstant()
                || field.isSynthetic()
                || Modifier.isTransient(modifiers)
                || Modifier.isStatic(modifiers)) {
            return Optional.empty();
        }

        // fetch annotations for method
        return AttributeHolder.locate(field);
    }

    public <T> ObjectNode generateSchema(Class<T> type) {
        TypeToken typeToken = TypeToken.of(type);
        Optional<AttributeHolder> rootAttributes = AttributeHolder.locate(typeToken.getRawType());

        ObjectNode schema = nodeFactory.objectNode();

        if (config.addSchemaVersion()) {
            schema.put("$schema", "http://json-schema.org/draft-04/schema#");
        }

        createSchemaForType(schema, type, rootAttributes);

        return schema;
    }

    private <T> void createSchemaForType(ObjectNode schema, Type type, Optional<AttributeHolder> attributes) {
        if (dictionary.contains(type)) {
            throw new IllegalStateException("Recursion detected, not supported!");
        }

        // Simple types are a schema with their type.
        Optional<String> s = SimpleTypeMappings.forClass(type);
        // If it is a simple type, then just put the type
        if (s.isPresent()) {
            addTypeToSchema(schema, s.get());
        } else if (SimpleTypeMappings.isCollectionLike(type)) {
            augmentSchemaWithCollection(schema, type);
        }
        // void to the null type. Does not really make sense.
        else if (type == Void.class || type == void.class) {
            addTypeToSchema(schema, "null");
        }
        // If it is an Enum than process like enum
        else if (isEnum(type, attributes)) {
            augmentSchemaWithEnum((Class<?>) type, schema);
        }
        // what about map?
        else {
            dictionary.add(type);
            augmentSchemaWithCustomType(schema, type, attributes);
            dictionary.remove(type);
        }
        attributes.ifPresent(schemaAttributes -> augmentAttributes(schema, type, schemaAttributes));
    }

    private <T> void augmentSchemaWithEnum(Class<T> type, ObjectNode schema) {
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

    private <T> void augmentSchemaWithCustomType(ObjectNode schema, Type type, Optional<AttributeHolder> attributeHolder) {
        addTypeToSchema(schema, "object");

        if (attributeHolder.isPresent()) {
            if (attributeHolder.get().ignoredProperties()) {
                return;
            }
        }

        if (config.processProperties()) {
            findSchemaPropertiesFromMethods(type, schema).forEach((propertyName, objectNode) -> addToProperties(schema, propertyName, objectNode));
        }

        if (config.processFields()) {
            findSchemaPropertiesFromFields(type, schema).forEach((propertyName, objectNode) -> addToProperties(schema, propertyName, objectNode));
        }
    }

    private Map<String, ObjectNode> findSchemaPropertiesFromMethods(Type type, ObjectNode parent) {
        Map<String, ObjectNode> propertyMap = config.sortSchemaProperties() ? new TreeMap<>() : new LinkedHashMap<>();

        TypeToken<?> typeToken = TypeToken.of(type);

        for (TypeToken<?> implementingTypeToken : typeToken.getTypes()) {
            Class<?> clazz = implementingTypeToken.getRawType();

            Method[] methods = clazz.getDeclaredMethods();

            for (Method method : methods) {
                Optional<AttributeHolder> attributeHolder = acceptMethod(method);
                if (attributeHolder.isPresent()) {
                    AttributeHolder attributes = attributeHolder.get();
                    String propertyName = attributes.named().orElse(propertyName(method));

                    if (propertyMap.containsKey(propertyName)) {
                        throw new IllegalStateException(format(Locale.ENGLISH,
                                "Property %s defined multiple times (saw %s)", propertyName, clazz.getSimpleName()));
                    }

                    if (attributes.required()) {
                        addToRequired(parent, propertyName);
                    }

                    if (attributes.ignored()) {
                        continue;
                    }

                    TypeToken<?> returnType = implementingTypeToken.resolveType(method.getGenericReturnType());

                    ObjectNode propertyNode = nodeFactory.objectNode();
                    createSchemaForType(propertyNode, returnType.getType(), Optional.of(attributes));
                    propertyMap.put(propertyName, propertyNode);
                }
            }
        }

        return propertyMap;
    }

    private Map<String, ObjectNode> findSchemaPropertiesFromFields(Type type, ObjectNode parent) {
        Map<String, ObjectNode> propertyMap = config.sortSchemaProperties() ? new TreeMap<>() : new LinkedHashMap<>();

        TypeToken<?> typeToken = TypeToken.of(type);

        for (TypeToken<?> implementingTypeToken : typeToken.getTypes()) {
            Class<?> clazz = implementingTypeToken.getRawType();

            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                Optional<AttributeHolder> attributeHolder = acceptField(field);
                if (attributeHolder.isPresent()) {
                    AttributeHolder attributes = attributeHolder.get();
                    String propertyName = attributes.named().orElse(propertyName(field));

                    if (propertyMap.containsKey(propertyName)) {
                        throw new IllegalStateException(format(Locale.ENGLISH,
                                "Property %s defined multiple times (saw %s)", propertyName, field.getName()));
                    }
                    if (attributes.required()) {
                        addToRequired(parent, propertyName);
                    }

                    if (attributes.ignored()) {
                        continue;
                    }

                    TypeToken fieldType = implementingTypeToken.resolveType(field.getGenericType());

                    ObjectNode propertyNode = nodeFactory.objectNode();
                    createSchemaForType(propertyNode, fieldType.getType(), Optional.of(attributes));
                    propertyMap.put(propertyName, propertyNode);
                }
            }
        }

        return propertyMap;
    }

    private void augmentAttributes(ObjectNode schema, Type type, AttributeHolder schemaAttributes) {
        schemaAttributes.augmentCommonAttributes(schema);
        schemaAttributes.$ref().ifPresent($ref -> schema.put("$ref", $ref));

        if (!schemaAttributes.additionalProperties()) {
            schema.put("additionalProperties", false);
        }

        // Check if the Nullable annotation is present, and if so, add 'null' to type attr
        if (schemaAttributes.nullable()) {
            if (isEnum(type, Optional.of(schemaAttributes))) {
                ((ArrayNode) schema.get("enum")).addNull();
            }
            addTypeToSchema(schema, "null");
        }
    }

    private boolean isEnum(Type type, Optional<AttributeHolder> schemaAttributes) {
        // enum annotation enforces enum type
        if (schemaAttributes.isPresent() && !schemaAttributes.get().enums().isEmpty()) {
            return true;
        }

        // enum class type enforces enum type
        if ((type instanceof Class && ((Class<?>) type).isEnum())) {
            return true;
        }

        return false;
    }

    private void augmentSchemaWithCollection(ObjectNode schema, Type type) {
        addTypeToSchema(schema, "array");

        TypeToken typeToken = TypeToken.of(type);

        if (typeToken.isArray()) {
            augmentItems(schema, typeToken.getComponentType().getType());
        } else {
            Class<?> clazz = typeToken.getRawType();
            checkState(clazz.getTypeParameters().length > 0, "No type arguments in return type found!");

            Type itemType = typeToken.resolveType(clazz.getTypeParameters()[0]).getType();
            augmentItems(schema, itemType);
        }
    }

    public void augmentItems(ObjectNode schema, Type itemType) {
        ObjectNode itemNode = nodeFactory.objectNode();
        TypeToken typeToken = TypeToken.of(itemType);
        Optional<AttributeHolder> itemAttributes = AttributeHolder.locate(typeToken.getRawType());
        createSchemaForType(itemNode, itemType, itemAttributes);
        schema.set("items", itemNode);
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

    private String propertyName(AnnotatedElement element) {
        if (element instanceof Field) {
            // Field name should be the same as the exposed property. Good luck.
            return ((Field) element).getName();
        } else if (element instanceof Method) {
            Method method = (Method) element;
            Class<?> clazz = method.getDeclaringClass();
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
        throw new IllegalArgumentException(format(Locale.ENGLISH, "%s is not a field or method", element));
    }

    private void addTypeToSchema(ObjectNode schema, String type) {
        if (schema.has("type")) {
            JsonNode typeNode = schema.get("type");
            if (typeNode.isArray()) {
                ArrayNode arrayNode = (ArrayNode) typeNode;
                // questionable as this may create duplicates.
                arrayNode.add(type);
            } else if (typeNode.isTextual()) {
                ArrayNode typeArray = schema.putArray("type");
                typeArray.add(typeNode);
                typeArray.add(type);
                schema.replace("type", typeArray);
            } else {
                throw new IllegalStateException("Return type is not nullable");
            }
        } else {
            schema.put("type", type);
        }
    }
}
