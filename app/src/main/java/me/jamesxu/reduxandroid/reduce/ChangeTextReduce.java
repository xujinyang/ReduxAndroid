package me.jamesxu.reduxandroid.reduce;

import me.jamesxu.reduxandroid.state.ChangeTextState;
import me.jamesxu.reduxlib.action.Action;
import me.jamesxu.reduxlib.reduce.Reduce;
import me.jamesxu.reduxlib.state.State;

/**
 * Created by mobilexu on 2/7/16.
 */
public class ChangeTextReduce implements Reduce {

    public static final int ACTION_SHOWLOADING = 0;
    public static final int ACTION_SHOWTEXT = 1;

    @Override
    public State handleAction(Action action) {
        switch (action.getType()) {
            case ACTION_SHOWLOADING:
                return new ChangeTextState(true, null);
            case ACTION_SHOWTEXT:
                return new ChangeTextState(false, action.getHashMap().get("text").toString());
            default:
                return null;
        }
    }
}
