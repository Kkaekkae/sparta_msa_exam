package com.sparta.msa_exam.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class WeightedLoadBalancerFilter extends AbstractGatewayFilterFactory<Object> {

    private final List<String> productServiceInstances = List.of(
            "http://localhost:19093", // 인스턴스 1
            "http://localhost:19094"  // 인스턴스 2
    );

    private final List<Integer> weights = List.of(70, 30); // 각 인스턴스의 가중치

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            // 가중치에 따라 인스턴스 선택
            String selectedInstance = selectInstance();

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
        };
    }

    private String selectInstance() {
        Random random = new Random();
        int totalWeight = weights.stream().mapToInt(Integer::intValue).sum();
        int randomWeight = random.nextInt(totalWeight);

        int currentWeight = 0;
        for (int i = 0; i < productServiceInstances.size(); i++) {
            currentWeight += weights.get(i);
            if (randomWeight < currentWeight) {
                return productServiceInstances.get(i);
            }
        }
        return null;
    }
}
