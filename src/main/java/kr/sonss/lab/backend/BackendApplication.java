package kr.sonss.lab.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@MapperScan("kr.sonss.lab.backend.*.mapper")
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    /*
    Vue Front-End 와 CORS 설정 (세션 공유 허용)
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")              //addMapping("/api/**") API 경로는 인증 없이 허용
                        .allowedOrigins("http://localhost:3000") // Vue 개발 서버 주소
                        .allowedMethods("*")     // allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true); // 인증정보 포함 허용 (JSESSIONID, 쿠키 등 허용)
            }
        };
    }
}


