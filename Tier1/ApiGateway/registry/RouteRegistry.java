package Tier1.ApiGateway.registry;

import Tier1.ApiGateway.model.RouteConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RouteRegistry {
    private final Map<String, RouteConfig> routes = new HashMap<>();

    public void addRoute(String path, RouteConfig config) {
        routes.put(path, config);
    }

    public Optional<RouteConfig> findRoute(String path) {
        // Longest Prefix Match Strategy
        var sortedPrefixes = new ArrayList<>(routes.keySet());
        sortedPrefixes.sort((a, b) -> b.length() - a.length());

        for (String prefix : sortedPrefixes) {
            if (path.startsWith(prefix)) {
                return Optional.of(routes.get(prefix));
            }
        }
        return Optional.empty();
    }
}
