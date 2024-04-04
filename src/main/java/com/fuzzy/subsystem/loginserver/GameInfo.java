package com.fuzzy.subsystem.loginserver;

import java.util.HashMap;
import java.util.Map;

public class GameInfo {
    public int id;
    public String name;

    public Map<Integer, Proxy> _proxy = new HashMap<Integer, Proxy>();

    public GameInfo(int i, String s) {
        id = i;
        name = s;
    }
}