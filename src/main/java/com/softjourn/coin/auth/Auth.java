package com.softjourn.coin.auth;


import com.softjourn.coin.auth.config.LdapConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.softjourn.coin.auth")
@EnableJpaRepositories("com.softjourn.coin.auth.repository")
@EntityScan(basePackages = "com.softjourn.coin.auth.entity")
@Import({LdapConfiguration.class})
@SpringBootApplication
@PropertySources({
        @PropertySource("classpath:security.properties")
})

public class Auth  {

    public static void main(String[] args) {
        SpringApplication.run(Auth.class, args);
    }


}
