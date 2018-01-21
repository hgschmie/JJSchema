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

package de.softwareforge.jsonschema.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Augments fields or properties with Json Format attributes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface JsonSchema {

    String $ref() default "";

    String id() default "";

    String title() default "";

    String description() default "";

    int maximum() default -1;

    boolean exclusiveMaximum() default false;

    int minimum() default -1;

    boolean exclusiveMinimum() default false;

    String pattern() default "";

    String format() default "";

    boolean required() default false;

    String[] enums() default {};

    int minItems() default 0;

    int maxItems() default -1;

    boolean uniqueItems() default false;

    int multipleOf() default 0;

    int minLength() default 0;

    int maxLength() default -1;

    boolean readonly() default false;

    boolean additionalProperties() default true;

    String type() default "";
}
