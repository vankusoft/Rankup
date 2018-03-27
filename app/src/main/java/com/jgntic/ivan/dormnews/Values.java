package com.jgntic.ivan.dormnews;

/**
 * Created by Ivan on 3/5/2018.
 */

public class Values {

    int valueInt;
    String user_id;
    String post_key;

    public void setPost_key(String post_key) {
        this.post_key = post_key;
    }

    public String getPost_key() {
        return post_key;
    }

    public void setValueInt(int valueInt) {
        this.valueInt=valueInt;
    }

     public int getValueInt() {
        return valueInt;
    }

    public void setUserId(String userId) {
        this.user_id=userId;
    }

    public String getUser_id() {
        return user_id;
    }
}
