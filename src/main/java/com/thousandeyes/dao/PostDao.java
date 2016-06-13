package com.thousandeyes.dao;


import com.thousandeyes.pojo.Post;
import com.thousandeyes.pojo.User;
import com.thousandeyes.pojo.UserRelation;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostDao {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    public PostDao(DataSource dataSource, JdbcTemplate jdbcTemplateObject) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = jdbcTemplateObject;
    }

    /**
     * to get all the posts from the post table for a given list of user ids
     * @param userIds a list of user ids
     * @return list of posts for the input user ids
     */
    public List<Post> getAll(List<Integer> userIds) {

        MapSqlParameterSource paramaters = new MapSqlParameterSource();
        paramaters.addValue("userIds", userIds);

        String query = "select * from post where user_id in (:userIds)";
        List<Post> posts = new NamedParameterJdbcTemplate(jdbcTemplateObject).query(query, paramaters, new BeanPropertyRowMapper(Post.class));
        return posts;
    }


}
