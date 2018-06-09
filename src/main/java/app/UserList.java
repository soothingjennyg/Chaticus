package app;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.*;


public class UserList {
        List<ChatUser> users;
        public UserList(){
            users = new LinkedList<ChatUser>();
        }

        public ChatUser addUser(String username, String password){
            ChatUser newUser = new ChatUser(username,password, 0);
            users.add(newUser);
            return newUser;
        }

        public ChatUser validateUser(String username, String password){
            Optional<ChatUser> user =users.stream().filter(u -> u.verify(username, password)).findFirst();
            if(user.isPresent()){
                return user.get();
            }
            return null;
        }

        public void logoutUser(String username){
            Optional<ChatUser> user =users.stream().filter(u -> username.equals(u.getUserName())).findFirst();
            if(user.isPresent()){
                user.get().setStatus(false);
            }
            return;
        }

        public UserState[] getUsers(){
            UserState[] uStates = new UserState[users.size()];
            for(int i = 0;i<users.size();i++){
                uStates[i] = new UserState();
                uStates[i].name = users.get(i).getUserName();
                uStates[i].status = users.get(i).getStatus();
            }
            // Get the list of all users
            return uStates;
        }

        public ChatUser findUser(String username) {
            Optional<ChatUser> user =users.stream().filter(u -> username.equals(u.getUserName())).findFirst();
            if(user.isPresent()){
                return user.get();
            }
            return null;
        }
    }
