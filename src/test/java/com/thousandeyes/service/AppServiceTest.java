package com.thousandeyes.service;

import com.thousandeyes.dao.PostDao;
import com.thousandeyes.dao.UserDao;
import com.thousandeyes.pojo.Post;
import com.thousandeyes.pojo.PostResult;
import com.thousandeyes.pojo.User;
import com.thousandeyes.pojo.UserRelation;
import javafx.geometry.Pos;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AppServiceTest {

    private AppService appService;
    private PostDao postDao;
    private UserDao userDao;

    @Before
    public void init() {
        postDao = mock(PostDao.class);
        userDao = mock(UserDao.class);
        appService = new AppService(postDao, userDao);
    }


    @Test
    public void testGetAllPosts() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("Sam");
        when(user.getId()).thenReturn(1);
        User user2 = new User();
        user2.setId(2);
        user2.setUsername("Tom");
        User user3 = new User();
        user3.setId(3);
        user3.setUsername("Harry");
        Post userPost = new Post();
        userPost.setUserId(1);
        userPost.setTweet("#User1");
        Post user2Post = new Post();
        user2Post.setUserId(2);
        user2Post.setTweet("#User2");
        Post user3Post = new Post();
        user3Post.setUserId(3);
        user3Post.setTweet("#User3");
        UserRelation relation1 = new UserRelation();
        relation1.setFollowed(user2.getId());
        UserRelation relation2 = new UserRelation();
        relation2.setFollowed(user3.getId());
        List<UserRelation> followedList = Arrays.asList(relation1, relation2);
        when(userDao.getUserId("Sam")).thenReturn(Optional.of(user));
        when(userDao.getFollowedUsers(1)).thenReturn(followedList);
        when(userDao.getUsersFromIds(Arrays.asList(2,3,1))).thenReturn(Arrays.asList(user2,user3,user));
        when(postDao.getAll(Arrays.asList(2,3,1))).thenReturn(Arrays.asList(user2Post,user3Post,userPost));


        PostResult postResult1 = new PostResult().setTweet("#User1").setUsername("Sam");
        PostResult postResult2 = new PostResult().setTweet("#User2").setUsername("Tom");
        PostResult postResult3 = new PostResult().setTweet("#User3").setUsername("Harry");

        List<PostResult> expectedPosts = Arrays.asList(postResult2, postResult3, postResult1);
        List<PostResult> actualPosts = appService.getAllPosts(user.getUsername(), null);

        assertEquals(expectedPosts, actualPosts);
    }

    @Test
    public void testGetAllPostsWithSearch() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("Sam");
        when(user.getId()).thenReturn(1);
        User user2 = new User();
        user2.setId(2);
        user2.setUsername("Tom");
        User user3 = new User();
        user3.setId(3);
        user3.setUsername("Harry");
        Post userPost = new Post();
        userPost.setUserId(1);
        userPost.setTweet("#User1");
        Post user2Post = new Post();
        user2Post.setUserId(2);
        user2Post.setTweet("#User2 #Football");
        Post user3Post = new Post();
        user3Post.setUserId(3);
        user3Post.setTweet("#User3");
        UserRelation relation1 = new UserRelation();
        relation1.setFollowed(user2.getId());
        UserRelation relation2 = new UserRelation();
        relation2.setFollowed(user3.getId());
        List<UserRelation> followedList = Arrays.asList(relation1, relation2);
        when(userDao.getUserId("Sam")).thenReturn(Optional.of(user));
        when(userDao.getFollowedUsers(1)).thenReturn(followedList);
        when(userDao.getUsersFromIds(Arrays.asList(2,3,1))).thenReturn(Arrays.asList(user2,user3,user));
        when(postDao.getAll(Arrays.asList(2,3,1))).thenReturn(Arrays.asList(user2Post,user3Post,userPost));

        PostResult postResult2 = new PostResult().setTweet("#User2 #Football").setUsername("Tom");

        List<PostResult> expectedPosts = Arrays.asList(postResult2);
        List<PostResult> actualPosts = appService.getAllPosts(user.getUsername(), "Football");

        assertEquals(expectedPosts, actualPosts);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAllPostsException() {
        when(userDao.getUserId("Sandy")).thenReturn(Optional.empty());
        appService.getAllPosts("Sandy", null);
    }

    @Test
    public void testFollowUser() {
        User sam = new User();
        sam.setUsername("Sam");
        sam.setId(1);
        User tom = new User();
        tom.setUsername("Tom");
        tom.setId(2);
        UserRelation relation = new UserRelation();
        relation.setFollower(1);
        relation.setFollowed(3);
        when(userDao.getUserId(sam.getUsername())).thenReturn(Optional.of(sam));
        when(userDao.getUserId(tom.getUsername())).thenReturn(Optional.of(tom));
        when(userDao.getFollowedUsers(sam.getId())).thenReturn(Arrays.asList(relation));
        when(userDao.followUser(1,2)).thenReturn(true);

        String expected = "Success";
        String actual = appService.followUser(sam.getUsername(), tom.getUsername());
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void testFollowUserException() {
        when(userDao.getUserId("Sandy")).thenReturn(Optional.empty());
        appService.followUser("Sandy","Tom");
    }

    @Test
    public void testFollowUserNoFollowedUser() {
        User sam = new User();
        sam.setUsername("Sam");
        sam.setId(1);
        User tom = new User();
        tom.setUsername("Tom");
        tom.setId(2);
        when(userDao.getUserId(sam.getUsername())).thenReturn(Optional.of(sam));
        when(userDao.getUserId(tom.getUsername())).thenReturn(Optional.empty());

        String expected = "Tom does not exist!";
        String actual = appService.followUser(sam.getUsername(), tom.getUsername());

        assertEquals(expected, actual);
    }

    @Test
    public void testFollowUserNoFollowItself() {
        User sam = new User();
        sam.setUsername("Sam");
        sam.setId(1);
        when(userDao.getUserId(sam.getUsername())).thenReturn(Optional.of(sam));

        String expected = "You can't follow yourself!";
        String actual = appService.followUser(sam.getUsername(), sam.getUsername());
        assertEquals(expected, actual);
    }

    @Test
    public void testFollowUserAlreadyFollows() {
        User sam = new User();
        sam.setUsername("Sam");
        sam.setId(1);
        User tom = new User();
        tom.setUsername("Tom");
        tom.setId(2);
        UserRelation relation = new UserRelation();
        relation.setFollower(1);
        relation.setFollowed(2);
        when(userDao.getUserId(sam.getUsername())).thenReturn(Optional.of(sam));
        when(userDao.getUserId(tom.getUsername())).thenReturn(Optional.of(tom));
        when(userDao.getFollowedUsers(sam.getId())).thenReturn(Arrays.asList(relation));

        String expected = "Sam already follows Tom";
        String actual = appService.followUser(sam.getUsername(), tom.getUsername());
        assertEquals(expected, actual);
    }

    @Test
    public void testFollowUserDBError() {
        User sam = new User();
        sam.setUsername("Sam");
        sam.setId(1);
        User tom = new User();
        tom.setUsername("Tom");
        tom.setId(2);
        UserRelation relation = new UserRelation();
        relation.setFollower(1);
        relation.setFollowed(3);
        when(userDao.getUserId(sam.getUsername())).thenReturn(Optional.of(sam));
        when(userDao.getUserId(tom.getUsername())).thenReturn(Optional.of(tom));
        when(userDao.getFollowedUsers(sam.getId())).thenReturn(Arrays.asList(relation));
        when(userDao.followUser(1,2)).thenReturn(false);

        String expected = "Failure";
        String actual = appService.followUser(sam.getUsername(), tom.getUsername());
        assertEquals(expected, actual);
    }

    @Test
    public void testUnfollowUser() {
        User sam = new User();
        sam.setUsername("Sam");
        sam.setId(1);
        User tom = new User();
        tom.setUsername("Tom");
        tom.setId(2);
        UserRelation relation = new UserRelation();
        relation.setFollower(1);
        relation.setFollowed(2);
        when(userDao.getUserId(sam.getUsername())).thenReturn(Optional.of(sam));
        when(userDao.getUserId(tom.getUsername())).thenReturn(Optional.of(tom));
        when(userDao.getFollowedUsers(sam.getId())).thenReturn(Arrays.asList(relation));
        when(userDao.unfollowUser(1, 2)).thenReturn(true);

        String expected = "Success";
        String actual = appService.unfollowUser(sam.getUsername(), tom.getUsername());
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void testUnfollowUserException() {
        when(userDao.getUserId("Sandy")).thenReturn(Optional.empty());
        appService.unfollowUser("Sandy","Tom");
    }

    @Test
    public void testUnfollowUserNoFollowedUser() {
        User sam = new User();
        sam.setUsername("Sam");
        sam.setId(1);
        User tom = new User();
        tom.setUsername("Tom");
        tom.setId(2);
        when(userDao.getUserId(sam.getUsername())).thenReturn(Optional.of(sam));
        when(userDao.getUserId(tom.getUsername())).thenReturn(Optional.empty());

        String expected = "Tom does not exist!";
        String actual = appService.unfollowUser(sam.getUsername(), tom.getUsername());

        assertEquals(expected, actual);
    }

    @Test
    public void testUnfollowUserNoFollowItself() {
        User sam = new User();
        sam.setUsername("Sam");
        sam.setId(1);
        when(userDao.getUserId(sam.getUsername())).thenReturn(Optional.of(sam));

        String expected = "You can't follow/unfollow yourself!";
        String actual = appService.unfollowUser(sam.getUsername(), sam.getUsername());
        assertEquals(expected, actual);
    }

    @Test
    public void testFollowUserAlreadyUnfollows() {
        User sam = new User();
        sam.setUsername("Sam");
        sam.setId(1);
        User tom = new User();
        tom.setUsername("Tom");
        tom.setId(2);
        UserRelation relation = new UserRelation();
        relation.setFollower(1);
        relation.setFollowed(3);
        when(userDao.getUserId(sam.getUsername())).thenReturn(Optional.of(sam));
        when(userDao.getUserId(tom.getUsername())).thenReturn(Optional.of(tom));
        when(userDao.getFollowedUsers(sam.getId())).thenReturn(Arrays.asList(relation));

        String expected = "Sam does not follow Tom";
        String actual = appService.unfollowUser(sam.getUsername(), tom.getUsername());
        assertEquals(expected, actual);
    }

    @Test
    public void testUnfollowUserDBError() {
        User sam = new User();
        sam.setUsername("Sam");
        sam.setId(1);
        User tom = new User();
        tom.setUsername("Tom");
        tom.setId(2);
        UserRelation relation = new UserRelation();
        relation.setFollower(1);
        relation.setFollowed(2);
        when(userDao.getUserId(sam.getUsername())).thenReturn(Optional.of(sam));
        when(userDao.getUserId(tom.getUsername())).thenReturn(Optional.of(tom));
        when(userDao.getFollowedUsers(sam.getId())).thenReturn(Arrays.asList(relation));
        when(userDao.unfollowUser(1,2)).thenReturn(false);

        String expected = "Failure";
        String actual = appService.unfollowUser(sam.getUsername(), tom.getUsername());
        assertEquals(expected, actual);
    }

}