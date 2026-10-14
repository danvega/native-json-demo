package features;

import jdk.incubator.json.Json;
import jdk.incubator.json.JsonNumber;
import jdk.incubator.json.JsonObject;
import jdk.incubator.json.JsonValue;
import jdk.incubator.json.JsonValueException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class Numbers {

    void main() {
        JsonValue order = Json.parse("""
                {"orderId": 12345678901234567890123, "unitsSold": 3000000000, "prices": [0.10, 0.20]}
                """);

        IO.println("unitsSold as long: " + order.get("unitsSold").asLong());
        try {
            order.get("unitsSold").asInt();
        } catch (JsonValueException e) {
            IO.println("unitsSold as int: " + e.getMessage());
        }

        BigInteger orderId = new BigInteger(order.get("orderId").toString());
        IO.println("orderId as BigInteger: " + orderId);

        double doubleTotal = 0;
        BigDecimal exactTotal = BigDecimal.ZERO;
        for (JsonValue price : order.get("prices").asList()) {
            doubleTotal += price.asDouble();
            exactTotal = exactTotal.add(new BigDecimal(price.toString()));
        }
        IO.println("total with double: " + doubleTotal);
        IO.println("total with BigDecimal: " + exactTotal);

        JsonObject receipt = JsonObject.of(Map.of("total", JsonNumber.of(exactTotal.toString())));
        IO.println(receipt);
    }
}
