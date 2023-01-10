package com.sr.dataexport.utils;

import java.util.HashMap;
import java.util.HashSet;

public class UserTracker {
    public static final HashMap<Long, String> users = new HashMap<>();

    public static void clearMap(){
        users.clear();
    }

}
