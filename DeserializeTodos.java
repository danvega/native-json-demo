import jdk.incubator.json.Json;
import jdk.incubator.json.JsonValue;

void main() {
    JsonValue root = Json.parse("""
            {
              "owner": "Dan",
              "updated": "2026-10-14",
              "todos": [
                {
                  "id": 1,
                  "title": "Try the new JSON API",
                  "done": true,
                  "priority": 2,
                  "tags": ["java", "jdk28"],
                  "due": null
                },
                {
                  "id": 2,
                  "title": "Record the video",
                  "done": false,
                  "priority": 1,
                  "tags": ["youtube"],
                  "due": "2026-10-20"
                },
                {
                  "id": 3,
                  "title": "Write the blog post",
                  "done": false,
                  "priority": 3,
                  "tags": []
                }
              ]
            }
            """);

    String owner = root.get("owner").asString();
    IO.println(owner);

    List<Todo> todos = root.get("todos").asList().stream()
            .map(Todo::from)
            .toList();

    todos.forEach(IO::println);

}