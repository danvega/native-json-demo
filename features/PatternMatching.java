package features;

import jdk.incubator.json.Json;
import jdk.incubator.json.JsonArray;
import jdk.incubator.json.JsonBoolean;
import jdk.incubator.json.JsonNull;
import jdk.incubator.json.JsonNumber;
import jdk.incubator.json.JsonObject;
import jdk.incubator.json.JsonString;
import jdk.incubator.json.JsonValue;

import java.util.List;

public class PatternMatching {

    void main() {
        JsonValue values = Json.parse("""
                ["Java", 28, true, null, ["a", "b"], {"name": "Dan"}]
                """);

        for (JsonValue value : values.asList()) {
            IO.println(describe(value));
        }

        JsonValue posts = Json.parse("""
                [
                  {"title": "One tag", "tags": "java"},
                  {"title": "Many tags", "tags": ["java", "jdk28"]}
                ]
                """);

        for (JsonValue post : posts.asList()) {
            IO.println(post.get("title").asString() + ": " + tags(post.get("tags")));
        }
    }

    String describe(JsonValue value) {
        return switch (value) {
            case JsonString s -> "string: " + s.asString();
            case JsonNumber n -> "number: " + n.asInt();
            case JsonBoolean b -> "boolean: " + b.asBoolean();
            case JsonNull _ -> "null";
            case JsonArray a -> "array with " + a.asList().size() + " items";
            case JsonObject o -> "object with keys " + o.asMap().keySet();
        };
    }

    List<String> tags(JsonValue tags) {
        return switch (tags) {
            case JsonString s -> List.of(s.asString());
            case JsonArray a -> a.asList().stream().map(JsonValue::asString).toList();
            default -> throw new IllegalArgumentException("tags must be a string or an array");
        };
    }
}
