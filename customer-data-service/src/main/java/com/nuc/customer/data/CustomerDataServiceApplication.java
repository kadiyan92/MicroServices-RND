package com.nuc.customer.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class CustomerDataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerDataServiceApplication.class, args);
	}
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2).
				select()
				.apis(RequestHandlerSelectors.basePackage("com.nuc.customer.data"))
				.paths(PathSelectors.any()).//URL for controller need to change
				build();
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
