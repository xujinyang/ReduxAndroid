package me.jamesxu.reduxandroid.reduce;

import me.jamesxu.reduxandroid.state.ChangeTextState;
import me.jamesxu.reduxandroid.state.SecondState;
import me.jamesxu.reduxlib.action.Action;
import me.jamesxu.reduxlib.reduce.Reduce;
import me.jamesxu.reduxlib.state.State;

/**
 * Created by mobilexu on 2/7/16.
 */
public class SecondReduce implements Reduce {


    public static final int ACTION_SECOND_SHOWLOADING = 3;
    public static final int ACTION_SECOND_SHOWTEXT = 4;

    @Override
    public State handleAction(Action action) {
        switch (action.getType()) {
            case ACTION_SECOND_SHOWLOADING:
                return new SecondState(true, null);
            case ACTION_SECOND_SHOWTEXT:
                return new SecondState(false, action.getHashMap().get("text").toString());
            default:
                return null;
        }
    }
}
