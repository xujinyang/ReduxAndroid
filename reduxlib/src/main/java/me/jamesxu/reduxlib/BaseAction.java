package me.jamesxu.reduxlib;

import java.util.HashMap;

/**
 * Created by mobilexu on 2/7/16.
 */
public class BaseAction implements IAction {

    private int type;
    private HashMap<String, Object> objectHashMap;

    public BaseAction(int type, HashMap<String, Object> objectHashMap) {
        this.type = type;
        this.objectHashMap = objectHashMap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public HashMap<String, Object> getObjectHashMap() {
        return objectHashMap;
    }

    public void setObjectHashMap(HashMap<String, Object> objectHashMap) {
        this.objectHashMap = objectHashMap;
    }
}
