package me.jamesxu.reduxlib.reduce;

import me.jamesxu.reduxlib.state.State;
import me.jamesxu.reduxlib.action.BaseAction;

/**
 * Created by mobilexu on 2/7/16.
 */
public interface IReduce {
    State handleAction(BaseAction action);
}
