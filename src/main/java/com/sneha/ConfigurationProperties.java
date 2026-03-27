package com.sneha;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationProperties {


    @Bean
    @org.springframework.boot.context.properties.ConfigurationProperties(prefix = "userservice")
    public  UserServiceConfig getUserServiceConfig(){
        return new UserServiceConfig();
    }

    @Bean
    @org.springframework.boot.context.properties.ConfigurationProperties(prefix = "pointservice")
    public  PointServiceConfig getPointServiceConfig(){
        return new PointServiceConfig();
    }

    @Getter
    @Setter
    public class UserServiceConfig{
        private String host;
        private String path;


    }


    @Setter
    @Getter
    public class PointServiceConfig {
        private String host;
        private String path;


    }

}
