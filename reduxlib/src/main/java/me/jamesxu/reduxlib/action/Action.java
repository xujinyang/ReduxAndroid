package me.jamesxu.reduxlib.action;

import java.util.HashMap;

import me.jamesxu.reduxlib.store.Store;

/**
 * Created by mobilexu on 2/7/16.
 */
public class Action {

    private int type;
    private HashMap<String, Object> hashMap;

    public int getType() {
        return type;
    }

    public void clearHashMap() {
        if (hashMap != null) {
            hashMap.clear();
        }
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

    public Action createAction(int type) {
        this.type = type;
        return this;
    }

    public void dispatch(Action action) {
        Store.getInstance().dispatch(action);
    }
}
