import jdk.incubator.json.Json;
import jdk.incubator.json.JsonObject;
import jdk.incubator.json.JsonString;
import jdk.incubator.json.JsonValue;

void main() {
    JsonValue json = Json.parse("""
            {"name": "Dan", "language": "Java", "version": 28}
            """);

    String name = json.get("name").asString();
    int version = json.get("version").asInt();
    IO.println(name + " is using Java " + version);

    JsonObject greeting = JsonObject.of(Map.of("message", JsonString.of("Hello, " + name + "!")));
    IO.println(greeting);
}
