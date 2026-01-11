package com.example.moro.global.config;

import com.example.moro.app.auth.handler.OAuth2FailureHandler;
import com.example.moro.app.auth.handler.OAuth2SuccessHandler;
import com.example.moro.app.member.repository.MemberRepository;
import com.example.moro.global.security.jwt.JwtAuthenticationFilter;
import com.example.moro.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // JWT 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, memberRepository),
                               org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)

                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )

                .authorizeHttpRequests(auth -> auth
                        // 인증 불필요 경로
                        .requestMatchers(
                                "/api/auth/**",          // 로그인 API
                                "/login/oauth2/**",      // OAuth2 로그인 엔드포인트
                                "/oauth2/**",            // OAuth2 관련 경로
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/sample/**",       // 샘플 API (테스트용)
                                "/api/s3/**"
                        ).permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","PATCH","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
