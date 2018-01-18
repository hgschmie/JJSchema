package com.github.reinert.jjschema;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class JJSchemaUtil {

    private JJSchemaUtil() {
    }

    public static void processCommonAttributes(ObjectNode node, AttributeHolder attributeHolder) {
        attributeHolder.id().ifPresent(id -> node.put("id", id));
        attributeHolder.description().ifPresent(description -> node.put("description", description));
        attributeHolder.pattern().ifPresent(pattern -> node.put("pattern", pattern));
        attributeHolder.format().ifPresent(format -> node.put("format", format));
        attributeHolder.title().ifPresent(title -> node.put("title", title));
        attributeHolder.maximum().ifPresent(maximum -> node.put("maximum", maximum));

        if (attributeHolder.exclusiveMaximum()) {
            node.put("exclusiveMaximum", true);
        }

        attributeHolder.minimum().ifPresent(minimum -> node.put("minimum", minimum));

        if (attributeHolder.exclusiveMinimum()) {
            node.put("exclusiveMinimum", true);
        }

        if (!attributeHolder.enums().isEmpty()) {
            ArrayNode enumArray = node.putArray("enum");
            for (String v : attributeHolder.enums()) {
                enumArray.add(v);
            }
        }

        if (attributeHolder.uniqueItems()) {
            node.put("uniqueItems", true);
        }

        attributeHolder.minItems().ifPresent(minItems -> node.put("minItems", minItems));
        attributeHolder.maxItems().ifPresent(maxItems -> node.put("maxItems", maxItems));
        attributeHolder.multipleOf().ifPresent(multipleOf -> node.put("multipleOf", multipleOf));
        attributeHolder.minLength().ifPresent(minLength -> node.put("minLength", minLength));
        attributeHolder.maxLength().ifPresent(maxLength -> node.put("maxLength", maxLength));

        if (attributeHolder.readonly()) {
            node.put("readonly", true);
        }
    }
}
