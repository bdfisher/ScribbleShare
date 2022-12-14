package com.example.scribbleshare.homepage;

/**
 * Holds data for a post
 */
public class PostModel {
    int id;
    String profileName;
    int likeCount;
    boolean isLiked;

    /**
     * Default constructor
     */
    public PostModel() {}

    /**
     * Constructor to initialize all the necessary components of a post
     * @param id Id of the post
     * @param profileName Name of the profile that is posting the post
     * @param likeCount Like count for the post
     * @param commentCount Comment count for the post
     */
    public PostModel(int id, String profileName, int likeCount, int commentCount, boolean isLiked){
        this.id = id;
        this.profileName = profileName;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }

    /**
     * This method returns the id of the post
     * @return Id of the post
     */
    public int getId() {
        return id;
    }

    /**
     * This method sets the id of the post
     * @param id Id of the post
     */
    public void setId(int id){
        this.id = id;
    }

    /**
     * This method returns the profile name
     * @return name of the profile
     */
    public String getProfileName(){
        return profileName;
    }

    /**
     * This method sets the profile name
     * @param profileName Name of the profile
     */
    public void setProfileName(String profileName){
        this.profileName = profileName;
    }

    /**
     * This method returns the like count of the post
     * @return Like count of the post
     */
    public int getLikeCount(){
        return likeCount;
    }

    /**
     * This method sets the like count of the post
     * @param likeCount
     */
    public void setLikeCount(int likeCount){
        this.likeCount = likeCount;
    }

    public boolean getIsLiked(){
        return isLiked;
    }

    public void setIsLiked(boolean isLiked){
        this.isLiked = isLiked;
    }

}
