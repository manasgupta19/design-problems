package Tier0.JSONParser;

import Tier0.JSONParser.service.SimpleJsonParser;

public class Driver {
    // Driver
    public static void main(String[] args) {
        String json = "{\"name\": \"Manas\", \"scores\": [10, 20.5], \"active\": true}";
        SimpleJsonParser parser = new SimpleJsonParser(json);
        Object result = parser.parse();
        System.out.println(result);
        // Output: {scores=[10.0, 20.5], name=Manas, active=true}
    }
}


