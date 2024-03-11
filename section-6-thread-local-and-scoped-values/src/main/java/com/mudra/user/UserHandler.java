package com.mudra.user;

import com.mudra.ThreadLocalSimplePlay;

public class UserHandler {

    public void handle() {
        
        User requestUser = ThreadLocalSimplePlay.user.get();
        print("handle - User => " + requestUser);
        
        // handle user 'requestUser'
    }

    public static void print(String m) {
        ThreadLocalSimplePlay.print(m);
    }

}
