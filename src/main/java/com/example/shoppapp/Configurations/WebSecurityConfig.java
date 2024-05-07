package com.example.shoppapp.Configurations;

import com.example.shoppapp.Models.Role;
import com.example.shoppapp.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
//@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> {
                    request.
                            requestMatchers(
                                    "/users/register",
                                    "/users/login"

                            ).permitAll()
                            .requestMatchers(GET, "/roles/**").permitAll()

                            .requestMatchers(GET, "/orders/**").permitAll()
                            .requestMatchers(POST, "/orders/**").hasRole(Role.USER)
                            .requestMatchers(PUT, "/orders/**").hasRole(Role.ADMIN)
                            .requestMatchers(DELETE, "/orders/**").hasRole(Role.ADMIN)

                            .requestMatchers(GET, "/category**").permitAll()
                            .requestMatchers(POST, "/category/**").hasRole(Role.ADMIN)
                            .requestMatchers(PUT, "/category/**").hasRole(Role.ADMIN)
                            .requestMatchers(DELETE, "/category/**").hasRole(Role.ADMIN)

                            .requestMatchers(GET, "/products/**").permitAll()
                            .requestMatchers(POST, "/products/**").hasRole(Role.ADMIN)
                            .requestMatchers(PUT, "/products/**").hasRole(Role.ADMIN)
                            .requestMatchers(GET, "/products/images/**").permitAll()

                            .requestMatchers(GET, "/order_detail/**").hasAnyRole(Role.ADMIN,Role.USER)
                            .requestMatchers(POST, "/order_detail/**").hasRole(Role.USER)
                            .requestMatchers(PUT, "/order_detail/**").hasRole(Role.ADMIN)
                            .requestMatchers(DELETE, "/order_detail/**").hasRole(Role.ADMIN)
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable);

        httpSecurity.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);}
            });
        return httpSecurity.build();
    }

}
