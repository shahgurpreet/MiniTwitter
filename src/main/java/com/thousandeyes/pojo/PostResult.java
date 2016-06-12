package com.thousandeyes.pojo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class PostResult {
    private String tweet;
    private String username;

    public String getTweet() {
        return tweet;
    }

    public PostResult setTweet(String tweet) {
        this.tweet = tweet;
        return this;
    }

    public String getUsername() {
        return username;
    }


    public PostResult setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostResult that = (PostResult) o;

        if (tweet != null ? !tweet.equals(that.tweet) : that.tweet != null) return false;
        return username != null ? username.equals(that.username) : that.username == null;

    }

    @Override
    public int hashCode() {
        int result = tweet != null ? tweet.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PostResult{" +
                "tweet='" + tweet + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
