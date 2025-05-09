package kr.sonss.lab.backend.common.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (API 서버용)
                .cors(Customizer.withDefaults()) // CORS 설정 적용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll() // API 경로는 인증 없이 허용 (.requestMatchers("/api/**").permitAll())
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login.disable()) // 기본 로그인 폼 제거
                .httpBasic(Customizer.withDefaults()); // 필요시 기본 인증 방식 허용

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

