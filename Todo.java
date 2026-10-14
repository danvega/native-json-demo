import jdk.incubator.json.JsonArray;
import jdk.incubator.json.JsonBoolean;
import jdk.incubator.json.JsonNull;
import jdk.incubator.json.JsonNumber;
import jdk.incubator.json.JsonObject;
import jdk.incubator.json.JsonString;
import jdk.incubator.json.JsonValue;

import java.util.LinkedHashMap;
import java.util.List;

record Todo(int id, String title, boolean done, int priority, List<String> tags, String due) {

    static Todo from(JsonValue json) {
        return new Todo(
                json.get("id").asInt(),
                json.get("title").asString(),
                json.get("done").asBoolean(),
                json.get("priority").asInt(),
                json.get("tags").asList().stream().map(JsonValue::asString).toList(),
                json.tryGet("due").flatMap(JsonValue::tryValue).map(JsonValue::asString).orElse(null));
    }

    JsonObject toJson() {
        var json = new LinkedHashMap<String, JsonValue>();
        json.put("id", JsonNumber.of(id));
        json.put("title", JsonString.of(title));
        json.put("done", JsonBoolean.of(done));
        json.put("priority", JsonNumber.of(priority));
        json.put("tags", JsonArray.of(tags.stream().map(JsonString::of).toList()));
        json.put("due", due == null ? JsonNull.of() : JsonString.of(due));
        return JsonObject.of(json);
    }
}
