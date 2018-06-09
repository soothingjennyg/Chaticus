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

    private static Map<WsSession, String> userUsernameMap = new ConcurrentHashMap<>();
    private static int nextUserNumber = 1; // Assign to username for next connecting user
    private static UserList users = new UserList();
    private static MessageList cl = new MessageList("public", true);

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
