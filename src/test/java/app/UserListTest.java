package app;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserListTest {

    UserList ul = new UserList();
    ChatUser cu;
    @Test
    public void addUser() {
        cu = ul.addUser("user", "pass");
    }

    @Test
    public void validateUser() {
        ChatUser t = ul.validateUser("user", "pass");
        assertEquals(t, cu);
    }

    @Test
    public void logoutUser() {
    }

    @Test
    public void getUsers() {
    }

    @Test
    public void findUser() {
    }
}