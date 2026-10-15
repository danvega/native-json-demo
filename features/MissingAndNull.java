package features;

import jdk.incubator.json.Json;
import jdk.incubator.json.JsonValue;
import jdk.incubator.json.JsonValueException;

public class MissingAndNull {

    void main() {
        JsonValue users = Json.parse("""
                [
                  {"name": "Dan", "email": "dan@example.com"},
                  {"name": "Ada", "email": null},
                  {"name": "Linus"}
                ]
                """);

        JsonValue ada = users.get(1);
        JsonValue linus = users.get(2);

        try {
            linus.get("email");
        } catch (JsonValueException e) {
            IO.println("get() on a missing member: " + e.getMessage());
        }

        try {
            ada.get("email").asString();
        } catch (JsonValueException e) {
            IO.println("asString() on null: " + e.getMessage());
        }

        IO.println("tryGet on a missing member: " + linus.tryGet("email"));
        IO.println("tryGet on a null member: " + ada.tryGet("email"));
        IO.println("tryValue on null: " + ada.get("email").tryValue());

        for (JsonValue user : users.asList()) {
            String email = user.tryGet("email")
                    .flatMap(JsonValue::tryValue)
                    .map(JsonValue::asString)
                    .orElse("no email");
            IO.println(user.get("name").asString() + ": " + email);
        }
    }
}
