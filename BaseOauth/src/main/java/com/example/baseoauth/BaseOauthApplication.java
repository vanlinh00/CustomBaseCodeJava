package com.example.baseoauth;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {
		"com.example.basecommon",
		"com.example.baseoauth"
})
@EnableJpaRepositories(basePackages = {
		"com.example.basecommon.repository"
})
@EntityScan(basePackages = {
		"com.example.basecommon.entity"
})

public class BaseOauthApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaseOauthApplication.class, args);
	}

}
