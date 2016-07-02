package me.jamesxu.reduxandroid;

import me.jamesxu.reduxlib.BaseAction;
import me.jamesxu.reduxlib.IReduce;

/**
 * Created by mobilexu on 2/7/16.
 */
public class ChangeReduce implements IReduce {

    public static final int ACTION_CHANGE = 1;

    @Override
    public void dispatch(BaseAction action) {
        switch (action.getType()) {
            case ACTION_CHANGE:

                break;
        }
    }
}
