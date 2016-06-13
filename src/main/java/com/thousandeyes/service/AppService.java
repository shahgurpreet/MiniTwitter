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

public class AppService {

    public static final String SUCCESS_MESSAGE = "Success";

    private PostDao postDao;
    private UserDao userDao;

    public AppService(PostDao postDao, UserDao userDao) {

        this.postDao = postDao;
        this.userDao = userDao;
    }

    /**
     * takes the username and the optional search keyword and returns all the posts of that
     * username and the users he follows.
     * @param name username
     * @param search search keyword
     * @return returns posts for the given username
     * @throws IllegalStateException if user id does not exist
     */
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

    /**
     * helper function - returns the posts filtered on the search keyword(if present) to getAllPosts()
     * @param users list of users
     * @param posts list of posts
     * @param search search keyword
     * @return posts of the input users
     */
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

    /**
     * to follow user, and do checks if the user to be followed is already being followed, or
     * does not exist or if user tries to follow itself
     * @param following username of the user who wants to follow
     * @param toBeFollowed username of the user who is to be followed
     * @return success or failure message
     * @throws IllegalStateException if following username is not present
     */
    public String followUser(String following, String toBeFollowed) {

        Optional<User> followingUser = userDao.getUserId(following);
        int followingId = followingUser.map(User::getId)
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

    /**
     * to unfollow user, and do checks if the user to be unfollowed is already being unfollowed, or
     * does not exist or if user tries to unfollow itself
     * @param following username of the user who wants to unfollow
     * @param tobeUnfollowed username of the user who is to be unfollowed
     * @return success or failure message
     * @throws IllegalStateException if following username is not present
     */
    public String unfollowUser(String following, String tobeUnfollowed) {

        Optional<User> followingUser = userDao.getUserId(following);
        int followingId = followingUser.map(User::getId)
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


