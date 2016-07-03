package me.jamesxu.reduxlib.action;

import java.util.HashMap;

/**
 * Created by mobilexu on 2/7/16.
 */
public class Action {

    private int type;
    private HashMap<String, Object> hashMap;

    public int getType() {
        return type;
    }


    public Action(int type) {
        this.type = type;
    }

    public Action(int type, HashMap<String, Object> hashMap) {
        this.type = type;
        this.hashMap = hashMap;
    }

    public HashMap<String, Object> appendHashParam(String key, Object object) {
        if (hashMap == null) {
            hashMap = new HashMap<>();
        }
        hashMap.put(key, object);
        return hashMap;
    }

    public HashMap<String, Object> getHashMap() {
        return hashMap;
    }


}
