package app;
import io.javalin.embeddedserver.jetty.websocket.WsSession;

public class ChatUser {
    //private string name;
    private String userName;
    private String password;
    private int userId;
    private boolean loggedIn;
    private WsSession session;
    //public ChatUser next;

    public ChatUser(String user, String aPassword){
        userName = user;
        password = aPassword;
        loggedIn = false;
    }

    public ChatUser(ChatUser toCopy){
        userName = toCopy.userName;
        password = toCopy.password;
        userId = toCopy.userId;
        loggedIn = false;
        //next = null;
    }
    public ChatUser(String username, String password, int userId) {
        this.userName = username;
        this.password = password;
        this.userId = userId;
        this.loggedIn = false;
    }
    public boolean findUserMatch(String user) {
        return userName.equals(user);
    }

    public boolean validatePassword(String pw){
        return pw.equals(password);
    }

    public boolean verify(String username, String password){
        if(this.userName.equals(username) && this.password.equals(password)){
            return true;
        }
        return false;
    }

    public boolean getStatus(){
        return this.loggedIn;
    }

    public String getUserName() {
        return userName;
    }

    public void setStatus(boolean b) {
        loggedIn = b;
    }

    public void setSession(WsSession s) {
        session = s;
    }

    public WsSession getSession(){
        return session;
    }
}

