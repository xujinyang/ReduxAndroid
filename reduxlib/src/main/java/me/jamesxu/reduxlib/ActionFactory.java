package me.jamesxu.reduxlib;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by mobilexu on 2/7/16.
 */
public class ActionFactory {

    private static ActionFactory sInstance;
    private EventBus eventBus;

    public static ActionFactory getInstance() {
        if (sInstance == null) {
            sInstance = new ActionFactory();
        }
        return sInstance;
    }

    public ActionFactory() {
        eventBus = EventBus.getDefault();
    }

    public void buildAction(int type, HashMap<String, Object> objectHashMap) {
        BaseAction action = new BaseAction(type, objectHashMap);
        eventBus.post(action);
    }

}
