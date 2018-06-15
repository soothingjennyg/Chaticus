package app;

import io.javalin.Javalin;
import io.javalin.embeddedserver.jetty.websocket.WsSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import app.util.HerokuUtil;
import static j2html.TagCreator.article;
import static j2html.TagCreator.attrs;
import static j2html.TagCreator.b;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;

public class Chat {

    private static Map<WsSession, ChatUser> userUsernameMap = new ConcurrentHashMap<>();
    private static int nextUserNumber = 1; // Assign to username for next connecting user
    private static UserList users = new UserList();
    //private static MessageList cl = new MessageList("public", true);
    private static ChatList cl = new ChatList();



    public static void main(String[] args) {
        Javalin.create()
                .port(HerokuUtil.getHerokuAssignedPort())
                .enableStaticFiles("/public")
                .ws("/chat", ws -> {
                    ws.onConnect(session -> {
                        System.out.println("Connect from a client");
                    });
                    ws.onClose((session, status, message) -> {
                        ChatUser user = userUsernameMap.get(session);
                        user.setStatus(false);
                        user.setSession(null);
                        userUsernameMap.remove(session);

                        MessageList ml = cl.findChat(null,null);
                        ml.unsubscribe(user);
                        if(ml != null){
                            // This will add the message to our local storage and forward it to all the clients.
                            ml.addMessage(new Message((user.getUserName() + " left the chat"),"Server"));
                        }
                    });
                    ws.onMessage((session, message) -> {
                        JSONObject obj = new JSONObject(message);

                        String username = null;
                        String password = null;
                        ChatUser newUser = null;

                        String messageType = obj.getString("type");
                        System.out.println("Message of type " + messageType);
                        switch(messageType){
                            case "msg":
                                ChatUser user = userUsernameMap.get(session);
                                ChatUser toUser = null;
                                MessageList ml;

                                if(user != null) {
                                    String toUsername = null;

                                    String text = createHtmlMessageFromSender(user.getUserName(), obj.getString("text"), !obj.isNull("toUser"));

                                    if(!obj.isNull("toUser")) {
                                        toUsername = obj.getString("toUser");
                                        toUser = users.findUser(toUsername);

                                        System.out.println("Private message request to user " + toUsername);
                                        ml = cl.findChat(user,toUser);

                                    }else {
                                        ml = cl.findChat(null,null);
                                    }

                                    if(ml != null){
                                        // This will add the message to our local storage and forward it to all the clients.
                                        ml.addMessage(new Message(text,"Server"));
                                    }

                                }else{
                                    // Error. User sent a message but isn't logged in...
                                    System.out.println("Error. Session got a message but user not logged in.");
                                }
                                break;
                            case "login":
                            case "create":
                                username = obj.getString("username");
                                password = obj.getString("password");

                                System.out.println("User attempt: " + username + " Password: " + password);
                                if(messageType.equals("login")) {
                                    newUser = users.validateUser(username, password);
                                }else {
                                    newUser = users.addUser(username, password);
                                }
                                if(newUser != null){
                                    // Add the user to the session map. The session map helps us locate the user
                                    // record on an event.
                                    userUsernameMap.put(session, newUser);

                                    newUser.setStatus(true);
                                    newUser.setSession(session);

                                    // Find the global public chat
                                    ml = cl.findChat(null,null);
                                    if(ml != null){
                                        // Subscribe to the global public chat.
                                        ml.subscribe(newUser);
                                    }

                                    // This will send the existing messages to the user.
                                    ml.replayMessages(newUser);

                                    // This will add the message to our local storage and forward it to all the clients.
                                    ml.addMessage(new Message((newUser.getUserName() + " joined the chat"),"Server"));
                                }else{
                                    if(messageType.equals("login")) {
                                        String err = buildErrorMessageString("Invalid username or password");
                                        System.out.println("Error: " + err);
                                        session.send(err);
                                    }else {
                                        String err = buildErrorMessageString("username already in use.");
                                        System.out.println("Error: " + err);
                                        session.send(err);
                                    }
                                }

                                break;
                            case "logout":

                                ChatUser u2 = userUsernameMap.get(session);
                                if (u2!= null) {
                                    u2.setStatus(false);
                                    u2.setSession(null);
                                    userUsernameMap.remove(session);

                                    MessageList ml1 = cl.findChat(null, null);
                                    ml1.unsubscribe(u2);
                                    if (ml1 != null) {
                                        // This will add the message to our local storage and forward it to all the clients.
                                        ml1.addMessage(new Message((u2.getUserName() + " left the chat"), "Server"));
                                    }
                                }
                                // Logging out a user
                                break;
                            case "loadMessages":
                                username = obj.getString("username");
                                ChatUser u3 = userUsernameMap.get(session);
                                if(u3 != null){
                                    if (username.equals("")) {
                                        ml = cl.findChat(null, null);
                                    } else {
                                        ChatUser u = users.findUser(username);
                                        ml = cl.findChat(u3, u);
                                    }
                                    if (ml != null) {
                                        ml.replayMessages(u3);
                                    }
                                }else{
                                    System.out.println("Error. Session got a message but user not logged in.");
                                }
                                break;
                        }
                    });
                })
                .start();
    }

    private static String buildMessageString(String text, String sender, boolean pri) {
        JSONArray ja = new JSONArray();
        UserState u[] = users.getUsers();

        /* Construct the current user state */
        for(int i=0;i<u.length;i++){
            ja.put(new JSONObject().put("name", u[i].name).put("status", u[i].status));
        }

        JSONObject data = new JSONObject()
                .put("userMessage", text)
                .put("from", sender)
                .put("private", true)
                .put("userlist", ja);

        return data.toString();
    }

    private static String buildErrorMessageString(String text) {
        JSONObject data = new JSONObject()
                .put("error", text);

        return data.toString();
    }

    // Sends a message to a single user.
    public static void sendMessage(ChatUser recipient, String sender, String message, boolean pri) {
        if(recipient.getSession().isOpen()) {
            recipient.getSession().send(buildMessageString(message, sender, pri));
        }
    }

    // Builds a HTML element with a sender-name, a message, and a timestamp
    private static String createHtmlMessageFromSender(String sender, String message, boolean priv) {
        String privStr = ".public";
        if(priv){
            privStr = ".private";
        }
        return article(
                attrs(privStr),
                b(sender + " says:"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message)
        ).render();
    }



}
    /*
    public static void main(String[] args) {
        Javalin.create()
                .port(HerokuUtil.getHerokuAssignedPort())
                .enableStaticFiles("/public")
                .ws("/chat", ws -> {
                    ws.onConnect(session -> {
                        String username = "User" + nextUserNumber++;
                        userUsernameMap.put(session, username);
                        broadcastMessage("Server", (username + " joined the chat"));
                    });
                    ws.onClose((session, status, message) -> {
                        String username = userUsernameMap.get(session);
                        userUsernameMap.remove(session);
                        broadcastMessage("Server", (username + " left the chat"));
                    });
                    ws.onMessage((session, message) -> {
                        broadcastMessage(userUsernameMap.get(session), message);
                    });
                })
                .start();
    }

    // Sends a message from one user to all users, along with a list of current usernames
    private static void broadcastMessage(String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.send(
                        new JSONObject()
                                .put("userMessage", createHtmlMessageFromSender(sender, message))
                                .put("userlist", userUsernameMap.values()).toString()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
       /* private static void privateMessage(String sender, String receiver, String message) {
            userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
                try {
                    session.send(
                            new JSONObject()
                                    .put("userMessage", createHtmlMessageFromSender(sender, message))
                                    .put("userlist", userUsernameMap.values()).toString()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });*/
    /*
    }

       private static String buildMessageString(String text, String sender, boolean pri) {
        JSONArray ja = new JSONArray();
        UserState u[] = users.getUsers();

        /* Construct the current user state */
    /*
        for(int i=0;i<u.length;i++){
            ja.put(new JSONObject().put("name", u[i].name).put("status", u[i].status));
        }

        JSONObject data = new JSONObject()
                .put("userMessage", text)
                .put("from", sender)
                .put("private", true)
                .put("userlist", ja);

        return data.toString();
    }

    private static String buildErrorMessageString(String text) {
        JSONObject data = new JSONObject()
                .put("error", text);

        return data.toString();
    }

    // Builds a HTML element with a sender-name, a message, and a timestamp
    private static String createHtmlMessageFromSender(String sender, String message) {
        return article(
                b(sender + " says:"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message)
        ).render();
    }

    public static void sendMessage(ChatUser recipient, String sender, String message, boolean pri) {
        if(recipient.getSession().isOpen()) {
            recipient.getSession().send(buildMessageString(message, sender, pri));
        }
    }
}
*/