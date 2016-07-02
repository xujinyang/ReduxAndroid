package me.jamesxu.reduxandroid.reduce;

import me.jamesxu.reduxandroid.state.ChangeTextState;
import me.jamesxu.reduxlib.action.BaseAction;
import me.jamesxu.reduxlib.reduce.IReduce;
import me.jamesxu.reduxlib.state.State;

/**
 * Created by mobilexu on 2/7/16.
 */
public class ChangeReduce implements IReduce {


    @Override
    public State handleAction(BaseAction action) {
        switch (action.getType()) {
            case 0:
                return new ChangeTextState(true, null);
            case 1:
                return new ChangeTextState(false, action.getHashMap().get("text").toString());
        }
        return new ChangeTextState(false, "默认");
    }
}
