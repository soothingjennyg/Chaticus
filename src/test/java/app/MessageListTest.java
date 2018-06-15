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
        MessageList ml = new MessageList("Flowers", false);
        ml.addMessage(new Message("Testing", "user"));
        ml.addMessage(new Message("Testing1", "user1"));
        assertEquals("Testing", ml.getMessages()[0].text);
        assertEquals("Testing1", ml.getMessages()[1].text);
        assertEquals(2, ml.getMessages().length);

    }


    @org.junit.Test
    public void getName() {
        MessageList ml = new MessageList("Flowers", false);
        assertEquals(ml.getName(), "Flowers");
    }

    @org.junit.Test
    public void subscribe() {
        ChatUser t = new ChatUser("user", "pass", 1);
        MessageList ml = new MessageList("Flowers", false);
        assertTrue(ml.subscribe(t));
    }

    @org.junit.Test
    public void unsubscribe() {
        ChatUser t = new ChatUser("user", "pass", 1);
        MessageList ml = new MessageList("Flowers", false);
        assertTrue(ml.subscribe(t));
        assertTrue(ml.unsubscribe(t));
    }
}