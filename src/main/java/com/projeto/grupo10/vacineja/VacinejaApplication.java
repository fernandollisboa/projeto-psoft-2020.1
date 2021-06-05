package com.projeto.grupo10.vacineja;

import com.projeto.grupo10.vacineja.filtros.TokenFilter;
import com.projeto.grupo10.vacineja.job.Verificador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaRepositories("com.projeto.grupo10.vacineja.repository")
@EntityScan(basePackages = {"com.projeto.grupo10.vacineja.model"})
public class VacinejaApplication {

	@Bean
	public FilterRegistrationBean<TokenFilter> filterJwt() {
		FilterRegistrationBean<TokenFilter> filterRB = new FilterRegistrationBean<TokenFilter>();
		filterRB.setFilter(new TokenFilter());
		filterRB.addUrlPatterns("/api/teste");
		return filterRB;
	}

	public static void main(String[] args) {
		SpringApplication.run(VacinejaApplication.class, args);
	}

}
