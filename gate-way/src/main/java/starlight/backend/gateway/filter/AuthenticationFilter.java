package starlight.backend.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import starlight.backend.gateway.enums.Role;
import starlight.backend.gateway.jwt.JwtUtil;
import starlight.backend.gateway.route.RouteValidator;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    @Autowired
    private RouteValidator validator;
    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            log.info("rr====>{}", exchange.getRequest().getPath());
            if (!validator.isSecured(exchange.getRequest().getPath().toString())) {

                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                Jws<Claims> claimsJws = jwtUtil.validateToken(authHeader);
                Claims claims = claimsJws.getBody();

                String id = claims.get("sub", String.class);

                String role = jwtUtil.getRole(claims);

                String status = jwtUtil.getStatus(claims);

                jwtUtil.checkIdWithRole(Long.parseLong(id), role);

                if (jwtUtil.checkTimeToken(claims)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired");
                }

                if (!jwtUtil.checkStatus(status)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bed status");
                }

                if (((!exchange.getRequest().getPath().toString().equals("/api/v1/talents/" + id))
                        || !role.equals(Role.TALENT.getAuthority()))
                        && (exchange.getRequest().getMethod() == HttpMethod.PATCH
                        || exchange.getRequest().getMethod() == HttpMethod.DELETE)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you cannot change profile another talent!!");
                }
                if (((!exchange.getRequest().getPath().toString().matches("/api/v1/proofs/[1-9][0-9]*/kudos"))
                        || !role.equals(Role.SPONSOR.getAuthority()))
                        && (exchange.getRequest().getMethod() == HttpMethod.POST)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you cannot do kodos if you are not a sponsor!!");
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}