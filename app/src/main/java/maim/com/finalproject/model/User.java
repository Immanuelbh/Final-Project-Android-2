package maim.com.finalproject.model;

import java.util.HashMap;
import java.util.HashSet;

public class User {

    //private long id;
    private String email;
    private String UID;
    private String name;
    private String age;
    private String maxRange;
    private String imageUrl;
    private String onlineStatus;
    private String typingTo;
    private HashMap<String,SubGenre> mySkillsList;

    //Preferred Location
    //Skills I can teach
    //Skills I want to learn
    //User image (url)


    public User() {   }

    public User(String email, String UID, String name, String age, String maxRange, String onlineStatus, String typingTo, HashMap<String,SubGenre> list)  {
        this.email = email;
        this.UID = UID;
        this.name = name;
        this.age = age;
        this.maxRange = maxRange;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
        this.mySkillsList = list;
        //TODO add image url to db
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(String maxRange) {
        this.maxRange = maxRange;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public HashMap<String, SubGenre> getMySkillsList() {
        return mySkillsList;
    }

    public void setMySkillsList(HashMap<String, SubGenre> mySkillsList) {
        this.mySkillsList = mySkillsList;
    }
}
