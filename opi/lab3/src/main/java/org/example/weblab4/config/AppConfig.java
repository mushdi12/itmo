package org.example.weblab4.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.example.weblab4")
@Import({SecurityConfig.class, DatabaseConfig.class, WebConfig.class})
public class AppConfig {
} 