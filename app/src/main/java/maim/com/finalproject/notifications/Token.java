package maim.com.finalproject.notifications;

public class Token {
    /*An FCM Token, or much commonly known as registrationToken.
    An ID issued by GCM connection servers to the client app that allows it to receive messages*/

    String Token;

    public Token(String token) {
        Token = token;
    }

    public Token (){
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
