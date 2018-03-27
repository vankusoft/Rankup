package com.jgntic.ivan.dormnews;

/**
 * Created by Ivan on 2/22/2018.
 */

public class CardViewInfo {

    private String title,description,image,profileImage,CommentsCount,votes;

    public CardViewInfo() {
    }

    public CardViewInfo(String title, String description, String image, String profileImage, String votes, String CommentsCount)
    {
        this.title=title;
        this.description=description;
        this.image=image;
        this.profileImage=profileImage;
        this.votes=votes;
        this.CommentsCount=CommentsCount;
    }

    public void setCommentsCount(String commentsCount) {
        CommentsCount = commentsCount;
    }

    public String getCommentsCount() {
        return CommentsCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getVotes() {
        return votes;
    }
}
