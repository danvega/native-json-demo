package features;

import jdk.incubator.json.Json;
import jdk.incubator.json.JsonParseException;
import jdk.incubator.json.JsonValue;
import jdk.incubator.json.JsonValueException;

public class ErrorMessages {

    void main() {
        JsonValue todo = Json.parse("""
                {"id": 1, "title": "Record the video", "tags": ["youtube"]}
                """);

        show("Missing member", () -> todo.get("due"));
        show("Wrong type", () -> todo.get("title").asInt());
        show("Bad index", () -> todo.get("tags").get(5));

        show("Comment", () -> Json.parse("{\"id\": 1 // the id\n}"));
        show("Trailing comma", () -> Json.parse("[1, 2, 3,]"));
        show("Single quotes", () -> Json.parse("{'id': 1}"));
        show("Duplicate key", () -> Json.parse("{\"id\": 1, \"id\": 2}"));

        try {
            Json.parse("""
                    {
                      "id": 1,
                      "title": "Record the video"
                      "done": false
                    }
                    """);
        } catch (JsonParseException e) {
            IO.println("Missing comma is on line " + e.getErrorLine() + ", position " + e.getErrorPosition());
        }
    }

    void show(String label, Runnable code) {
        try {
            code.run();
        } catch (JsonValueException | JsonParseException e) {
            IO.println(label + ": " + e.getMessage());
        }
    }
}
