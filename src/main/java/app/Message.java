package app;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    public String text;
    public Date timeStamp;
    public String sender;
    public Message next;

    public Message(String txt, String userName) {
        text = txt;
        sender = userName;
        timeStamp = new Date();
    }
}
