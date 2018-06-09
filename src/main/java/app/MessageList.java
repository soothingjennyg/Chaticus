
package app;

import java.util.*;

public class MessageList {

    List<Message> messages;
    List<ChatUser> users;
    private String name;
    private boolean pri;

    public MessageList(String name, boolean pri){
        this.name = name;
        messages = new LinkedList<Message>();
        users = new LinkedList<ChatUser>();
        this.pri = pri;
    }

    public Message addMessage(Message m){
        messages.add(m);

        /* For each of the users... send the message to them */
        for(int i=0;i<users.size();i++){
            if(users.get(i).getStatus()) {
                Chat.sendMessage(users.get(i), m.sender, m.text, this.pri);
            }
        }

        return m;
    }

    public Message[] getMessages(){
        Message[] m = new Message[messages.size()];
        for(int i=0;i<messages.size();i++){
            m[i] = messages.get(i);
        }
        return m;
    }

    public void replayMessages(ChatUser u){
        Message m;
        for(int i=0;i<messages.size();i++){
            m = messages.get(i);
            Chat.sendMessage(u, m.sender, m.text, this.pri);
        }
    }

    public String getName(){ return name; }

    public boolean subscribe(ChatUser u){
        users.add(u);
        return true;
    }

    public boolean unsubscribe(ChatUser u){
        // Remove the user...
        users.remove(u);
        return true;
    }
}
