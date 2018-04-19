package com.example.demo;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        builder = builder.driverClassName("org.postgresql.Driver");
        builder = builder.url("jdbc:postgresql://172.28.128.3:5432/db1");
        builder = builder.username("user1");
        builder = builder.password("user1");
        return builder.build();
    }

    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }
}
