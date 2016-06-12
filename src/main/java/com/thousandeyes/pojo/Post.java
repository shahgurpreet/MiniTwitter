package com.thousandeyes.pojo;

import org.apache.tomcat.jni.Local;

import java.sql.Timestamp;
import java.util.Date;

public class Post {

    private int id;
    private int userId;
    private String tweet;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        if (id != post.id) return false;
        if (userId != post.userId) return false;
        return tweet != null ? tweet.equals(post.tweet) : post.tweet == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userId;
        result = 31 * result + (tweet != null ? tweet.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", userId=" + userId +
                ", tweet='" + tweet + '\'' +
                '}';
    }
}
