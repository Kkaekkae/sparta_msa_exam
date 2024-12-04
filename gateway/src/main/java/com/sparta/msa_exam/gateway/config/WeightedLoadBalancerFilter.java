package com.sparta.msa_exam.gateway.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class WeightedLoadBalancerFilter extends AbstractGatewayFilterFactory<Object> {

    private final ReactiveDiscoveryClient discoveryClient;

    private final List<Integer> weights = List.of(70, 30); // 각 인스턴스의 가중치

    public WeightedLoadBalancerFilter(ReactiveDiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) ->
                // 서비스 디스커버리의 인스턴스 정보를 가져옴
                discoveryClient.getInstances("product-service")
                .collectList()
                .flatMap(instances -> {
                    // 현재 실행중인 인스턴스의 URI 목록을 추출함
                    List<URI> uris = instances.stream()
                            .map(ServiceInstance::getUri)
                            .toList();

                    // 7:3 비율로 로드밸런싱 하여 선택한 인스턴스의 주소를 가져옴
                    URI selectedInstance = selectInstance(uris);

                    if (selectedInstance == null) {
                        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        return exchange.getResponse().setComplete();
                    }

                    // URI 재설정
                    ServerWebExchangeUtils.addOriginalRequestUrl(exchange, exchange.getRequest().getURI());

                    // 원래 호출되어야 하는 서비스의 요청 정보 재활용
                    Route originalRoute = (Route) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

                    Route route = Route.async() // Predicate 는 Async 타입의 Router 에서만 설정이 가능함.
                            .id(originalRoute.getId())
                            .uri(selectedInstance) // 7:3 으로 설정한 인스턴스 주소로 변경
                            .order(originalRoute.getOrder())
                            .asyncPredicate(originalRoute.getPredicate())
                            .filters(originalRoute.getFilters()).build(); // Gateway 요청객체를 하나 생성함.

                    // 라우팅 정보 수정
                    exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR, route);

                    return chain.filter(exchange);
                });
    }

    private URI selectInstance(List<URI> uris) {
        Random random = new Random();
        int totalWeight = weights.stream().mapToInt(Integer::intValue).sum();
        int randomWeight = random.nextInt(totalWeight);

        int currentWeight = 0;
        for (int i = 0; i < uris.size(); i++) {
            currentWeight += weights.get(i);
            if (randomWeight < currentWeight) {
                return uris.get(i);
            }
        }
        return null;
    }
}
