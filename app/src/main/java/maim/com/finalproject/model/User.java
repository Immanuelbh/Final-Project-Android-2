package maim.com.finalproject.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class User implements Serializable {

    //private long id;
    private String email;
    private String UID;
    private String name;
    private String age;
    private String maxRange;
    private String imageUrl;
    private String onlineStatus;
    private String typingTo;
    private double locationLat;
    private double locationLon;
    private String locationAddress;
    private HashMap<String,SubGenre> mySkillsList;
    private HashMap<String,SubGenre> myLearnList;
    private HashMap<String,Confirmation> myConfirmations;
    private HashMap<String,String> myChatList;

    public User() {   }

    public User(String email, String UID, String name, String age, String maxRange, String onlineStatus, String typingTo, HashMap<String,SubGenre> mySkillsList, HashMap<String,SubGenre> myLearnList)  {
        this.email = email;
        this.UID = UID;
        this.name = name;
        this.age = age;
        this.maxRange = maxRange;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
        this.mySkillsList = mySkillsList;
        this.myLearnList = myLearnList;
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

    public double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }

    public double getLocationLon() {
        return locationLon;
    }

    public void setLocationLon(double locationLon) {
        this.locationLon = locationLon;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public HashMap<String, SubGenre> getMySkillsList() {
        return mySkillsList;
    }

    public void setMySkillsList(HashMap<String, SubGenre> mySkillsList) {
        this.mySkillsList = mySkillsList;
    }

    public HashMap<String, SubGenre> getMyLearnList() {
        return myLearnList;
    }

    public void setMyLearnList(HashMap<String, SubGenre> myLearnList) {
        this.myLearnList = myLearnList;
    }

    public HashMap<String, Confirmation> getMyConfirmations() {
        return myConfirmations;
    }

    public void setMyConfirmations(HashMap<String, Confirmation> myConfirmations) {
        this.myConfirmations = myConfirmations;
    }

    public HashMap<String, String> getMyChatList() {
        return myChatList;
    }

    public void setMyChatList(HashMap<String, String> myChatList) {
        this.myChatList = myChatList;
    }
}
