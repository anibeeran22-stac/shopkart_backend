package com.shopkart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
	
	@Bean
	WebMvcConfigurer corsConfigurer() {
		
		return new WebMvcConfigurer() {
			
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				
				registry.addMapping("/**")
				.allowedOrigins("https://shopkart-frontend-yd6e-git-main-anibeeran-s-projects.vercel.app/")
				.allowedMethods("*")
				.allowedHeaders("*");
			}
		};
	}
	

}
