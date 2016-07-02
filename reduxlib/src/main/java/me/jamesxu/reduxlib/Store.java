package me.jamesxu.reduxlib;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by mobilexu on 2/7/16.
 */
public class Store {
    private static Store sInstance;
    private EventBus eventBus;

    private List<IReduce> iReducers;

    public static Store getInstance() {
        if (sInstance == null) {
            sInstance = new Store();
        }
        return sInstance;
    }

    public Store() {
        eventBus = EventBus.getDefault();
        iReducers = new ArrayList<>();
        eventBus.register(this);
    }


    public void register(IReduce iReduce) {
        iReducers.add(iReduce);
    }

    public void unRegister(IReduce iReduce) {
        iReducers.remove(iReduce);
    }


    public void OnEvent(BaseAction baseAction) {
        for (IReduce iReduce : iReducers) {
            iReduce.dispatch(baseAction);
            iReduce.onStateChange();
        }
    }

}
