package app;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserListTest {

    UserList ul = new UserList();
    ChatUser cu;
    @Test
    public void addUser() {
        cu = ul.addUser("user", "pass");
        assertEquals(ul.findUser("user"), cu);
    }

    @Test
    public void validateUser() {
        ChatUser t = ul.validateUser("user", "pass");
        assertEquals(t, cu);
        cu = ul.addUser("user", "pass");
        ChatUser x = ul.validateUser("user", "pass");
        assertEquals(x.getUserName(), "user");
    }

    @Test
    public void logoutUser() {

        cu = ul.addUser("user", "pass");
        cu = ul.addUser("user1", "pass1");
        cu.setStatus(true);
        ul.logoutUser("user1");
        assertFalse(ul.findUser("user1").getStatus());
    }

    @Test
    public void getUsers() {
        cu = ul.addUser("user", "pass");
        cu = ul.addUser("user1", "pass1");

        UserState[] uStates = ul.getUsers();
       assertEquals(uStates.length, 2);
    }

    @Test
    public void findUser() {
        cu = ul.addUser("user", "pass");
        ChatUser pu = ul.addUser("user1", "pass1");
        assertEquals(ul.findUser("user"), cu);
    }
}