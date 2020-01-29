package maim.com.finalproject.model;

import androidx.annotation.NonNull;

public class Message {

    String message, receiver, sender, timeStamp;
    boolean isSeen;

    public Message() {
    }

    public Message(String message, String receiver, String sender, String timeStamp, boolean isSeen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.isSeen = isSeen;
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
        return isSeen;
    }

    //both don't catch : No setter found on class
    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    @NonNull
    @Override
    public String toString() {
        return "message: " + getMessage() + " | "  + "sender: " + getSender();
    }
}
