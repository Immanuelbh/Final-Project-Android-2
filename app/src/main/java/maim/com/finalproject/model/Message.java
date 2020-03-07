package maim.com.finalproject.model;

import androidx.annotation.NonNull;

public class Message {

    private String message, receiver, sender, timeStamp, type;
    private String senderCid, receiverCid;
    private boolean seen;
    private String completeStatus;

    public Message() {
    }

    public Message(String message, String receiver, String sender, String timeStamp, boolean isSeen, String type) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.seen = isSeen;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getCompleteStatus() {
        return completeStatus;
    }

    public void setCompleteStatus(String completeStatus) {
        this.completeStatus = completeStatus;
    }

    @NonNull
    @Override
    public String toString() {
        return "message: " + getMessage() + " | "  + "sender: " + getSender();
    }
}
