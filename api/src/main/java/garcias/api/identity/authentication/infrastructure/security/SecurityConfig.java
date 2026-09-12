package garcias.api.identity.authentication.infrastructure.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import garcias.api.identity.authentication.infrastructure.security.jwt.JwtAuthenticationFilter;
import garcias.api.shared.ratelimit.RateLimitFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final RateLimitFilter rateLimitFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Value("${app.cors.allowed-origins:http://localhost:4200,https://eloa-mercados-e-panificacoes.vercel.app}")
    private String allowedOriginsConfig;

    public SecurityConfig(
            @Autowired(required = false) RateLimitFilter rateLimitFilter,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            CustomAccessDeniedHandler accessDeniedHandler
    ) {
        this.rateLimitFilter = rateLimitFilter;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> origins = Arrays.stream(allowedOriginsConfig.split(","))
                .map(String::trim)
                .map(origin -> origin.replaceAll("/+$", ""))
                .filter(origin -> !origin.isEmpty())
                .toList();

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/bootstrap",
                                "/api/auth/refresh",
                                "/api/auth/logout",
                                "/api/public/**",
                                "/api/storage/images/**"
                        ).permitAll()

                        .requestMatchers(
                                "/api/admin/products",
                                "/api/admin/products/**",
                                "/api/admin/categories",
                                "/api/admin/categories/**"
                        ).hasAnyRole("SUPER_ADMIN", "ADMIN", "EDITOR")

                        .requestMatchers(
                                "/api/admin/content",
                                "/api/admin/content/**"
                        )
                        .hasAnyRole("SUPER_ADMIN", "ADMIN")

                        .requestMatchers("/api/admin/**")
                        .hasRole("SUPER_ADMIN")

                        .anyRequest()
                        .authenticated()
                );

        if (rateLimitFilter != null) {
            http.addFilterAfter(
                    rateLimitFilter,
                    CorsFilter.class
            );
        }

        return http
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }
}

