package edu.uncc.hw07;

public class Forum {

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

    String title, creator_name, description, forum_id, creator_id;

    /**
     * Default Constructor
     */
    public Forum(){}

    public Forum(String title, String creator_name, String description, String forum_id, String creator_id){
        this.title = title;
        this.creator_name = creator_name;
        this.description = description;
        this.forum_id = forum_id;
        this.creator_id = creator_id;
    }
}
