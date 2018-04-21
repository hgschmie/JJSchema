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

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

enum SimpleTypeMappings {
    // Integer types
    PRIMITIVE_BYTE(byte.class, "integer", ""),
    PRIMITIVE_SHORT(short.class, "integer", ""),
    PRIMITIVE_INTEGER(int.class, "integer", ""),
    PRIMITIVE_LONG(long.class, "integer", ""),
    BYTE(Byte.class, "integer", ""),
    SHORT(Short.class, "integer", ""),
    INTEGER(Integer.class, "integer", ""),
    LONG(Long.class, "integer", ""),
    BIGINTEGER(BigInteger.class, "integer", ""),
    // Number types
    PRIMITIVE_FLOAT(float.class, "number", ""),
    PRIMITIVE_DOUBLE(double.class, "number", ""),
    FLOAT(Float.class, "number", ""),
    DOUBLE(Double.class, "number", ""),
    BIGDECIMAL(BigDecimal.class, "number", ""),
    // Boolean types
    PRIMITIVE_BOOLEAN(boolean.class, "boolean", ""),
    BOOLEAN(Boolean.class, "boolean", ""),
    // String types
    PRIMITIVE_CHAR(char.class, "string", ""),
    CHAR(Character.class, "string", ""),
    CHARSEQUENCE(CharSequence.class, "string", ""),
    STRING(String.class, "string", ""),
    // special
    UUID(UUID.class, "string", "uuid"),
    URI(URI.class, "string", "uri"),

    // date and time
    ZONEDDATETIME(ZonedDateTime.class, "string", "date-time"),
    LOCALDATETIME(LocalDateTime.class, "string", "date-time"),
    OFFSETDATETIME(OffsetDateTime.class, "string", "date-time"),
    LOCALDATE(LocalDate.class, "string", "date"),
    LOCALTIME(LocalTime.class, "string", "time"),
    OFFSETTIME(OffsetTime.class, "string", "time"),
    DATE(Date.class, "string", "date-time"),
    INSTANT(Instant.class, "string", "date-time");

    private static final Class[] COLLECTION_CLASSES = new Class[]{Collection.class, Iterable.class};

    private static final Map<Class<?>, String> TYPE_MAPPINGS;
    private static final Map<Class<?>, String> FORMAT_MAPPINGS;

    static {
        // Class objects are all singletons, so we can use that
        TYPE_MAPPINGS = new IdentityHashMap<Class<?>, String>();
        FORMAT_MAPPINGS = new IdentityHashMap<Class<?>, String>();

        for (final SimpleTypeMappings mapping : values()) {
            TYPE_MAPPINGS.put(mapping.c, mapping.schemaType);
            if (!mapping.formatHint.isEmpty()) {
                FORMAT_MAPPINGS.put(mapping.c, mapping.formatHint);
            }
        }
    }

    private final Class<?> c;
    private final String schemaType;
    private final String formatHint;

    SimpleTypeMappings(final Class<?> c, final String schemaType, final String formatHint) {
        this.c = c;
        this.schemaType = schemaType;
        this.formatHint = formatHint;
    }

    static Optional<String> forClass(final Type type) {
        if (!(type instanceof Class)) {
            TypeToken token = TypeToken.of(type);
            Class<?> clazz = token.getRawType();
            if (Optional.class.isAssignableFrom(clazz)) {
                checkState(clazz.getTypeParameters().length > 0, "No type arguments in return type found!");
                Type itemType = token.resolveType(clazz.getTypeParameters()[0]).getType();

                return forClass(itemType);
            } else {
                return Optional.empty();
            }
        } else if (TYPE_MAPPINGS.containsKey(type)) {
            return Optional.of(TYPE_MAPPINGS.get(type));
        } else {
            return Optional.empty();
        }
    }

    static Optional<String> formatHint(final Type type) {
        if (!(type instanceof Class)) {
            return Optional.empty();
        } else if (FORMAT_MAPPINGS.containsKey(type)) {
            return Optional.of(FORMAT_MAPPINGS.get(type));
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
