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

import com.google.common.reflect.TypeToken;

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

enum SimpleTypeMappings {
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

    private static final Class[] COLLECTION_CLASSES = new Class[]{Collection.class, Iterable.class};

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

    static Optional<String> forClass(final Type type) {
        if (!(type instanceof Class)) {
            return Optional.empty();
        } else if (MAPPINGS.containsKey(type)) {
            return Optional.of(MAPPINGS.get(type));
        } else {
            return Optional.empty();
        }
    }

    static boolean isCollectionLike(Type type) {
        TypeToken token = TypeToken.of(type);
        for (Class collectionClass : COLLECTION_CLASSES) {
            if (collectionClass.isAssignableFrom(token.getRawType())) {
                return true;
            }
        }
        return false;
    }
}
