package com.sr.dataexport.utils;

import java.util.HashMap;
import java.util.HashSet;

public class UserTracker {
    public static final HashSet<Long> users = new HashSet<>();

    public static void clearMap(){
        users.clear();
    }

}
