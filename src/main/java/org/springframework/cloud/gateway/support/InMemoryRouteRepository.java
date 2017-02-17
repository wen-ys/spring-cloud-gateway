package org.springframework.cloud.gateway.support;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.cloud.gateway.api.Route;
import org.springframework.cloud.gateway.api.RouteLocator;
import org.springframework.cloud.gateway.api.RouteWriter;

import static java.util.Collections.synchronizedMap;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Spencer Gibb
 */
public class InMemoryRouteRepository implements RouteLocator, RouteWriter {

	private final Map<String, Route> routes = synchronizedMap(new LinkedHashMap<String, Route>());

	@Override
	public Mono<Void> save(Mono<Route> route) {
		return route.then( r -> {
			routes.put(r.getId(), r);
			return Mono.empty();
		});
	}

	@Override
	public Mono<Void> delete(Mono<String> routeId) {
		return routeId.then(id -> {
			if (routes.containsKey(id)) {
				routes.remove(id);
				return Mono.empty();
			}
			return Mono.error(new NotFoundException("Route not found: "+routeId));
		});
	}

	@Override
	public Flux<Route> getRoutes() {
		return Flux.fromIterable(routes.values());
	}
}