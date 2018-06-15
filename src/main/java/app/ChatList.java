package app;
import java.util.*;

public class ChatList {
    private MessageList publicChat = null;
    List<MessageList> chats;

    public ChatList(){
        publicChat = new MessageList("public", false);
        chats = new LinkedList<MessageList>();
    }

    private MessageList addChat(String name, boolean pri){
        MessageList ml = new MessageList(name, pri);
        chats.add(ml);
        return ml;
    }

    public MessageList findChat(ChatUser p1, ChatUser p2){
        if(p1 == null){
            return publicChat;
        }else{
            String chatname = p1.getUserName() + '-' + p2.getUserName();

            Optional<MessageList> ml = chats.stream().filter(u -> chatname.equals(u.getName())).findFirst();
            if(ml.isPresent()){
                return ml.get();
            }
            /* Create the chat room and subscribe both the users */
            MessageList msgList = addChat(chatname, true);
            msgList.subscribe(p1);
            msgList.subscribe(p2);
            return msgList;
        }
    }
}
