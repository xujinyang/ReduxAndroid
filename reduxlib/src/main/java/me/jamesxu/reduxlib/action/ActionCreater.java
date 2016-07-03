package me.jamesxu.reduxlib.action;

import me.jamesxu.reduxlib.store.Store;

/**
 * Created by mobilexu on 3/7/16.
 */
public class ActionCreater {

    public void dispatch(Action action) {
        Store.getInstance().dispatch(action);
    }
}
