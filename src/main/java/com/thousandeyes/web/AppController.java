package com.thousandeyes.web;

import com.thousandeyes.pojo.Result;
import com.thousandeyes.pojo.PostResult;
import com.thousandeyes.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AppController {

    private AppService appService;

    @Autowired
    public AppController(AppService appService) {
        this.appService = appService;
    }

    /**
     * show welcome message and endpoint instructions if login is successful
     * @return
     */
    @RequestMapping("/")
    public String showInfo() {
        String name = getUsername();
        return "Hi " + name + ", Welcome to Mini Twitter App!" + "<br>" + "<br>"+
                "Here are the list of endpoints for you to use" + "<br>" +
                "/getPosts : to see your and your followers' posts" + "<br>" +
                "/getPosts?search={search_term} : to see your and your followers' posts filtered based on your search term" + "<br>" +
                "/followUser?username={username} : to follow a user" + "<br>" +
                "/unfollowUser?username={username} : to unfollow a user" + "<br>"+
                "/logout : to logout" + "<br>" + "<br>" +
                "Thanks, have fun!";

    }

    /**
     * endpoint to get all posts for a username and optional keyword
     * @param search
     * @return
     */
    @RequestMapping("/getPosts")
    public List<PostResult> getPosts(@RequestParam(required = false) String search) {
        String name = getUsername();
        return appService.getAllPosts(name, search);
    }


    /**
     * endpoint to make logged in user follow a username
     * @param username
     * @return
     */
    @RequestMapping("/followUser")
    public Result followUser(@RequestParam String username) {
        String name = getUsername();
        String result = appService.followUser(name, username);

        if(result.equals(AppService.SUCCESS_MESSAGE)) {
            return new Result().setSuccess(true);
        } else {
            return new Result().setSuccess(false).setErrorMessage(result);
        }
    }


    /**
     * endpoint to make logged in user unfollow a username
     * @param username
     * @return
     */
    @RequestMapping("/unfollowUser")
    public Result unfollowUser(@RequestParam String username) {
        String name = getUsername();
        String result = appService.unfollowUser(name, username);
        if(result.equals(AppService.SUCCESS_MESSAGE)) {
            return new Result().setSuccess(true);
        } else {
            return new Result().setSuccess(false).setErrorMessage(result);
        }
    }


    /**
     * to get logged in user's username from the current context
     * @return
     */
    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }


}
