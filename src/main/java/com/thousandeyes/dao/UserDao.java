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

    /**
     * to get userId given a username
     * @param username username of a user
     * @return optional user object
     */
    public Optional<User> getUserId(String username) {
        String userIdQuery = "select * from user where username=?";
        List<User> users = jdbcTemplateObject.query(userIdQuery, new Object [] {username} ,new BeanPropertyRowMapper(User.class));
        return users.stream().findFirst();
    }

    /**
     * to get the user ids followed by a user id
     * @param userId user id
     * @return list of users followed by the input user id
     */
    public List<UserRelation> getFollowedUsers(int userId) {
        String followedQuery = "select * from user_relations where follower=?";
        List<UserRelation> followedUsers = jdbcTemplateObject.query(followedQuery, new Object[] {userId} ,new BeanPropertyRowMapper(UserRelation.class));
        return followedUsers;
    }

    /**
     * to get user objects from a list of user ids
     * @param userIds a list of user ids
     * @return list of users corresponding to the list of user ids
     */
    public List<User> getUsersFromIds(List<Integer> userIds) {
        MapSqlParameterSource paramaters = new MapSqlParameterSource();
        paramaters.addValue("userIds", userIds);

        String query = "select * from user where id in (:userIds)";
        List<User> users = new NamedParameterJdbcTemplate(jdbcTemplateObject).query(query, paramaters, new BeanPropertyRowMapper(User.class));
        return users;
    }

    /**
     * returns a boolean indicating if the follow user operation was successful, depending on
     * the number of rows inserted
     * @param followerId id of the user who wants to follow
     * @param toBeFollowedId the id of the user who has to be followed
     * @return boolean, indicating if the follow operation was success or not
     */
    public boolean followUser(int followerId, int toBeFollowedId) {
        String query = "insert into user_relations (follower, followed) values (?,?)";
        int rowsInserted = jdbcTemplateObject.update(query, new Object[] {followerId, toBeFollowedId});
        return rowsInserted == 1;
    }


    /**
     * returns a boolean indicating if the unfollow user operation was successful, depending on
     * the number of rows deleted
     * @param followerId id of the user who wants to unfollow
     * @param toBeUnfollowedId the id of the user who has to be unfollowed
     * @return boolean, indicating if the unfollow operation was success or not
     */
    public boolean unfollowUser(int followerId, int toBeUnfollowedId) {
        String query = "delete from user_relations where follower=? and followed=?";
        int rowsDeleted = jdbcTemplateObject.update(query, new Object[] {followerId, toBeUnfollowedId});
        return rowsDeleted == 1;
    }


}
