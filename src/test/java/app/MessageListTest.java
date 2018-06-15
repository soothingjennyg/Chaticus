package app;

import static org.junit.Assert.*;

public class MessageListTest {

    @org.junit.Test
    public void addMessage() {
        MessageList ml = new MessageList("Flowers", false);
        ml.addMessage(new Message("Testing", "user"));
        assertEquals("Testing", ml.getMessages()[0].text);
    }

    @org.junit.Test
    public void getMessages() {

    }


    @org.junit.Test
    public void getName() {
    }

    @org.junit.Test
    public void subscribe() {
    }

    @org.junit.Test
    public void unsubscribe() {
    }
}