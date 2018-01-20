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

import com.google.common.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Mapping of builtin Java types to their matching JSON Schema primitive type
 *
 * @author fge
 */
public enum SimpleTypeMappings {
    // Integer types
    PRIMITIVE_BYTE(byte.class, "integer"),
    PRIMITIVE_SHORT(short.class, "integer"),
    PRIMITIVE_INTEGER(int.class, "integer"),
    PRIMITIVE_LONG(long.class, "integer"),
    BYTE(Byte.class, "integer"),
    SHORT(Short.class, "integer"),
    INTEGER(Integer.class, "integer"),
    LONG(Long.class, "integer"),
    BIGINTEGER(BigInteger.class, "integer"),
    // Number types
    PRIMITIVE_FLOAT(float.class, "number"),
    PRIMITIVE_DOUBLE(double.class, "number"),
    FLOAT(Float.class, "number"),
    DOUBLE(Double.class, "number"),
    BIGDECIMAL(BigDecimal.class, "number"),
    // Boolean types
    PRIMITIVE_BOOLEAN(boolean.class, "boolean"),
    BOOLEAN(Boolean.class, "boolean"),
    // String types
    PRIMITIVE_CHAR(char.class, "string"),
    CHAR(Character.class, "string"),
    CHARSEQUENCE(CharSequence.class, "string"),
    STRING(String.class, "string"),
    UUID(UUID.class, "string"),
    ZONEDDATETIME(ZonedDateTime.class, "string"),
    LOCALDATE(LocalDate.class, "string"),
    INSTANT(Instant.class, "string");

    private static final Map<Class<?>, String> MAPPINGS;

    static {
        // Class objects are all singletons, so we can use that
        MAPPINGS = new IdentityHashMap<Class<?>, String>();

        for (final SimpleTypeMappings mapping : values()) {
            MAPPINGS.put(mapping.c, mapping.schemaType);
        }
    }

    private final Class<?> c;
    private final String schemaType;

    SimpleTypeMappings(final Class<?> c, final String schemaType) {
        this.c = c;
        this.schemaType = schemaType;
    }

    /**
     * Return a primitive type for a given class, if any
     *
     * @param type the class
     * @return the primitive type if found, {@code null} otherwise
     */
    public static Optional<String> forClass(final Type type) {
        if (!(type instanceof Class)) {
            return Optional.empty();
        } else if (MAPPINGS.containsKey(type)) {
            return Optional.of(MAPPINGS.get(type));
        } else {
            return Optional.empty();
        }
    }

    private static Class [] COLLECTION_CLASSES = new Class[] {Collection.class, Iterable.class};
    public static boolean isCollectionLike(Type type) {
        TypeToken token = TypeToken.of(type);
        for (Class collectionClass : COLLECTION_CLASSES) {
            if (collectionClass.isAssignableFrom(token.getRawType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Informs which the given type is some Java default type
     *
     * @param type the class
     * @return true if c is a Java default Ype, false otherwise
     */
    public static boolean isSimpleType(final Type type) {
        return forClass(type) != null;
    }
}
