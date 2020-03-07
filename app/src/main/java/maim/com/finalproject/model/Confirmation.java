package maim.com.finalproject.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Confirmation implements Serializable  {

    private String senderUid;
    private String receiverUid;

    private String senderName;
    private String receiverName;

    private String skill1;
    private String date1;
    private String location1;

    private String skill2;
    private String date2;
    private String location2;

    private String senderCid;
    private String receiverCid;

    private String completeStatus;

    public Confirmation(){}

    public Confirmation(String senderUid, String receiverUid, String senderName, String receiverName, String skill1, String date1, String location1) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.skill1 = skill1;
        this.date1 = date1;
        this.location1 = location1;
    }

    public Confirmation(String senderUid, String receiverUid, String senderName, String receiverName, String skill1, String date1, String location1, String senderCid, String receiverCid){
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.skill1 = skill1;
        this.date1 = date1;
        this.location1 = location1;
        this.senderCid = senderCid;
        this.receiverCid = receiverCid;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSkill1() {
        return skill1;
    }

    public void setSkill1(String skill1) {
        this.skill1 = skill1;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getLocation1() {
        return location1;
    }

    public void setLocation1(String location1) {
        this.location1 = location1;
    }

    public String getSkill2() {
        return skill2;
    }

    public void setSkill2(String skill2) {
        this.skill2 = skill2;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public String getLocation2() {
        return location2;
    }

    public void setLocation2(String location2) {
        this.location2 = location2;
    }


    public String getSenderCid() {
        return senderCid;
    }

    public void setSenderCid(String senderCid) {
        this.senderCid = senderCid;
    }

    public String getReceiverCid() {
        return receiverCid;
    }

    public void setReceiverCid(String receiverCid) {
        this.receiverCid = receiverCid;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getCompleteStatus() {
        return completeStatus;
    }

    public void setCompleteStatus(String completeStatus) {
        this.completeStatus = completeStatus;
    }

    @NonNull
    @Override
    public String toString() {
        return "Date: " + this.date1 + " | His UID: " + this.receiverUid;
    }
}
