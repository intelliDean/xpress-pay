package com.api.xpress;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


@Slf4j
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class XpressPay {

    public static void main(String[] args) {
        Dotenv.configure()
                .ignoreIfMissing()
                .systemProperties()
                .load();

        SpringApplication application = new SpringApplication(XpressPay.class);
        application.setBannerMode(Banner.Mode.LOG);

        ConfigurableApplicationContext context = application.run(args);
        Environment environment = context.getEnvironment();

        String port = environment.getProperty("local.server.port");
        String appName = environment.getProperty("spring.application.name", "Xpress Pay");
        String profile = String.join(", ", environment.getActiveProfiles());

        log.info("""
                        
                        -----------------------------------------------
                        🚀 {} is Running!
                        🌐 URL:     http://localhost:{}
                        📄 Swagger: http://localhost:{}/swagger-ui/index.html
                        🔧 Profile: {}
                        -----------------------------------------------
                        """,
                appName, port, port, profile.isEmpty() ? "default" : profile
        );
    }
}