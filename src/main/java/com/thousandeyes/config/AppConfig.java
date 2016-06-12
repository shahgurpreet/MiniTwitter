package com.thousandeyes.config;

import com.thousandeyes.dao.PostDao;
import com.thousandeyes.dao.UserDao;
import com.thousandeyes.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Autowired
    DataSource dataSource;

    @Bean
    public PostDao postDao() {
       return new PostDao(dataSource, jdbcTemplate());
    }

    @Bean
    public UserDao userDao() {
        return new UserDao(dataSource, jdbcTemplate());
    }

    @Bean
    public AppService appService() {
        return new AppService(postDao(),userDao());
    }

    @Bean
    public JdbcTemplate jdbcTemplate() { return new JdbcTemplate(dataSource); }
}

