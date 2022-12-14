/**
 * Group 9 HW 07
 * Forum.java
 * Phi Ha
 * Srinath Dittakavi
 */

package edu.uncc.hw07;

import java.io.Serializable;

public class Forum implements Serializable {

    public String getTitle() {
        return title;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public String getDescription() {
        return description;
    }

    public String getForum_id() {
        return forum_id;
    }

    public String getCreator_id() {
        return creator_id;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public int getLikes() {
        return likes;
    }

    String title;
    String creator_name;
    String description;
    String forum_id;
    String creator_id;
    String createdAt;
    int likes;

    public int getComments() {
        return comments;
    }

    int comments;

    /**
     * Default Constructor
     */
    public Forum(){}

    public Forum(String title, String creator_name, String description, String forum_id, String creator_id, String createdAt, int likes, int comments){
        this.title = title;
        this.creator_name = creator_name;
        this.description = description;
        this.forum_id = forum_id;
        this.creator_id = creator_id;
        this.createdAt = createdAt;
        this.likes = likes;
        this.comments = comments;
    }
}
