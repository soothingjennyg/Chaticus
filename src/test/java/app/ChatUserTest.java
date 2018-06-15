package app;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChatUserTest {

    @Test
    public void verify() {
        ChatUser t = new ChatUser("user", "pass", 1);
        assertTrue(t.verify("user", "pass"));
        assertFalse(t.verify("user", "goop"));
    }

}