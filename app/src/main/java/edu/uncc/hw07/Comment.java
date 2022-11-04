/**
 * Group 9 HW 07
 * Comment.java
 * Phi Ha
 * Srinath Dittakavi
 */

package edu.uncc.hw07;

import java.io.Serializable;

public class Comment implements Serializable {
    String text;
    String user_id;
    String user_name;
    String created_At;

    public String getText() {
        return text;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getCreated_At() {
        return created_At;
    }

    public String getComment_id() {
        return comment_id;
    }

    String comment_id;

    @Override
    public String toString() {
        return "Comment{" +
                "text='" + text + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", created_At='" + created_At + '\'' +
                ", comment_id='" + comment_id + '\'' +
                '}';
    }
}
