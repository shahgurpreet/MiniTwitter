package com.thousandeyes.service;

import com.thousandeyes.dao.PostDao;
import com.thousandeyes.dao.UserDao;
import com.thousandeyes.pojo.Post;
import com.thousandeyes.pojo.PostResult;
import com.thousandeyes.pojo.User;
import com.thousandeyes.pojo.UserRelation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AppService {

    public static final String SUCCESS_MESSAGE = "Success";

    private PostDao postDao;
    private UserDao userDao;

    public AppService(PostDao postDao, UserDao userDao) {

        this.postDao = postDao;
        this.userDao = userDao;
    }

    public List<PostResult> getAllPosts(String name, String search) {
        Optional<User> user = userDao.getUserId(name);
        int userId = user.map(userObject -> userObject.getId())
                .orElseThrow(IllegalStateException::new);
        List<UserRelation> followedUsers = userDao.getFollowedUsers(userId);
        List<Integer> followedUserIds = followedUsers.stream().map(UserRelation::getFollowed).collect(Collectors.toList());
        followedUserIds.add(userId);

        List<User> users = userDao.getUsersFromIds(followedUserIds);
        List<Post> posts = postDao.getAll(followedUserIds);
        return getPostResults(users, posts, search);
    }

    private List<PostResult> getPostResults(List<User> users, List<Post> posts, String search) {
        Map<Integer, String> userMap = users
                .stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        List<PostResult> postResults = posts.stream()
                .filter(post -> search == null || post.getTweet().toLowerCase().contains(search.toLowerCase()))
                .map(post -> {
                    return new PostResult()
                   .setTweet(post.getTweet())
                   .setUsername(userMap.get(post.getUserId()));
        }).collect(Collectors.toList());

        return postResults;
    }

    public String followUser(String following, String toBeFollowed) {

        Optional<User> followingUser = userDao.getUserId(following);
        int followingId = followingUser.map(userObject -> userObject.getId())
                .orElseThrow(IllegalStateException::new);

        Optional<User> toBeFollowedUser = userDao.getUserId(toBeFollowed);

        if (!toBeFollowedUser.isPresent()) {
            return toBeFollowed + " does not exist!";
        }

        int toBeFollowedId = toBeFollowedUser.get().getId();

        if (followingId == toBeFollowedId) {
            return "You can't follow yourself!";
        }

        List<UserRelation> followedUsers = userDao.getFollowedUsers(followingId);
        Optional<UserRelation> relation = followedUsers.stream()
                .filter(userRelation -> userRelation.getFollowed() == toBeFollowedId)
                .findFirst();

        return relation.map(rel -> following +" already follows " + toBeFollowed)
                .orElseGet(() -> {
                    boolean followFlag = userDao.followUser(followingId, toBeFollowedId);
                    return followFlag ? SUCCESS_MESSAGE : "Failure";
                });
    }

    public String unfollowUser(String following, String tobeUnfollowed) {

        Optional<User> followingUser = userDao.getUserId(following);
        int followingId = followingUser.map(userObject -> userObject.getId())
                .orElseThrow(IllegalStateException::new);

        Optional<User> toBeUnfollowedUser = userDao.getUserId(tobeUnfollowed);

        if (!toBeUnfollowedUser.isPresent()) {
            return tobeUnfollowed + " does not exist!";
        }

        int toBeUnfollowedId = toBeUnfollowedUser.get().getId();
        List<UserRelation> followedUsers = userDao.getFollowedUsers(followingId);
        Optional<UserRelation> relation = followedUsers.stream()
                .filter(userRelation -> userRelation.getFollowed() == toBeUnfollowedId)
                .findFirst();

        if (followingId == toBeUnfollowedId) {
            return "You can't follow/unfollow yourself!";
        }

        return relation.map(rel -> {
            boolean unfollowFlag = userDao.unfollowUser(followingId, toBeUnfollowedId);
            return unfollowFlag ? SUCCESS_MESSAGE : "Failure";
        }).orElse(following +" does not follow " + tobeUnfollowed);

    }


}


