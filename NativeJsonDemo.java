import jdk.incubator.json.Json;
import jdk.incubator.json.JsonNumber;
import jdk.incubator.json.JsonObject;
import jdk.incubator.json.JsonString;
import jdk.incubator.json.JsonValue;

void main() {
    JsonValue list = Json.parse("""
            {
              "owner": "Dan",
              "todos": [
                {"title": "Try the new JSON API", "done": true},
                {"title": "Record the video", "done": false}
              ]
            }
            """);

    IO.println(list.get("owner").asString() + "'s todos:");
    List<JsonValue> todos = list.get("todos").asList();
    int doneCount = 0;
    for (JsonValue todo : todos) {
        boolean done = todo.get("done").asBoolean();
        if (done) doneCount++;
        IO.println((done ? "[x] " : "[ ] ") + todo.get("title").asString());
    }

    var summary = new LinkedHashMap<String, JsonValue>();
    summary.put("owner", list.get("owner"));
    summary.put("total", JsonNumber.of(todos.size()));
    summary.put("done", JsonNumber.of(doneCount));
    summary.put("status", JsonString.of(doneCount == todos.size() ? "all done" : "keep going"));

    IO.println(Json.toDisplayString(JsonObject.of(summary), "  "));
}
