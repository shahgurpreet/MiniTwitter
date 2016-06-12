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
import java.util.Optional;

public class UserDao {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    public UserDao(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = jdbcTemplate;
    }

    public Optional<User> getUserId(String username) {
        String userIdQuery = "select * from user where username=?";
        List<User> users = jdbcTemplateObject.query(userIdQuery, new Object [] {username} ,new BeanPropertyRowMapper(User.class));
        return users.stream().findFirst();
    }

    public List<UserRelation> getFollowedUsers(int userId) {
        String followedQuery = "select * from user_relations where follower=?";
        List<UserRelation> followedUsers = jdbcTemplateObject.query(followedQuery, new Object[] {userId} ,new BeanPropertyRowMapper(UserRelation.class));
        return followedUsers;
    }

    public List<User> getUsersFromIds(List<Integer> userIds) {
        MapSqlParameterSource paramaters = new MapSqlParameterSource();
        paramaters.addValue("userIds", userIds);

        String query = "select * from user where id in (:userIds)";
        List<User> users = new NamedParameterJdbcTemplate(jdbcTemplateObject).query(query, paramaters, new BeanPropertyRowMapper(User.class));
        return users;
    }

    public boolean followUser(int followerId, int toBeFollowedId) {
        String query = "insert into user_relations (follower, followed) values (?,?)";
        int rowsInserted = jdbcTemplateObject.update(query, new Object[] {followerId, toBeFollowedId});
        return rowsInserted == 1;
    }

    public boolean unfollowUser(int followerId, int toBeFollowedId) {
        String query = "delete from user_relations where follower=? and followed=?";
        int rowsDeleted = jdbcTemplateObject.update(query, new Object[] {followerId, toBeFollowedId});
        return rowsDeleted == 1;
    }


}
