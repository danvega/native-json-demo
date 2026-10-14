package todo;

import jdk.incubator.json.Json;
import jdk.incubator.json.JsonArray;

import java.util.List;

public class SerializeTodos {

    void main() {
        List<Todo> todos = List.of(
                new Todo(1, "Try the new JSON API", true, 2, List.of("java", "jdk28"), null),
                new Todo(2, "Record the video", false, 1, List.of("youtube"), "2026-10-20"),
                new Todo(3, "Write the \"JSON in Java\" post", false, 3, List.of(), null));

        JsonArray json = JsonArray.of(todos.stream().map(Todo::toJson).toList());

        IO.println(json);
        IO.println(Json.toDisplayString(json, "  "));
    }
}
