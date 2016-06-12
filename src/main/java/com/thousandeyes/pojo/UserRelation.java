package com.thousandeyes.pojo;

public class UserRelation {

    private int id;
    private int follower;
    private int followed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }

    public int getFollowed() {
        return followed;
    }

    public void setFollowed(int followed) {
        this.followed = followed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRelation that = (UserRelation) o;

        if (id != that.id) return false;
        if (follower != that.follower) return false;
        return followed == that.followed;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + follower;
        result = 31 * result + followed;
        return result;
    }

    @Override
    public String toString() {
        return "UserRelation{" +
                "id=" + id +
                ", follower=" + follower +
                ", followed=" + followed +
                '}';
    }
}
